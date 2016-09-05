/*
 * Copyright (c) 2016. markus endres, felix weichmann
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package preference.sql.index.measurment.group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import explicit.bnl.BNL;
import explicit.preference.BetweenPreference;
import explicit.preference.PriorToPerference;
import explicit.util.IPreference;
import preference.sql.index.BestMatchOnly;
import preference.sql.index.CachedRelation;
import preference.sql.index.NumericalIndex;
import preference.sql.index.measurment.FlatLevelResultSetTestIndex;
import preference.sql.index.measurment.generator.ComplexDataGenerator;
import preference.sql.index.measurment.generator.GaussianDataGenerator;
import preference.sql.index.measurment.generator.LinearDataGenerator;
import preference.sql.index.measurment.generator.RandomDataGenerator;
import preference.sql.index.measurment.generator.SimpleDataGenerator;
import preference.sql.index.numerical.IntegerRangeTree;

public class MeasurePriorTo {

	public static void main(String[] args) {

		// Define the Data to be generated
		List<SimpleDataGenerator> sdgs = new ArrayList<SimpleDataGenerator>();
		sdgs.add(new LinearDataGenerator(0, 9));
		sdgs.add(new RandomDataGenerator(0, 0, 9));
		sdgs.add(new GaussianDataGenerator(2718281828l, 5, 2, 1, 9));
		sdgs.add(new RandomDataGenerator(3141592653l, 0, 9));

		// generate the Data as Result Set
		FlatLevelResultSetTestIndex fullRs = new FlatLevelResultSetTestIndex(
				ComplexDataGenerator.generateData(1000000, sdgs));

		// encapsule the result set as CachedRelation for Index
		CachedRelation cr = new CachedRelation(fullRs, new String[] { "linear", "random0", "gaussEuler", "randomPI" });

		System.out.println("limit 10");
		cr.print(10);
		System.out.println();

		// Build Index
		NumericalIndex<Integer> index = new IntegerRangeTree(cr, 0);

		// Build normal PriorTo Preference and Execute BNL on full data
		List<BetweenParam> fullPrefs = new ArrayList<BetweenParam>();
		fullPrefs.add(new BetweenParam(5, 5, 0, 0));// linear.between(5,5,0)
													// PRIOR TO
		fullPrefs.add(new BetweenParam(5, 5, 0, 1));// random0.between(5,5,0)
													// PRIOR TO
		fullPrefs.add(new BetweenParam(5, 5, 0, 2));// gaussEuler.between(5,5,0)
													// PRIOR TO
		fullPrefs.add(new BetweenParam(5, 5, 0, 3));// randomPI.between(5,5,0)
		IPreference fullPref = generateBetweenPriorToChain(fullPrefs);

		fullRs.reset();
		BNL fullBnl = new BNL(fullRs, fullPref);
		fullBnl.hasNext();

		System.out.println("fullBnl size: " + fullBnl.size());

		// Get BMO of first Base Preference on index
		BestMatchOnly bmo = index.between(5, 5, 0); // linear.between(5,5,0)
		FlatLevelResultSetTestIndex partRs = new FlatLevelResultSetTestIndex(bmo);

		// Build PriorToPreference without the first Base Preference and Execute
		// on BMO from Index
		List<BetweenParam> partPrefs = new ArrayList<BetweenParam>();
		partPrefs.add(new BetweenParam(5, 5, 0, 1));// random0.between(5,5,0)
													// PRIOR TO
		partPrefs.add(new BetweenParam(5, 5, 0, 2));// gaussEuler.between(5,5,0)
													// PRIOR TO
		partPrefs.add(new BetweenParam(5, 5, 0, 3));// randomPI.between(5,5,0)
		IPreference partPref = generateBetweenPriorToChain(partPrefs);

		BNL partBnl = new BNL(partRs, partPref);
		partBnl.hasNext();

		System.out.println("partBnl size: " + partBnl.size());

	}

	private static IPreference generateBetweenPriorToChain(List<BetweenParam> params) {
		Iterator<BetweenParam> iterator = params.iterator();
		BetweenParam param = iterator.next();
		IPreference between = new BetweenPreference(param.low, param.up, param.d);

		BetweenParam next = iterator.next();
		if (iterator.hasNext()) {
			return new PriorToPerference(between, param.column, generateBetweenPriorToChainRecursion(next, iterator));
		} else {
			IPreference last = new BetweenPreference(next.low, next.up, next.d);
			return new PriorToPerference(between, param.column, last, next.column);
		}

	}

	private static IPreference generateBetweenPriorToChainRecursion(BetweenParam param,
			Iterator<BetweenParam> iterator) {
		IPreference between = new BetweenPreference(param.low, param.up, param.d);
		if (iterator.hasNext()) {
			BetweenParam next = iterator.next();
			if (iterator.hasNext()) {
				return new PriorToPerference(between, param.column,
						generateBetweenPriorToChainRecursion(next, iterator));
			} else {
				IPreference last = new BetweenPreference(next.low, next.up, next.d);
				return new PriorToPerference(between, param.column, last, next.column);
			}
		} else {
			return between;
		}
	}

}

class BetweenParam {
	int low;
	int up;
	int d;
	int column;

	public BetweenParam(int low, int up, int d, int column) {
		this.low = low;
		this.up = up;
		this.d = d;
		this.column = column;
	}
}

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

package preference.sql.index.measurment.generator;

import java.util.Random;

public class GaussianDataGenerator implements SimpleDataGenerator {

	private Random rand;

	private int mean;
	private int standardDeviation;
	private int infimum;
	private int supremum;

	public GaussianDataGenerator(long seed, int mean, int standardDeviation) {
		this(seed, mean, standardDeviation, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public GaussianDataGenerator(long seed, int mean, int standardDeviation, int infimum, int supremum) {
		if (1 > standardDeviation) {
			throw new IllegalArgumentException("standardDeviation must be greater than zero");
		}
		if (infimum > supremum) {
			throw new IllegalArgumentException("infimum must be smaler than or equal to supremum");
		}

		this.rand = new Random(seed);

		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.infimum = infimum;
		this.supremum = supremum;
	}

	@Override
	public Object generateNext() {
		int ret;

		do {
			ret = (int) Math.round(rand.nextGaussian() * standardDeviation + mean);
		} while (ret < infimum || ret > supremum);

		return ret;
	}

}

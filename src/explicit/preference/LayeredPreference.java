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

package explicit.preference;

import explicit.flatlc.levels.FlatLevelCombination;
import explicit.util.IPreference;

public class LayeredPreference implements IPreference {

	String[][] layers;

	public LayeredPreference(String[][] layers) {
		this.layers = layers;
	}

	@Override
	public int compare(Object objA, Object objB) {
		return compare(objA, objB, 0);
	}

	@Override
	public int compare(Object objA, Object objB, int idx) {
		String a = saveToString(((FlatLevelCombination) objA).getValue(idx));
		String b = saveToString(((FlatLevelCombination) objB).getValue(idx));

		int levelA = getScoreForComparison(a);
		int levelB = getScoreForComparison(b);

		if (levelA < levelB) {
			return IPreference.GREATER;
		} else if (levelA > levelB) {
			return IPreference.LESS;
		} else {
			return IPreference.EQUAL;
		}
	}

	private String saveToString(Object o) {
		if (null == o) {
			return null;
		} else {
			return o.toString();
		}
	}

	private int getScoreForComparison(String value) {
		for (int i = 0; i < layers.length; i++) {
			for (String s : layers[i]) {
				if (s.equals(value)) {
					return i;
				}
			}
		}

		return layers.length;
	}

}

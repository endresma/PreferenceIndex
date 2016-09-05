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

public class BetweenPreference implements IPreference {

	private int minBoundary;
	private int maxBoundary;
	private int d;

	public BetweenPreference(int low, int up, int d) {
		this.minBoundary = low;
		this.maxBoundary = up;
		this.d = d;
	}

	public BetweenPreference(int low, int up) {
		this.minBoundary = low;
		this.maxBoundary = up;
		this.d = 0;
	}

	/*
	 * Ceiling compliment to the Math.floorDiv(long,long) function.
	 */
	private long ceilDiv(long x, long y) {
		return Math.floorDiv(x, y) + (x % y == 0 ? 0 : 1);
	}

	@Override
	public int compare(Object objA, Object objB) {
		return compare(objA, objB, 0);
	}

	@Override
	public int compare(Object objA, Object objB, int idx) {

		int a = (int) ((FlatLevelCombination) objA).getValue(idx);
		int b = (int) ((FlatLevelCombination) objB).getValue(idx);

		long levelA = getScoreForComparison(a);
		long levelB = getScoreForComparison(b);

		if (levelA < levelB) {
			return IPreference.GREATER;
		} else if (levelA > levelB) {
			return IPreference.LESS;
		} else {
			return IPreference.EQUAL;
		}

	}

	private long getScoreForComparison(int value) {
		if (0 < d) {
			return distanceD(value);
		} else {
			return distance(value);
		}
	}

	private long distanceD(int value) {
		if (value < minBoundary) {
			return ceilDiv(minBoundary - value, d);
		} else if (value > maxBoundary) {
			return ceilDiv(value - maxBoundary, d);
		} else {
			return 0;
		}
	}

	private long distance(int value) {
		if (value < minBoundary) {
			return minBoundary - value;
		} else if (value > maxBoundary) {
			return value - maxBoundary;
		} else {
			return 0;
		}
	}
}

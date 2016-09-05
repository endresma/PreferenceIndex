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

import explicit.util.IPreference;

public class PriorToPerference implements IPreference {

	private IPreference dominant;
	private int idxD;
	private IPreference submissive;
	private int idxS;

	public PriorToPerference(IPreference dominant, int idxD, IPreference submissive, int idxS) {
		this.dominant = dominant;
		this.idxD = idxD;
		this.submissive = submissive;
		this.idxS = idxS;
	}

	public PriorToPerference(IPreference dominant, int idxD, IPreference submissive) {
		this(dominant, idxD, submissive, 0);
	}

	public PriorToPerference(IPreference dominant, IPreference submissive, int idxS) {
		this(dominant, 0, submissive, idxS);
	}

	public PriorToPerference(IPreference dominant, IPreference submissive) {
		this(dominant, 0, submissive, 0);
	}

	@Override
	public int compare(Object objA, Object objB) {
		int ret = this.dominant.compare(objA, objB, this.idxD);
		if (IPreference.EQUAL == ret) {
			return this.submissive.compare(objA, objB, this.idxS);
		} else {
			return ret;
		}
	}

	@Override
	public int compare(Object objA, Object objB, int idx) {
		return this.compare(objA, objB);
	}

}

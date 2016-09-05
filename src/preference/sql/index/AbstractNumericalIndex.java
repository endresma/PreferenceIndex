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

package preference.sql.index;

/**
 * A skeletal implementation for numerical preference indices.
 * 
 * @author Felix Weichmann
 *
 */
public abstract class AbstractNumericalIndex<T extends Number & Comparable<T>> extends AbstractPreferenceIndex
		implements NumericalIndex<T> {

	/**
	 * Create the numerical preference index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 */
	public AbstractNumericalIndex(CachedRelation relation, int column) {
		super(relation, column);
	}

	@Override
	public IndexDomain getDomain() {
		return IndexDomain.NUMERICAL;
	}

}

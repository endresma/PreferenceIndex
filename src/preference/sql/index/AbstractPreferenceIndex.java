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
 * A skeletal implementation for all preference indices.
 * 
 * @author Felix Weichmann
 *
 */
public abstract class AbstractPreferenceIndex implements PreferenceIndex {

	protected CachedRelation relation;
	protected int column;
	
	/**
	 * Create the preference index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index of the column of the relation to be indexed.
	 */
	public AbstractPreferenceIndex(CachedRelation relation, int column) {
		this.relation = relation;
		this.column = column;
	}

	/**
	 * Creates the preference index from the column of the cached relation.
	 */
	abstract protected void build();

	/**
	 * Returns the domain of the index implementation.
	 * 
	 * @return The domain of the index implementation.
	 */
	abstract public IndexDomain getDomain();

	/**
	 * Returns the cached relation of this index.
	 * 
	 * @return The cached relation of this index.
	 */
	public CachedRelation getCachedTable() {
		return relation;
	}

	/**
	 * Returns the column of the cached relation this index is build on.
	 * 
	 * @return The column of the cached relation this index is build on.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Checks if the index has any entries.
	 * 
	 * @return true if there are no entries.
	 */
	abstract public boolean isEmpty();
}

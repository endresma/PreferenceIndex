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
 * Defines the functions common to all numerical preference index structures.
 * 
 * @author Felix Weichmann
 *
 * @param <T>
 */
public interface NumericalIndex<T extends Number & Comparable<T>> extends PreferenceIndex {
	
	//
	// TODO: The score preference has to be defined.
	//
	
	/**
	 * Returns the BMO for the 'between' preference on the relation.
	 * 
	 * @param low
	 *            The inclusive lower border of the preference.
	 * @param up
	 *            The inclusive upper border of the preference.
	 * @param d
	 *            The increments of widening the preference by levels. For zero
	 *            as d the level is equal to the distance to z.
	 * @return The BMO on the relation.
	 */
	public BestMatchOnly between(T low, T up, T d);

	/**
	 * Returns the BMO for the 'around' preference on the relation.
	 * 
	 * @param z
	 *            The optimal point of the preference.
	 * @param d
	 *            The increments of widening the preference by levels. For zero
	 *            as d the level is equal to the distance to z.
	 * @return The BMO on the relation.
	 */
	public BestMatchOnly around(T z, T d);

	/**
	 * Returns the BMO for the 'lowest' preference on the relation.
	 * 
	 * @param d
	 *            The increments of widening the preference by levels. For zero
	 *            as d the level is equal to the distance to z.
	 * @return The BMO on the relation.
	 */
	public BestMatchOnly lowest(T d);

	/**
	 * Returns the BMO for the 'highest' preference on the relation.
	 * 
	 * @param d
	 *            The increments of widening the preference by levels. For zero
	 *            as d the level is equal to the distance to z.
	 * @return The BMO on the relation.
	 */
	public BestMatchOnly highest(T d);

	/**
	 * Returns the BMO for the 'less than'/'at most' preference on the relation.
	 * 
	 * @param z
	 *            The inclusive maximum for level 0 of the preference.
	 * @param d
	 *            The increments of widening the preference by levels. For zero
	 *            as d the level is equal to the distance to z.
	 * @return The BMO on the relation.
	 */
	public BestMatchOnly atMost(T z, T d);

	/**
	 * Returns the BMO for the 'more than'/'at least' preference on the
	 * relation.
	 * 
	 * @param z
	 *            The inclusive minimum for level 0 of the preference.
	 * @param d
	 *            The increments of widening the preference by levels. For zero
	 *            as d the level is equal to the distance to z.
	 * @return The BMO on the relation.
	 */
	public BestMatchOnly atLeast(T z, T d);
	
}

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

package preference.sql.index.categorical;

import preference.sql.index.AbstractCategoricalIndex;
import preference.sql.index.CachedRelation;

import java.util.*;

/**
 * An implementation of the categorical preference index based on a hash map.
 * 
 * @author Felix Weichmann
 *
 */
public class HashIndex extends AbstractCategoricalIndex {

	/**
	 * Create and build a hash index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 */
	public HashIndex(CachedRelation relation, int column) {
		this(relation, column, false);
	}

	/**
	 * Create a hash index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 * @param manualBuild
	 *            Set as true if build() will be called manually at a later
	 *            point in time.
	 */
	public HashIndex(CachedRelation relation, int column, boolean manualBuild) {
		super(relation, column);
		if (!manualBuild) {
			build();
		}
	}

	private Map<String, List<Object[]>> map;

	@Override
	protected void build() {
		Object field;
		Set<String> keys;
		String key;

		// make a temporary map for index creation
		Map<String, List<Object[]>> tempMap = new HashMap<String, List<Object[]>>();

		// Get the key lists for each index
		for (Object[] tuple : relation.data) {

			field = tuple[column];

			// ignore null fields in the index
			if (null == field) {
				continue;
			} else { // cast it otherwise
				key = field.toString();
			}

			// insert new list into the map
			// on the first appearance of a key
			if (!tempMap.containsKey(key)) {
				tempMap.put(key, new ArrayList<Object[]>());
			}

			// add the tuple to it's key list
			tempMap.get(key).add(tuple);
		}

		// get the set of keys for iteration
		keys = tempMap.keySet();

		// initialize map with high lookup speed
		map = new HashMap<String, List<Object[]>>((int) Math.ceil(keys.size() / 0.5), 0.5f);

		// keep the lists of tuples for each key
		for (String k : keys) {
			map.put(k, tempMap.get(k));
		}
	}

	@Override
	public boolean isEmpty() {
		return (map.size() < 1);
	}

	@Override
	public List<Object[]> lookup(String key) {
		if (!map.containsKey(key)) {
			return new ArrayList<Object[]>();
		} else {
			return map.get(key);
		}
	}

	@Override
	public Set<String> keys() {
		return new HashSet<String>(map.keySet());
	}

}

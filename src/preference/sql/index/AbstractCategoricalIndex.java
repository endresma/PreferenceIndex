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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A skeletal implementation for categorical preference indices.
 * 
 * @author Felix Weichmann
 *
 */
public abstract class AbstractCategoricalIndex extends AbstractPreferenceIndex implements CategoricalIndex {

	/**
	 * Create the categorical preference index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 */
	public AbstractCategoricalIndex(CachedRelation relation, int column) {
		super(relation, column);
	}

	@Override
	public IndexDomain getDomain() {
		return IndexDomain.CATEGORICAL;
	}

	/**
	 * Look for the tuples in the relation corresponding to the key.
	 * 
	 * @param key
	 *            The key to be looked up in the index.
	 * @return A list of tuples corresponding to the key.
	 */
	abstract public List<Object[]> lookup(String key);

	/**
	 * Returns a copy of the keys contained in this index.
	 * 
	 * @return The keys contained in this index.
	 */
	abstract public Set<String> keys();

	@Override
	public BestMatchOnly layered(String[][] layers) {
		if (isEmpty()) {
			// The index is empty. Return all tuples in relation.
			return new BestMatchOnly(getCachedTable().data, layers.length);
		}

		// A counter for the level
		int level = -1;

		// The BMO set
		List<Object[]> bmo = new ArrayList<Object[]>();

		// Loop over all Layers
		for (String[] layer : layers) {
			// increment rank counter
			level++;

			// Loop over all members of the layer
			for (String member : layer) {

				// Add all tuples found for the member
				bmo.addAll(lookup(member));

			}

			// Return BMO if any tuples have been found for this layer
			if (0 != bmo.size()) {
				return new BestMatchOnly(bmo, level);
			}

		}

		// increment rank counter
		level++;

		// The BMO is still empty. Return all tuples in relation.
		return new BestMatchOnly(getCachedTable().data, level);
	}

	@Override
	public BestMatchOnly pos(String[] posSet) {
		return layered(new String[][] { posSet });
	}

	@Override
	public BestMatchOnly neg(String[] negSet) {
		Set<String> keys = keys();
		keys.removeAll(Arrays.asList(negSet));
		return layered(new String[][] { keys.toArray(new String[keys.size()]) });
	}

	@Override
	public BestMatchOnly posPos(String[] posSet1, String[] posSet2) {
		return layered(new String[][] { posSet1, posSet2 });
	}

	@Override
	public BestMatchOnly posNeg(String[] posSet, String[] negSet) {
		Set<String> keys = keys();
		keys.removeAll(Arrays.asList(posSet));
		keys.removeAll(Arrays.asList(negSet));
		return layered(new String[][] { posSet, keys.toArray(new String[keys.size()]) });
	}

}

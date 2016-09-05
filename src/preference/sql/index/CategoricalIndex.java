
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
 * Defines the functions common to all categorical preference index structures.
 *
 * @author Felix Weichmann
 */
public interface CategoricalIndex extends PreferenceIndex {

    /**
     * Returns the BMO for the 'layered' preference on the relation.
     *
     * @param layers An array containing arrays of keys for each layer.
     * @return The BMO on the relation.
     */
    public BestMatchOnly layered(String[][] layers);

    /**
     * Returns the BMO for the 'pos' preference on the relation.
     *
     * @param posSet An array containing keys for the this layer.
     * @return The BMO on the relation.
     */
    public BestMatchOnly pos(String[] posSet);

    /**
     * Returns the BMO for the 'neg' preference on the relation.
     *
     * @param negSet An array containing keys for the this layer.
     * @return The BMO on the relation.
     */
    public BestMatchOnly neg(String[] negSet);

    /**
     * Returns the BMO for the 'pos/pos' preference on the relation.
     *
     * @param posSet1 An array containing keys for the this layer.
     * @param posSet2 An array containing keys for the this layer.
     * @return The BMO on the relation.
     */
    public BestMatchOnly posPos(String[] posSet1, String[] posSet2);

    /**
     * Returns the BMO for the 'pos/neg' preference on the relation.
     *
     * @param posSet An array containing keys for the this layer.
     * @param negSet An array containing keys for the this layer.
     * @return The BMO on the relation.
     */
    public BestMatchOnly posNeg(String[] posSet, String[] negSet);
}

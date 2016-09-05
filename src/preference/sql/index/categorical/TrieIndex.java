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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import preference.sql.index.CachedRelation;
import preference.sql.index.AbstractCategoricalIndex;

/**
 * An implementation of the categorical preference index based on a prefix tree.
 * 
 * @author Felix Weichmann
 *
 */
public class TrieIndex extends AbstractCategoricalIndex {

	private TrieNode root;
	private Set<String> keys;

	/**
	 * Create and build a trie index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 */
	public TrieIndex(CachedRelation relation, int column) {
		this(relation, column, false);
	}
	
	/**
	 * Create a trie index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 * @param manualBuild
	 *            Set as true if build() will be called manually at a later
	 *            point in time.
	 */
	public TrieIndex(CachedRelation relation, int column, boolean manualBuild) {
		super(relation, column);
		if (!manualBuild) {
			build();
		}
	}

	@Override
	protected void build() {
		// Create the root node
		// it is always empty and the key of no consequence
		root = new TrieNode(Byte.MIN_VALUE);

		// use a HashIndex for simplified creation
		HashIndex index = new HashIndex(relation, column);

		// get the index keys
		keys = index.keys();

		// insert the tuple lists for each key into the tree
		for (String key : keys) {
			insert(key.getBytes(StandardCharsets.UTF_8), 0, root, index.lookup(key));
		}
	}

	@Override
	public boolean isEmpty() {
		return (keys.size() < 1);
	}

	/*
	 * Recursively insert the string into the tree.
	 */
	private synchronized void insert(byte[] string, Integer offset, TrieNode node, List<Object[]> payload) {
		// check for recursion finish
		if (string.length <= offset) {
			// only this simple because we already use the unique keys
			node.payload = payload;
			return;
		}

		// get the next node for this byte
		byte b = string[offset];
		TrieNode next = node.children[b + 128];

		// this byte has not yet been inserted
		if (null == next) {
			next = new TrieNode(b);
			node.children[b + 128] = next;
		}

		// call insert recursively
		insert(string, offset + 1, next, payload);
	}

	@Override
	public List<Object[]> lookup(String key) {
		// get the bytes from the search string
		byte[] string = key.getBytes(StandardCharsets.UTF_8);

		// start with the root node
		TrieNode node = root;

		// follow the tree down the nodes for each byte of the search key
		for (byte b : string) {
			node = node.children[b + 128];

			// if the child node dose not exist
			// the search key is not contained in the tree
			if (null == node) {
				return new ArrayList<Object[]>();
			}

		}

		// return the tuple list of the found node
		return node.payload;
	}

	@Override
	public Set<String> keys() {
		return new HashSet<String>(keys);
	}

}

/*
 * A single node of the prefix tree.
 */
class TrieNode {
	TrieNode[] children;
	List<Object[]> payload;

	public TrieNode(byte ch) {
		children = new TrieNode[256];
		payload = new ArrayList<Object[]>();
	}
}

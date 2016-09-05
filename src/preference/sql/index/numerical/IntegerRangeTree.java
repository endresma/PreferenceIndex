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

package preference.sql.index.numerical;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import preference.sql.index.CachedRelation;
import preference.sql.index.AbstractNumericalIndex;
import preference.sql.index.BestMatchOnly;

/**
 * An implementation of the numerical preference index for Integers based on a
 * range tree.
 * 
 * @author Felix Weichmann
 *
 */
public class IntegerRangeTree extends AbstractNumericalIndex<Integer> {

	private TreeMap<Integer, GroupListElement> map;

	private GroupListElement groupStart;
	private GroupListElement groupEnd;

	private int groupSize;

	/**
	 * Create and build a range tree index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 */
	public IntegerRangeTree(CachedRelation relation, int column) {
		this(relation, column, false);
	}

	/**
	 * Create a range tree index for the column of the relation.
	 * 
	 * @param relation
	 *            The cached relation to be indexed.
	 * @param column
	 *            The index column of the relation to be indexed.
	 * @param manualBuild
	 *            Set as true if build() will be called manually at a later
	 *            point in time.
	 */
	public IntegerRangeTree(CachedRelation relation, int column, boolean manualBuild) {
		super(relation, column);
		if (!manualBuild) {
			build();
		}
	}

	@Override
	protected void build() {
		Object field;
		Integer key;
		Entry<Integer, GroupListElement> prev, next;
		GroupListElement curr;

		// make a map for index creation
		// based on red black tree
		map = new TreeMap<Integer, GroupListElement>();

		// counter for the number of groups
		groupSize = 0;

		// Get the key lists for each index
		for (Object[] tuple : relation.data) {

			field = tuple[column];

			// ignore null fields in the index
			if (null == field) {
				continue;
			} else { // cast it otherwise
				key = (Integer) field;
			}

			// insert new GroupListElement into the map
			// on the first appearance of a key
			if (!map.containsKey(key)) {
				curr = new GroupListElement(key);

				prev = map.floorEntry(key);
				next = map.ceilingEntry(key);
				if (null != prev) {
					prev.getValue().insertOtherAsNext(curr);
				} else if (null != next) {
					next.getValue().insertOtherAsPrevious(curr);
				}

				map.put(key, curr);

				// count the number of groups
				groupSize++;
			}

			// add the tuple to it's GroupListElement
			map.get(key).add(tuple);
		}

		// get the start and end of the GroupList
		groupStart = map.firstEntry().getValue();
		groupEnd = map.lastEntry().getValue();

		// index the positions
		TupleListElement element;
		long position = 0;
		element = groupStart.low;
		while (null != element) {
			element.position = position;
			position++;
			element = element.next;
		}
	}

	@Override
	public boolean isEmpty() {
		return (groupSize < 1);
	}

	/*
	 * Ceiling compliment to the Math.floorDiv(long,long) function.
	 */
	private long ceilDiv(long x, long y) {
		return Math.floorDiv(x, y) + (x % y == 0 ? 0 : 1);
	}

	@Override
	public BestMatchOnly between(Integer low, Integer up, Integer d) {
		if (low > up) {
			throw new IllegalArgumentException("low needs to be smaller than or equal to up!");
		}
		if (0 > d) {
			throw new IllegalArgumentException("d needs to be greater than or equal to zero!");
		}

		if (isEmpty()) { // if the index is empty it has an empty BMO
			return new BestMatchOnly(new ArrayList<Object[]>(), 0);
		}

		// parameters for the return
		long level = 0;
		GroupListElement a, b;

		Entry<Integer, GroupListElement> current = map.floorEntry(up);
		if (current == null) { // up below the smallest key
			b = groupStart;
			a = b;
			level = b.key - up;

			if (0 != d) { // the BMO has to be grouped by level
				level = ceilDiv(level, d);
				while (null != b.next) {
					if (ceilDiv(b.next.key - up, d) != level) {
						break;
					} else {
						b = b.next;
					}
				}
			}
		} else {
			b = current.getValue();
			a = b;

			if (b.key < low && b.next == null) { // low above the highest key
				level = low - a.key;

				if (0 != d) { // the BMO has to be grouped by level
					level = ceilDiv(level, d);

					while (null != a.prev) {
						if (ceilDiv(low - a.prev.key, d) != level) {
							break;
						} else {
							a = a.prev;
						}
					}
				}
			} else if (b.key < low) { // low and up between two keys
				b = b.next;
				long levelA = low - a.key;
				long levelB = b.key - up;

				if (0 != d) { // the BMO has to be grouped by level
					levelA = ceilDiv(levelA, d);
					while (null != a.prev) {
						if (ceilDiv(low - a.prev.key, d) != levelA) {
							break;
						} else {
							a = a.prev;
						}
					}

					levelB = ceilDiv(levelB, d);
					while (null != b.next) {
						if (ceilDiv(b.next.key - up, d) != levelB) {
							break;
						} else {
							b = b.next;
						}
					}
				}

				// choose the better matching level
				if (levelA < levelB) {
					level = levelA;
					b = a;
				} else if (levelB < levelA) {
					level = levelB;
					a = b;
				} else {
					level = levelA;
				}

			} else { // there are keys between low and up
				while (null != a.prev) {
					if (a.prev.key < low) {
						break;
					} else {
						a = a.prev;
					}
				}
			}
		}

		return new BestMatchOnly(new TupleList(a.low, b.up), level);
	}

	@Override
	public BestMatchOnly around(Integer z, Integer d) {
		return between(z, z, d);
	}

	@Override
	public BestMatchOnly lowest(Integer d) {
		if (0 > d) {
			throw new IllegalArgumentException("d needs to be greater than or equal to zero!");
		}

		if (isEmpty()) { // if the index is empty it has an empty BMO
			return new BestMatchOnly(new ArrayList<Object[]>(), 0);
		}

		// parameters for the return
		long level = 0;
		GroupListElement a, b;

		b = groupStart;
		a = b;
		level = b.key - Integer.MIN_VALUE;

		if (0 != d) { // the BMO has to be grouped by level
			level = ceilDiv(level, d);
			while (null != b.next) {
				if (ceilDiv(b.next.key - Integer.MIN_VALUE, d) != level) {
					break;
				} else {
					b = b.next;
				}
			}
		}

		return new BestMatchOnly(new TupleList(a.low, b.up), level);
	}

	@Override
	public BestMatchOnly highest(Integer d) {
		if (0 > d) {
			throw new IllegalArgumentException("d needs to be greater than or equal to zero!");
		}

		if (isEmpty()) { // if the index is empty it has an empty BMO
			return new BestMatchOnly(new ArrayList<Object[]>(), 0);
		}

		// parameters for the return
		long level = 0;
		GroupListElement a, b;

		b = groupEnd;
		a = b;
		level = Integer.MAX_VALUE - a.key;

		if (0 != d) { // the BMO has to be grouped by level
			level = ceilDiv(level, d);

			while (null != a.prev) {
				if (ceilDiv(Integer.MAX_VALUE - a.prev.key, d) != level) {
					break;
				} else {
					a = a.prev;
				}
			}
		}

		return new BestMatchOnly(new TupleList(a.low, b.up), level);
	}

	@Override
	public BestMatchOnly atMost(Integer z, Integer d) {
		if (0 > d) {
			throw new IllegalArgumentException("d needs to be greater than or equal to zero!");
		}

		if (isEmpty()) { // if the index is empty it has an empty BMO
			return new BestMatchOnly(new ArrayList<Object[]>(), 0);
		}

		// parameters for the return
		long level = 0;
		GroupListElement a, b;

		b = groupStart;
		a = b;

		if (b.key <= z) { // the lowest key is lower than or equal to z
			while (null != b.next) {
				if (b.next.key > z) {
					break;
				} else {
					b = b.next;
				}
			}
		} else { // there is no key lower than or equal to z
			level = b.key - z;

			if (0 != d) { // the BMO has to be grouped by level
				level = ceilDiv(level, d);

				while (null != b.next) {
					if (ceilDiv(b.next.key - z, d) != level) {
						break;
					} else {
						b = b.next;
					}
				}
			}
		}

		return new BestMatchOnly(new TupleList(a.low, b.up), level);
	}

	@Override
	public BestMatchOnly atLeast(Integer z, Integer d) {
		if (0 > d) {
			throw new IllegalArgumentException("d needs to be greater than or equal to zero!");
		}

		if (isEmpty()) { // if the index is empty it has an empty BMO
			return new BestMatchOnly(new ArrayList<Object[]>(), 0);
		}

		// parameters for the return
		long level = 0;
		GroupListElement a, b;

		b = groupEnd;
		a = b;

		if (a.key >= z) { // the highest key is greater than or equal to z
			while (null != a.prev) {
				if (a.prev.key < z) {
					break;
				} else {
					a = a.prev;
				}
			}
		} else { // there is no key greater than or equal to z
			level = z - a.key;

			if (0 != d) { // the BMO has to be grouped by level
				level = ceilDiv(level, d);

				while (null != a.prev) {
					if (ceilDiv(z - a.prev.key, d) != level) {
						break;
					} else {
						a = a.prev;
					}
				}
			}
		}

		return new BestMatchOnly(new TupleList(a.low, b.up), level);
	}

}

/*
 * Doubly linked list element for groups that holds reference for each entry of
 * equal value.
 */
class GroupListElement {
	Integer key;

	protected GroupListElement(Integer key) {
		this.key = key;
	}

	GroupListElement prev;
	GroupListElement next;

	TupleListElement low;
	TupleListElement up;

	protected synchronized void insertOtherAsNext(GroupListElement other) {
		other.prev = this;
		other.next = next;

		if (next != null) {
			next.prev = other;
		}

		next = other;
	}

	protected synchronized void insertOtherAsPrevious(GroupListElement other) {
		other.next = this;
		other.prev = prev;

		if (prev != null) {
			prev.next = other;
		}

		prev = other;
	}

	protected synchronized void add(Object[] tuple) {
		TupleListElement other = new TupleListElement(tuple);

		if (low == null) {

			low = other;
			up = other;

			if (prev != null) {
				prev.up.insertOtherAsNext(other);
			} else if (next != null) {
				next.low.insertOtherAsPrevious(other);
			}

		} else {
			up.insertOtherAsNext(other);
			up = other;
		}
	}

}

/*
 * Doubly linked list element for tuples that hold the reference of one row.
 */
class TupleListElement {
	TupleListElement prev;
	TupleListElement next;

	long position;

	Object[] tuple;

	public TupleListElement(Object[] tuple) {
		this.tuple = tuple;
	}

	protected synchronized void insertOtherAsNext(TupleListElement other) {
		other.prev = this;
		other.next = next;

		if (next != null) {
			next.prev = other;
		}

		next = other;
	}

	protected synchronized void insertOtherAsPrevious(TupleListElement other) {
		other.next = this;
		other.prev = prev;

		if (prev != null) {
			prev.next = other;
		}

		prev = other;
	}
}

/*
 * List iterator for a view on the tuple list.
 */
class TupleListIterator implements ListIterator<Object[]> {

	private static final String UNSUPPORTED = "Invalid operation for RangeTree.TupleListIterator";

	private final TupleListElement start, end;
	private TupleListElement next, prev;
	private int index;

	protected TupleListIterator(TupleListElement start, TupleListElement end) {
		this.start = start;
		this.end = end;
		this.next = start;
		this.prev = null;
		this.index = -1;
	}

	protected TupleListIterator(TupleListElement start, TupleListElement end, int index) {
		this(start, end);

		for (int i = 0; i < index; i++) {
			next();
		}
	}

	@Override
	public void add(Object[] e) {
		throw new UnsupportedOperationException(UNSUPPORTED);
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public boolean hasPrevious() {
		return prev != null;
	}

	@Override
	public Object[] next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		prev = next;
		index++;

		if (next == end) { // same object reference
			next = null;
		} else {
			next = next.next;
		}

		return prev.tuple;
	}

	@Override
	public int nextIndex() {
		return index + 1;
	}

	@Override
	public Object[] previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}

		next = prev;
		index--;

		if (prev == start) { // same object reference
			prev = null;
		} else {
			prev = prev.prev;
		}

		return next.tuple;
	}

	@Override
	public int previousIndex() {
		return index - 1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(UNSUPPORTED);
	}

	@Override
	public void set(Object[] e) {
		throw new UnsupportedOperationException(UNSUPPORTED);
	}

}

/*
 * Implementation of a sequential list for a view on the tuple list.
 */
class TupleList extends AbstractSequentialList<Object[]> {

	private final TupleListElement start, end;
	private final int size;

	protected TupleList(TupleListElement start, TupleListElement end) {
		this.start = start;
		this.end = end;

		// calculate size;
		int size;
		if (start == null) {
			size = 0;
		} else {
			size = Math.toIntExact(end.position - start.position + 1);
		}
		this.size = size;
	}

	@Override
	public ListIterator<Object[]> listIterator(int index) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException();
		}

		return new TupleListIterator(start, end, index);
	}

	@Override
	public int size() {
		return size;
	}

}
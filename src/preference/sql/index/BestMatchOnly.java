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

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A wrapper class for a list of best matches.
 * 
 * @author Felix Weichmann
 *
 */
public class BestMatchOnly implements List<Object[]> {

	/**
	 * The reference of the list of best matches.
	 */
	public final List<Object[]> list;

	/**
	 * The level of the preference evaluation.
	 */
	public final BigInteger level;

	/**
	 * Creates a wrapper for a list of best matches.
	 * 
	 * @param list
	 *            The reference of the list of best matches.
	 * @param level
	 *            The level of the preference evaluation.
	 */
	public BestMatchOnly(List<Object[]> list, long level) {
		this.list = list;
		this.level = BigInteger.valueOf(level);
	}

	/**
	 * Creates a wrapper for a list of best matches.
	 * 
	 * @param list
	 *            The reference of the list of best matches.
	 * @param level
	 *            The level of the preference evaluation.
	 */
	public BestMatchOnly(List<Object[]> list, BigInteger level) {
		this.list = list;
		this.level = level;
	}

	@Override
	public boolean add(Object[] arg0) {
		return list.add(arg0);
	}

	@Override
	public void add(int arg0, Object[] arg1) {
		list.add(arg0, arg1);
	}

	@Override
	public boolean addAll(Collection<? extends Object[]> arg0) {
		return list.addAll(arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends Object[]> arg1) {
		return list.addAll(arg0, arg1);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return list.containsAll(arg0);
	}

	@Override
	public Object[] get(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
		return list.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<Object[]> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return list.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<Object[]> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Object[]> listIterator(int arg0) {
		return list.listIterator(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		return list.remove(arg0);
	}

	@Override
	public Object[] remove(int arg0) {
		return list.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return list.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return list.retainAll(arg0);
	}

	@Override
	public Object[] set(int arg0, Object[] arg1) {
		return list.set(arg0, arg1);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<Object[]> subList(int arg0, int arg1) {
		return list.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return list.toArray(arg0);
	}

}

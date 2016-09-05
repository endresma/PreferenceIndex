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

package preference.sql.index.measurment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import explicit.flatlc.inputrelations.FlatLCResultSetA;
import explicit.flatlc.levels.FlatLevelCombination;

public class FlatLevelResultSetTestIndex extends FlatLCResultSetA {

	private ArrayList<FlatLevelCombination> list;
	private Iterator<FlatLevelCombination> iterator;

	public FlatLevelResultSetTestIndex(List<Object[]> list) {
		this.list = new ArrayList<FlatLevelCombination>();
		putDataIntoList(list);
		reset();
	}

	@Override
	public Object getMetaData() {
		return null;
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}

	@Override
	public Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsPeek() {
		return false;
	}

	@Override
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsRemove() {
		return false;
	}

	@Override
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsUpdate() {
		return false;
	}

	@Override
	public boolean supportsReset() {
		return true;
	}

	@Override
	public ArrayList<Object> getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object next() {
		return this.iterator.next();
	}

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public void reset() {
		this.iterator = this.list.iterator();
	}

	private void putDataIntoList(List<Object[]> list) {
		FlatLevelCombination flc;
		int[] levels = new int[0];
		for (Object[] or : list) {
			flc = new FlatLevelCombination(levels, or);
			this.list.add(flc);
		}
	}

}

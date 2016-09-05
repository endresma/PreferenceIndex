/*
 * Copyright (c) 2015. markus endres, timotheus preisinger
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

package explicit.bnl;

import explicit.flatlc.levels.FlatLevelCombination;
import explicit.preference.BetweenPreference;
import explicit.preference.PriorToPerference;
import explicit.util.IPreference;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An implementation of the BNL algorithm for <code>FlatLevelCombination</code>
 * objects. These objects will only be used as input objects. The level values
 * they contain will not be used - only the object attributes these level are
 * computed with. This behaviour is miming the standard BNL, although the
 * elements read are always kept in memory. No externalization is done.
 *
 * @author markus endres
 * @author Timotheus Preisinger
 * @version 2007-06-18
 */
public class BNL implements Iterator<Object> {
	/**
	 * the preference used by this object
	 */
	IPreference preference;

	/**
	 * input cursor
	 */
	Iterator<Object> input;

	/**
	 * candidate list
	 */
	ArrayList<FlatLevelCombination> candidates;

	/**
	 * position in candidate list while returning result set elements
	 */
	int position = -1;
	int nrOfcomparison = 0;

	int idx;

	public boolean isBasePreference;

	/**
	 * Constructor.
	 *
	 * @param input
	 *            cursor on the input relation
	 * @param preference
	 *            the pareto preference this BNL is evaluating
	 */
	public BNL(Iterator<Object> input, IPreference preference) {
		this(input, preference, 0);
	}

	public BNL(Iterator<Object> input, IPreference preference, int idx) {
		this.preference = preference;
		this.input = input;
		this.idx = idx;

		if (preference instanceof BetweenPreference) {
			isBasePreference = true;
		} else if (preference instanceof PriorToPerference) {
			isBasePreference = true;
		} else {
			isBasePreference = false;
		}
	}

	private boolean addObject(FlatLevelCombination cand) {

		// System.out.println("bnl: " + cand);
		// System.out.println("Window Size: " + candidates.size());

		try {
			// FlatLevelCombination cand = (FlatLevelCombination) object;
			// compare the new tuple to all tuples in the candidate list
			@SuppressWarnings("unused")
			int cCounter = 0;
			for (int i = 0; i < candidates.size(); i++) {
				cCounter++;
				int result = preference.compare(candidates.get(i), cand, idx);
				nrOfcomparison++;

				// System.out.println("current: " + cand);
				// System.out.println("result: " + result);

				switch (result) {
				case IPreference.GREATER:
					// element in candidate list is better: discard new element
					// System.out.println("Checked: " + cCounter);
					return false;
				case IPreference.LESS:
					// element in candidate list is worse: remove it
					int last = candidates.size() - 1;
					if (i < last) {
						// Overwrite the current position with the last element
						// and
						// make sure that this element will be checked, too.
						candidates.set(i--, candidates.get(last));
						candidates.remove(last);
					} else {
						// last element reached: just remove it
						candidates.remove(last);
					}
					break;
				case IPreference.EQUAL:
					// objects are equal: new element cannot be dominated by
					// others
					// case IPreference.SUBSTITUTABLE:
					// objects are substitutable: cand cannot be dominated by
					// others
					candidates.add(cand);
					// System.out.println("Checked: " + cCounter);
					return true;
				}
			}
			// System.out.println("Checked: " + cCounter);

			// new candidate is incomparable to or better than any other: add it
			candidates.add(cand);
			// System.out.println("Window Size: " + candidates.size());

		} catch (Exception e) {
			e.printStackTrace(System.out);
			return false;
		}

		return true;
	}

	private boolean addObjectB(FlatLevelCombination cand) {
		if (0 == candidates.size()) {
			candidates.add(cand);
			return true;
		}

		int result = preference.compare(candidates.get(0), cand, idx);
		nrOfcomparison++;

		switch (result) {
		case IPreference.GREATER:
			// element in candidate list is better: discard new element
			return false;
		case IPreference.LESS:
			// element in candidate list is worse: remove it
			candidates.clear();
			candidates.add(cand);
			return true;
		case IPreference.EQUAL:
			// objects are equal: new element cannot be dominated by
			// others
			candidates.add(cand);
			return true;
		}

		return false;
	}

	public boolean hasNext() {
		// compute the result set
		if (candidates == null) {
			candidates = new ArrayList<FlatLevelCombination>();
			while (input.hasNext()) {
				if (!isBasePreference) {
					addObject((FlatLevelCombination) input.next());
				} else {
					addObjectB((FlatLevelCombination) input.next());
				}
			}
			position = 0;
			// System.out.println("*************** BNL nrOfComparison: " +
			// nrOfcomparison);

		}

		return candidates.size() > position;
	}

	public Object next() {
		return candidates.get(position++);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public int size() {
		if (null != candidates) {
			return candidates.size();
		} else {
			return 0;
		}
	}

}

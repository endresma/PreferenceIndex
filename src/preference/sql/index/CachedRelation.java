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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import explicit.flatlc.inputrelations.FlatLCResultSetA;
import explicit.flatlc.levels.FlatLevelCombination;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.relational.Tuple;

/**
 * A cache structure for a relation build from a result set.
 * 
 * @author Felix Weichmann
 *
 */
public class CachedRelation {

	/**
	 * The data of the relation as a list of tuples.
	 */
	public final List<Object[]> data;

	/**
	 * A map containing some meta data for each column.
	 */
	public final Map<Integer, MetaData> metadata;

	/**
	 * Create the cache from a result set.
	 * 
	 * @param resultSet
	 *            the relation to be cached
	 * @throws SQLException
	 */
	public CachedRelation(MetaDataCursor resultSet) throws SQLException {
		Tuple currentTuple;

		// get the Meta-Data for later use
		ResultSetMetaData resulSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
		if (resulSetMetaData == null && resultSet.hasNext()) {
			currentTuple = (Tuple) resultSet.peek();
			resulSetMetaData = currentTuple.getMetaData();
		}

		// save some metadate for better access
		int numberOfColumns = resulSetMetaData.getColumnCount();
		Map<Integer, MetaData> metaDataMap = new TreeMap<Integer, MetaData>();
		String lable;
		for (int i = 0; i < numberOfColumns; i++) {
			lable = resulSetMetaData.getColumnLabel(i + 1);
			metaDataMap.put(i, new MetaData(lable));
		}

		// for toString()
		for (int i = 0; i < numberOfColumns; i++) {
			metaDataMap.get(i).width = resulSetMetaData.getColumnLabel(i + 1).length();
		}

		// save the Result-Set into an ArrayList for fast access later on
		List<Object[]> dataList = new ArrayList<Object[]>();
		Object[] row;
		while (resultSet.hasNext()) {
			currentTuple = (Tuple) resultSet.next();
			row = new Object[numberOfColumns];
			for (int i = 0; i < numberOfColumns; i++) {
				row[i] = currentTuple.getObject(i + 1);
				if (nullToString(currentTuple.getObject(i + 1)).length() > metaDataMap.get(i).width) {
					metaDataMap.get(i).width = nullToString(currentTuple.getObject(i + 1)).length();
				}
			}
			dataList.add(row);
		}

		metadata = metaDataMap;
		data = dataList;
	}
	
	public CachedRelation(FlatLCResultSetA resultSet, String[] lables) {
		Map<Integer, MetaData> metaDataMap = new TreeMap<Integer, MetaData>();
		int numberOfColumns = lables.length;
		
		// save some metadate for better access
		for (int i = 0; i < numberOfColumns; i++) {
			metaDataMap.put(i, new MetaData(lables[i]));
			metaDataMap.get(i).width = lables[i].length();
		}
		
		// save the Result-Set into an ArrayList for fast access later on
		List<Object[]> dataList = new ArrayList<Object[]>();
		FlatLevelCombination flc;
		Object[] row;
		while (resultSet.hasNext()) {
			flc = (FlatLevelCombination) resultSet.next();
			row = new Object[numberOfColumns];
			for (int i = 0; i < numberOfColumns; i++) {
				row[i] = flc.getValue(i);
				if (nullToString(flc.getValue(i)).length() > metaDataMap.get(i).width) {
					metaDataMap.get(i).width = nullToString(flc.getValue(i)).length();
				}
			}
			dataList.add(row);
        }
		
		metadata = metaDataMap;
		data = dataList;
	}

	/*
	 * toSting with catch for null objects
	 */
	private String nullToString(Object object) {
		if (null == object) {
			return "null";
		} else {
			return object.toString();
		}
	}

	/**
	 * Print all the rows of this cache into System.out.
	 */
	public void print() {
		print(System.out, Integer.MAX_VALUE);
	}

	/**
	 * Print all the rows of this cache into the output.
	 * 
	 * @param output
	 *            The stream to be printed into.
	 */
	public void print(OutputStream output) {
		print(output, Integer.MAX_VALUE);
	}

	/**
	 * Print the first n rows of this cache into System.out.
	 * 
	 * @param limit
	 *            A limit on the number of rows to be printed.
	 */
	public void print(int limit) {
		print(System.out, limit);
	}

	/**
	 * Print the first n rows of this cache into the output.
	 * 
	 * @param output
	 *            The stream to be printed into.
	 * @param limit
	 *            A limit on the number of rows to be printed.
	 */
	public void print(OutputStream output, int limit) {
		int[] positions = new int[metadata.size()];
		int linewidth = 0;
		for (int i = 0; i < metadata.size(); i++) {
			positions[i] = 2 + i * 3 + linewidth;
			linewidth += metadata.get(i).width;
		}
		linewidth += 1 + metadata.size() * 3;

		// Set up the output stream
		PrintWriter out = new PrintWriter(new OutputStreamWriter(output));

		// Create a horizontal divider line we use in the table.
		// Also create a blank line that is the initial value of each
		// line of the table
		StringBuffer divider = new StringBuffer(linewidth);
		StringBuffer blankline = new StringBuffer(linewidth);
		for (int i = 0; i < linewidth; i++) {
			divider.insert(i, '-');
			blankline.insert(i, " ");
		}

		// Put special marks in the divider line at the column positions
		for (int i = 0; i < metadata.size(); i++) {
			divider.setCharAt(positions[i] - 2, '+');
		}
		divider.setCharAt(linewidth - 1, '+');

		// Begin the table output with a divider line
		out.println(divider);

		// Make the head line
		for (int i = 0; i < metadata.size(); i++) {
			out.print("| ");
			printFor(out, metadata.get(i).lable, metadata.get(i).width, ' ');
			out.print(" ");
		}
		out.println("|");

		// Then output the line of column labels and another divider
		out.println(divider);

		// print all the tuples
		Object[] tuple;
		for (int n = 0; n < data.size() && n < limit; n++) {
			tuple = data.get(n);
			for (int i = 0; i < metadata.size(); i++) {
				out.print("| ");
				printFor(out, nullToString(tuple[i]), metadata.get(i).width, ' ');
				out.print(" ");
			}
			out.println("|");
		}

		// Finally, end the table with one last divider line.
		out.println(divider);
		out.flush();
	}

	/*
	 * helper function for print
	 */
	private static void printFor(PrintWriter out, String s, int length, char filler) {
		out.print(s);
		if (s.length() >= length) {
			return;
		}
		for (int i = 0; i < length - s.length(); i++) {
			out.print(filler);
		}
	}

}

/*
 * Data class for the meta data. Expand as necessary.
 */
class MetaData {
	public final String lable;

	protected int width;

	protected MetaData(String lable) {
		this.lable = lable;
		this.width = 0;
	}
}
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

package preference.sql.index.measurment.group;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import explicit.bnl.BNL;
import explicit.preference.BetweenPreference;
import preference.sql.SQLEngine;
import preference.sql.index.BestMatchOnly;
import preference.sql.index.CachedRelation;
import preference.sql.index.NumericalIndex;
import preference.sql.index.measurment.FlatLevelResultSetTestIndex;
import preference.sql.index.measurment.Measurement;
import preference.sql.index.measurment.MemoryObserver;
import preference.sql.index.numerical.IntegerRangeTree;
import preference.sql.parser.PSQLExecutor;
import explicit.util.IPreference;

public class MeasureBetweenDatasets {

	private static String outputFolder = "C:\\mesurements\\";
	private static String outputEncoding = "utf-8";

	private static boolean writeDatasetFiles = false;
	private static boolean abortMeasurements = false;

	public static void main(String[] args) {
		int repititions = 100;

		datasetZ(repititions);
	}

	private static void datasetZ(int repititions) {
		List<int[]> betweens;

		int column;
		FlatLevelResultSetTestIndex rs;
		CachedRelation cr;

		String sql;
		Connection sqlCon = null;

		OutputStream stream = null;

		String DBUSER = "psqldbuser";
		String DBPASS = "psqldbpwd"; //
		String DBDRIVER = "org.postgresql.Driver";
		String DBURL = "jdbc:postgresql://gemini.informatik.uni-augsburg.de:5432/jmdb";

		try {
			Class.forName(DBDRIVER);
			sqlCon = DriverManager.getConnection(DBURL, DBUSER, DBPASS);

			SQLEngine sqlEng = new SQLEngine(sqlCon, DBUSER, DBPASS);
			PSQLExecutor psqlExecutor = new PSQLExecutor(sqlEng);

			if (writeDatasetFiles) {
				stream = new FileOutputStream(outputFolder + "datasetZ.txt");

				sql = "select count(*) as numberOfObjects from ratings;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);

				sql = "select votes, count(*) as count from ratings group by votes order by votes asc;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);
			}

			sql = "select * from ratings;"; // 468097 column 2
			column = 2; // votes
			psqlExecutor.execute(sql);

			cr = new CachedRelation(psqlExecutor.getResultSet());
			if (writeDatasetFiles) {
				cr.print(stream);
			}

			if (abortMeasurements) {
				return;
			}

			rs = new FlatLevelResultSetTestIndex(cr.data);

			int[] ds = new int[] { 0, 10, 20, 30, 40, 50 };

			for (int d : ds) {

				betweens = new ArrayList<int[]>();
				betweens.add(new int[] { 0, 1000000, d });
				betweens.add(new int[] { 250000, 750000, d });
				betweens.add(new int[] { 0, 0, d });
				betweens.add(new int[] { 1000000, 1000000, d });
				measure(repititions, "between-datasetz-d" + d, column, betweens, rs, cr);

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			if (e instanceof SQLException)
				System.err.println("SQL State: " + ((SQLException) e).getSQLState());
		} finally {
			try {
				if (sqlCon != null && !sqlCon.isClosed())
					sqlCon.close();
			} catch (Exception e) {
				/* ignore */
			}
		}
	}

	public static void measure(int repititions, String name, int column, List<int[]> betweens,
			FlatLevelResultSetTestIndex rs, CachedRelation cr) {
		System.out.println(name);

		String[] pointNames = new String[] { "MemoryUsedIndex", "BuildTimeIndex", "UseTimeIndex", "UseTimeBNL",
				"DeltaUseTime" };

		long[][] points;

		NumericalIndex<Integer> index;

		BestMatchOnly bmo = null;
		BNL bnl = null;
		int bnlSize = 0;
		IPreference preference;

		Measurement measurement;
		long startTime, endTime, elapsedTime;
		long startMem, endMem, deltaMem;

		MemoryObserver.initMemory();

		Writer writer = null;
		int line = 1;

		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputFolder + name + ".txt"), outputEncoding));

			writer.write("# Experiment: " + name + "\r\n");
			writer.write("# mean50 of " + repititions + " repetitions" + "\r\n");
			writer.write(
					"# the objects in the test relation have values between minLevel and maxLevel with a gaussian distribution"
							+ "\r\n");
			writer.write("# the memory was measured in bytes" + "\r\n");
			writer.write("# the time was measured in nanoseconds" + "\r\n");
			writer.write("# line numberOfObjects column low up d bmoSize");
			for (String pointName : pointNames) {
				String[] labels = Measurement.getLabels(pointName);
				for (String s : labels) {
					writer.write(" " + s);
				}
			}

			for (int[] between : betweens) {

				preference = new BetweenPreference(between[0], between[1], between[2]);
				points = new long[pointNames.length][repititions];

				for (int r = 0; r < repititions; r++) {

					startMem = MemoryObserver.currentMemory();
					startTime = System.nanoTime();
					index = new IntegerRangeTree(cr, column);
					endTime = System.nanoTime();
					endMem = MemoryObserver.currentMemory();
					elapsedTime = endTime - startTime;
					deltaMem = endMem - startMem;
					points[0][r] = deltaMem;
					points[1][r] = elapsedTime;

					startTime = System.nanoTime();
					bmo = index.between(between[0], between[1], between[2]);
					endTime = System.nanoTime();
					elapsedTime = endTime - startTime;
					points[2][r] = elapsedTime;

					index = null;

					rs.reset();
					bnl = new BNL(rs, preference, column);

					startTime = System.nanoTime();
					bnl.hasNext();
					endTime = System.nanoTime();
					elapsedTime = endTime - startTime;
					points[3][r] = elapsedTime;

					bnlSize = bnl.size();
					bnl = null;

					points[4][r] = points[3][r] - points[2][r];
				}
				System.out.println("bmo sizes: " + bmo.size() + "  " + bnlSize);

				writer.write("\r\n");

				// numberOfObjects minLevel maxLevel low up d bmoSize
				writer.write(line++ + " " + cr.data.size() + " " + column + " " + between[0] + " " + between[1] + " "
						+ between[2] + " " + bmo.size());

				for (int n = 0; n < pointNames.length; n++) {
					measurement = new Measurement(pointNames[n], points[n]);
					String[] values = measurement.getValues();
					for (String s : values) {
						writer.write(" " + s);
					}
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				/* ignore */
			}
		}

		System.out.println();
	}
}

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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import explicit.bnl.BNL;
import explicit.preference.LayeredPreference;
import preference.sql.SQLEngine;
import preference.sql.index.AbstractCategoricalIndex;
import preference.sql.index.BestMatchOnly;
import preference.sql.index.CachedRelation;
import preference.sql.index.categorical.HashIndex;
import preference.sql.index.categorical.TrieIndex;
import preference.sql.index.measurment.FlatLevelResultSetTestIndex;
import preference.sql.index.measurment.Measurement;
import preference.sql.index.measurment.MemoryObserver;
import preference.sql.parser.PSQLExecutor;
import explicit.util.IPreference;

public class MeasureLayeredDatasets {

	private static final int HASH = 1;
	private static final int TRIE = 2;

	private static String outputFolder = "C:\\mesurements\\";
	private static String outputEncoding = "utf-8";

	private static boolean writeDatasetFiles = false;
	private static boolean abortMeasurements = false;

	public static void main(String[] args) {
		int repititions = 100;

		datasetA(repititions);
		datasetB(repititions);
		datasetC(repititions);

	}

	private static void datasetA(int repititions) {
		List<String[][]> layersList;

		int column;
		FlatLevelResultSetTestIndex rs;
		CachedRelation cr;

		String sql;
		Connection sqlCon = null;

		OutputStream stream = null;

		String DBUSER = "psqldbuser";
		String DBPASS = "psqldbpwd"; //
		String DBDRIVER = "org.postgresql.Driver";
		String DBURL = "jdbc:postgresql://gemini.informatik.uni-augsburg.de/psqldb";

		try {
			Class.forName(DBDRIVER);
			sqlCon = DriverManager.getConnection(DBURL, DBUSER, DBPASS);

			SQLEngine sqlEng = new SQLEngine(sqlCon, DBUSER, DBPASS);
			PSQLExecutor psqlExecutor = new PSQLExecutor(sqlEng);

			if (writeDatasetFiles) {
				stream = new FileOutputStream(outputFolder + "datasetA.txt");

				sql = "select count(*) as numberOfObjects from notebooks;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);

				sql = "select make, count(*) as count from notebooks group by make order by make asc;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);
			}

			sql = "select * from notebooks;"; // 16200 column 6
			column = 6; // make
			psqlExecutor.execute(sql);

			cr = new CachedRelation(psqlExecutor.getResultSet());
			if (writeDatasetFiles) {
				cr.print(stream);
			}

			if (abortMeasurements) {
				return;
			}

			rs = new FlatLevelResultSetTestIndex(cr.data);

			layersList = new ArrayList<String[][]>();
			layersList.add(new String[][] { {} });
			layersList.add(new String[][] { { "ACER" } });
			layersList.add(new String[][] { { "ACER", "ASUS" } });
			layersList.add(new String[][] { { "ACER", "ASUS", "FUJITSU" } });
			layersList.add(new String[][] { { "ACER", "ASUS", "FUJITSU", "HP" } });
			layersList.add(new String[][] { { "ACER", "ASUS", "FUJITSU", "HP", "LENOVO" } });
			layersList.add(new String[][] { { "ACER", "ASUS", "FUJITSU", "HP", "LENOVO", "MSI" } });
			layersList.add(new String[][] { { "ACER", "ASUS", "FUJITSU", "HP", "LENOVO", "MSI", "SONY" } });
			layersList.add(new String[][] { { "ACER", "ASUS", "FUJITSU", "HP", "LENOVO", "MSI", "SONY", "TOSHIBA" } });
			layersList.add(new String[][] {
					{ "APPLE", "ACER", "ASUS", "FUJITSU", "HP", "LENOVO", "MSI", "SONY", "TOSHIBA" } });
			layersList.add(new String[][] {
					{ "APPLE", "IBM", "ACER", "ASUS", "FUJITSU", "HP", "LENOVO", "MSI", "SONY", "TOSHIBA" } });
			measure(repititions, "layered-hash-dataseta", layersList, cr, rs, column, HASH);
			measure(repititions, "layered-trie-dataseta", layersList, cr, rs, column, TRIE);

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

	private static void datasetB(int repititions) {
		List<String[][]> layersList;

		int column;
		FlatLevelResultSetTestIndex rs;
		CachedRelation cr;

		String sql;
		Connection sqlCon = null;

		OutputStream stream = null;

		String DBUSER = "psqldbuser";
		String DBPASS = "psqldbpwd"; //
		String DBDRIVER = "org.postgresql.Driver";
		String DBURL = "jdbc:postgresql://gemini.informatik.uni-augsburg.de/psqldb";

		try {
			Class.forName(DBDRIVER);
			sqlCon = DriverManager.getConnection(DBURL, DBUSER, DBPASS);

			SQLEngine sqlEng = new SQLEngine(sqlCon, DBUSER, DBPASS);
			PSQLExecutor psqlExecutor = new PSQLExecutor(sqlEng);

			if (writeDatasetFiles) {
				stream = new FileOutputStream(outputFolder + "datasetB.txt");

				sql = "select count(*) as numberOfObjects from cars;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);

				sql = "select make, count(*) as count from cars group by make order by make asc;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);
			}

			sql = "select * from cars;"; // 300 column 2
			column = 2; // make
			psqlExecutor.execute(sql);

			cr = new CachedRelation(psqlExecutor.getResultSet());
			if (writeDatasetFiles) {
				cr.print(stream);
			}

			if (abortMeasurements) {
				return;
			}

			rs = new FlatLevelResultSetTestIndex(cr.data);

			layersList = new ArrayList<String[][]>();
			layersList.add(new String[][] { {} });
			layersList.add(new String[][] { { "bmw" } });
			layersList.add(new String[][] { { "bmw", "chevrolet" } });
			layersList.add(new String[][] { { "bmw", "chevrolet", "ford" } });
			layersList.add(new String[][] { { "bmw", "chevrolet", "ford", "honda" } });
			layersList.add(new String[][] { { "bmw", "chevrolet", "ford", "honda", "mercedes" } });
			layersList.add(new String[][] { { "bmw", "chevrolet", "ford", "honda", "mercedes", "porsche" } });
			layersList.add(new String[][] { { "bmw", "chevrolet", "ford", "honda", "mercedes", "porsche", "toyota" } });
			layersList.add(
					new String[][] { { "bmw", "chevrolet", "ford", "honda", "mercedes", "porsche", "toyota", "vw" } });
			layersList.add(new String[][] {
					{ "fiat", "bmw", "chevrolet", "ford", "honda", "mercedes", "porsche", "toyota", "vw" } });
			layersList.add(new String[][] { { "mitsubishi", "fiat", "bmw", "chevrolet", "ford", "honda", "mercedes",
					"porsche", "toyota", "vw" } });
			measure(repititions, "layered-hash-datasetb", layersList, cr, rs, column, HASH);
			measure(repititions, "layered-trie-datasetb", layersList, cr, rs, column, TRIE);

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

	private static void datasetC(int repititions) {
		List<String[][]> layersList;

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
				stream = new FileOutputStream(outputFolder + "datasetC.txt");

				sql = "select count(*) as numberOfObjects from genres;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);

				sql = "select genre, count(*) as count from genres group by genre order by genre asc;";
				psqlExecutor.execute(sql);
				cr = new CachedRelation(psqlExecutor.getResultSet());
				cr.print(stream);
			}

			sql = "select * from genres;"; // 1580880 column 1
			column = 1; // genre
			psqlExecutor.execute(sql);

			cr = new CachedRelation(psqlExecutor.getResultSet());
			if (writeDatasetFiles) {
				cr.print(stream);
			}

			if (abortMeasurements) {
				return;
			}

			rs = new FlatLevelResultSetTestIndex(cr.data);

			layersList = new ArrayList<String[][]>();
			layersList.add(new String[][] { {} });
			layersList.add(new String[][] { { "Action" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation" } });
			layersList.add(new String[][] {
					{ "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy", "Commercial" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama", "Experimental", "Family", "Fantasy" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama", "Experimental", "Family", "Fantasy", "Film-Noir",
					"Game-Show", "History" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama", "Experimental", "Family", "Fantasy", "Film-Noir",
					"Game-Show", "History", "Horror", "Lifestyle", "Music" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama", "Experimental", "Family", "Fantasy", "Film-Noir",
					"Game-Show", "History", "Horror", "Lifestyle", "Music", "Musical", "Mystery", "News" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama", "Experimental", "Family", "Fantasy", "Film-Noir",
					"Game-Show", "History", "Horror", "Lifestyle", "Music", "Musical", "Mystery", "News", "Reality-TV",
					"Romance", "Sci-Fi" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama", "Experimental", "Family", "Fantasy", "Film-Noir",
					"Game-Show", "History", "Horror", "Lifestyle", "Music", "Musical", "Mystery", "News", "Reality-TV",
					"Romance", "Sci-Fi", "Short", "Sport", "Talk-Show" } });
			layersList.add(new String[][] { { "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
					"Commercial", "Crime", "Documentary", "Drama", "Experimental", "Family", "Fantasy", "Film-Noir",
					"Game-Show", "History", "Horror", "Lifestyle", "Music", "Musical", "Mystery", "News", "Reality-TV",
					"Romance", "Sci-Fi", "Short", "Sport", "Talk-Show", "Thriller", "War", "Western" } });
			measure(repititions, "layered-hash-datasetc", layersList, cr, rs, column, HASH);
			measure(repititions, "layered-trie-datasetc", layersList, cr, rs, column, TRIE);

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

	private static void measure(int repititions, String name, List<String[][]> layersList, CachedRelation cr,
			FlatLevelResultSetTestIndex rs, int column, int indexType) {
		System.out.println(name);

		String[] pointNames = new String[] { "MemoryUsedIndex", "BuildTimeIndex", "UseTimeIndex", "UseTimeBNL",
				"DeltaUseTime" };

		long[][] points;
		Measurement measurement;
		long startTime, endTime, elapsedTime;
		long startMem, endMem, deltaMem;

		AbstractCategoricalIndex index = null;
		Set<String> keys = null;
		BestMatchOnly bmo = null;
		BNL bnl = null;
		int bnlSize = 0;
		IPreference preference;

		Writer writer = null;

		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputFolder + name + ".txt"), outputEncoding));

			writer.write("# Experiment: " + name + "\r\n");
			writer.write("# mean50 of " + repititions + " repetitions" + "\r\n");
			writer.write("# the memory was measured in bytes" + "\r\n");
			writer.write("# the time was measured in nanoseconds" + "\r\n");
			writer.write(
					"# numberOfObjects numberOfKeys keys numberOfKeysInLayers numberOfKeysInLayer1 layers bmoSize");
			for (String pointName : pointNames) {
				String[] labels = Measurement.getLabels(pointName);
				for (String s : labels) {
					writer.write(" " + s);
				}
			}

			for (String[][] layers : layersList) {
				preference = new LayeredPreference(layers);

				points = new long[pointNames.length][repititions];

				for (int r = 0; r < repititions; r++) {

					if (indexType == HASH) {

						startMem = MemoryObserver.currentMemory();
						startTime = System.nanoTime();
						index = new HashIndex(cr, column);
						endTime = System.nanoTime();
						endMem = MemoryObserver.currentMemory();
						elapsedTime = endTime - startTime;
						deltaMem = endMem - startMem;
						points[0][r] = deltaMem;
						points[1][r] = elapsedTime;

					} else if (indexType == TRIE) {

						startMem = MemoryObserver.currentMemory();
						startTime = System.nanoTime();
						index = new TrieIndex(cr, column);
						endTime = System.nanoTime();
						endMem = MemoryObserver.currentMemory();
						elapsedTime = endTime - startTime;
						deltaMem = endMem - startMem;
						points[0][r] = deltaMem;
						points[1][r] = elapsedTime;

					}

					startTime = System.nanoTime();
					bmo = index.layered(layers);
					endTime = System.nanoTime();
					elapsedTime = endTime - startTime;
					points[2][r] = elapsedTime;

					keys = index.keys();
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

				// numberOfObjects numberOfKeys keys numberOfKeysInLayers
				// numberOfKeysInLayer1 layers bmoSize
				writer.write(cr.data.size() + " " + keys.size() + " " + gnuplotKeys(keys) + " " + layers.length + " "
						+ layers[0].length + " " + gnuplotLayers(layers) + " " + bmo.size());

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

	private static String gnuplotLayers(String[][] layers) {
		String s = Arrays.deepToString(layers);
		s = s.replace(" ", "");
		return s;
	}

	private static String gnuplotKeys(Set<String> keys) {
		String s = Arrays.toString(keys.toArray());
		s = s.replace(" ", "");
		return s;
	}

}

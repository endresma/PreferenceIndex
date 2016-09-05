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
import java.io.OutputStreamWriter;
import java.io.Writer;

import explicit.bnl.BNL;
import explicit.flatlc.inputrelations.FlatLCResultSetA;
import explicit.preference.BetweenPreference;
import preference.sql.index.BestMatchOnly;
import preference.sql.index.CachedRelation;
import preference.sql.index.NumericalIndex;
import preference.sql.index.measurment.FlatLevelResultSetTestIndex;
import preference.sql.index.measurment.Measurement;
import preference.sql.index.measurment.MemoryObserver;
import preference.sql.index.measurment.generator.KeyRatioDataGenerator;
import preference.sql.index.numerical.IntegerRangeTree;
import explicit.util.IPreference;

public class MeasureBetweenKeyRatio {

	private static String outputFolder = "C:\\mesurements\\";
	private static String outputEncoding = "utf-8";

	public static void main(String[] args) {
		int repititions = 100;

		int[] ds = new int[] { 0, 10, 20, 30 };

		for (int d : ds) {
//			measure(repititions, "between-keyratio-0000000-0000000-d" + d, 0, 0, d);
//			measure(repititions, "between-keyratio-0000000-0250000-d" + d, 0, 250000, d);
//			measure(repititions, "between-keyratio-0000000-0500000-d" + d, 0, 500000, d);
//			measure(repititions, "between-keyratio-0000000-0750000-d" + d, 0, 750000, d);
			measure(repititions, "between-keyratio-0000000-1000000-d" + d, 0, 1000000, d);
		}

	}

	public static void measure(int repititions, String name, int low, int up, int d) {
		System.out.println(name);

		String[] pointNames = new String[] { "MemoryUsedIndex", "BuildTimeIndex", "UseTimeIndex", "UseTimeBNL",
				"DeltaUseTime" };

		long[][] points;

		FlatLCResultSetA rs;
		CachedRelation cr;
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

		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputFolder + name + ".txt"), outputEncoding));

			writer.write("# Experiment: " + name + "\r\n");
			writer.write("# mean50 of " + repititions + " repetitions" + "\r\n");
			writer.write("# there are n keys with m ojects in the relation" + "\r\n");
			writer.write("# the key values start with 1 and increment by 1" + "\r\n");
			writer.write("# the memory was measured in bytes" + "\r\n");
			writer.write("# the time was measured in nanoseconds" + "\r\n");
			writer.write("# numberOfObjects numberOfKeys numberOfObjectsPerKey low up d bmoSize");
			for (String pointName : pointNames) {
				String[] labels = Measurement.getLabels(pointName);
				for (String s : labels) {
					writer.write(" " + s);
				}
			}

			int numberOfObjectsPerKey;
			int numberOfObjects = 1000000;
			for (int numberOfKeys = 1; numberOfKeys <= numberOfObjects; numberOfKeys *= 10) {
				numberOfObjectsPerKey = Math.floorDiv(numberOfObjects, numberOfKeys);

				rs = new FlatLevelResultSetTestIndex(KeyRatioDataGenerator.generateLinearData(numberOfKeys,
						numberOfObjectsPerKey, Math.floorDiv(numberOfObjectsPerKey, 2), numberOfObjectsPerKey));

				cr = new CachedRelation(rs, new String[] { "value" });

				preference = new BetweenPreference(low, up, d);
				points = new long[pointNames.length][repititions];

				System.out.println("numberOfKeys: " + numberOfKeys);

				for (int r = 0; r < repititions; r++) {

					startMem = MemoryObserver.currentMemory();
					startTime = System.nanoTime();
					index = new IntegerRangeTree(cr, 0);
					endTime = System.nanoTime();
					endMem = MemoryObserver.currentMemory();
					elapsedTime = endTime - startTime;
					deltaMem = endMem - startMem;
					points[0][r] = deltaMem;
					points[1][r] = elapsedTime;

					startTime = System.nanoTime();
					bmo = index.between(low, up, d);
					endTime = System.nanoTime();
					elapsedTime = endTime - startTime;
					points[2][r] = elapsedTime;

					index = null;

					rs.reset();
					bnl = new BNL(rs, preference, 0);

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

				// numberOfObjects numberOfKeys numberOfObjectsPerKey low up d
				// bmoSize
				writer.write(numberOfObjects + " " + numberOfKeys + " " + numberOfObjectsPerKey + " " + low + " " + up
						+ " " + d + " " + bmo.size());

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

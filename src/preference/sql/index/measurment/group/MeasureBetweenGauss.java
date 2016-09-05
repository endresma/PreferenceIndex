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
import explicit.bnl.BNL;
import explicit.flatlc.inputrelations.FlatLCResultSetA;
import explicit.preference.BetweenPreference;
import explicit.util.IPreference;
import explicit.util.InputGenerator;
import preference.sql.index.BestMatchOnly;
import preference.sql.index.CachedRelation;
import preference.sql.index.NumericalIndex;
import preference.sql.index.measurment.Measurement;
import preference.sql.index.measurment.MemoryObserver;
import preference.sql.index.numerical.IntegerRangeTree;

import java.io.*;

public class MeasureBetweenGauss {
	
	private static String outputFolder = "C:\\mesurements\\";
	private static String outputEncoding = "utf-8";

	public static void main(String[] args) {
		int repititions = 100;

		measure(repititions, "between-gauss-0000-0000-d0", 0, 0, 0);
		measure(repititions, "between-gauss-0250-0250-d0", 250, 250, 0);
		measure(repititions, "between-gauss-0500-0500-d0", 500, 500, 0);
		measure(repititions, "between-gauss-0000-0100-d0", 0, 100, 0);
		measure(repititions, "between-gauss-0250-0350-d0", 250, 350, 0);
		measure(repititions, "between-gauss-0450-0550-d0", 450, 550, 0);
		measure(repititions, "between-gauss-0000-0400-d0", 0, 400, 0);
		measure(repititions, "between-gauss-0300-0700-d0", 300, 700, 0);
		measure(repititions, "between-gauss-0000-0700-d0", 0, 700, 0);
		measure(repititions, "between-gauss-0150-0850-d0", 150, 850, 0);
		measure(repititions, "between-gauss-0000-1000-d0", 0, 1000, 0);

		measure(repititions, "between-gauss-0000-0000-d10", 0, 0, 10);
		measure(repititions, "between-gauss-0250-0250-d10", 250, 250, 10);
		measure(repititions, "between-gauss-0500-0500-d10", 500, 500, 10);
		measure(repititions, "between-gauss-0000-0100-d10", 0, 100, 10);
		measure(repititions, "between-gauss-0250-0350-d10", 250, 350, 10);
		measure(repititions, "between-gauss-0450-0550-d10", 450, 550, 10);
		measure(repititions, "between-gauss-0000-0400-d10", 0, 400, 10);
		measure(repititions, "between-gauss-0300-0700-d10", 300, 700, 10);
		measure(repititions, "between-gauss-0000-0700-d10", 0, 700, 10);
		measure(repititions, "between-gauss-0150-0850-d10", 150, 850, 10);
		measure(repititions, "between-gauss-0000-1000-d10", 0, 1000, 10);
	}

	public static void measure(int repititions, String name, int low, int up, int d) {
		System.out.println(name);

		String[] pointNames = new String[] { "MemoryUsedIndex", "BuildTimeIndex", "UseTimeIndex", "UseTimeBNL",
				"DeltaUseTime" };

		int[] inputSizes = new int[] { 100, 1000, 10000, 100000, 1000000 };
		int[] minLevel = { 0 };
		int[] maxLevel = { 1000 };

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
			writer.write(
					"# the objects in the test relation have values between minLevel and maxLevel with a gaussian distribution"
							+ "\r\n");
			writer.write("# the memory was measured in bytes" + "\r\n");
			writer.write("# the time was measured in nanoseconds" + "\r\n");
			writer.write("# numberOfObjects minLevel maxLevel low up d bmoSize");
			for (String pointName : pointNames) {
				String[] labels = Measurement.getLabels(pointName);
				for (String s : labels) {
					writer.write(" " + s);
				}
			}

			for (int i = 0; i < inputSizes.length; i++) {

				rs = InputGenerator.generateInput(maxLevel, "gauss", inputSizes[i]);
				cr = new CachedRelation(rs, new String[] { "value" });

				preference = new BetweenPreference(low, up, d);
				points = new long[pointNames.length][repititions];

				System.out.println("inputSizes: " + inputSizes[i]);

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

				// numberOfObjects minLevel maxLevel low up d bmoSize
				writer.write(inputSizes[i] + " " + minLevel[0] + " " + maxLevel[0] + " " + low + " " + up + " " + d
						+ " " + bmo.size());

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

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
import java.util.Arrays;

public class Measurement {

	public final String name;

	public final long mean;
	public final long mean50;

	public final long median;
	public final long max;
	public final long min;

	public final double standardDeviation;

	public Measurement(String name, long[] points) {
		this.name = name;

		Arrays.sort(points);

		this.mean = Measurement.mean(points);
		this.mean50 = Measurement.mean50(points);
		this.median = Measurement.median(points);
		this.min = points[0];
		this.max = points[points.length - 1];
		this.standardDeviation = Measurement.standardDeviation(points);
	}

	public String[] getLabels() {
		return Measurement.getLabels(this.name);
	}

	public String[] getValues() {
		String[] values = new String[6];
		values[0] = Long.toString(this.mean);
		values[1] = Long.toString(this.mean50);
		values[2] = Long.toString(this.median);
		values[3] = Long.toString(this.max);
		values[4] = Long.toString(this.min);
		values[5] = Double.toString(this.standardDeviation);
		return values;
	}

	public static String[] getLabels(String name) {
		String[] lables = new String[6];
		lables[0] = name + "_mean";
		lables[1] = name + "_mean50";
		lables[2] = name + "_median";
		lables[3] = name + "_max";
		lables[4] = name + "_min";
		lables[5] = name + "_standardDeviation";
		return lables;
	}

	public static long mean(long[] m) {
		long sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return Math.floorDiv(sum, m.length);
	}

	// the array long[] m MUST BE SORTED
	public static long mean50(long[] m) {
		long sum = 0;
		int start = Math.floorDiv(m.length, 4);
		int end = start * 3;
		int length = end - start;
		for (int i = start; i < end; i++) {
			sum += m[i];
		}
		return Math.floorDiv(sum, length);
	}

	// the array long[] m MUST BE SORTED
	public static long median(long[] m) {
		int middle = m.length / 2;
		if (m.length % 2 == 1) {
			return m[middle];
		} else {
			return Math.floorDiv((m[middle - 1] + m[middle]), 2);
		}
	}

	public static double standardDeviation(long[] m) {
		double mean = mean(m);
		double ret = 0;
		for (int i = 0; i < m.length; i++) {
			ret += Math.pow((double) m[i] - mean, 2);
		}
		ret = ret / (double) (m.length - 1);
		return Math.sqrt(ret);
	}

	// converts nano seconds to seconds
	public static double nanoToSeconds(long nano) {
		return nano / 1000. / 1000. / 1000.;
	}

}

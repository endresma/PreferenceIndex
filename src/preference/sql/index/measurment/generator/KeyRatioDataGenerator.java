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

package preference.sql.index.measurment.generator;
import java.util.Arrays;
import java.util.List;

public class KeyRatioDataGenerator {

	public static List<Object[]> generateLinearData(int numberOfKeys, int objectsPerKey) {
		return generateLinearData(numberOfKeys, objectsPerKey, 1, 1);
	}

	public static List<Object[]> generateLinearData(int numberOfKeys, int objectsPerKey, int keyStart,
			int keyIncrement) {
		Object[][] keys = new Object[numberOfKeys][];
		Object[] key;
		Object[][] objects = new Object[numberOfKeys * objectsPerKey][];

		for (int i = 0; i < keys.length; i++) {
			key = new Object[] { (keyStart + i * keyIncrement) };
			keys[i] = key;
		}

		int c = 0;
		while (c < objects.length) {
			for (Object[] k : keys) {
				if (c >= objects.length) {
					break;
				}

				objects[c] = k;

				c++;
			}
		}
		
		return Arrays.asList(objects);
	}

}

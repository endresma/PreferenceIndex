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

public class ComplexDataGenerator {

	public static List<Object[]> generateData(int numberOfObjects, List<SimpleDataGenerator> sdgs) {
		int width = sdgs.size();
		Object[][] list = new Object[numberOfObjects][width];
		int x;

		for (int y = 0; y < numberOfObjects; y++) {
			x = 0;
			for (SimpleDataGenerator sdg : sdgs) {
				list[y][x] = sdg.generateNext();
				x++;
			}
		}

		return Arrays.asList(list);
	}

}

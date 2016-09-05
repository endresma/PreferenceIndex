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

import java.util.Random;

public class RandomDataGenerator implements SimpleDataGenerator {

	private Random rand;

	private int min;
	private int bound;

	public RandomDataGenerator(long seed, int infimum, int supremum) {
		if (infimum > supremum) {
			throw new IllegalArgumentException("infimum must be smaler than or equal to supremum");
		}

		this.rand = new Random(seed);

		this.min = infimum;
		this.bound = supremum - infimum + 1;
	}

	@Override
	public Object generateNext() {
		return rand.nextInt(bound) + min;
	}

}

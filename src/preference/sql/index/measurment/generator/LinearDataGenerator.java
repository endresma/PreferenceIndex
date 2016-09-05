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

public class LinearDataGenerator implements SimpleDataGenerator {

	private int infimum;
	private int supremum;
	private int current;

	public LinearDataGenerator(int infimum, int supremum) {
		if (infimum > supremum) {
			throw new IllegalArgumentException("infimum must be smaler than or equal to supremum");
		}

		this.current = infimum - 1;
		this.infimum = infimum;
		this.supremum = supremum;
	}

	@Override
	public Object generateNext() {
		current++;

		if (current > supremum) {
			current = infimum;
		} else if (current < infimum) {
			current = infimum;
		}

		return current;
	}

}

/*
 * Copyright (C) Justo Montiel, David Torres, Sergio Gomez, Alberto Fernandez
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see
 * <http://www.gnu.org/licenses/>
 */

package definicions;

import java.io.Serializable;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Coordinates (x, y)
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class Coordenada<tipo> implements Serializable {

	tipo x, y;

	public Coordenada(final tipo x, final tipo y) {
		this.x = x;
		this.y = y;
	}

	public tipo getX() {
		return x;
	}

	public void setX(final tipo x) {
		this.x = x;
	}

	public tipo getY() {
		return y;
	}

	public void setY(final tipo y) {
		this.y = y;
	}

}

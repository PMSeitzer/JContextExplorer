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

package parser.figures;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import tipus.Orientation;
import definicions.Coordenada;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Circle figure
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class Cercle extends Figura {
	private double radi;
	private String nom = "";

	public Cercle(final Coordenada<Double> pos, final double r, final int prec,
			final String n) {
		super(pos.getX(), pos.getY(), prec);
		radi = r;
		nom = n;
	}

	public void setNom(final String nom) {
		this.nom = nom;
	}

	public String getNom() {
		return nom;
	}

	public double getRadi() {
		return radi;
	}

	public void setRadi(final double radi) {
		this.radi = radi;
	}

	@Override
	public void dibuixa(final Graphics2D g, final Orientation or) {
		double x, y, r1, r2, rr;

		// no volem una el�lipse
		rr = this.getRadi();
		r1 = this.getEscala().parserX_ABS(rr);
		r2 = this.getEscala().parserY_ABS(rr);
		rr = (r1 <= r2) ? r1 : r2;

		if ((or == Orientation.EAST) || (or == Orientation.WEST)) {
			y = this.getEscala().getHeightValues() - this.getPosReal().getX();
			x = this.getEscala().parserX(0d);
			y = this.getEscala().parserY(y);
			y -= (rr / 2d);
		} else {
			x = this.getEscala().parserX(this.getPosReal().getX());
			y = this.getEscala().parserY(0d);
			x -= (rr / 2d);
		}

		g.fill(new Ellipse2D.Double(x, y, rr, rr));
	}

	@Override
	public void dibuixa(Orientation or) {
	}
}

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

package parser.EPS.figures;

import java.awt.Color;

import parser.Escalado;
import parser.EPS.EPSWriter;
import tipus.Orientation;
import tipus.tipusDades;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Axis EPS figure
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class EscalaEPS {
	private double y_min, y_max;
	private final double dist;
	private Color color = Color.BLACK;
	private Escalado parser;

	public EscalaEPS(final double y_min, final double y_max, final double dist,
			final double tics) {
		this.dist = dist;
		this.y_min = y_min;
		this.y_max = y_max;
	}

	public void setEscala(final Escalado e) {
		parser = e;
	}

	public Escalado getEscala() {
		return parser;
	}

	public double getY_max() {
		return y_max;
	}

	public void setY_max(final double h) {
		y_max = h;
	}

	public double getY_min() {
		return y_min;
	}

	public void setY_min(final double h) {
		y_min = h;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(final Color c) {
		color = c;
	}

	public double getDist() {
		return this.dist;
	}

	public void dibuixa(final Orientation or, final tipusDades tipDades,
			final int ticks) {
		double x0, x1, x2;
		double y0, y1, y2;
		double y, x, inc, n;

		EPSWriter.writeLine("gsave");
		EPSWriter.writeLine(EPSWriter.setRGBColor(
				this.getColor().getRed() / 255f,
				this.getColor().getGreen() / 255f,
				this.getColor().getBlue() / 255f));

		/* s'ha de vigilar el parser acepta nomes dades entre y_min y y_max */
		if (or.equals(Orientation.WEST) || or.equals(Orientation.EAST)) {
			inc = this.getEscala().parserX_ABS(y_min + dist);
			inc -= this.getEscala().parserX_ABS(y_min);
		} else {
			inc = this.getEscala().parserY_ABS(y_min + dist);
			inc -= this.getEscala().parserY_ABS(y_min);
		}
		if (inc > 0) { // sempre s'ha de donar aquest cas
			x1 = this.getEscala().parserX_ABS(0);
			y1 = this.getEscala().parserY_ABS(0);

			if (or.equals(Orientation.EAST) || or.equals(Orientation.WEST)) {
				/* OEST i EST */
				x0 = this.getEscala().parserX(y_min);
				x1 = x0;
				x2 = this.getEscala().parserX(y_max);

				y0 = this.getEscala().parserY(0);
				y1 = this.getEscala().parserY(1);
				y2 = this.getEscala().parserY(2);

				EPSWriter.writeLine(EPSWriter.dLine(
						(float) (EPSWriter.xmin + x0),
						(float) (EPSWriter.ymax + y1),
						(float) (EPSWriter.xmin + x2),
						(float) (EPSWriter.ymax + y1)));

				y0 = this.getEscala().parserY(0.5);
				y2 = this.getEscala().parserY(1.5);
				n = 0;
				if (tipDades.equals(tipusDades.DISTANCIA)) {
					/* DISTANCIES */
					if (Orientation.EAST.equals(or)) {
						x = x0;
						while (x <= (this.getEscala().parserX(y_max))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(0)),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(2))));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y0),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y2)));
							}
							x += inc;
							n++;
						}
					} else {
						x = x2;
						while (x >= (this.getEscala().parserX(y_min))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(0)),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(2))));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y0),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y2)));
							}
							x -= inc;
							n++;
						}
					}
				} else {
					/* PESOS */
					if (Orientation.EAST.equals(or)) {
						x = x2;
						while (x >= (this.getEscala().parserX(y_min))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(0)),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(2))));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y0),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y2)));
							}
							x -= inc;
							n++;
						}
					} else {
						x = x0;
						while (x <= (this.getEscala().parserX(y_max))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(0)),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + this
												.getEscala().parserY(2))));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y0),
										(float) (EPSWriter.xmin + x),
										(float) (EPSWriter.ymax + y2)));
							}
							x += inc;
							n++;
						}
					}
				}
			} else if (or.equals(Orientation.SOUTH)
					|| or.equals(Orientation.NORTH)) {
				/* SUD i NORD */
				x0 = this.getEscala().parserX(0);
				x1 = this.getEscala().parserX(1);
				x2 = this.getEscala().parserX(2);

				y0 = this.getEscala().parserY(y_min);
				y1 = this.getEscala().parserY(0);
				y2 = this.getEscala().parserY(y_max);

				EPSWriter.writeLine(EPSWriter.dLine(
						(float) (EPSWriter.xmin + x1),
						(float) (EPSWriter.ymax + y0),
						(float) (EPSWriter.xmin + x1),
						(float) (EPSWriter.ymax + y2)));

				x0 = this.getEscala().parserX(0.5);
				x2 = this.getEscala().parserX(1.5);

				n = 0;
				if (tipDades.equals(tipusDades.DISTANCIA)) {
					/* DISTANCIES */
					if (Orientation.NORTH.equals(or)) {
						y = y0;
						while (y <= (this.getEscala().parserY(y_max))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(0)),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(2)),
										(float) (EPSWriter.ymax + y)));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x0),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + x2),
										(float) (EPSWriter.ymax + y)));
							}
							y += inc;
							n++;
						}
					} else {
						y = y2;
						while (y >= (this.getEscala().parserY(y_min))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(0)),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(2)),
										(float) (EPSWriter.ymax + y)));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x0),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + x2),
										(float) (EPSWriter.ymax + y)));
							}
							y -= inc;
							n++;
						}
					}

				} else {
					/* PESOS */
					if (Orientation.NORTH.equals(or)) {
						y = y2;
						while (y >= (this.getEscala().parserY(y_min))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(0)),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(2)),
										(float) (EPSWriter.ymax + y)));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x0),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + x2),
										(float) (EPSWriter.ymax + y)));
							}
							y -= inc;
							n++;
						}
					} else {
						y = y0;
						while (y <= (this.getEscala().parserY(y_max))) {
							if ((n % ticks) == 0) {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(0)),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + this
												.getEscala().parserX(2)),
										(float) (EPSWriter.ymax + y)));
							} else {
								EPSWriter.writeLine(EPSWriter.dLine(
										(float) (EPSWriter.xmin + x0),
										(float) (EPSWriter.ymax + y),
										(float) (EPSWriter.xmin + x2),
										(float) (EPSWriter.ymax + y)));
							}
							y += inc;
							n++;
						}
					}
				}
			}
		}

		EPSWriter.writeLine("grestore");
	}

}

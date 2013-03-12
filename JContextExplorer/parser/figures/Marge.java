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

import inicial.FesLog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import moduls.frm.FrmPrincipalDesk;

import tipus.Orientation;
import utils.MiMath;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Band figure
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class Marge extends Figura {

	private double alcada, ample;
	private boolean filled;
	
	//phylogeny drawing related
	private boolean FromPhyloTree = false;
	private double PhyloWeight = -1;
	private FrmPrincipalDesk fr;
	private Font font;

	public Marge(final double x, final double y, final double alcada,
			final double ample, final int prec) {
		super(x, y, prec, Color.GRAY);
		this.alcada = alcada;
		this.ample = ample;
		this.filled = true;

	}

	public Marge(final double x, final double y, final double alcada,
			final double ample, final int prec, final Color c) {
		super(x, y, prec, c);
		this.alcada = alcada;
		this.ample = ample;
		this.filled = true;
	}

	public double getAlcada() {
		return alcada;
	}

	public void setAlcada(final double h) {
		alcada = h;
	}

	public double getAmple() {
		return ample;
	}

	public void setAmple(final double w) {
		ample = w;
	}

	public boolean getFilled() {
		return filled;
	}

	public void setFilled(final boolean w) {
		filled = w;
	}

	@Override
	public void dibuixa(final Graphics2D g, final Orientation or) {
		double x1, y1, w, h;
		double xx1, yy1, ww, hh;
		int prec = getPrecisio();
		Color col_org = g.getColor();

		xx1 = this.getPosReal().getX();
		yy1 = MiMath.Arodoneix(this.getPosReal().getY(), prec);
		ww = this.getPosReal().getX() + this.getAmple();
		hh = MiMath
				.Arodoneix(this.getPosReal().getY() + this.getAlcada(), prec);

//		FesLog.LOG.finest("Orientacio: " + or.toString());
//		FesLog.LOG.finest("Precisio: " + prec);
//		FesLog.LOG.finest("Coord. Real: x=" + xx1 + "    y=("
//				+ getPosReal().getY() + ") " + yy1 + "   aglom: ("
//				+ getAlcada() + ")  " + hh + "    ample= " + ww);

		if (or == Orientation.EAST) {
			y1 = yy1;
			yy1 = (this.getEscala().get_Max_Y() - xx1)
					+ this.getEscala().get_Min_Y();
			xx1 = y1;

			h = hh;
			hh = (this.getEscala().get_Max_Y() - ww)
					+ this.getEscala().get_Min_Y();
			ww = h;

			y1 = yy1;
			yy1 = hh;
			hh = y1;

		} else if (or == Orientation.WEST) {
			// rotem P(x,y)
			x1 = xx1;
			xx1 = (this.getEscala().get_Max_X() - yy1)
					+ this.getEscala().get_Min_X();
			yy1 = (this.getEscala().get_Max_Y() - x1)
					+ this.getEscala().get_Min_Y();

			// rotem P(x',y')
			w = ww;
			ww = (this.getEscala().get_Max_X() - hh)
					+ this.getEscala().get_Min_X();
			hh = (this.getEscala().get_Max_Y() - w)
					+ this.getEscala().get_Min_Y();

			// desplacem punt origen eix Y
			y1 = yy1;
			yy1 = hh;
			hh = y1;

			// desplacem punt origen eix X
			x1 = xx1;
			xx1 = ww;
			ww = x1;

		} else if (or == Orientation.SOUTH) {

			// rotem P(x,y)
			yy1 = (this.getEscala().get_Max_Y() - yy1)
					+ this.getEscala().get_Min_Y();

			hh = (this.getEscala().get_Max_Y() - hh)
					+ this.getEscala().get_Min_Y();
			h = yy1;
			yy1 = hh;
			hh = h;

		} else {
			// En nord no cal fer res.
		}

		// escalem
		x1 = this.getEscala().parserX(xx1);
		y1 = this.getEscala().parserY(yy1);
		w = this.getEscala().parserX(ww);
		h = this.getEscala().parserY(hh);

		//debugging: display
		//System.out.println("x: " + x1 + " y: " + y1 + " w:" + w + " h: " + h);
		
		// dibuixem
		if (filled) {
			g.setPaint(this.getColor());
			g.setColor(getColor());
		} else {
			g.setColor(Color.BLACK);
		}
		
		g.draw(new Rectangle2D.Double(x1, y1, w - x1, h - y1));
		
		//add label, if appropriate
		if (FromPhyloTree){
			
			//draw weights
			if (fr.getPanPhyTreeMenu().getChkWeights().isSelected()){
				
				if (PhyloWeight != -1){

					//create graphical text layout
					FontRenderContext renderFont = new FontRenderContext(null,
							true, true);
					Font font = this.getFont();
					String txt = String.valueOf(PhyloWeight);
					TextLayout tl = new TextLayout(txt, font, renderFont);
					
					//draw layout
					AffineTransform defaultAT = g.getTransform();
					g.scale(1, -1);
					tl.draw(g, (float)(x1+1), (float)-(y1+1));
					g.setTransform(defaultAT);
				}
			}
		}
		
		
//		//This code will draw dashed lines, instead of solid lines.
//		float[] dash = {5F,5F};
//		Stroke dashedStroke = new BasicStroke( 2F, BasicStroke.CAP_SQUARE,  
//				BasicStroke.JOIN_MITER, 3F, dash, 0F );  
//		
//		g.draw(dashedStroke.createStrokedShape(new Rectangle2D.Double(x1, y1, w - x1, h - y1)));

//		FesLog.LOG.finest("draw Rectangle2D(" + x1 + ", " + y1 + ", "
//				+ (w - x1) + ", " + (h - y1) + ")");

		g.setColor(col_org);
	}

	@Override
	public void dibuixa(Orientation or) {
	}

	public boolean isFromPhyloTree() {
		return FromPhyloTree;
	}

	public void setFromPhyloTree(boolean fromPhyloTree) {
		FromPhyloTree = fromPhyloTree;
	}

	public double getPhyloWeight() {
		return PhyloWeight;
	}

	public void setPhyloWeight(double phyloWeight) {
		PhyloWeight = phyloWeight;
	}

	public FrmPrincipalDesk getFr() {
		return fr;
	}

	public void setFr(FrmPrincipalDesk fr) {
		this.fr = fr;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}

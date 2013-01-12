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

package moduls.frm;

import importExport.DadesExternes;
import importExport.FitxerDades;
import inicial.Language;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JOptionPane;

import moduls.frm.Panels.Jpan_Menu;
import moduls.frm.children.FrmSearchResults;
import tipus.Orientation;
import tipus.metodo;
import tipus.rotacioNoms;
import tipus.tipusDades;
import definicions.MatriuDistancies;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Internal frame data
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class InternalFrameData {
	
	// ----- Fields ---------------------------------------------------//
	
	// Input data
	private DadesExternes de;

	// Settings on type of data, method, and precision
	private tipusDades typeData;
	private metodo method;
	private int precision;

	// MultiDendrogram
	private MatriuDistancies multiDendro = null;

	// TREE
	private Orientation treeOrientation = Orientation.NORTH;
	
	//making this change alone is insufficient
	//private Orientation treeOrientation = Orientation.WEST;
	
	private boolean showBands = true;
	private Color colorBands = Color.LIGHT_GRAY;

	// NODES
	private int nodesSize = 0;
	private boolean showNodesLabels = true;
	private Font fontNodesLabels;
	private Color colorNodesLabels;
	private rotacioNoms nodesLabelsOrientation = rotacioNoms.VERTICAL;

	// AXIS
	private boolean showAxis = true;
	private Color colorAxis = Color.BLACK;
	private double minValue = 0.0;
	private double maxValue = 1.0;
	private double ticksSeparation = 0.1;
	private boolean showAxisLabels = true;
	private Font fontAxisLabels;
	private Color colorAxisLabels = Color.BLACK;
	private int labelsEvery = 10;
	private int labelsDecimals = 0;
	
	//------Version 1.1 --------//
	//QUERY DATA
	private QueryData QD;	//Data associated with the original query, info
	private FrmSearchResults SearchResultsFrame;	


// ----- Methods ---------------------------------------------------//		
	
	public InternalFrameData(final FitxerDades fitx, MatriuDistancies md) {
		try {
			de = new DadesExternes(fitx);
			multiDendro = md;
			Jpan_Menu.getConfigPanel(this);
		} catch (Exception e) {
			showError("Error: \n" + e.toString());
		}
	}
	
	public InternalFrameData(final DadesExternes DE, final MatriuDistancies md){
		try {
			//adjustments
			this.de = DE;
			this.multiDendro = md;
			
			Jpan_Menu.getConfigPanel(this);
		} catch (Exception e){
			showError("Problem loading internal frame data");	
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(null, message, Language.getLabel(7),
				JOptionPane.ERROR_MESSAGE);
	}

	//GETTERS AND SETTERS
	public DadesExternes getDadesExternes() {
		return de;
	}

	public tipusDades getTypeData() {
		return typeData;
	}

	public void setTypeData(final tipusDades tipoDatos) {
		typeData = tipoDatos;
	}

	public metodo getMethod() {
		return method;
	}

	public void setMethod(final metodo metode) {
		method = metode;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(final int decimalsSignificatius) {
		precision = decimalsSignificatius;
	}

	public MatriuDistancies getMultiDendrogram() {
		return multiDendro;
	}

	public Orientation getOrientacioDendograma() {
		return treeOrientation;
	}

	public void setOrientacioDendograma(final Orientation orientacio) {
		treeOrientation = orientacio;
	}

	public boolean isFranjaVisible() {
		return showBands;
	}

	public void setFranjaVisible(final boolean franjaVisible) {
		showBands = franjaVisible;
	}

	public Color getColorMarge() {
		return colorBands;
	}

	public void setColorMarge(final Color colorMarge) {
		colorBands = colorMarge;
	}

	public int getRadiBullets() {
		return nodesSize;
	}

	public void setRadiBullets(final int radiBullets) {
		nodesSize = radiBullets;
	}

	public boolean isNomsVisibles() {
		return showNodesLabels;
	}

	public void setNomsVisibles(final boolean nomsVisibles) {
		showNodesLabels = nomsVisibles;
	}

	public Font getFontNoms() {
		return fontNodesLabels;
	}

	public void setFontNoms(final Font fontNoms) {
		fontNodesLabels = fontNoms;
	}

	public Color getColorNoms() {
		return colorNodesLabels;
	}

	public void setColorNoms(final Color colorNoms) {
		colorNodesLabels = colorNoms;
	}

	public rotacioNoms getRotNoms() {
		return nodesLabelsOrientation;
	}

	public void setRotNoms(final rotacioNoms rotNoms) {
		nodesLabelsOrientation = rotNoms;
	}

	public boolean isEscalaVisible() {
		return showAxis;
	}

	public void setEscalaVisible(final boolean escalaVisible) {
		showAxis = escalaVisible;
	}

	public Color getColorEix() {
		return colorAxis;
	}

	public void setColorEix(final Color colorEix) {
		colorAxis = colorEix;
	}

	public double getValMin() {
		return minValue;
	}

	public void setValMin(final double valMin) {
		minValue = valMin;
	}

	public double getValMax() {
		return maxValue;
	}

	public void setValMax(final double valMax) {
		maxValue = valMax;
	}

	public double getIncrement() {
		return ticksSeparation;
	}

	public void setIncrement(final double increment) {
		ticksSeparation = increment;
	}

	public boolean isEtiquetaEscalaVisible() {
		return showAxisLabels;
	}

	public void setEtiquetaEscalaVisible(final boolean etiquetaVisible) {
		showAxisLabels = etiquetaVisible;
	}

	public Font getFontLabels() {
		return fontAxisLabels;
	}

	public void setFontLabels(final Font fontLabels) {
		fontAxisLabels = fontLabels;
	}

	public Color getColorLabels() {
		return colorAxisLabels;
	}

	public void setColorLabels(final Color colorLabels) {
		colorAxisLabels = colorLabels;
	}

	public int getTics() {
		return labelsEvery;
	}

	public void setTics(final int tics) {
		labelsEvery = tics;
	}

	public int getAxisDecimals() {
		return labelsDecimals;
	}

	public void setAxisDecimals(final int axisDecimals) {
		labelsDecimals = axisDecimals;
	}

	public QueryData getQD() {
		return QD;
	}

	public void setQD(QueryData qD) {
		QD = qD;
	}

	public FrmSearchResults getSearchResultsFrame() {
		return SearchResultsFrame;
	}

	public void setSearchResultsFrame(FrmSearchResults searchResultsFrame) {
		SearchResultsFrame = searchResultsFrame;
	}

}

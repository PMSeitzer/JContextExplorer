package parser;

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

import inicial.Language;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import tipus.tipusDades;
import utils.MathUtils;

import definicions.Cluster;


/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Save dendrogram as Newick tree text file
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class ToNewick2 {

	private final Cluster root;
	private final int precision;
	private final tipusDades typeData;
	private final double heightBottom;
	private PrintWriter printWriter;

	public ToNewick2(Cluster root, int precision, tipusDades simType,
			double heightBottom) {
		this.root = root;
		this.precision = precision;
		this.typeData = simType;
		this.heightBottom = heightBottom;
		
		//all clusters that are also leaves score -1
		for (Cluster c : this.root.getLstFills()){
			c.setAlcada(-1);
		}
		
	}

	public void saveAsNewick(String sPath) throws Exception {
		File file;
		FileWriter fileWriter;
		String errMsg;

		file = new File(sPath);
		try {
			fileWriter = new FileWriter(file);
			printWriter = new PrintWriter(fileWriter);
			showCluster(root, root.getAlcada());
			printWriter.print(";");
			printWriter.close();
		} catch (Exception e) {
			errMsg = Language.getLabel(83);
			throw new Exception(errMsg);
		}
	}

	private void showCluster(final Cluster cluster, final double heightParent)
			throws Exception {
		String name;
		double length;
		int n;

		if (cluster.getAlcada() < 0.0) {

			name = cluster.getNom();
			name = name.replace(' ', '_');
			name = name.replace('\'', '"');
			name = name.replace(':', '|');
			name = name.replace(';', '|');
			name = name.replace(',', '|');
			name = name.replace('(', '{');
			name = name.replace(')', '}');
			name = name.replace('[', '{');
			name = name.replace(']', '}');
			
			printWriter.print(name); //write the name of the node to the file

			if (typeData.equals(tipusDades.DISTANCIA))  {
				length = MathUtils.round(heightParent - heightBottom, precision);
			} else {
				length = MathUtils.round(heightBottom - heightParent, precision);
			}
			if (length > 0) {
				printWriter.print(":" + length);
			}
		} else if (cluster.getNumSubclusters() > 1) {
			printWriter.print("(");
			for (n = 0; n < cluster.getNumSubclusters(); n++) {
				showCluster(cluster.getFill(n), cluster.getAlcada());
				if (n < cluster.getNumSubclusters() - 1) {
					printWriter.print(",");
				}
			}
			printWriter.print(")");
			if (typeData.equals(tipusDades.DISTANCIA)) {
				length = MathUtils.round(heightParent - cluster.getAlcada(), precision);
			} else {
				length = MathUtils.round(cluster.getAlcada() - heightParent, precision);
			}
			if (length > 0) {
				printWriter.print(":" + length);
			}
		}
	}

}

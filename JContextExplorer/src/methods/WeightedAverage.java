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

package methods;

import utils.PrecDouble;
import definicions.Cluster;
import definicions.MatriuDistancies;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Weighted Average clustering algorithm
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class WeightedAverage extends Method {

	public WeightedAverage(final Cluster ci, final Cluster cj,
			final MatriuDistancies md) {
		super(ci, cj, md);
	}

	@Override
	public double getAlfa_ij(final Cluster i, final Cluster j) {
		double res;
		final PrecDouble pr = new PrecDouble("1.0");
		int a, b;
		a = cI.isNado() ? cI.getCardinalitat() : 1;
		b = cJ.isNado() ? cJ.getCardinalitat() : 1;
		res = (a * b);
		pr.Division(res);

		return pr.parserToDouble();
	}

	@Override
	public double getBeta_ii(final Cluster i, final Cluster j) {
		return 0;
	}

	@Override
	public double getBeta_jj(final Cluster j, final Cluster jj) {
		return 0;
	}

	@Override
	public double getGamma_ij(final Cluster i, final Cluster j) {
		return 0;
	}

	@Override
	public double CalculLinkage() {
		return 0;
	}
}

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
 * Unweighted Centroid clustering algorithm
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class UnweightedCentroid extends Method {

	public UnweightedCentroid(final Cluster ci, final Cluster cj,
			final MatriuDistancies md) {
		super(ci, cj, md);
	}

	@Override
	protected double CalculLinkage() {
		return 0;
	}

	@Override
	protected double getAlfa_ij(final Cluster i, final Cluster j) {
		PrecDouble res, tmp;
		tmp = new PrecDouble(cI.getFills());
		tmp.Producto(cJ.getFills());

		res = new PrecDouble(i.getFills());
		res.Producto(j.getFills());

		res.Division(tmp);
		return res.parserToDouble();
	}

	@Override
	protected double getBeta_ii(final Cluster i, final Cluster ii) {
		PrecDouble res, tmp;
		res = new PrecDouble(i.getFills());
		res.Producto(ii.getFills());
		tmp = new PrecDouble(cI.getFills());
		tmp.Pow(2);

		res.Division(tmp);
		res.CanviSigne();
		return res.parserToDouble();
	}

	@Override
	protected double getBeta_jj(final Cluster j, final Cluster jj) {
		PrecDouble res, tmp;
		res = new PrecDouble(j.getFills());
		res.Producto(jj.getFills());

		tmp = new PrecDouble(cJ.getFills());
		tmp.Pow(2);
		res.Division(tmp);
		res.CanviSigne();
		return res.parserToDouble();

	}

	@Override
	protected double getGamma_ij(final Cluster i, final Cluster j) {
		return 0;
	}
}

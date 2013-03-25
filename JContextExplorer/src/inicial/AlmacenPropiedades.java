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

package inicial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Properties;

import errors.ErrorProperties;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Reads configuration file
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */

//translate: warehouse properties?
public class AlmacenPropiedades {

	//path to configuration file - Mac OS path
	//private static final String CONFIGURATION_FILE = "/Users/phillipseitzer/Documents/EclipseProjects/multidendrograms-2.1.0-src/ini/dendo.ini";
	
	//Windows path
	private static final String CONFIGURATION_FILE = "C:/Users/phil/WinEclipse/ECRONGUI/ECrons/multidendrograms-2.1.0-src/ini/dendo.ini";
	
	//original path
	//private static final String CONFIGURATION_FILE = "ini/dendo.ini";

	private static HashMap<String, String> propiedades;

	public AlmacenPropiedades() throws Exception {
		FesLog.LOG.info("Created a new instance of the object.");

		try {
			final FileInputStream f = new FileInputStream(
					AlmacenPropiedades.CONFIGURATION_FILE);

			final Properties propiedadesTemporales = new Properties();
			propiedadesTemporales.load(f);
			f.close();

			AlmacenPropiedades.propiedades = new HashMap(propiedadesTemporales);
		} catch (final FileNotFoundException e) {
			FesLog.LOG.warning("Could not find the boot file" + e);
			throw new FileNotFoundException("Could not find the boot file");
		} catch (final Exception e) {
			String msg_err = "ERROR in the property file format " + "\n"
					+ e.getStackTrace();
			FesLog.LOG
					.throwing("AlmacenPropiedades", "AlmacenPropiedades()", e);
			throw new Exception(msg_err);
		}
	}

	public static String getPropiedad(final String nombre)
			throws ErrorProperties {
		final String valor = AlmacenPropiedades.propiedades.get(nombre);
		if (valor == null) {
			String msg_err = Language.getLabel(66) + " " + nombre;
			FesLog.LOG.warning(msg_err);
			throw new ErrorProperties(msg_err);
		}
		return valor; //translate: value
	}
}
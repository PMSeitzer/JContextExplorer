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

package parser.EPS;

import inicial.Language;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.JOptionPane;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Utils for the creation of the EPS file
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class EPSWriter {
	private static String EPSFileName;
	private static FileWriter eps_fw;
	private static BufferedWriter eps_bw;

	public static int xmin;
	public static int xmax;
	public static int ymin;
	public static int ymax;

	public EPSWriter(int x0, int y0, int x1, int y1) {
		xmin = x0;
		ymin = y0;
		xmax = x1;
		ymax = y1;
	}

	public static void open(String eps_path) {
		try {
			EPSFileName = eps_path;
			eps_fw = new FileWriter(EPSFileName);
			eps_bw = new BufferedWriter(eps_fw);
		} catch (FileNotFoundException e) {
			System.out.println("EPSWriter 67");
			String msg = Language.getLabel(106);
			JOptionPane.showMessageDialog(null, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			System.out.println("EPSWriter 73");
			String msg = Language.getLabel(107);
			JOptionPane.showMessageDialog(null, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void close() {
		try {
			eps_bw.close();
		} catch (IOException e) {
			System.out.println("EPSWriter 82");
			String msg = Language.getLabel(108) + EPSFileName;
			JOptionPane.showMessageDialog(null, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void writeComments(String creator, String orientation) {
		// DATA I HORA
		Calendar c = Calendar.getInstance();

		int hora = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		int ss = c.get(Calendar.SECOND);
		int dia = c.get(Calendar.DATE);
		int mes = c.get(Calendar.MONTH);
		int any = c.get(Calendar.YEAR);

		String seg = Integer.toString(ss);
		if (ss < 10)
			seg = "0" + seg;

		// COMENTARIS
		writeLine("%!PS-Adobe-3.0 EPSF-3.0");
		writeLine("%%Title: (" + EPSFileName + ")");
		writeLine("%%Creator: (" + creator + ")");
		writeLine("%%CreationDate: (" + dia + "/" + mes + "/" + any + ")"
				+ " (" + hora + ":" + min + ":" + seg + ")");
		writeLine("%%BoundingBox: " + xmin + " " + ymin + " " + xmax + " "
				+ ymax);
		writeLine("%%Orientation: " + orientation);
		writeLine("%%Pages: 1");
		writeLine("%%EndComments");
		writeLine("");
	}

	public static void writeProlog(String prolog_path) {
		try {
			FileReader prolog_fr = new FileReader(prolog_path);
			BufferedReader prolog_bf = new BufferedReader(prolog_fr);

			String line;
			while ((line = prolog_bf.readLine()) != null)
				writeLine(line);

			prolog_bf.close();
		} catch (IOException e) {
			System.out.println("EPSWriter 131");
			String msg = Language.getLabel(109) + EPSFileName;
			JOptionPane.showMessageDialog(null, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	//instead of importing a file, write the contents of the file directly.
	public static void writePrologManual(){
		
		//Instead of importing from file, all contents of file are recapitulated here.
		writeLine("%% BeginProlog");
		writeLine("% Begin definitions summary");
		writeLine("%   Variables");
		writeLine("%     n: Natural");
		writeLine("%     x*, y*, dx, dy, r, a: Float");
		writeLine("%     rr, gg, bb, ww: Float range 0.0..1.0");
		writeLine("%     ff: AnyFontName");
		writeLine("%     fn: FontNameAbreviation(Cour, Helv, Helvn, Tim, Sym)");
		writeLine("%     str: String_Without_Quotation_Marks");
		writeLine("%   Functions and their usage");
		writeLine("%     SetLineWidth: r slw");
		writeLine("%     SetDashPattern: x1 x2 ... xn n sdp");
		writeLine("%     SetRGBColor: rr gg bb c");
		writeLine("%     SetGray: ww g");
		writeLine("%     Stroke: s");
		writeLine("%     MoveTo: x y m");
		writeLine("%     LineTo: x y l");
		writeLine("%     RMoveTo: dx dy rm");
		writeLine("%     RLineTo: dx dy rl");
		writeLine("%     DupDup: x y dup2  ->  x y x y");
		writeLine("%     DupDupDup: x y z dup3  ->  x y z x y z");
		writeLine("%     VectorSubstract: x1 y1 x2 y2 vsub  ->  x2-x1 y2-y1");
		writeLine("%     Circle    : x y r dci");
		writeLine("%     = (filled): x y r fci");
		writeLine("%     = (opaque): x y r oci");
		writeLine("%     Square    : x y r dsq");
		writeLine("%     = (filled): x y r fsq");
		writeLine("%     = (opaque): x y r osq");
		writeLine("%     Triangle N: x y r dtn");
		writeLine("%     = (filled): x y r ftn");
		writeLine("%     = (opaque): x y r otn");
		writeLine("%     Triangle W: x y r dtw");
		writeLine("%     = (filled): x y r ftw");
		writeLine("%     = (opaque): x y r otw");
		writeLine("%     Triangle S: x y r dts");
		writeLine("%     = (filled): x y r fts");
		writeLine("%     = (opaque): x y r ots");
		writeLine("%     Triangle E: x y r dte");
		writeLine("%     = (filled): x y r fte");
		writeLine("%     = (opaque): x y r ote");
		writeLine("%     Diamond   : x y r ddi");
		writeLine("%     = (filled): x y r fdi");
		writeLine("%     = (opaque): x y r odi");
		writeLine("%     Plus      : x y r dpl");
		writeLine("%     = (filled): x y r fpl");
		writeLine("%     = (opaque): x y r opl");
		writeLine("%     Times     : x y r dxx");
		writeLine("%     = (filled): x y r fxx");
		writeLine("%     = (opaque): x y r oxx");
		writeLine("%     Splat     : x y r dsp");
		writeLine("%     = (filled): x y r fsp");
		writeLine("%     = (opaque): x y r osp");
		writeLine("%     Rectangle : x1 y1 x2 y2 drc");
		writeLine("%     = (filled): x1 y1 x2 y2 frc");
		writeLine("%     = (opaque): x1 y1 x2 y2 orc");
		writeLine("%     Line      : x1 y1 x2 y2 dli");
		writeLine("%     = (filled): x1 y1 x2 y2 fli");
		writeLine("%     = (opaque): x1 y1 x2 y2 oli");
		writeLine("%     ScaleSetFont   : fn r fss | ff r fss");
		writeLine("%     = (italic)     : fnem r fss");
		writeLine("%     = (bold)       : fnbf r fss");
		writeLine("%     = (bold-italic): fnbfem r fss");
		writeLine("%     Text:");
		writeLine("%     = BoundingBox        : x y (str) ssbb -> lx ly rx uy");
		writeLine("%     = LowercaseBottomTop : x y (str) ssbt -> by ty");
		writeLine("%     = Lower: Referred to BoundingBox");
		writeLine("%     =   (lower left)    : x y (str) ssll");
		writeLine("%     =   (lower middle)  : x y (str) sslm");
		writeLine("%     =   (lower right)   : x y (str) sslr");
		writeLine("%     = Middle: Referred to BoundingBox");
		writeLine("%     =   (middle left)   : x y (str) ssml");
		writeLine("%     =   (middle middle) : x y (str) ssmm");
		writeLine("%     =   (middle right)  : x y (str) ssmr");
		writeLine("%     = Upper: Referred to BoundingBox");
		writeLine("%     =   (upper left)    : x y (str) ssul");
		writeLine("%     =   (upper middle)  : x y (str) ssum");
		writeLine("%     =   (upper right)   : x y (str) ssur");
		writeLine("%     = Bottom: Referred to LowercaseBottomTop");
		writeLine("%     =   (bottom left)   : x y (str) ssbl");
		writeLine("%     =   (bottom middle) : x y (str) ssbm");
		writeLine("%     =   (bottom right)  : x y (str) ssbr");
		writeLine("%     = Center: Referred to LowercaseBottomTop");
		writeLine("%     =   (center left)   : x y (str) sscl");
		writeLine("%     =   (center middle) : x y (str) sscm");
		writeLine("%     =   (center right)  : x y (str) sscr");
		writeLine("%     = Top: Referred to LowercaseBottomTop");
		writeLine("%     =   (top left)      : x y (str) sstl");
		writeLine("%     =   (top middle)    : x y (str) sstm");
		writeLine("%     =   (top right)     : x y (str) sstr");
		writeLine("%     Text Rotated:");
		writeLine("%     = Lower: Referred to BoundingBox");
		writeLine("%     =   (lower left)    : x y a (str) ssall");
		writeLine("%     =   (lower middle)  : x y a (str) ssalm");
		writeLine("%     =   (lower right)   : x y a (str) ssalr");
		writeLine("%     = Middle: Referred to BoundingBox");
		writeLine("%     =   (middle left)   : x y a (str) ssaml");
		writeLine("%     =   (middle middle) : x y a (str) ssamm");
		writeLine("%     =   (middle right)  : x y a (str) ssamr");
		writeLine("%     = Upper: Referred to BoundingBox");
		writeLine("%     =   (upper left)    : x y a (str) ssaul");
		writeLine("%     =   (upper middle)  : x y a (str) ssaum");
		writeLine("%     =   (upper right)   : x y a (str) ssaur");
		writeLine("%     = Bottom: Referred to LowercaseBottomTop");
		writeLine("%     =   (bottom left)   : x y a (str) ssabl");
		writeLine("%     =   (bottom middle) : x y a (str) ssabm");
		writeLine("%     =   (bottom right)  : x y a (str) ssabr");
		writeLine("%     = Center: Referred to LowercaseBottomTop");
		writeLine("%     =   (center left)   : x y a (str) ssacl");
		writeLine("%     =   (center middle) : x y a (str) ssacm");
		writeLine("%     =   (center right)  : x y a (str) ssacr");
		writeLine("%     = Top: Referred to LowercaseBottomTop");
		writeLine("%     =   (top left)      : x y a (str) ssatl");
		writeLine("%     =   (top middle)    : x y a (str) ssatm");
		writeLine("%     =   (top right)     : x y a (str) ssatr");
		writeLine("% End definitions summary");
		writeLine("% Begin definitions");
		writeLine("% Basic");	
		writeLine("/slw {setlinewidth} bind def");
		writeLine("/sdp  {array astore 0 setdash} bind def");
		writeLine("/c {setrgbcolor} bind def");
		writeLine("/g {setgray} bind def");
		writeLine("/s {stroke} bind def");
		writeLine("/m {moveto} bind def");
		writeLine("/l {lineto} bind def");
		writeLine("/rm {rmoveto} bind def");
		writeLine("/rl {rlineto} bind def");
		writeLine("/dup2 {dup 3 -1 roll dup 4 1 roll exch} bind def");
		writeLine("/dup3 {dup 4 2 roll dup 5 1 roll 3 1 roll dup 6 1 roll 3 1 roll} bind def");
		writeLine("/vsub {4 2 roll 3 -1 roll exch sub 3 1 roll sub exch} bind def");
		writeLine("% Fill");
		writeLine("/f { gsave fill grestore s} bind def");
		writeLine("% Opaque");
		writeLine("/o { gsave 1.0 1.0 1.0 c fill grestore s} bind def");
		writeLine("% Circle");
		writeLine("/ci { dup3 3 -1 roll add exch m 0 360 arc } bind def");
		writeLine("/dci { ci s } bind def");
		writeLine("/fci { ci f } bind def");
		writeLine("/oci { ci o } bind def");
		writeLine("% Square");
		writeLine("/sq { 3 1 roll m dup dup rm 2 mul dup neg 0 rl dup neg 0 exch rl 0 rl closepath } bind def");
		writeLine("/dsq { sq s } bind def");
		writeLine("/fsq { sq f } bind def");
		writeLine("/osq { sq o } bind def");
		writeLine("% Triangle N");
		writeLine("/tn { 3 1 roll m dup 0 exch rm dup neg dup 2 mul rl 2 mul 0 rl closepath } bind def");
		writeLine("/dtn { tn s } bind def");
		writeLine("/ftn { tn f } bind def");
		writeLine("/otn { tn o } bind def");
		writeLine("% Triangle W");
		writeLine("/tw { 3 1 roll m dup neg 0 rm dup dup 2 mul exch neg rl 2 mul 0 exch rl closepath } bind def");
		writeLine("/dtw { tw s } bind def");
		writeLine("/ftw { tw f } bind def");
		writeLine("/otw { tw o } bind def");
		writeLine("% Triangle S");
		writeLine("/ts { 3 1 roll m dup neg 0 exch rm dup dup 2 mul rl neg 2 mul 0 rl closepath } bind def");
		writeLine("/dts { ts s } bind def");
		writeLine("/fts { ts f } bind def");
		writeLine("/ots { ts o } bind def");
		writeLine("% Triangle E");
		writeLine("/te { 3 1 roll m dup 0 rm dup dup -2 mul exch rl -2 mul 0 exch rl closepath } bind def");
		writeLine("/dte { te s } bind def");
		writeLine("/fte { te f } bind def");
		writeLine("/ote { te o } bind def");
		writeLine("% Diamond");
		writeLine("/di { 3 1 roll m dup 0 exch rm dup neg dup rl dup dup neg rl dup rl closepath } bind def");
		writeLine("/ddi { di s } bind def");
		writeLine("/fdi { di f } bind def");
		writeLine("/odi { di o } bind def");
		writeLine("% Plus symbol");
		writeLine("/pl { 3 1 roll m dup 0 rm dup -2 mul 0 rl dup dup rm -2 mul 0 exch rl } bind def");
		writeLine("/dpl { pl s } bind def");
		writeLine("/fpl { pl f } bind def");
		writeLine("/opl { pl o } bind def");
		writeLine("% Times symbol");
		writeLine("/xx { 3 1 roll m 45 cos mul dup dup rm dup -2 mul dup rl 2 mul dup 0 rm dup neg exch rl } bind def");
		writeLine("/dxx { xx s } bind def");
		writeLine("/fxx { xx f } bind def");
		writeLine("/oxx { xx o } bind def");
		writeLine("% Splat symbol");
		writeLine("/sp { dup 4 2 roll dup2 6 -1 roll pl 3 -1 roll xx } bind def");
		writeLine("/dsp { sp s } bind def");
		writeLine("/fsp { sp f } bind def");
		writeLine("/osp { sp o } bind def");
		writeLine("% Rectangle");
		writeLine("/rc { dup2 m vsub dup neg 0 exch rl exch neg 0 rl 0 exch rl closepath } bind def");
		writeLine("/drc { rc s } bind def");
		writeLine("/frc { rc f } bind def");
		writeLine("/orc { rc o } bind def");
		writeLine("% Line");
		writeLine("/li { 4 2 roll m l } bind def");
		writeLine("/dli { li s } bind def");
		writeLine("/fli { li f } bind def");
		writeLine("/oli { li o } bind def");
		writeLine("% Fonts");
		writeLine("/fss { selectfont } bind def");
		writeLine("/Avant { /AvantGarde-Book } bind def");
		writeLine("/Avantem { /AvantGarde-BookOblique } bind def");
		writeLine("/Avantbf { /AvantGarde-Demi } bind def");
		writeLine("/Avantbfem { /AvantGarde-DemiOblique } bind def");
		writeLine("/Bookm { /Bookman-Light } bind def");
		writeLine("/Bookmem { /Bookman-LightItalic } bind def");
		writeLine("/Bookmbf { /Bookman-Demi } bind def");
		writeLine("/Bookmbfem { /Bookman-DemiItalic } bind def");
		writeLine("/Cour { /Courier } bind def");
		writeLine("/Courem { /Courier-Oblique } bind def");
		writeLine("/Courbf { /Courier-Bold } bind def");
		writeLine("/Courbfem { /Courier-BoldOblique } bind def");
		writeLine("/Helv { /Helvetica } bind def");
		writeLine("/Helvem { /Helvetica-Oblique } bind def");
		writeLine("/Helvbf { /Helvetica-Bold } bind def");
		writeLine("/Helvbfem { /Helvetica-BoldOblique } bind def");
		writeLine("/Helvn { /Helvetica-Narrow } bind def");
		writeLine("/Helvnem { /Helvetica-Narrow-Oblique } bind def");
		writeLine("/Helvnbf { /Helvetica-Narrow-Bold } bind def");
		writeLine("/Helvnbfem { /Helvetica-Narrow-BoldOblique } bind def");
		writeLine("/Cent { /NewCenturySchlbk-Roman } bind def");
		writeLine("/Centem { /NewCenturySchlbk-Italic } bind def");
		writeLine("/Centbf { /NewCenturySchlbk-Bold } bind def");
		writeLine("/Centbfem { /NewCenturySchlbk-BoldItalic } bind def");
		writeLine("/Palat { /Palatino-Roman } bind def");
		writeLine("/Palatem { /Palatino-Italic } bind def");
		writeLine("/Palatbf { /Palatino-Bold } bind def");
		writeLine("/Palatbfem { /Palatino-BoldItalic } bind def");
		writeLine("/Sym { /Symbol } bind def");
		writeLine("/Symem { /Symbol } bind def");
		writeLine("/Symbf { /Symbol } bind def");
		writeLine("/Symbfem { /Symbol } bind def");
		writeLine("/Tim { /Times-Roman } bind def");
		writeLine("/Timem { /Times-Italic } bind def");
		writeLine("/Timbf { /Times-Bold } bind def");
		writeLine("/Timbfem { /Times-BoldItalic } bind def");
		writeLine("/Zchanc { /ZapfChancery-MediumItalic } bind def");
		writeLine("/Zchancem { /ZapfChancery-MediumItalic } bind def");
		writeLine("/Zchancbf { /ZapfChancery-MediumItalic } bind def");
		writeLine("/Zchancbfem { /ZapfChancery-MediumItalic } bind def");
		writeLine("/Zding { /ZapfDingbats } bind def");
		writeLine("/Zdingem { /ZapfDingbats } bind def");
		writeLine("/Zdingbf { /ZapfDingbats } bind def");
		writeLine("/Zdingbfem { /ZapfDingbats } bind def");
		writeLine("% Text");
		writeLine("/ssbb {gsave 3 1 roll m false charpath pathbbox clippath grestore} bind def");
		writeLine("/ssbt {pop (a) ssbb exch pop 3 -1 roll pop} bind def");
		writeLine("/sslx {ssbb pop pop pop} bind def");
		writeLine("/ssrx {ssbb pop exch pop exch pop} bind def");
		writeLine("/ssmx {dup3 sslx 4 1 roll ssrx add 2 div} bind def");
		writeLine("/ssly {ssbb pop pop exch pop} bind def");
		writeLine("/ssuy {ssbb exch pop exch pop exch pop} bind def");
		writeLine("/ssmy {dup3 ssly 4 1 roll ssuy add 2 div} bind def");
		writeLine("/ssby {ssbt pop} bind def");
		writeLine("/ssty {ssbt exch pop} bind def");
		writeLine("/sscy {dup3 ssby 4 1 roll ssty add 2 div} bind def");
		writeLine("/ssll {dup3 dup3 sslx 4 1 roll ssly 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sslm {dup3 dup3 ssmx 4 1 roll ssly 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sslr {dup3 dup3 ssrx 4 1 roll ssly 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssml {dup3 dup3 sslx 4 1 roll ssmy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssmm {dup3 dup3 ssmx 4 1 roll ssmy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssmr {dup3 dup3 ssrx 4 1 roll ssmy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssul {dup3 dup3 sslx 4 1 roll ssuy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssum {dup3 dup3 ssmx 4 1 roll ssuy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssur {dup3 dup3 ssrx 4 1 roll ssuy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssbl {dup3 dup3 sslx 4 1 roll ssby 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssbm {dup3 dup3 ssmx 4 1 roll ssby 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssbr {dup3 dup3 ssrx 4 1 roll ssby 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sscl {dup3 dup3 sslx 4 1 roll sscy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sscm {dup3 dup3 ssmx 4 1 roll sscy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sscr {dup3 dup3 ssrx 4 1 roll sscy 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sstl {dup3 dup3 sslx 4 1 roll ssty 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sstm {dup3 dup3 ssmx 4 1 roll ssty 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/sstr {dup3 dup3 ssrx 4 1 roll ssty 5 -2 roll dup2 m vsub rm show} bind def");
		writeLine("/ssall {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 sslx 4 1 roll ssly 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssalm {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssmx 4 1 roll ssly 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssalr {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssrx 4 1 roll ssly 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssaml {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 sslx 4 1 roll ssmy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssamm {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssmx 4 1 roll ssmy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssamr {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssrx 4 1 roll ssmy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssaul {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 sslx 4 1 roll ssuy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssaum {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssmx 4 1 roll ssuy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssaur {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssrx 4 1 roll ssuy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssabl {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 sslx 4 1 roll ssby 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssabm {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssmx 4 1 roll ssby 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssabr {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssrx 4 1 roll ssby 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssacl {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 sslx 4 1 roll sscy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssacm {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssmx 4 1 roll sscy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssacr {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssrx 4 1 roll sscy 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssatl {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 sslx 4 1 roll ssty 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssatm {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssmx 4 1 roll ssty 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("/ssatr {gsave 4 2 roll translate 0 0 m 0 0 3 -1 roll dup3 dup3 ssrx 4 1 roll ssty 5 -2 roll vsub 4 -1 roll rotate rm show grestore} bind def");
		writeLine("% End definitions");
		writeLine("%%EndProlog");
		
	}
	
	public static void writeEnd() {
		// Mostrem la p�gina
		writeLine("showpage");
		writeLine("");
		writeLine("%%EOF");
	}

	public static void writeLine(String line) {
		try {
			eps_bw.write(line + "\n");
		} catch (IOException e) {
			System.out.println("EPSWriter 149");
			String msg = Language.getLabel(108) + EPSFileName;
			JOptionPane.showMessageDialog(null, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static String setLineWidth(float r) {
		return (r + " slw");
	}

	public static String setDashpattern(float x[], int n) {
		String str = new String();
		for (int i = 0; i < x.length; i++)
			str = str + x[i] + " ";
		return (str + n + " sdp");
	}

	public static String setRGBColor(float rr, float gg, float bb) {
		return (rr + " " + gg + " " + bb + " c");
	}

	public static String setGray(float ww) {
		return (ww + " g");
	}

	public static String stroke() {
		return ("s");
	}

	public static String moveTo(float x, float y) {
		return (x + " " + y + " m");
	}

	public static String lineTo(float x, float y) {
		return (x + " " + y + " l");
	}

	public static String rMoveTo(float dx, float dy) {
		return (dx + " " + dy + " rm");
	}

	public static String rLineTo(float dx, float dy) {
		return (dx + " " + dy + " rl");
	}

	public static String dupDup(float x, float y) {
		return (x + " " + y + " dup2");
	}

	public static String dupDupDup(float x, float y, float z) {
		return (x + " " + y + " " + z + " dup3");
	}

	public static String vectorSubstract(float x1, float y1, float x2, float y2) {
		return (x1 + " " + y1 + " " + x2 + " " + y2 + " vsub");
	}

	public static String dCircle(float x, float y, float r) {
		return (x + " " + y + " " + r + " dci");
	}

	public static String fCircle(float x, float y, float r) {
		return (x + " " + y + " " + r + " fci");
	}

	public static String oCircle(float x, float y, float r) {
		return (x + " " + y + " " + r + " oci");
	}

	public static String dSquare(float x, float y, float r) {
		return (x + " " + y + " " + r + " dsq");
	}

	public static String fSquare(float x, float y, float r) {
		return (x + " " + y + " " + r + " fsq");
	}

	public static String oSquare(float x, float y, float r) {
		return (x + " " + y + " " + r + " osq");
	}

	public static String dTriangleN(float x, float y, float r) {
		return (x + " " + y + " " + r + " dtn");
	}

	public static String fTriangleN(float x, float y, float r) {
		return (x + " " + y + " " + r + " ftn");
	}

	public static String oTriangleN(float x, float y, float r) {
		return (x + " " + y + " " + r + " otn");
	}

	public static String dTriangleW(float x, float y, float r) {
		return (x + " " + y + " " + r + " dtw");
	}

	public static String fTriangleW(float x, float y, float r) {
		return (x + " " + y + " " + r + " ftw");
	}

	public static String oTriangleW(float x, float y, float r) {
		return (x + " " + y + " " + r + " otw");
	}

	public static String dTriangleS(float x, float y, float r) {
		return (x + " " + y + " " + r + " dts");
	}

	public static String fTriangleS(float x, float y, float r) {
		return (x + " " + y + " " + r + " fts");
	}

	public static String oTriangleS(float x, float y, float r) {
		return (x + " " + y + " " + r + " ots");
	}

	public static String dTriangleE(float x, float y, float r) {
		return (x + " " + y + " " + r + " dte");
	}

	public static String fTriangleE(float x, float y, float r) {
		return (x + " " + y + " " + r + " fte");
	}

	public static String oTriangleE(float x, float y, float r) {
		return (x + " " + y + " " + r + " ote");
	}

	public static String dDiamond(float x, float y, float r) {
		return (x + " " + y + " " + r + " ddi");
	}

	public static String fDiamond(float x, float y, float r) {
		return (x + " " + y + " " + r + " fdi");
	}

	public static String oDiamond(float x, float y, float r) {
		return (x + " " + y + " " + r + " odi");
	}

	public static String dPlus(float x, float y, float r) {
		return (x + " " + y + " " + r + " dpl");
	}

	public static String fPlus(float x, float y, float r) {
		return (x + " " + y + " " + r + " fpl");
	}

	public static String oPlus(float x, float y, float r) {
		return (x + " " + y + " " + r + " opl");
	}

	public static String dTimes(float x, float y, float r) {
		return (x + " " + y + " " + r + " dxx");
	}

	public static String fTimes(float x, float y, float r) {
		return (x + " " + y + " " + r + " fxx");
	}

	public static String oTimes(float x, float y, float r) {
		return (x + " " + y + " " + r + " oxx");
	}

	public static String dSplat(float x, float y, float r) {
		return (x + " " + y + " " + r + " dsp");
	}

	public static String fSplat(float x, float y, float r) {
		return (x + " " + y + " " + r + " fsp");
	}

	public static String oSplat(float x, float y, float r) {
		return (x + " " + y + " " + r + " osp");
	}

	public static String dRectangle(float x1, float y1, float x2, float y2) {
		return (x1 + " " + y1 + " " + x2 + " " + y2 + " drc");
	}

	public static String fRectangle(float x1, float y1, float x2, float y2) {
		return (x1 + " " + y1 + " " + x2 + " " + y2 + " frc");
	}

	public static String oRectangle(float x1, float y1, float x2, float y2) {
		return (x1 + " " + y1 + " " + x2 + " " + y2 + " orc");
	}

	public static String dLine(float x1, float y1, float x2, float y2) {
		return (x1 + " " + y1 + " " + x2 + " " + y2 + " dli");
	}

	public static String fLine(float x1, float y1, float x2, float y2) {
		return (x1 + " " + y1 + " " + x2 + " " + y2 + " fli");
	}

	public static String oLine(float x1, float y1, float x2, float y2) {
		return (x1 + " " + y1 + " " + x2 + " " + y2 + " oli");
	}

	public static String scaleSetFont(String fn, float r) {
		return (fn + " " + r + " fss");
	}

	public static String boundingBoxText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssbb");
	}

	public static String lowercaseBottomTopText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssbt");
	}

	public static String lowerLeftText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssll");
	}

	public static String lowerMiddleText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sslm");
	}

	public static String lowerRightText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sslr");
	}

	public static String middleLeftText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssml");
	}

	public static String middleMiddleText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssmm");
	}

	public static String middleRightText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssmr");
	}

	public static String upperLeftText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssul");
	}

	public static String upperMiddleText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssum");
	}

	public static String upperRightText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssur");
	}

	public static String bottomLeftText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssbl");
	}

	public static String bottomMiddleText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssbm");
	}

	public static String bottomRightText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") ssbr");
	}

	public static String centerLeftText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sscl");
	}

	public static String centerMiddleText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sscm");
	}

	public static String centerRightText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sscr");
	}

	public static String topLeftText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sstl");
	}

	public static String topMiddleText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sstm");
	}

	public static String topRightText(float x, float y, String str) {
		return (x + " " + y + " (" + str + ") sstr");
	}

	public static String lowerLeftTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssall");
	}

	public static String lowerMiddleTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssalm");
	}

	public static String lowerRightTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssalr");
	}

	public static String middleLeftTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssaml");
	}

	public static String middleMiddleTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssamm");
	}

	public static String middleRightTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssamr");
	}

	public static String upperLeftTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssaul");
	}

	public static String upperMiddleTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssaum");
	}

	public static String upperRightTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssaur");
	}

	public static String bottomLeftTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssabl");
	}

	public static String bottomMiddleTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssabm");
	}

	public static String bottomRightTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssabr");
	}

	public static String centerLeftTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssacl");
	}

	public static String centerMiddleTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssacm");
	}

	public static String centerRightTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssacr");
	}

	public static String topLeftTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssatl");
	}

	public static String topMiddleTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssatm");
	}

	public static String topRightTextRotated(float x, float y, float a,
			String str) {
		return (x + " " + y + " " + a + " (" + str + ") ssatr");
	}
}

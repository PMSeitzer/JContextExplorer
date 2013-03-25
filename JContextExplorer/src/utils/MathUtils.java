package utils;

public class MathUtils {

	public static double round(final double num, final int prec) {
		double result;
		double factor;

		factor = Math.pow(10, prec);
		result = Math.round(num * factor) / factor;
		return result;
	}
	
}

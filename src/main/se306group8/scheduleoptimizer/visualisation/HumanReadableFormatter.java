package se306group8.scheduleoptimizer.visualisation;

public class HumanReadableFormatter {

	private static final String[] orderOfMagnitude = {" ","k","M","G","T","P","Y"};

	/**
	 * Format a given integer to a number of decimal places,
	 * including the magnitude and unit.
	 * Extra places can be configured.
	 * @param number
	 * @param unit
	 * @param extraPlaces
	 * @return
	 */
	public static String format(long number, String unit, int minimumPlaces) {
		int minimum = 1;
		for(int i = 1; i < minimumPlaces; i++) {
			minimum *= 10;
		}

		int mag = 0;
		while(mag + 1 < orderOfMagnitude.length && number / 1000 >= minimum) {
			number /= 1000;
			mag++;
		}
		
		return String.valueOf(number) + " " + orderOfMagnitude[mag] + unit;
	}

	public static String format(long number, String unit) {
		return format(number,unit,2);
	}

}

package se306group8.scheduleoptimizer.visualisation;

public class HumanReadableFormatter {

	private static final String[] orderOfMagnitude = {" ","k","M","G","T","P","Y"};

	/**
	 * Format a given integer to three places,
	 * including the magnitude and unit.
	 * Extra places can be configured.
	 * @param number
	 * @param unit
	 * @param extraPlaces
	 * @return
	 */
	public static String format(int number, String unit, int extraPlaces) {
		int mag = (int)Math.floor(Math.log10(number)/3);

		// Verify correct range.
		if(mag < 0 || mag >= orderOfMagnitude.length) {
			mag = 0;
		}

		return String.valueOf((int) Math.floor(number / (Math.pow(10, mag * 3 -extraPlaces)))) + " " + orderOfMagnitude[mag] + unit;
	}

	public static String format(int number, String unit) {
		return format(number,unit,0);
	}

}

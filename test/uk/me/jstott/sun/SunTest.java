package uk.me.jstott.sun;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import uk.me.jstott.coordconv.LatitudeLongitude;

public class SunTest extends TestCase {

	public void testSunrise() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.sunriseTime(cal, ll, gmt, dst);
		assertEquals("04:41:55", t.toString());
	}

	public void testMorningCivilTwilight() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.morningCivilTwilightTime(cal, ll, gmt, dst);
		assertEquals("03:54:27", t.toString());
	}

	public void testMorningNauticalTwilight() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.morningNauticalTwilightTime(cal, ll, gmt, dst);
		assertEquals("02:40:51", t.toString());
	}

	public void testMorningAstronomicalTwilight() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.morningAstronomicalTwilightTime(cal, ll, gmt, dst);
		assertEquals("00:00:00", t.toString());
	}

	public void testSunset() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.sunsetTime(cal, ll, gmt, dst);
		assertEquals("21:18:35", t.toString());
	}

	public void testEveningCivilTwilight() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.eveningCivilTwilightTime(cal, ll, gmt, dst);
		assertEquals("22:06:13", t.toString());
	}

	public void testEveningNauticalTwilight() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.eveningNauticalTwilightTime(cal, ll, gmt, dst);
		assertEquals("23:20:20", t.toString());
	}

	public void testEveningAstronomicalTwilight() {
		LatitudeLongitude ll = new LatitudeLongitude(51.51236489989193,
				-0.22371768951416016);
		TimeZone gmt = TimeZone.getTimeZone("Europe/London");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.YEAR, 2011);
		boolean dst = true;
		Time t = Sun.eveningAstronomicalTwilightTime(cal, ll, gmt, dst);
		assertEquals("00:00:00", t.toString());
	}
}

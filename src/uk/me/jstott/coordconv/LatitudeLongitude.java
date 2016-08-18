//-----------------------------------------------------------------------------
// GridReferenceConverter.java
//
// (c) 2003 Jonathan Stott
//
// 0.3 - 06 Mar 2005
//  - Updated Javadoc
//  - Fixed incorrect sign for longitudes (longitudes west of the Greenwich
//    Meridian should be negative)
//  - Added exceptions to constructors
//  - Added new constructor to allow the specification of north-south/east-west
//    latitudes and longitudes
// 0.2 - 02 Mar 2004
//  - Added exceptions to setLongitude() and setLatitude()
// 0.1 - 11 Nov 2003
//  - First version
//-----------------------------------------------------------------------------

package uk.me.jstott.coordconv;

/**
 * An object to represent a latitude and longitude pair. Latitudes and
 * longitudes are used to represent a point on a spherical surface, for example,
 * a point on the surface of Earth. Latitudes and longitudes are measured in
 * degrees, minutes and seconds.
 * 
 * For more information on using this class, look at
 * http://www.jstott.me.uk/jsuntimes/
 * 
 * @author Jonathan Stott
 * @version 0.3
 * @since 0.1
 */
public class LatitudeLongitude {
    private double latitude;

    private double longitude;

    public static final int NORTH = 1;

    public static final int SOUTH = -1;

    public static final int EAST = 1;

    public static final int WEST = -1;

    /**
     * Construct a latitude and longitude pair. Negative values of lat and lng
     * are Southerly latitudes and easterly longitudes respectively.
     * 
     * @param lat
     *            the latitude
     * @param lng
     *            the longitude
     * @since 0.1
     */
    public LatitudeLongitude(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

    /**
     * Construct a latitude and longitude pair.
     * 
     * @param northSouth
     *            one of LatitudeLongitude.NORTH and LatitudeLongitude.SOUTH to
     *            represent whether the given latitude is north or south of the
     *            equator respectively
     * @param degreesLat
     *            degrees of latitude
     * @param minutesLat
     *            minutes of latitude
     * @param secondsLat
     *            seconds of latitude
     * @param eastWest
     *            one of LatitudeLongitude.East and LatitudeLongitude.WEST to
     *            represent whether the given longitude is east or west of the
     *            Greenwich Meridian respectively
     * @param degreesLong
     *            degrees of longitude
     * @param minutesLong
     *            minutes of longitude
     * @param secondsLong
     *            seconds of longitude
     * @since 0.3
     */
    public LatitudeLongitude(int northSouth, int degreesLat, int minutesLat,
            double secondsLat, int eastWest, int degreesLong, int minutesLong,
            double secondsLong) {

        if (northSouth != NORTH && northSouth != SOUTH) {
            throw new IllegalArgumentException("northSouth must be one of "
                    + "LatitudeLongitude.NORTH or LatitudeLongitde.SOUTH");
        }

        if (eastWest != EAST && eastWest != WEST) {
            throw new IllegalArgumentException("eastWest must be one of "
                    + "LatitudeLongitude.EAST or LatitudeLongitde.WEST");
        }

        if (degreesLat < 0 || degreesLat > 90) {
            throw new IllegalArgumentException(
                    "degreesLat must be a value from " + " 0 through 90");
        }

        if (minutesLat < 0 || minutesLat >= 60) {
            throw new IllegalArgumentException(
                    "minutesLat must be a value from " + " 0 through 59");
        }

        if (secondsLat < 0.0 || secondsLat >= 60.0) {
            throw new IllegalArgumentException(
                    "secondsLat must be a value from "
                            + "0.0 up to, but not including 60.0");
        }

        if (degreesLong < 0 || degreesLong > 180) {
            throw new IllegalArgumentException(
                    "degreesLong must be a value from " + " 0 through 180");
        }

        if (minutesLong < 0 || minutesLong >= 60) {
            throw new IllegalArgumentException(
                    "minutesLong must be a value from " + " 0 through 59");
        }

        if (secondsLong < 0.0 || secondsLong >= 60.0) {
            throw new IllegalArgumentException(
                    "secondsLong must be a value from "
                            + "0.0 up to, but not including 60.0");
        }

        latitude = northSouth * degreesLat
                + ((minutesLat + (secondsLat / 60.0)) / 60.0);
        longitude = eastWest * degreesLong
                + ((minutesLong + (secondsLong / 60.0)) / 60.0);
    }

    /**
     * Construct a latitude and longitude pair. Negative values of degreesLat
     * represent latitudes south of the equator. Negative values of degreesLong
     * represent longitudes west of the Greenwich Meridian.
     * 
     * @param degreesLat
     *            degrees of latitude
     * @param minutesLat
     *            minutes of latitude
     * @param secondsLat
     *            seconds of latitude
     * @param degreesLong
     *            degrees of longitude
     * @param minutesLong
     *            minutes of longitude
     * @param secondsLong
     *            seconds of longitude
     * @since 0.1
     */
    public LatitudeLongitude(int degreesLat, int minutesLat, double secondsLat,
            int degreesLong, int minutesLong, double secondsLong) {

        if (degreesLat < 0 || degreesLat > 90) {
            throw new IllegalArgumentException(
                    "degreesLat must be a value from " + " 0 through 90");
        }

        if (minutesLat < 0 || minutesLat >= 60) {
            throw new IllegalArgumentException(
                    "minutesLat must be a value from " + " 0 through 59");
        }

        if (secondsLat < 0.0 || secondsLat >= 60.0) {
            throw new IllegalArgumentException(
                    "secondsLat must be a value from "
                            + "0.0 up to, but not including 60.0");
        }

        if (degreesLong < 0 || degreesLong > 180) {
            throw new IllegalArgumentException(
                    "degreesLong must be a value from " + " 0 through 180");
        }

        if (minutesLong < 0 || minutesLong >= 60) {
            throw new IllegalArgumentException(
                    "minutesLong must be a value from " + " 0 through 59");
        }

        if (secondsLong < 0.0 || secondsLong >= 60.0) {
            throw new IllegalArgumentException(
                    "secondsLong must be a value from "
                            + "0.0 up to, but not including 60.0");
        }

        latitude = degreesLat + ((minutesLat + (secondsLat / 60.0)) / 60.0);
        longitude = degreesLong
                + ((minutesLong + (secondsLong / 60.0)) / 60.0);
    }

    /**
     * Get the latitude
     * 
     * @return the latitude
     * @since 0.1
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Get the longitude
     * 
     * @return the longitude
     * @since 0.1
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set the latitude
     * 
     * @param latitude
     *            the new value of the latitude
     * @since 0.1
     */
    public void setLatitude(double latitude) {
        if (latitude > 90 || latitude < -90) {
            throw new IllegalArgumentException(
                    "latitude must be between -90 and " + "90");
        }

        this.latitude = latitude;
    }

    /**
     * Set the longitude
     * 
     * @param longitude
     *            the new value of the longitude
     * @since 0.1
     */
    public void setLongitude(double longitude) {
        if (longitude > 180 || longitude < -180) {
            throw new IllegalArgumentException(
                    "longitude must be between -180 and " + "180");
        }

        this.longitude = longitude;
    }

    /**
     * Get a string representation of the latitude and longitude in the form
     * 52�39'27.2531"N 1�43'4.5177"E
     * 
     * @return the latitude and longitude as a string.
     * @since 0.1
     */
    public String toString() {
        String lat = "";
        int latDeg = (int) Math.floor(Math.abs(getLatitude()));
        int latMin = (int) Math.floor((Math.abs(getLatitude()) - latDeg) * 60);
        double latSec = (((Math.abs(getLatitude()) - latDeg) * 60) - latMin) * 60;
        lat = latDeg + "�" + latMin + "'" + latSec + "\"";
        if (getLatitude() < 0) {
            lat = lat + "S";
        } else {
            lat = lat + "N";
        }

        String lng = "";
        int lngDeg = (int) Math.floor(Math.abs(getLongitude()));
        int lngMin = (int) Math.floor((Math.abs(getLongitude()) - lngDeg) * 60);
        double lngSec = (((Math.abs(getLongitude()) - lngDeg) * 60) - lngMin) * 60;
        lng = lngDeg + "�" + lngMin + "'" + lngSec + "\"";
        if (getLongitude() < 0) {
            lng = lng + "W";
        } else {
            lng = lng + "E";
        }

        return lat + " " + lng;
    }

}

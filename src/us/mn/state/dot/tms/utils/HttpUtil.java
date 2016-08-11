/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015 California Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * Utility methods to help in HTTP interactions.
 *
 * @author Dan Rossiter
 */
public final class HttpUtil {

    /** Default timeout for direct URL Connections */
    static public final int TIMEOUT_DIRECT = 5 * 1000;

    /** Cache of retrieved content types */
    private static final HashMap<URL, String> cachedContentTypes
            = new HashMap<URL, String>();

    /**
     * Gets the content type reported for the given URL.
     * Results are cached throughout runtime.
     */
    public static String getContentType(String uri) {
        String contentType = null;
        try {
            contentType = getContentType(new URL(uri));
        } catch (Exception e) { }
        return contentType;
    }

    /**
     * Gets the content type reported for the given URL.
     * Results are cached throughout runtime.
     */
    public static String getContentType(URL uri) {
        if (!cachedContentTypes.containsKey(uri)) {
            String contentType = null;
            try {
                HttpURLConnection c = (HttpURLConnection)  uri.openConnection();
                HttpURLConnection.setFollowRedirects(true);
                c.setConnectTimeout(TIMEOUT_DIRECT);
                c.setReadTimeout(TIMEOUT_DIRECT);
                c.setRequestMethod("HEAD");
                c.connect();
                contentType = c.getContentType();
            } catch (Exception e) { }
            cachedContentTypes.put(uri, contentType);
        }
        return cachedContentTypes.get(uri);
    }
}

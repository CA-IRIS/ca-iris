/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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

import java.lang.IllegalArgumentException;
import java.net.URI;
import java.util.regex.Pattern;


/**
 * Misc. URI/URL utilities
 *
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class URIUtils {

	/** Source: http://jmrware.com/articles/2009/uri_regexp/URI_regex.html */
	private static final Pattern valid_uri_regex = Pattern.compile(
		"^" +
		"# RFC-3986 URI component: URI-reference" +
		"(?:                                                               # (" +
		"  [A-Z][A-Z0-9+\\-.]* :                                      # URI" +
		"  (?: //" +
		"    (?: (?:[A-Z0-9\\-._~!$&'()*+,;=:]|%[0-9A-F]{2})* @)?" +
		"    (?:" +
		"      \\[" +
		"      (?:" +
		"        (?:" +
		"          (?:                                                    (?:[0-9A-F]{1,4}:){6}" +
		"          |                                                   :: (?:[0-9A-F]{1,4}:){5}" +
		"          | (?:                            [0-9A-F]{1,4})? :: (?:[0-9A-F]{1,4}:){4}" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,1} [0-9A-F]{1,4})? :: (?:[0-9A-F]{1,4}:){3}" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,2} [0-9A-F]{1,4})? :: (?:[0-9A-F]{1,4}:){2}" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,3} [0-9A-F]{1,4})? ::    [0-9A-F]{1,4}:" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,4} [0-9A-F]{1,4})? ::" +
		"          ) (?:" +
		"              [0-9A-F]{1,4} : [0-9A-F]{1,4}" +
		"            | (?: (?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?) \\.){3}" +
		"                  (?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
		"            )" +
		"        |   (?: (?:[0-9A-F]{1,4}:){0,5} [0-9A-F]{1,4})? ::    [0-9A-F]{1,4}" +
		"        |   (?: (?:[0-9A-F]{1,4}:){0,6} [0-9A-F]{1,4})? ::" +
		"        )" +
		"      | V[0-9A-F]+\\.[A-Z0-9\\-._~!$&'()*+,;=:]+" +
		"      )" +
		"      \\]" +
		"    | (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
		"         (?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
		"    | (?:[A-Z0-9\\-._~!$&'()*+,;=]|%[0-9A-F]{2})*" +
		"    )" +
		"    (?: : [0-9]* )?" +
		"    (?:/ (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})* )*" +
		"  | /" +
		"    (?:    (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})+" +
		"      (?:/ (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})* )*" +
		"    )?" +
		"  |        (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})+" +
		"      (?:/ (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})* )*" +
		"  |" +
		"  )" +
		"  (?:\\? (?:[A-Z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-F]{2})* )?" +
		"  (?:\\# (?:[A-Z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-F]{2})* )?" +
		"| (?: //                                                          # / relative-ref" +
		"    (?: (?:[A-Z0-9\\-._~!$&'()*+,;=:]|%[0-9A-F]{2})* @)?" +
		"    (?:" +
		"      \\[" +
		"      (?:" +
		"        (?:" +
		"          (?:                                                    (?:[0-9A-F]{1,4}:){6}" +
		"          |                                                   :: (?:[0-9A-F]{1,4}:){5}" +
		"          | (?:                            [0-9A-F]{1,4})? :: (?:[0-9A-F]{1,4}:){4}" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,1} [0-9A-F]{1,4})? :: (?:[0-9A-F]{1,4}:){3}" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,2} [0-9A-F]{1,4})? :: (?:[0-9A-F]{1,4}:){2}" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,3} [0-9A-F]{1,4})? ::    [0-9A-F]{1,4}:" +
		"          | (?: (?:[0-9A-F]{1,4}:){0,4} [0-9A-F]{1,4})? ::" +
		"          ) (?:" +
		"              [0-9A-F]{1,4} : [0-9A-F]{1,4}" +
		"            | (?: (?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?) \\.){3}" +
		"                  (?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
		"            )" +
		"        |   (?: (?:[0-9A-F]{1,4}:){0,5} [0-9A-F]{1,4})? ::    [0-9A-F]{1,4}" +
		"        |   (?: (?:[0-9A-F]{1,4}:){0,6} [0-9A-F]{1,4})? ::" +
		"        )" +
		"      | V[0-9A-F]+\\.[A-Z0-9\\-._~!$&'()*+,;=:]+" +
		"      )" +
		"      \\]" +
		"    | (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
		"         (?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
		"    | (?:[A-Z0-9\\-._~!$&'()*+,;=]|%[0-9A-F]{2})*" +
		"    )" +
		"    (?: : [0-9]* )?" +
		"    (?:/ (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})* )*" +
		"  | /" +
		"    (?:    (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})+" +
		"      (?:/ (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})* )*" +
		"    )?" +
		"  |        (?:[A-Z0-9\\-._~!$&'()*+,;=@] |%[0-9A-F]{2})+" +
		"      (?:/ (?:[A-Z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-F]{2})* )*" +
		"  |" +
		"  )" +
		"  (?:\\? (?:[A-Z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-F]{2})* )?" +
		"  (?:\\# (?:[A-Z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-F]{2})* )?" +
		")                                                                       # )" +
		"$",
		Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);

	/**
	 * Check that the scheme of a given URI matches the given String.
	 * @param uri a String representation of the URI
	 * @param scheme the scheme to match against
	 * @return Returns false if either argument is null, false if uri
	 *         violates RFC 2396 or has an undefined scheme,
	 *         otherwise returns whether uri string-matches scheme.
	 */
	static public boolean checkScheme(String uri, String scheme) {
		if ( (uri == null) || (scheme == null) )
			return false;
		URI uriObj;
		try {
			uriObj = URI.create(uri);
		}
		catch (IllegalArgumentException e) {
			/* RFC 2396 violation */
			return false;
		}
		String uriScheme = uriObj.getScheme();
		if (uriScheme == null)
			/* scheme undefined */
			return false;
		return (uriScheme.equals(scheme));
	}

	/**
	 * Tests whether the given URI matches RFC-3986 specs.
	 * @param uri The string to be tested.
	 * @return Whether the given string is a valid URI.
	 */
	static public boolean isValidUri(String uri) {
		return uri != null && valid_uri_regex.matcher(uri).matches();
	}

}


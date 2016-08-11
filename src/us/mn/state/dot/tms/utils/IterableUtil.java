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

import java.util.Iterator;

/**
 * Utility methods to help in Iterable interactions.
 *
 * @author Dan Rossiter
 */
public final class IterableUtil {

    /** Compares two sequences of values and returns true if sequence members are equal and in the same order */
    public static <T> boolean sequenceEqual(Iterable<T> one, Iterable<T> two) {
        Iterator<T> iter1 = one.iterator();
        Iterator<T> iter2 = two.iterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            if (iter1.next() != iter2.next()) {
                return false;
            }
        }
        return (!iter1.hasNext() && !iter2.hasNext());
    }
}

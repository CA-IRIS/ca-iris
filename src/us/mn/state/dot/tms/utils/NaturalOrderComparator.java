/*
 NaturalOrderComparator.java -- Perform 'natural order' comparisons of strings in Java.
 Copyright (C) 2003 by Pierre-Luc Paour <natorder@paour.com>

 Based on the C version by Martin Pool, of which this is more or less a straight conversion.
 Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

 This software is provided 'as-is', without any express or implied
 warranty.  In no event will the authors be held liable for any damages
 arising from the use of this software.

 Permission is granted to anyone to use this software for any purpose,
 including commercial applications, and to alter it and redistribute it
 freely, subject to the following restrictions:

 1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
 2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
 3. This notice may not be removed or altered from any source distribution.
 */

package us.mn.state.dot.tms.utils;

import java.util.Comparator;

public class NaturalOrderComparator<T> implements Comparator<T>
{
	int compareRight(String a, String b)
	{
		int bias = 0;
		int ia = 0;
		int ib = 0;

		// The longest run of digits wins. That aside, the greatest
		// value wins, but we can't know that it will until we've scanned
		// both numbers to know that they have the same magnitude, so we
		// remember it in BIAS.
		for (;; ia++, ib++)
		{
			char ca = charAt(a, ia);
			char cb = charAt(b, ib);

			if (!Character.isDigit(ca) && !Character.isDigit(cb))
			{
				return bias;
			}
			else if (!Character.isDigit(ca))
			{
				return -1;
			}
			else if (!Character.isDigit(cb))
			{
				return +1;
			}
			else if (ca < cb)
			{
				if (bias == 0)
				{
					bias = -1;
				}
			}
			else if (ca > cb)
			{
				if (bias == 0)
					bias = +1;
			}
			else if (ca == 0 && cb == 0)
			{
				return bias;
			}
		}
	}

	/**
	 * compare objects, but respect case-sensitivity
	 * over-ride if case-insensitive is needed
	 */
	public int compare(T o1, T o2) {
		return compare(o1, o2, true);
	}

	/**
	 * compare two objects.
	 *
	 * @param o1 first object
	 * @param o2 second object
	 * @param cs compare case-sensitive? (uppercase before lowercase values)
	 * @return
	 */
	public int compare(T o1, T o2, boolean cs) {
		String a = cs ? o1.toString() : o1.toString().toLowerCase();
		String b = cs ? o2.toString() : o2.toString().toLowerCase();

		int ia = 0, ib = 0;
		int nza = 0, nzb = 0;
		char ca, cb;
		int result;

		while (true)
		{
			// only count the number of zeroes leading the last number compared
			nza = nzb = 0;

			ca = charAt(a, ia);
			cb = charAt(b, ib);

			// skip over leading spaces or zeros
			while (Character.isSpaceChar(ca) || ca == '0')
			{
				if (ca == '0')
				{
					nza++;
				}
				else
				{
					// only count consecutive zeroes
					nza = 0;
				}

				ca = charAt(a, ++ia);
			}

			while (Character.isSpaceChar(cb) || cb == '0')
			{
				if (cb == '0')
				{
					nzb++;
				}
				else
				{
					// only count consecutive zeroes
					nzb = 0;
				}

				cb = charAt(b, ++ib);
			}

			// process run of digits
			if (Character.isDigit(ca) && Character.isDigit(cb))
			{
				if ((result = compareRight(a.substring(ia), b.substring(ib))) != 0)
				{
					return result;
				}
			}

			if (ca == 0 && cb == 0)
			{
				// The strings compare the same. Perhaps the caller
				// will want to call strcmp to break the tie.
				return nza - nzb;
			}

			if (ca < cb)
			{
				return -1;
			}
			else if (ca > cb)
			{
				return +1;
			}

			++ia;
			++ib;
		}
	}

	// added for use by ProxyComparator
	public static int compareStrings(String arg_a, String arg_b, boolean caseSensitive) {
		NaturalOrderComparator n = new NaturalOrderComparator();
		return n.compare(arg_a, arg_b, caseSensitive);
	}

	static char charAt(String s, int i)
	{
		if (i >= s.length())
		{
			return 0;
		}
		else
		{
			return s.charAt(i);
		}
	}

// ALTERED from original source.
// Commented out the un-needed static main entry point
//	public static void main(String[] args)
//	{
//		String[] strings = new String[] { "1-2", "1-02", "1-20", "10-20", "fred", "jane", "pic01",
//			"pic2", "pic02", "pic02a", "pic3", "pic4", "pic 4 else", "pic 5", "pic05", "pic 5",
//			"pic 5 something", "pic 6", "pic   7", "pic100", "pic100a", "pic120", "pic121",
//			"pic02000", "tom", "x2-g8", "x2-y7", "x2-y08", "x8-y8" };
//
//		List orig = Arrays.asList(strings);
//
//		System.out.println("Original: " + orig);
//
//		List scrambled = Arrays.asList(strings);
//		Collections.shuffle(scrambled);
//
//		System.out.println("Scrambled: " + scrambled);
//
//		Collections.sort(scrambled, new NaturalOrderComparator());
//
//		System.out.println("Sorted: " + scrambled);
//	}
}
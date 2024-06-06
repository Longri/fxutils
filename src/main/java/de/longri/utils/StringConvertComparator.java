/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of fxutils.
 *
 * fxutils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * fxutils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with fxutils. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public abstract class StringConvertComparator implements Comparator<String> {

    public StringConvertComparator(Type type) {
        TYPE = type;
    }

    public enum Type {
        INT, DATE
    }

    private final Type TYPE;
    private SimpleDateFormat dateFormater = new SimpleDateFormat("dd.MM.yyyy ss:mm:ss");

    public void setDateFormater(String s) {
        dateFormater = new SimpleDateFormat(s);
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * <p>
     * The implementor must ensure that {@link Integer#signum
     * signum}{@code (compare(x, y)) == -signum(compare(y, x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * compare(x, y)} must throw an exception if and only if {@code
     * compare(y, x)} throws an exception.)<p>
     * <p>
     * The implementor must also ensure that the relation is transitive:
     * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
     * {@code compare(x, z)>0}.<p>
     * <p>
     * Finally, the implementor must ensure that {@code compare(x,
     * y)==0} implies that {@code signum(compare(x,
     * z))==signum(compare(y, z))} for all {@code z}.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     * @throws NullPointerException if an argument is null and this
     *                              comparator does not permit null arguments
     * @throws ClassCastException   if the arguments' types prevent them from
     *                              being compared by this comparator.
     * @apiNote It is generally the case, but <i>not</i> strictly required that
     * {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     */
    @Override
    public int compare(String o1, String o2) {

        switch (TYPE) {

            case INT -> {
                return compareInt(o1, o2);
            }
            case DATE -> {
                return compareDate(o1, o2);
            }
        }

        return 0;
    }

    public abstract String processString(String s);

    private int compareInt(String o1, String o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        Integer i1 = null;
        try {
            i1 = Integer.valueOf(processString(o1));
        } catch (NumberFormatException ignored) {
        }
        Integer i2 = null;
        try {
            i2 = Integer.valueOf(processString(o2));
        } catch (NumberFormatException ignored) {
        }

        if (i1 == null && i2 == null) return o1.compareTo(o2);
        if (i1 == null) return -1;
        if (i2 == null) return 1;

        return i1 - i2;
    }

    private int compareDate(String o1, String o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        Long i1 = null;
        try {
            i1 = dateFormater.parse(processString(o1)).getTime();
        } catch (ParseException ignored) {
        }
        Long i2 = null;
        try {
            i2 = dateFormater.parse(processString(o2)).getTime();
        } catch (ParseException ignored) {
        }

        if (i1 == null && i2 == null) return o1.compareTo(o2);
        if (i1 == null) return -1;
        if (i2 == null) return 1;


//        if (i1 % 10000 == 0 && i2 % 10000 == 0) {
//            i1 /= 10000;
//            i2 /= 10000;
//            return (int) (i1 - i2);
//        } else {
            if (i1 == i2) return 0;
            if (i1 > i2) return 1;
            if (i1 < i2) return -1;
//        }
        return 0;
    }
}

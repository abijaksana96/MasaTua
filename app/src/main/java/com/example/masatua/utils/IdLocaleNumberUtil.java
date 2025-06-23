package com.example.masatua.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class IdLocaleNumberUtil {
    /**
     * Parsing string angka format Indonesia (koma=desimal, titik=grouping) ke double.
     * @param s input string, misal "1.234,54"
     * @return double hasil parse, misal 1234.54
     */
    public static double parseIdNumber(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        try {
            Number n = nf.parse(s.trim());
            return n.doubleValue();
        } catch (ParseException e) {
            return 0;
        }
    }
}
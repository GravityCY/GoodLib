package me.gravityio.goodlib.dev;

public class AnsiCodes {
    // ANSI escape code for resetting colors

    private static final String S = "\u001B[";
    public static final String RST = S+"0m";

    /** Black */
    public static final String BL = S+"30m";
    /** Full White */
    public static final String W = S+"97";
    /** Weak White */
    public static final String WW = S+"37m";
    /** Gray */
    public static final String G = S+"90";
    /** Red */
    public static final String R = S+"31m";
    /** Green */
    public static final String GR = S+"32m";
    /** Yellow */
    public static final String Y = S+"33m";
    /** Blue */
    public static final String B = S+"34m";
    /** Magenta */
    public static final String M = S+"35m";
    /** Cyan */
    public static final String C = S+"36m";


    /** Bright Red */
    public static final String BR = S+"91m";
    /** Bright Green */
    public static final String BG = S+"92m";
    /** Bright Yellow */
    public static final String BY = S+"93m";
    /** Bright Blue */
    public static final String BB = S+"94m";
    /** Bright Magenta */
    public static final String BM = S+"95m";
    /** Bright Cyan */
    public static final String BC = S+"96m";

}

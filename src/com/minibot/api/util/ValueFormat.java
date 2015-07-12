package com.minibot.api.util;

/**
 * @author Jacob Doiron
 */
public class ValueFormat {

    public static final byte COMMAS = 0x1;
    public static final byte THOUSANDS = 0x40;

    private static final char[] PREFIXES = {'K', 'M', 'B', 'T'};
    private static final char DASH = '-';
    private static final char COMMA = ',';
    private static final char ZERO = '0';
    private static final char PERIOD = '.';

    public static int PRECISION(int precision) {
        return precision << 2;
    }

    public static String toString(int settings) {
        return String.format("Prefix: %b, Precision: %b, Commas: %b", settings >> 6 > PREFIXES.length ?
                PREFIXES.length : settings >> 6, (settings >> 2) & 0xF, (settings & COMMAS) == COMMAS);
    }

    public static String format(long value, int settings) {
        StringBuilder sb = new StringBuilder(32);
        sb.append(value);
        char[] data = sb.toString().toCharArray();
        boolean commas = (settings & COMMAS) == COMMAS;
        int precision = 0;
        int prefix = 0;
        if (settings >= 0x40) {
            prefix = settings >> 6;
            if (prefix > PREFIXES.length) {
                prefix = PREFIXES.length;
            }
        }
        if (settings > COMMAS) {
            precision = (settings >> 2) & 0xF;
        }
        sb.setLength(0);
        int negative = 0;
        if (data[0] == DASH) {
            negative = 1;
        }
        int length = data.length - negative;
        if (prefix * 3 >= length) {
            prefix = (int) (length * 0.334);
            if (prefix * 3 == length && precision == 0) {
                --prefix;
            }
        }
        int end = length - (prefix * 3);
        int start = (length % 3);
        if (start == 0) {
            start = 3;
        }
        start += negative;
        if (end > 0 && negative == 1) {
            sb.append(DASH);
        }
        int max = end + negative;
        for (int i = negative; i < max; i++) {
            if (i == start && i + 2 < max && commas) {
                start += 3;
                sb.append(COMMA);
            }
            sb.append(data[i]);
        }
        if (prefix > 0) {
            if (end == 0) {
                if (negative == 1 && precision > 0) {
                    sb.append(DASH);
                }
                sb.append(ZERO);
            }
            max = precision + end + negative;
            if (max > data.length) {
                max = data.length;
            }
            end += negative;
            while (max > end) {
                if (data[max - 1] == ZERO) {
                    --max;
                    continue;
                }
                break;
            }
            if ((max - end) != 0) {
                sb.append(PERIOD);
            }
            for (int i = end; i < max; i++) {
                sb.append(data[i]);
            }
            sb.append(PREFIXES[prefix - 1]);
        }
        return sb.toString();
    }
}
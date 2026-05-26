package com.project.hexagonal.shared.core.util;

public class MaskingUtil {

    private static final int DEFAULT_VISIBLE_START = 3;
    private static final int DEFAULT_VISIBLE_END = 2;
    private static final char DEFAULT_MASK_CHAR = '*';
    private static final int MINIMUM_LENGTH_FOR_MASKING = 5;

    private MaskingUtil() {
        throw new UnsupportedOperationException("MaskingUtil Utility Class cannot be instantiated !");
    }

    public static String mask(String value) {
        return mask(value, DEFAULT_VISIBLE_START, DEFAULT_VISIBLE_END);
    }

    public static String mask(String value, int visibleStart, int visibleEnd) {
        return maskWithChar(value, visibleStart, visibleEnd, DEFAULT_MASK_CHAR);
    }

    public static String maskWithChar(String value, int visibleStart, int visibleEnd, char maskChar) {
        if (value == null) {
            return null;
        }

        if (value.isEmpty()) {
            return value;
        }

        int length = value.length();
        int totalVisible = visibleStart + visibleEnd;

        // If string is too short, mask everything
        if (length < MINIMUM_LENGTH_FOR_MASKING || length <= totalVisible) {
            return String.valueOf(maskChar).repeat(length);
        }

        int maskedLength = length - totalVisible;
        return value.substring(0, visibleStart) +
                String.valueOf(maskChar).repeat(maskedLength) +
                value.substring(length - visibleEnd);
    }
}

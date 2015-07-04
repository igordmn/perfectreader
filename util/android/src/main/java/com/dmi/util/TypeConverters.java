package com.dmi.util;

public abstract class TypeConverters {
    @SuppressWarnings("unchecked")
    public static <T> T stringToType(String value, Class<?> type) throws ParseException {
        try {
            if (value == null || String.class.equals(type) || Void.TYPE.equals(type)) {
                return (T) value;
            } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
                return (T) Long.valueOf(value);
            } else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
                return (T) Integer.valueOf(value);
            } else if (Short.class.equals(type) || Short.TYPE.equals(type)) {
                return (T) Short.valueOf(value);
            } else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
                return (T) Double.valueOf(value);
            } else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
                return (T) Float.valueOf(value);
            } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
                return (T) Boolean.valueOf(value);
            } else if (Enum.class.isAssignableFrom(type)) {
                return (T) Enum.valueOf((Class) type, value);
            }
        } catch (Exception e) {
            throw new ParseException(e);
        }
        throw new UnsupportedOperationException();
    }

    public static String typeToString(Object value, Class<?> type) {
        try {
            if (value == null || String.class.equals(type) || Void.TYPE.equals(type)) {
                return (String) value;
            } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
                return String.valueOf((long) value);
            } else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
                return String.valueOf((int) value);
            } else if (Short.class.equals(type) || Short.TYPE.equals(type)) {
                return String.valueOf((short) value);
            } else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
                return String.valueOf((double) value);
            } else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
                return String.valueOf((float) value);
            } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
                return String.valueOf((boolean) value);
            } else if (Enum.class.isAssignableFrom(type)) {
                return ((Enum) value).name();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        throw new UnsupportedOperationException();
    }

    public static class ParseException extends Exception {
        public ParseException(Exception cause) {
            super(cause);
        }
    }
}

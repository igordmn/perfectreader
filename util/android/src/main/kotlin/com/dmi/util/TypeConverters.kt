package com.dmi.util

object TypeConverters {
    @Suppress("UNCHECKED_CAST")
    @Throws(ParseException::class)
    fun <T> stringToType(value: String?, type: Class<*>): T {
        try {
            if (value == null || String::class.java == type || Void.TYPE == type) {
                return value as T
            } else if (Long::class.java == type || java.lang.Long.TYPE == type) {
                return java.lang.Long.valueOf(value) as T
            } else if (Int::class.java == type || Integer.TYPE == type) {
                return Integer.valueOf(value) as T
            } else if (Short::class.java == type || java.lang.Short.TYPE == type) {
                return java.lang.Short.valueOf(value) as T
            } else if (Double::class.java == type || java.lang.Double.TYPE == type) {
                return java.lang.Double.valueOf(value) as T
            } else if (Float::class.java == type || java.lang.Float.TYPE == type) {
                return java.lang.Float.valueOf(value) as T
            } else if (Boolean::class.java == type || java.lang.Boolean.TYPE == type) {
                return java.lang.Boolean.valueOf(value) as T
            } else if (Enum::class.java.isAssignableFrom(type)) {
                throw UnsupportedOperationException("not implemented. todo: implement")
            }
        } catch (e: Exception) {
            throw ParseException(e)
        }

        throw UnsupportedOperationException()
    }

    fun typeToString(value: Any?, type: Class<*>): String {
        try {
            if (value == null || String::class.java == type || Void.TYPE == type) {
                return value as String
            } else if (Long::class.java == type || java.lang.Long.TYPE == type) {
                return (value as Long).toString()
            } else if (Int::class.java == type || Integer.TYPE == type) {
                return (value as Int).toString()
            } else if (Short::class.java == type || java.lang.Short.TYPE == type) {
                return (value as Short).toString()
            } else if (Double::class.java == type || java.lang.Double.TYPE == type) {
                return (value as Double).toString()
            } else if (Float::class.java == type || java.lang.Float.TYPE == type) {
                return (value as Float).toString()
            } else if (Boolean::class.java == type || java.lang.Boolean.TYPE == type) {
                return (value as Boolean).toString()
            } else if (Enum::class.java.isAssignableFrom(type)) {
                throw UnsupportedOperationException("not implemented. todo: implement")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }

        throw UnsupportedOperationException()
    }

    class ParseException(cause: Exception) : Exception(cause)
}

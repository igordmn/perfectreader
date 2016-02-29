package com.dmi.util.annotation


/**
 * Классы, помеченные этой аннотацией, нельзя сохранять в памяти, т.к. они будут изменяться для использования в другом месте.

 * Т.е. взяли значение, записали туда данные, считали эти значения чуть позже в другой функции.

 * Также эти значения нельзя использовать одновременно в двух потоках.

 * Обычно используется для того, чтобы не создавать много маленьких однотипных объектов.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Reusable

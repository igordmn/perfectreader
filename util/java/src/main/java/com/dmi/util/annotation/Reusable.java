package com.dmi.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Классы, помеченные этой аннотацией, нельзя сохранять в памяти, т.к. они будут изменяться для использования в другом месте.
 *
 * Т.е. взяли значение, записали туда данные, считали эти значения чуть позже в другой функции.
 *
 * Также эти значения нельзя использовать одновременно в двух потоках.
 *
 * Обычно используется для того, чтобы не создавать много маленьких однотипных объектов.
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Reusable {
}

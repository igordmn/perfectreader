package com.dmi.util.aspect

import com.dmi.util.debug.measureTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect

@Aspect
class MeasureTimeAspect {
    @Around("execution(* *(..)) && @annotation(com.dmi.util.debug.MeasureTime)")
    fun around(point: ProceedingJoinPoint): Any? {
        val signature = point.signature
        val clsName = signature.declaringType.simpleName
        val methodName = signature.name
        return measureTime("$clsName.$methodName") {
            point.proceed()
        }
    }
}
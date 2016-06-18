package com.dmi.util.debug

/**
 * Необходим для мониторинга утечек памяти (гуглить LeakCanary). Создан интерфейс для common-модуля, независимый от android классов
 */
interface RefWatcher {
    fun watch(watchedReference: Any)
}

object DisabledRefWatcher : RefWatcher {
    override fun watch(watchedReference: Any) = Unit
}
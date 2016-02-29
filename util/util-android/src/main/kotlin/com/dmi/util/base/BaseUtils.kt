package com.dmi.util.base

import me.tatarka.simplefragment.SimpleFragmentManagerProvider

internal object BaseUtils {
    fun handleAttachedFragments(managerProvider: SimpleFragmentManagerProvider,
                                handle: (BaseFragment) -> Boolean): Boolean {
        val fragments = managerProvider.simpleFragmentManager.attachedFragments
        val iterator = fragments.listIterator(fragments.size)
        while (iterator.hasPrevious()) {
            val fragment = iterator.previous()
            if (fragment is BaseFragment) {
                if (handle(fragment)) {
                    return true
                }
            }
        }
        return false
    }
}

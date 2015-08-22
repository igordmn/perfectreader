package com.dmi.util.base;

import java.util.List;
import java.util.ListIterator;

import java8.util.function.Predicate;
import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentManagerProvider;

abstract class BaseUtils {
    public static boolean handleAttachedFragments(SimpleFragmentManagerProvider managerProvider,
                                                  Predicate<BaseFragment> handler) {
        List<SimpleFragment> fragments = managerProvider.getSimpleFragmentManager().getAttachedFragments();
        ListIterator<SimpleFragment> iterator = fragments.listIterator(fragments.size());
        while (iterator.hasPrevious()) {
            SimpleFragment fragment = iterator.previous();
            if (fragment instanceof BaseFragment) {
                if (handler.test((BaseFragment) fragment)) {
                    return true;
                }
            }
        }
        return false;
    }
}

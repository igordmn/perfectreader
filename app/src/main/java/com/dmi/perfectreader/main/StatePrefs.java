package com.dmi.perfectreader.main;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface StatePrefs {
    String bookFilePath();

    long bookPosition();
}

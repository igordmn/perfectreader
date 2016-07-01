package com.dmi.util.android.ext

import android.view.MenuItem

fun MenuItem.onClick(action: () -> Unit) = setOnMenuItemClickListener { action(); true }
package com.dmi.util.ext

import android.view.MenuItem

fun MenuItem.onClick(action: () -> Unit) = setOnMenuItemClickListener { action(); true }
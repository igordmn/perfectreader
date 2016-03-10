package com.dmi.util

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo

infix fun <T> T.shouldEquals(other: T) = assertThat(this, equalTo(other))

package com.rff.boingballdemo.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

internal fun LocalDateTime.toDateText(): String {
    val dd = day.toString().padStart(2, '0')
    val mm = month.number.toString().padStart(2, '0')
    return "$dd.$mm.$year"
}

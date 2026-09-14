package com.rff.boingballdemo.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

internal fun LocalDateTime.toDateText(): String {
    val dd = day.toString().padStart(2, '0')
    val mm = month.number.toString().padStart(2, '0')
    return "$dd.$mm.$year"
}

/** AmigaDOS `date` style, e.g. "Monday 14-Sep-26". */
internal fun LocalDateTime.toAmigaDateText(): String {
    val weekday = dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val month = month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val dd = day.toString().padStart(2, '0')
    val yy = (year % 100).toString().padStart(2, '0')
    return "$weekday $dd-$month-$yy"
}

/** AmigaDOS `date` style time, e.g. "10:42:31". */
internal fun LocalDateTime.toAmigaTimeText(): String {
    val hh = hour.toString().padStart(2, '0')
    val mm = minute.toString().padStart(2, '0')
    val ss = second.toString().padStart(2, '0')
    return "$hh:$mm:$ss"
}

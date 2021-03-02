package com.pubnub.demo.telemedicine.util.extension

import org.joda.time.DateTime

fun DateTime.isToday() =
    DateTime.now().isOnSameDay(this)

fun DateTime.isTomorrow() =
    DateTime.now().plusDays(1).isOnSameDay(this)

fun DateTime.isYesterday() =
    DateTime.now().minusDays(1).isOnSameDay(this)

fun DateTime.isOnSameDay(timeOnDayToCheck: DateTime) =
    timeOnDayToCheck.toLocalDate().toInterval(this.zone).contains(this)

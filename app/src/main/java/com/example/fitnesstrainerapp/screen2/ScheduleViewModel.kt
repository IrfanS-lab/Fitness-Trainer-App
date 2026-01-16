package com.example.fitnesstrainerapp.screen2

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.YearMonth

class ScheduleViewModel : ViewModel() {
    private val _markedDaysPerMonth = mutableStateOf(mapOf(YearMonth.now() to setOf(15, 16, 17)))
    val markedDaysPerMonth: Map<YearMonth, Set<Int>> get() = _markedDaysPerMonth.value

    fun onDayClick(yearMonth: YearMonth, day: Int) {
        val currentDays = _markedDaysPerMonth.value[yearMonth] ?: emptySet()
        val newDays = if (currentDays.contains(day)) {
            currentDays - day
        } else {
            currentDays + day
        }
        _markedDaysPerMonth.value = _markedDaysPerMonth.value + (yearMonth to newDays)
    }
}

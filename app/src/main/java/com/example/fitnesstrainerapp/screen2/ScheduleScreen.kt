package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateBack: () -> Unit
) {
    // State for the currently displayed month, initialized to the current month
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    // Store marked days per month
    var markedDaysPerMonth by remember {
        mutableStateOf(mapOf(YearMonth.now() to setOf(15, 16, 17)))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // This would be a LazyColumn if you wanted to scroll through many months
            Column {
                CalendarView(
                    yearMonth = currentMonth,
                    onPrevMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                    // Pass the marked days for the currently displayed month
                    markedDays = markedDaysPerMonth[currentMonth] ?: emptySet(),
                    onDayClick = { day ->
                        val currentDays = markedDaysPerMonth[currentMonth] ?: emptySet()
                        val newDays = if (currentDays.contains(day)) {
                            currentDays - day
                        } else {
                            currentDays + day
                        }
                        // Update the map with the new set of marked days for the current month
                        markedDaysPerMonth = markedDaysPerMonth + (currentMonth to newDays)
                    }
                )

                // You could add more months here if needed
                // Spacer(modifier = Modifier.height(24.dp))
                // CalendarView( ... for next month ... )
            }
        }
    }
}

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    markedDays: Set<Int>,
    onDayClick: (Int) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek
    // Calculate the number of empty cells needed at the beginning of the month
    val emptyCells = (firstDayOfMonth.value % 7)

    Column {
        MonthHeader(
            yearMonth = yearMonth,
            onPrev = onPrevMonth,
            onNext = onNextMonth
        )
        Spacer(modifier = Modifier.height(16.dp))
        DayOfWeekHeader()
        Spacer(modifier = Modifier.height(8.dp))
        CalendarGrid(
            daysInMonth = daysInMonth,
            emptyCells = emptyCells,
            markedDays = markedDays,
            onDayClick = onDayClick
        )
    }
}

@Composable
fun MonthHeader(yearMonth: YearMonth, onPrev: () -> Unit, onNext: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
        }
        Text(
            text = yearMonth.format(formatter).uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
        }
    }
}

@Composable
fun DayOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val days = DayOfWeek.values()
        // Adjust for locale if needed, e.g., starting on Monday
        val adjustedDays = days.drop(0) + days.take(0) // Simple rotation logic if needed

        for (day in adjustedDays) {
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CalendarGrid(daysInMonth: Int, emptyCells: Int, markedDays: Set<Int>, onDayClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(((daysInMonth + emptyCells + 6) / 7 * 50).dp)
    ) {
        // 1. Empty cells
        items(emptyCells) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .border(0.5.dp, Color.LightGray)
            )
        }

        // 2. Day cells
        items(daysInMonth) { day ->
            val dateNumber = day + 1
            val isMarked = dateNumber in markedDays

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFFFEBEE))
                    .border(0.5.dp, Color.White)
                    .clickable { onDayClick(dateNumber) }
                // We removed contentAlignment here to use specific aligns below
            ) {
                // The Number: Always Black, Always Centered
                Text(
                    text = dateNumber.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                // The Star: Smaller, Bottom Right
                if (isMarked) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Marked",
                        tint = Color(0xFF6A1B9A),
                        modifier = Modifier
                            .size(16.dp) // Much smaller size
                            .align(Alignment.BottomEnd) // Puts it in bottom right corner
                            .padding(2.dp) // Slight padding so it doesn't touch the edge
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    FitnessTrainerAppTheme {
        ScheduleScreen(onNavigateBack = {})
    }
}

package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnesstrainerapp.data.ScheduleEntity
import com.example.fitnesstrainerapp.data.AppDatabase
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    // Load all marked days from database
    val allScheduledDays by db.scheduleDao().getAllScheduledDays().collectAsState(initial = emptyList())
    
    // Filter marked days for the current month view
    val markedDays = remember(allScheduledDays, currentMonth) {
        allScheduledDays
            .filter { it.date.startsWith(currentMonth.toString()) }
            .map { it.date.substringAfterLast("-").toInt() }
            .toSet()
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
            CalendarView(
                yearMonth = currentMonth,
                onPrevMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                markedDays = markedDays,
                onDayClick = { day ->
                    val dateString = String.format("%s-%02d", currentMonth.toString(), day)
                    scope.launch {
                        if (markedDays.contains(day)) {
                            db.scheduleDao().deleteSchedule(dateString)
                        } else {
                            db.scheduleDao().insertSchedule(ScheduleEntity(dateString, true))
                        }
                    }
                }
            )
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
    val emptyCells = (firstDayOfMonth.value % 7)

    Column {
        MonthHeader(yearMonth = yearMonth, onPrev = onPrevMonth, onNext = onNextMonth)
        Spacer(modifier = Modifier.height(16.dp))
        DayOfWeekHeader()
        Spacer(modifier = Modifier.height(8.dp))
        CalendarGrid(daysInMonth = daysInMonth, emptyCells = emptyCells, markedDays = markedDays, onDayClick = onDayClick)
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
        IconButton(onClick = onPrev) { Icon(Icons.Default.ChevronLeft, contentDescription = "Prev") }
        Text(text = yearMonth.format(formatter).uppercase(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        IconButton(onClick = onNext) { Icon(Icons.Default.ChevronRight, contentDescription = "Next") }
    }
}

@Composable
fun DayOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        DayOfWeek.values().forEach { day ->
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
        items(emptyCells) {
            Box(modifier = Modifier.size(50.dp).border(0.5.dp, Color.LightGray))
        }
        items(daysInMonth) { day ->
            val dateNumber = day + 1
            val isMarked = dateNumber in markedDays
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFFFEBEE))
                    .border(0.5.dp, Color.White)
                    .clickable { onDayClick(dateNumber) }
            ) {
                Text(text = dateNumber.toString(), modifier = Modifier.align(Alignment.Center))
                if (isMarked) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFF6A1B9A),
                        modifier = Modifier.size(16.dp).align(Alignment.BottomEnd).padding(2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    FitnessTrainerAppTheme { ScheduleScreen(onNavigateBack = {}) }
}

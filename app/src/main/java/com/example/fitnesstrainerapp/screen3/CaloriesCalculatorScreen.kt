package com.example.fitnesstrainerapp.screen3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

enum class Gender { FEMALE, MALE }

data class ActivityLevel(val name: String, val description: String, val multiplier: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaloriesCalculatorScreen(
    onNavigateBack: () -> Unit
) {
    val activityLevels = listOf(
        ActivityLevel("BMR", "Basal Metabolic Rate", 1.0),
        ActivityLevel("Sedentary", "little or no exercise", 1.2),
        ActivityLevel("Light", "exercise 1-3 times/week", 1.375),
        ActivityLevel("Moderate", "exercise 4-5 times/week", 1.55),
        ActivityLevel("Active", "daily exercise or intense exercise 3-4 times/week", 1.725),
        ActivityLevel("Very Active", "intense exercise 6-7 times/week", 1.9),
        ActivityLevel("Extra Active", "very intense exercise daily, or physical job", 2.0)
    )

    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(Gender.FEMALE) }
    var selectedActivity by remember { mutableStateOf(activityLevels[4]) } // Default to 'Active' as per your image

    var dailyCalories by remember { mutableStateOf<Double?>(null) }
    val showResultDialog = dailyCalories != null

    val isFormValid = age.isNotBlank() && height.isNotBlank() && weight.isNotBlank()

    fun calculateCalories() {
        val a = age.toDoubleOrNull() ?: 0.0
        val h = height.toDoubleOrNull() ?: 0.0
        val w = weight.toDoubleOrNull() ?: 0.0

        if (a > 0 && h > 0 && w > 0) {
            // 1. Calculate BMR
            val bmr = if (selectedGender == Gender.MALE) {
                (10.0 * w) + (6.25 * h) - (5.0 * a) + 5.0
            } else {
                (10.0 * w) + (6.25 * h) - (5.0 * a) - 161.0
            }

            // 2. Apply Activity Multiplier
            dailyCalories = bmr * selectedActivity.multiplier
        }
    }

    if (showResultDialog) {
        CaloriesResultDialog(
            calories = dailyCalories!!,
            onRecalculate = { dailyCalories = null }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("calories calculator", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            CalculatorHeader()
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("PERSONAL DATA", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                DataInputRow("AGE", age, { age = it }, "Enter your age")
                GenderSelector(selectedGender) { selectedGender = it }
                DataInputRow("HEIGHT (CM)", height, { height = it }, "Enter your height")
                DataInputRow("WEIGHT (KG)", weight, { weight = it }, "Enter your weight")

                ActivityDropdown(
                    selectedActivity = selectedActivity,
                    activityLevels = activityLevels,
                    onActivitySelected = { selectedActivity = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            FormButtons(
                isCalculateEnabled = isFormValid,
                onCalculateClick = { calculateCalories() },
                onClearClick = {
                    age = ""; height = ""; weight = ""; dailyCalories = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDropdown(
    selectedActivity: ActivityLevel,
    activityLevels: List<ActivityLevel>,
    onActivitySelected: (ActivityLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "ACTIVITY", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = "${selectedActivity.name}: ${selectedActivity.description}",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                activityLevels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text("${level.name}: ${level.description}", fontSize = 14.sp) },
                        onClick = {
                            onActivitySelected(level)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CaloriesResultDialog(calories: Double, onRecalculate: () -> Unit) {
    AlertDialog(
        onDismissRequest = onRecalculate,
        title = {
            Text("YOUR TOTAL CALORIES", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                text = "${"%.2f".format(calories)} kcal/day",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        confirmButton = {
            Button(
                onClick = onRecalculate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
            ) {
                Text("RECALCULATE", color = Color.White)
            }
        }
    )
}

@Composable
fun CalculatorHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(2.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your daily calories target", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Calculate to see your results", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun DataInputRow(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun GenderSelector(selectedGender: Gender, onGenderSelect: (Gender) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(100.dp))
        GenderRadioButton("FEMALE", selectedGender == Gender.FEMALE) { onGenderSelect(Gender.FEMALE) }
        Spacer(modifier = Modifier.width(24.dp))
        GenderRadioButton("MALE", selectedGender == Gender.MALE) { onGenderSelect(Gender.MALE) }
    }
}

@Composable
fun GenderRadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.selectable(selected = selected, onClick = onClick)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = text, modifier = Modifier.padding(start = 4.dp), fontSize = 14.sp)
    }
}

@Composable
fun FormButtons(isCalculateEnabled: Boolean, onCalculateClick: () -> Unit, onClearClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = onCalculateClick,
            enabled = isCalculateEnabled,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
        ) {
            Text("CALCULATE", color = Color.White)
        }
        Button(
            onClick = onClearClick,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1D1D1))
        ) {
            Text("CLEAR", color = Color.Black)
        }
    }
}

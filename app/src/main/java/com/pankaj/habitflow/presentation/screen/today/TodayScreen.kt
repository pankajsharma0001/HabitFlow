package com.pankaj.habitflow.presentation.screen.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.pankaj.habitflow.presentation.components.ConfettiCelebration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pankaj.habitflow.presentation.components.HabitCard
import com.pankaj.habitflow.presentation.components.ProgressRing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onAddHabitClick: () -> Unit,
    onHabitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val stats by viewModel.dayStats.collectAsState()

    var triggerConfetti by remember { mutableStateOf(false) }
    var previousCompletionRate by remember { mutableStateOf(stats.completionRate) }

    var showBudgetDialog by remember { mutableStateOf(false) }
    var selectedBudgetHabit by remember { mutableStateOf<com.pankaj.habitflow.domain.model.Habit?>(null) }
    var spendingAmount by remember { mutableStateOf("") }
    var spendingNote by remember { mutableStateOf("") }

    LaunchedEffect(stats) {
        if (stats.completionRate >= 1.0f && stats.totalHabits > 0 && previousCompletionRate < 1.0f) {
            triggerConfetti = true
        }
        previousCompletionRate = stats.completionRate
    }

    val dateText = when (selectedDate) {
        LocalDate.now() -> "Today"
        LocalDate.now().minusDays(1) -> "Yesterday"
        LocalDate.now().plusDays(1) -> "Tomorrow"
        else -> selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HabitFlow",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabitClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Habit"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Date Navigation Controller
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.moveToPreviousDay() }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Day",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (selectedDate != LocalDate.now()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.selectDate(LocalDate.now()) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = "Jump to Today",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    val isToday = selectedDate == LocalDate.now()
                    IconButton(
                        onClick = { if (!isToday) viewModel.moveToNextDay() },
                        enabled = !isToday
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Day",
                            tint = if (!isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                // Completion Progress Banner
                if (stats.totalHabits > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                val greeting = when {
                                    stats.completionRate >= 1.0f -> "Perfect Day! 🎉"
                                    stats.completionRate >= 0.75f -> "Awesome work! 🔥"
                                    stats.completionRate >= 0.5f -> "Over halfway there! 👍"
                                    stats.completionRate > 0f -> "Keep making progress!"
                                    else -> "Start your streak today!"
                                }
                                Text(
                                    text = greeting,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${stats.completedHabits} of ${stats.totalHabits} habits completed",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            ProgressRing(
                                progress = stats.completionRate,
                                size = 80.dp,
                                strokeWidth = 8.dp,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                progressColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Habits List
                if (habits.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = "No Habits",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outlineVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No active habits for this day",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap + to create a new habit and start tracking.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            if (selectedDate == LocalDate.now()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = onAddHabitClick) {
                                    Text("Create a Habit")
                                }
                            }
                        }
                    }
                } else {
                    val morningHabits = habits.filter { it.timeOfDay == "MORNING" }
                    val afternoonHabits = habits.filter { it.timeOfDay == "AFTERNOON" }
                    val eveningHabits = habits.filter { it.timeOfDay == "EVENING" }
                    val anytimeHabits = habits.filter { it.timeOfDay == "ANYTIME" }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val groups = listOf(
                            Triple("Morning 🌅", "MORNING", morningHabits),
                            Triple("Afternoon ☀️", "AFTERNOON", afternoonHabits),
                            Triple("Evening 🌙", "EVENING", eveningHabits),
                            Triple("Anytime 📅", "ANYTIME", anytimeHabits)
                        )

                        groups.forEach { (title, key, list) ->
                            if (list.isNotEmpty()) {
                                item(key = "header_$key") {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                items(
                                    items = list,
                                    key = { it.id }
                                ) { habit ->
                                    val isToday = selectedDate == LocalDate.now()
                                    val haptic = LocalHapticFeedback.current

                                    val dismissState = rememberSwipeToDismissBoxState(
                                        confirmValueChange = { value ->
                                            if (isToday && value == SwipeToDismissBoxValue.StartToEnd) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.toggleHabit(habit.id)
                                            }
                                            false // Always snap back
                                        }
                                    )

                                    SwipeToDismissBox(
                                        state = dismissState,
                                        enableDismissFromEndToStart = false,
                                        enableDismissFromStartToEnd = isToday,
                                        backgroundContent = {
                                            val color =
                                                if (habit.isCompletedToday) MaterialTheme.colorScheme.error.copy(
                                                    alpha = 0.15f
                                                ) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            val iconColor =
                                                if (habit.isCompletedToday) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                            val icon =
                                                if (habit.isCompletedToday) Icons.Default.Close else Icons.Default.Check
                                            val text =
                                                if (habit.isCompletedToday) "Mark Incomplete" else "Mark Complete"

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(color)
                                                    .padding(horizontal = 20.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = iconColor,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Text(
                                                        text = text,
                                                        color = iconColor,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        },
                                        content = {
                                            HabitCard(
                                                habit = habit,
                                                onToggle = { viewModel.toggleHabit(habit.id) },
                                                onClick = { onHabitClick(habit.id) },
                                                onLogProgress = { newVal ->
                                                    viewModel.logProgress(
                                                        habit.id,
                                                        newVal,
                                                        newVal >= (habit.targetValue ?: 1.0),
                                                        null
                                                    )
                                                },
                                                onLogBudgetClick = {
                                                    selectedBudgetHabit = habit
                                                    spendingAmount = habit.valueToday?.toString() ?: ""
                                                    spendingNote = habit.noteToday ?: ""
                                                    showBudgetDialog = true
                                                },
                                                enabled = isToday
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (showBudgetDialog && selectedBudgetHabit != null) {
                    val habit = selectedBudgetHabit!!
                    AlertDialog(
                        onDismissRequest = {
                            showBudgetDialog = false
                            selectedBudgetHabit = null
                        },
                        title = {
                            Text(
                                text = "Log Spending: ${habit.name}",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = spendingAmount,
                                    onValueChange = { spendingAmount = it },
                                    label = { Text("Amount Spent (${habit.valueUnit ?: "$"})") },
                                    placeholder = { Text("0.00") },
                                    singleLine = true,
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = spendingNote,
                                    onValueChange = { spendingNote = it },
                                    label = { Text("Where did you spend it? (Notes)") },
                                    placeholder = { Text("e.g. Coffee, Groceries") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val amount = spendingAmount.toDoubleOrNull()
                                    if (amount != null) {
                                        viewModel.logProgress(
                                            habitId = habit.id,
                                            value = amount,
                                            isCompleted = true,
                                            note = spendingNote.trim().takeIf { it.isNotBlank() }
                                        )
                                    } else if (spendingAmount.isBlank()) {
                                        viewModel.logProgress(
                                            habitId = habit.id,
                                            value = null,
                                            isCompleted = false,
                                            note = null
                                        )
                                    }
                                    showBudgetDialog = false
                                    selectedBudgetHabit = null
                                }
                            ) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showBudgetDialog = false
                                    selectedBudgetHabit = null
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                ConfettiCelebration(
                    trigger = triggerConfetti,
                    onAnimationEnd = { triggerConfetti = false }
                )
            }
        }
    }
}

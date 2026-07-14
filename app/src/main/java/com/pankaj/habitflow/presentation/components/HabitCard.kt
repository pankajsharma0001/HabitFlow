package com.pankaj.habitflow.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pankaj.habitflow.domain.model.Habit
import com.pankaj.habitflow.presentation.theme.StreakFire

/**
 * A card displaying a single habit with completion toggle, counter progress, or budget display.
 */
@Composable
fun HabitCard(
    habit: Habit,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLogProgress: (Double) -> Unit = {},
    onLogBudgetClick: () -> Unit = {},
    enabled: Boolean = true
) {
    val habitColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val checkScale by animateFloatAsState(
        targetValue = if (habit.isCompletedToday) 1.0f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkScale"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (habit.isCompletedToday) 0.85f else 1f,
        animationSpec = tween(300),
        label = "cardAlpha"
    )

    val checkColor by animateColorAsState(
        targetValue = if (habit.isCompletedToday) habitColor else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(300),
        label = "checkColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(habitColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Category emoji
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(habitColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.category.emoji,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Habit info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = cardAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtitle: Streak for normal/quantity, spent amount for budget
                if (habit.habitType == "BUDGET") {
                    if (habit.valueToday != null && habit.valueToday > 0.0) {
                        Text(
                            text = "${habit.valueUnit ?: "$"}${String.format("%.2f", habit.valueToday)}${if (!habit.noteToday.isNullOrBlank()) " - ${habit.noteToday}" else ""}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "Tap edit to log spending",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    if (habit.currentStreak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Streak",
                                modifier = Modifier.size(14.dp),
                                tint = StreakFire
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${habit.currentStreak} day${if (habit.currentStreak > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = StreakFire,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Interaction controls based on Habit Type
            when (habit.habitType) {
                "QUANTITY" -> {
                    val currentValue = habit.valueToday ?: 0.0
                    val target = habit.targetValue ?: 1.0
                    val unit = habit.valueUnit ?: ""

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (currentValue > 0) {
                                    val newVal = (currentValue - 1.0).coerceAtLeast(0.0)
                                    onLogProgress(newVal)
                                }
                            },
                            enabled = enabled,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = habitColor.copy(alpha = if (currentValue > 0) 1f else 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "${currentValue.toInt()} / ${target.toInt()} $unit",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = {
                                val newVal = currentValue + 1.0
                                onLogProgress(newVal)
                            },
                            enabled = enabled,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = habitColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                "BUDGET" -> {
                    IconButton(
                        onClick = onLogBudgetClick,
                        enabled = enabled,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Log Spending",
                            tint = habitColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                else -> {
                    // Check toggle
                    val haptic = LocalHapticFeedback.current
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .scale(checkScale)
                            .clip(CircleShape)
                            .then(
                                if (enabled) Modifier.clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onToggle()
                                }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (habit.isCompletedToday) Icons.Filled.CheckCircle
                            else Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = if (habit.isCompletedToday) "Completed" else "Not completed",
                            modifier = Modifier.size(32.dp),
                            tint = if (enabled) checkColor
                                   else checkColor.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

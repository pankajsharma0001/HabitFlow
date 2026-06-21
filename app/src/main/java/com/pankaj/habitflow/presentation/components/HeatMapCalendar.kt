package com.pankaj.habitflow.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pankaj.habitflow.presentation.theme.HeatMapEmpty
import com.pankaj.habitflow.presentation.theme.HeatMapEmptyDark
import com.pankaj.habitflow.presentation.theme.HeatMapLevel1
import com.pankaj.habitflow.presentation.theme.HeatMapLevel2
import com.pankaj.habitflow.presentation.theme.HeatMapLevel3
import com.pankaj.habitflow.presentation.theme.HeatMapLevel4
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * GitHub-style heat map calendar showing habit completion density.
 */
@Composable
fun HeatMapCalendar(
    completionData: Map<LocalDate, Int>,
    maxCompletions: Int,
    modifier: Modifier = Modifier,
    weeksToShow: Int = 16,
    isDarkTheme: Boolean = false
) {
    val today = LocalDate.now()
    val endDate = today
    val startDate = endDate.minusWeeks(weeksToShow.toLong())
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val emptyColor = if (isDarkTheme) HeatMapEmptyDark else HeatMapEmpty

    // Build grid: columns = weeks, rows = days of week
    val weeks = mutableListOf<List<LocalDate?>>()
    var currentWeekStart = startDate

    while (!currentWeekStart.isAfter(endDate)) {
        val week = mutableListOf<LocalDate?>()
        for (dayOfWeek in 0..6) {
            val date = currentWeekStart.plusDays(dayOfWeek.toLong())
            week.add(if (date.isAfter(endDate)) null else date)
        }
        weeks.add(week)
        currentWeekStart = currentWeekStart.plusWeeks(1)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Day labels
        val dayLabels = listOf("M", "", "W", "", "F", "", "S")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Day label column
            Column(
                modifier = Modifier.padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                dayLabels.forEach { label ->
                    Box(
                        modifier = Modifier.size(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Heat map grid
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                weeks.forEach { week ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        week.forEach { date ->
                            val count = date?.let { completionData[it] } ?: 0
                            val color = if (date == null) Color.Transparent
                            else getHeatMapColor(count, maxCompletions, emptyColor)

                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )
            listOf(emptyColor, HeatMapLevel1, HeatMapLevel2, HeatMapLevel3, HeatMapLevel4).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                        .padding(end = 2.dp)
                )
            }
            Text(
                text = "More",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

private fun getHeatMapColor(count: Int, max: Int, emptyColor: Color): Color {
    if (count == 0 || max == 0) return emptyColor
    val ratio = count.toFloat() / max
    return when {
        ratio <= 0.25f -> HeatMapLevel1
        ratio <= 0.50f -> HeatMapLevel2
        ratio <= 0.75f -> HeatMapLevel3
        else -> HeatMapLevel4
    }
}

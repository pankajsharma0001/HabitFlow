package com.pankaj.habitflow.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pankaj.habitflow.MainActivity
import com.pankaj.habitflow.data.local.AppDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color

class HabitFlowWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read directly from database (widget runs outside Hilt scope)
        val db = AppDatabase.getInstance(context)
        val habitDao = db.habitDao()
        val recordDao = db.habitRecordDao()

        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val activeHabits = habitDao.getActiveHabits()
        val completedIds = recordDao.getCompletedHabitIdsForDate(todayStr).toSet()

        val total = activeHabits.size
        val done = activeHabits.count { it.id in completedIds }
        val pendingHabits = activeHabits
            .filter { it.id !in completedIds }
            .take(5) // show up to 5 pending habits in the widget

        provideContent {
            GlanceTheme {
                WidgetContent(
                    totalHabits = total,
                    completedCount = done,
                    pendingHabits = pendingHabits.map { it.name },
                    pendingColors = pendingHabits.map { it.colorHex }
                )
            }
        }
    }
}

@Composable
private fun WidgetContent(
    totalHabits: Int,
    completedCount: Int,
    pendingHabits: List<String>,
    pendingColors: List<String>
) {
    val percentage = if (totalHabits > 0) (completedCount * 100 / totalHabits) else 0
    val allDone = completedCount >= totalHabits && totalHabits > 0

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(24.dp)
            .background(ColorProvider(Color(0xFF1C1B1F)))
            .clickable(actionStartActivity<MainActivity>())
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header row
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ HabitFlow",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "$completedCount / $totalHabits",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFB0BEC5)),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        // Progress text
        Text(
            text = if (allDone) "All done for today! 🎉" else "$percentage% completed",
            style = TextStyle(
                color = ColorProvider(
                    if (allDone) Color(0xFF66BB6A) else Color(0xFF90CAF9)
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        )

        Spacer(modifier = GlanceModifier.height(10.dp))

        // Pending habits list
        if (pendingHabits.isEmpty() && allDone) {
            Text(
                text = "Great work today! ✨",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF78909C)),
                    fontSize = 12.sp
                )
            )
        } else {
            pendingHabits.forEachIndexed { index, name ->
                val dotColor = try {
                    Color(AndroidColor.parseColor(pendingColors.getOrElse(index) { "#90CAF9" }))
                } catch (_: Exception) {
                    Color(0xFF90CAF9)
                }

                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "●",
                        style = TextStyle(
                            color = ColorProvider(dotColor),
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = name,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFECEFF1)),
                            fontSize = 13.sp
                        ),
                        maxLines = 1
                    )
                }
            }
            if (totalHabits - completedCount > 5) {
                Text(
                    text = "+${totalHabits - completedCount - 5} more",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF78909C)),
                        fontSize = 11.sp
                    ),
                    modifier = GlanceModifier.padding(top = 2.dp)
                )
            }
        }
    }
}

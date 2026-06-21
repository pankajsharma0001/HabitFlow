package com.pankaj.habitflow.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Animated bar chart composable for displaying completion data.
 */
@Composable
fun BarChart(
    data: List<Pair<String, Float>>,   // label to value (0f–1f)
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    maxValue: Float = 1f
) {
    val animatedProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, animationSpec = tween(800))
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (data.isEmpty()) return@Canvas

        val bottomPadding = 28.dp.toPx()
        val topPadding = 8.dp.toPx()
        val chartHeight = size.height - bottomPadding - topPadding
        val barWidth = (size.width / data.size) * 0.6f
        val gapWidth = (size.width / data.size) * 0.4f

        data.forEachIndexed { index, (label, value) ->
            val normalizedValue = (value / maxValue).coerceIn(0f, 1f)
            val animatedHeight = chartHeight * normalizedValue * animatedProgress.value
            val x = index * (barWidth + gapWidth) + gapWidth / 2

            // Bar
            drawRoundRect(
                color = barColor.copy(alpha = 0.15f),
                topLeft = Offset(x, topPadding),
                size = Size(barWidth, chartHeight),
                cornerRadius = CornerRadius(6.dp.toPx())
            )

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, topPadding + chartHeight - animatedHeight),
                size = Size(barWidth, animatedHeight),
                cornerRadius = CornerRadius(6.dp.toPx())
            )

            // Label
            val textLayout = textMeasurer.measure(
                text = label,
                style = TextStyle(
                    color = labelColor,
                    fontSize = 10.sp
                )
            )
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
                    x + barWidth / 2 - textLayout.size.width / 2,
                    size.height - bottomPadding + 8.dp.toPx()
                )
            )
        }
    }
}

/**
 * Animated line chart composable.
 */
@Composable
fun LineChart(
    data: List<Float>,     // values from 0f to 1f
    labels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val animatedProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, animationSpec = tween(1000))
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (data.size < 2) return@Canvas

        val bottomPadding = 28.dp.toPx()
        val topPadding = 16.dp.toPx()
        val sidePadding = 8.dp.toPx()
        val chartHeight = size.height - bottomPadding - topPadding
        val chartWidth = size.width - sidePadding * 2
        val stepX = chartWidth / (data.size - 1)

        val points = data.mapIndexed { index, value ->
            Offset(
                x = sidePadding + index * stepX,
                y = topPadding + chartHeight * (1f - value.coerceIn(0f, 1f)) * animatedProgress.value
            )
        }

        // Fill area
        val fillPath = Path().apply {
            moveTo(points.first().x, topPadding + chartHeight)
            points.forEach { point -> lineTo(point.x, point.y) }
            lineTo(points.last().x, topPadding + chartHeight)
            close()
        }
        drawPath(fillPath, fillColor)

        // Line
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val controlX1 = prev.x + (curr.x - prev.x) / 3
                val controlX2 = prev.x + 2 * (curr.x - prev.x) / 3
                cubicTo(controlX1, prev.y, controlX2, curr.y, curr.x, curr.y)
            }
        }
        drawPath(linePath, lineColor, style = Stroke(width = 3.dp.toPx()))

        // Dots
        points.forEach { point ->
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = point
            )
        }

        // Labels (show every Nth label to avoid crowding)
        val labelInterval = maxOf(1, labels.size / 7)
        labels.forEachIndexed { index, label ->
            if (index % labelInterval == 0 || index == labels.lastIndex) {
                val textLayout = textMeasurer.measure(
                    text = label,
                    style = TextStyle(color = labelColor, fontSize = 9.sp)
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        points[index].x - textLayout.size.width / 2,
                        size.height - bottomPadding + 8.dp.toPx()
                    )
                )
            }
        }
    }
}

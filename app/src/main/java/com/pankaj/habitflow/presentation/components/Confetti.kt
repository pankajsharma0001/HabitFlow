package com.pankaj.habitflow.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val id: Int,
    var x: Float,
    var y: Float,
    val color: Color,
    val vx: Float,
    var vy: Float,
    val radius: Float,
    val isCircle: Boolean,
    val rotationSpeed: Float,
    var rotation: Float = 0f,
    val lifetime: Float, // in ms
    var age: Float = 0f
)

@Composable
fun ConfettiCelebration(
    trigger: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val particles = remember { mutableStateListOf<Particle>() }

    LaunchedEffect(trigger) {
        particles.clear()
        val random = Random(System.currentTimeMillis())
        val colors = listOf(
            Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
            Color(0xFFA78BFA), Color(0xFF6BCB77), Color(0xFFFF8C42),
            Color(0xFF7B68EE), Color(0xFFFF69B4), Color(0xFF50C878),
            Color(0xFFFF7F50)
        )
        
        repeat(100) { id ->
            val fromLeft = id % 2 == 0
            val angle = if (fromLeft) {
                random.nextFloat() * 45f - 60f // -15 to -60 degrees (up and right)
            } else {
                random.nextFloat() * 45f - 165f // -120 to -165 degrees (up and left)
            }
            val speed = random.nextFloat() * 15f + 12f
            val rad = Math.toRadians(angle.toDouble())
            val vx = (cos(rad) * speed).toFloat()
            val vy = (sin(rad) * speed).toFloat()

            particles.add(
                Particle(
                    id = id,
                    x = if (fromLeft) 0f else 1000f,
                    y = 1500f,
                    color = colors[random.nextInt(colors.size)],
                    vx = vx,
                    vy = vy,
                    radius = random.nextFloat() * 8f + 8f,
                    isCircle = random.nextBoolean(),
                    rotationSpeed = random.nextFloat() * 10f - 5f,
                    lifetime = random.nextFloat() * 1000f + 1200f
                )
            )
        }

        var lastTime = System.currentTimeMillis()
        while (particles.any { it.age < it.lifetime }) {
            withFrameNanos {
                val now = System.currentTimeMillis()
                val dt = (now - lastTime).toFloat()
                lastTime = now

                for (i in particles.indices) {
                    val p = particles[i]
                    if (p.age < p.lifetime) {
                        p.x += p.vx * (dt / 16f)
                        p.y += p.vy * (dt / 16f)
                        p.vy += 0.4f * (dt / 16f) // gravity
                        p.rotation += p.rotationSpeed * (dt / 16f)
                        p.age += dt
                    }
                }
            }
        }
        onAnimationEnd()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            if (p.age == 0f) {
                p.x = if (p.vx > 0) 0f else width
                p.y = height
            }

            if (p.age < p.lifetime) {
                val alpha = (1f - (p.age / p.lifetime)).coerceIn(0f, 1f)
                val paintColor = p.color.copy(alpha = alpha)

                if (p.isCircle) {
                    drawCircle(
                        color = paintColor,
                        radius = p.radius,
                        center = Offset(p.x, p.y)
                    )
                } else {
                    val sizePx = p.radius * 2
                    val rectSize = Size(sizePx, sizePx / 2)
                    val offset = Offset(p.x - p.radius, p.y - p.radius / 2)
                    rotate(p.rotation, pivot = Offset(p.x, p.y)) {
                        drawRect(
                            color = paintColor,
                            topLeft = offset,
                            size = rectSize
                        )
                    }
                }
            }
        }
    }
}

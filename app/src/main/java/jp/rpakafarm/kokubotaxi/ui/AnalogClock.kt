package jp.rpakafarm.kokubotaxi.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin

/**
 * アナログ時計
 * @since 0.1.0
 * @author ChatGPT 4o
 */
@Composable
fun AnalogClock() {
    val currentTime = remember { mutableStateOf(LocalTime.now()) }

    // 毎秒更新
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = LocalTime.now()
            delay(1000L)
        }
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        val clockRadius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // 時計の外枠
        drawCircle(
            color = Color.Black,
            radius = clockRadius,
            style = Stroke(width = 4.dp.toPx())
        )

        // 時間の目盛りと数字
        for (i in 1..12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val lineStart = Offset(
                x = center.x + (clockRadius - 10.dp.toPx()) * cos(angle).toFloat(),
                y = center.y + (clockRadius - 10.dp.toPx()) * sin(angle).toFloat()
            )
            val lineEnd = Offset(
                x = center.x + clockRadius * cos(angle).toFloat(),
                y = center.y + clockRadius * sin(angle).toFloat()
            )
            drawLine(
                color = Color.Black,
                start = lineStart,
                end = lineEnd,
                strokeWidth = 2.dp.toPx()
            )

            val textOffset = Offset(
                x = center.x + (clockRadius - 21.dp.toPx()) * cos(angle).toFloat(),
                y = center.y + (clockRadius - 21.dp.toPx()) * sin(angle).toFloat()
            )
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                textOffset.x,
                textOffset.y + 10.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 24.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // 時針
        val hourAngle = Math.toRadians(( (currentTime.value.hour % 12 * 30) + (currentTime.value.minute * 0.5) - 90).toDouble())
        drawLine(
            color = Color.Black,
            start = center,
            end = Offset(
                x = center.x + (clockRadius - 50.dp.toPx()) * cos(hourAngle).toFloat(),
                y = center.y + (clockRadius - 50.dp.toPx()) * sin(hourAngle).toFloat()
            ),
            strokeWidth = 8.dp.toPx()
        )

        // 分針
        val minuteAngle = Math.toRadians((currentTime.value.minute * 6 - 90).toDouble())
        drawLine(
            color = Color.Gray,
            start = center,
            end = Offset(
                x = center.x + (clockRadius - 30.dp.toPx()) * cos(minuteAngle).toFloat(),
                y = center.y + (clockRadius - 30.dp.toPx()) * sin(minuteAngle).toFloat()
            ),
            strokeWidth = 6.dp.toPx()
        )

        // 秒針
        val secondAngle = Math.toRadians((currentTime.value.second * 6 - 90).toDouble())
        drawLine(
            color = Color.Red,
            start = center,
            end = Offset(
                x = center.x + (clockRadius - 20.dp.toPx()) * cos(secondAngle).toFloat(),
                y = center.y + (clockRadius - 20.dp.toPx()) * sin(secondAngle).toFloat()
            ),
            strokeWidth = 4.dp.toPx()
        )
    }
}
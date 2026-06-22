package com.example.crew_wiki.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** 하단 네비게이션 바용 단순 벡터 아이콘 (외부 아이콘 라이브러리 의존 없이 Canvas로 직접 그림) */

@Composable
fun HomeNavIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val roof = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.92f, h * 0.45f)
            lineTo(w * 0.78f, h * 0.45f)
            lineTo(w * 0.78f, h * 0.88f)
            lineTo(w * 0.22f, h * 0.88f)
            lineTo(w * 0.22f, h * 0.45f)
            lineTo(w * 0.08f, h * 0.45f)
            close()
        }
        drawPath(roof, color = tint, style = Stroke(width = w * 0.09f))
        drawRect(
            color = tint,
            topLeft = Offset(w * 0.42f, h * 0.6f),
            size = Size(w * 0.16f, h * 0.28f),
            style = Stroke(width = w * 0.07f),
        )
    }
}

@Composable
fun HistoryNavIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val radius = w * 0.38f
        val center = Offset(w * 0.5f, h * 0.5f)
        drawCircle(color = tint, radius = radius, center = center, style = Stroke(width = w * 0.09f))
        // 시계 분침/시침
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x, center.y - radius * 0.55f),
            strokeWidth = w * 0.08f,
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x + radius * 0.4f, center.y),
            strokeWidth = w * 0.08f,
        )
    }
}

@Composable
fun EyeNavIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val eyePath = Path().apply {
            moveTo(w * 0.06f, h * 0.5f)
            quadraticBezierTo(w * 0.5f, h * 0.12f, w * 0.94f, h * 0.5f)
            quadraticBezierTo(w * 0.5f, h * 0.88f, w * 0.06f, h * 0.5f)
            close()
        }
        drawPath(eyePath, color = tint, style = Stroke(width = w * 0.08f))
        drawCircle(color = tint, radius = w * 0.13f, center = Offset(w * 0.5f, h * 0.5f))
    }
}

@Composable
fun SettingsNavIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.5f)
        val outerRadius = w * 0.42f
        val innerRadius = w * 0.16f
        val teeth = 8
        val gearPath = Path()
        for (i in 0 until teeth) {
            val angle = (2 * kotlin.math.PI * i / teeth)
            val nextAngle = (2 * kotlin.math.PI * (i + 0.5) / teeth)
            val outerPoint = Offset(
                center.x + outerRadius * kotlin.math.cos(angle).toFloat(),
                center.y + outerRadius * kotlin.math.sin(angle).toFloat(),
            )
            val innerPoint = Offset(
                center.x + innerRadius * 2.2f * kotlin.math.cos(nextAngle).toFloat(),
                center.y + innerRadius * 2.2f * kotlin.math.sin(nextAngle).toFloat(),
            )
            if (i == 0) gearPath.moveTo(outerPoint.x, outerPoint.y) else gearPath.lineTo(outerPoint.x, outerPoint.y)
            gearPath.lineTo(innerPoint.x, innerPoint.y)
        }
        gearPath.close()
        drawPath(gearPath, color = tint)
    }
}

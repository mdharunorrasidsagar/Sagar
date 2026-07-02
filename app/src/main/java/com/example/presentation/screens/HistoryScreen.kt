package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WeightLogEntity
import com.example.presentation.HealthViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HealthViewModel) {
    val weightLogs by viewModel.allWeightLogs.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showLogDialog by remember { mutableStateOf(false) }
    var inputWeight by remember { mutableStateOf("") }

    // Chart mode tab
    var selectedChartTab by remember { mutableStateOf("Weight") } // "Weight", "BMI"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ANALYTICS & HISTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftMutedGray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Health Trends",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkCharcoal
                    )
                }

                Button(
                    onClick = { showLogDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("history_log_weight_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Weight", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Tabs Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OffWhiteSurface)
                    .padding(4.dp)
            ) {
                listOf("Weight", "BMI").forEach { tab ->
                    val isSelected = selectedChartTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedChartTab = tab }
                            .testTag("history_tab_${tab.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MintGreen else SoftMutedGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Line Chart Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = ShadowColor,
                        spotColor = ShadowColor
                    ),
                colors = CardDefaults.cardColors(containerColor = OffWhiteSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = if (selectedChartTab == "Weight") "Weight Progression (kg)" else "BMI Variation Index",
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (weightLogs.size >= 2) {
                        // Plot beautifully animated trend on custom canvas
                        val points = remember(weightLogs, selectedChartTab, userProfile) {
                            val heightM = (userProfile?.heightCm ?: 170f) / 100f
                            weightLogs.map { log ->
                                if (selectedChartTab == "Weight") {
                                    log.weightKg
                                } else {
                                    if (heightM > 0) log.weightKg / (heightM * heightM) else 0f
                                }
                            }
                        }

                        val labels = remember(weightLogs) {
                            val sdf = SimpleDateFormat("M/dd", Locale.getDefault())
                            weightLogs.map { log ->
                                sdf.format(Date(log.timestamp))
                            }
                        }

                        TrendLineChart(
                            points = points,
                            labels = labels,
                            primaryColor = if (selectedChartTab == "Weight") MintGreen else SkyBlue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                    } else {
                        // Empty State for Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ShowChart, contentDescription = "Empty Chart", tint = SoftMutedGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Add at least 2 logs to visualize trends.", color = SoftMutedGray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Historic Logs Listing
            Text(
                text = "HISTORIC JOURNAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SoftMutedGray,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (weightLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No logs recorded yet.", color = SoftMutedGray, fontSize = 14.sp)
                }
            } else {
                weightLogs.reversed().forEach { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = OffWhiteSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val logDate = remember(log.timestamp) {
                                    SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(log.timestamp))
                                }
                                Text(logDate, fontWeight = FontWeight.Bold, color = DarkCharcoal, fontSize = 14.sp)
                                Text("Metric Log Point", fontSize = 11.sp, color = SoftMutedGray)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${log.weightKg} kg", fontWeight = FontWeight.ExtraBold, color = MintGreen, fontSize = 16.sp)
                                    val heightM = (userProfile?.heightCm ?: 170f) / 100f
                                    val logBmi = if (heightM > 0) log.weightKg / (heightM * heightM) else 0f
                                    Text(String.format(Locale.US, "BMI: %.1f", logBmi), fontSize = 12.sp, color = SoftMutedGray)
                                }

                                IconButton(
                                    onClick = { viewModel.deleteWeightLog(log.id) },
                                    modifier = Modifier.testTag("delete_log_${log.id}")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Log", tint = SoftCoral)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Weight Log Dialog
        if (showLogDialog) {
            AlertDialog(
                onDismissRequest = { showLogDialog = false },
                title = { Text("Log Daily Weight") },
                text = {
                    Column {
                        Text("Record your current scale reading in kilograms.", color = SoftMutedGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
                        OutlinedTextField(
                            value = inputWeight,
                            onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) inputWeight = it },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("log_weight_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MintGreen, focusedLabelColor = MintGreen)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val w = inputWeight.toFloatOrNull()
                            if (w != null) {
                                viewModel.logWeight(w)
                                inputWeight = ""
                                showLogDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                    ) {
                        Text("Record")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogDialog = false }) {
                        Text("Cancel", color = SoftMutedGray)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun TrendLineChart(
    points: List<Float>,
    labels: List<String>,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    // Chart transition draw path animator
    val pathAnimationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "chart_draw"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val maxVal = (points.maxOrNull() ?: 100f) * 1.05f
        val minVal = (points.minOrNull() ?: 0f) * 0.95f
        val diff = if (maxVal - minVal > 0) maxVal - minVal else 1f

        val paddingBottom = 40f
        val paddingTop = 20f
        val paddingLeft = 40f
        val paddingRight = 40f

        val plotWidth = w - paddingLeft - paddingRight
        val plotHeight = h - paddingTop - paddingBottom

        // Draw horizontal grid lines (3 lines)
        val gridCount = 3
        for (i in 0..gridCount) {
            val y = paddingTop + plotHeight * (i.toFloat() / gridCount)
            drawLine(
                color = DividerGray,
                start = Offset(paddingLeft, y),
                end = Offset(w - paddingRight, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw date and weight labels if coordinates are mapped
        val coordinates = points.indices.map { index ->
            val x = paddingLeft + plotWidth * (index.toFloat() / (points.size - 1).coerceAtLeast(1))
            val ratio = (points[index] - minVal) / diff
            val y = h - paddingBottom - (plotHeight * ratio)
            Offset(x, y)
        }

        // Animated gradient path
        if (coordinates.isNotEmpty()) {
            val strokePath = Path().apply {
                moveTo(coordinates[0].x, coordinates[0].y)
                for (i in 1 until coordinates.size) {
                    // Draw smooth curve using cubicTo or simple lineTo
                    val currentX = coordinates[i].x
                    val currentY = coordinates[i].y
                    val prevX = coordinates[i - 1].x
                    val prevY = coordinates[i - 1].y
                    // Bezier interpolation
                    cubicTo(
                        (prevX + currentX) / 2, prevY,
                        (prevX + currentX) / 2, currentY,
                        currentX, currentY
                    )
                }
            }

            // Fill gradient path under the curve
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(coordinates.last().x, h - paddingBottom)
                lineTo(coordinates[0].x, h - paddingBottom)
                close()
            }

            // Use clipPath to animate drawing from left to right
            val clipPath = Path().apply {
                addRect(androidx.compose.ui.geometry.Rect(0f, 0f, w * pathAnimationProgress, h))
            }

            drawContext.canvas.save()
            drawContext.canvas.clipPath(clipPath)

            // Draw shadow/gradient fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                    startY = paddingTop,
                    endY = h - paddingBottom
                )
            )

            // Draw line
            drawPath(
                path = strokePath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw line nodes/points
            coordinates.forEach { offset ->
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = offset
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = offset
                )
            }

            drawContext.canvas.restore()
        }

        // Draw bottom labels
        // Draw first, middle, and last date labels
        val step = (labels.size - 1).coerceAtLeast(1)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#7F8C8D")
            textSize = 10.dp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        labels.forEachIndexed { index, label ->
            if (index == 0 || index == step || index == step / 2) {
                val x = paddingLeft + plotWidth * (index.toFloat() / step)
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    h - 10f,
                    textPaint
                )
            }
        }
    }
}

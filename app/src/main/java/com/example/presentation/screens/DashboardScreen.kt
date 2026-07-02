package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.HealthCalculations
import com.example.presentation.HealthViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin

@Composable
fun DashboardScreen(viewModel: HealthViewModel, onNavigateToProfile: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val calculations by viewModel.healthCalculations.collectAsStateWithLifecycle()
    val waterLogs by viewModel.todayWaterLogs.collectAsStateWithLifecycle()

    val totalWaterLogged = waterLogs.sumOf { it.amountMl }

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
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val formattedDate = remember {
                        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
                    }
                    Text(
                        text = formattedDate.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftMutedGray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hello, ${userProfile?.name ?: "User"}!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkCharcoal
                    )
                }

                IconButton(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .size(48.dp)
                        .background(OffWhiteSurface, CircleShape)
                        .testTag("dashboard_edit_profile_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Profile",
                        tint = MintGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            calculations?.let { calc ->
                // BMI Section Card
                BmiVisualizerCard(calc = calc)

                Spacer(modifier = Modifier.height(24.dp))

                // BMR & TDEE Metabolism Section
                BmrBreakdownCard(calc = calc)

                Spacer(modifier = Modifier.height(24.dp))

                // Hydration Tracker Row
                HydrationTrackerWidget(
                    loggedMl = totalWaterLogged,
                    targetMl = calc.dailyWaterTargetMl,
                    onLogGlass = { viewModel.addWaterLog(250) },
                    onReset = {
                        waterLogs.forEach { log -> viewModel.removeWaterLog(log.id) }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Customized Diet & Meal Planner Guide
                DietAndMealGuide(calc = calc)
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MintGreen)
            }
        }
    }
}

@Composable
fun BmiVisualizerCard(calc: HealthCalculations) {
    // Smooth gauge glide animation on dashboard load
    val animatedBmi by animateFloatAsState(
        targetValue = calc.bmi,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "bmi_glide"
    )

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BODY MASS INDEX (BMI)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftMutedGray,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (calc.bmiCategory) {
                                "Normal" -> MintGreenLight
                                "Underweight" -> SkyBlueLight
                                "Overweight" -> SoftCoralLight
                                else -> SoftCoralLight
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = calc.bmiCategory.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (calc.bmiCategory) {
                            "Normal" -> MintGreen
                            "Underweight" -> SkyBlue
                            "Overweight" -> SoftCoral
                            else -> SoftCoral
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Massive display BMI
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f", animatedBmi),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkCharcoal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "kg/m²",
                    fontSize = 14.sp,
                    color = SoftMutedGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gauge spectrum bar
            // Underweight: <18.5, Normal: 18.5 - 24.9, Overweight: 25 - 29.9, Obese: >=30
            // Map BMI range [15..35] to 0% to 100%
            val minBmi = 15f
            val maxBmi = 35f
            val progressFraction = ((animatedBmi - minBmi) / (maxBmi - minBmi)).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
            ) {
                // Background Spectrum
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .align(Alignment.Center)
                ) {
                    val w = size.width
                    val h = size.height
                    val r = h / 2

                    // Colors for category boundaries
                    // 15 to 18.5 (17.5% width) - SkyBlue
                    // 18.5 to 25 (32.5% width) - MintGreen
                    // 25 to 30 (25% width) - Orange/Coral
                    // 30 to 35 (25% width) - Deep Coral
                    val uW = w * 0.175f
                    val nW = w * 0.325f
                    val oW = w * 0.25f
                    val obW = w * 0.25f

                    // Draw Underweight
                    drawRoundRect(
                        color = SkyBlue,
                        topLeft = Offset(0f, 0f),
                        size = Size(uW + r, h),
                        cornerRadius = CornerRadius(r, r)
                    )
                    // Draw Normal
                    drawRect(
                        color = MintGreen,
                        topLeft = Offset(uW, 0f),
                        size = Size(nW, h)
                    )
                    // Draw Overweight
                    drawRect(
                        color = Color(0xFFF39C12),
                        topLeft = Offset(uW + nW, 0f),
                        size = Size(oW, h)
                    )
                    // Draw Obese
                    drawRoundRect(
                        color = SoftCoral,
                        topLeft = Offset(uW + nW + oW - r, 0f),
                        size = Size(obW + r, h),
                        cornerRadius = CornerRadius(r, r)
                    )
                }

                // Smooth sliding indicator circle
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterEnd)
                            .shadow(4.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .border(3.dp, DarkCharcoal, CircleShape)
                    )
                }
            }

            // Spectrum labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("15", fontSize = 10.sp, color = SoftMutedGray)
                Text("18.5", fontSize = 10.sp, color = SoftMutedGray)
                Text("25", fontSize = 10.sp, color = SoftMutedGray)
                Text("30", fontSize = 10.sp, color = SoftMutedGray)
                Text("35+", fontSize = 10.sp, color = SoftMutedGray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your ideal weight range is ${calc.bmiMinNormalWeight}kg - ${calc.bmiMaxNormalWeight}kg based on height.",
                fontSize = 12.sp,
                color = SoftMutedGray
            )
        }
    }
}

@Composable
fun BmrBreakdownCard(calc: HealthCalculations) {
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
                text = "METABOLIC ENGINE (CALORIES)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SoftMutedGray,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Stats (BMR vs TDEE)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Baseline BMR
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = "BMR", tint = SoftCoral, modifier = Modifier.size(16.dp))
                        Text("BMR Baseline", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftMutedGray)
                    }
                    Text(
                        text = "${calc.bmr} kcal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkCharcoal,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text("Baseline metabolism", fontSize = 10.sp, color = SoftMutedGray)
                }

                // Active TDEE
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.DirectionsRun, contentDescription = "TDEE", tint = MintGreen, modifier = Modifier.size(16.dp))
                        Text("Active TDEE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftMutedGray)
                    }
                    Text(
                        text = "${calc.tdee} kcal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkCharcoal,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text("Total with activities", fontSize = 10.sp, color = SoftMutedGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Adjusted Goal Calories bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Adjusted Goal Intake", fontWeight = FontWeight.Bold, color = DarkCharcoal, fontSize = 14.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoftCoralLight)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${calc.targetCalories} KCAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SoftCoral
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal meter comparing BMR / Target / TDEE
                LinearProgressIndicator(
                    progress = { (calc.targetCalories.toFloat() / calc.tdee.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1.2f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SoftCoral,
                    trackColor = DividerGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Calorie goal tailored to sustain weight goals safely.",
                    fontSize = 11.sp,
                    color = SoftMutedGray
                )
            }
        }
    }
}

@Composable
fun HydrationTrackerWidget(
    loggedMl: Int,
    targetMl: Int,
    onLogGlass: () -> Unit,
    onReset: () -> Unit
) {
    val progress = (loggedMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)

    // Smooth wave/filling progress bar animation
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "water_fill"
    )

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Animated Water wave beaker drawn inside Canvas
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(2.dp, DividerGray, RoundedCornerShape(20.dp))
            ) {
                // Sine wave cycle for water effect
                val infiniteTransition = rememberInfiniteTransition(label = "wave")
                val waveOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 2f * Math.PI.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "offset"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val waterHeight = h * animatedProgress
                    val waterY = h - waterHeight

                    val wavePath = Path().apply {
                        moveTo(0f, waterY)
                        for (x in 0..w.toInt() step 5) {
                            val sineVal = sin((x.toFloat() / w * 2f * Math.PI.toFloat()) + waveOffset)
                            val relativeWaveHeight = if (progress in 0.01f..0.99f) 6.dp.toPx() else 0f
                            val y = waterY + sineVal * relativeWaveHeight
                            lineTo(x.toFloat(), y)
                        }
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }

                    // Clip to rounded border
                    val clipP = Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = androidx.compose.ui.geometry.Rect(0f, 0f, w, h),
                                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                            )
                        )
                    }

                    clipPath(clipP) {
                        drawPath(
                            path = wavePath,
                            brush = Brush.verticalGradient(
                                colors = listOf(SkyBlue, SkyBlue.copy(alpha = 0.7f))
                            )
                        )
                    }
                }

                // Percentage center overlay
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = if (progress > 0.45f) Color.White else DarkCharcoal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Hydration Control Pane
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "HYDRATION MILESTONE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftMutedGray,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "$loggedMl / $targetMl ml",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkCharcoal,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Button(
                        onClick = onLogGlass,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("dashboard_log_water_btn")
                    ) {
                        Icon(Icons.Outlined.WaterDrop, contentDescription = "Add Glass", modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+250ml", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    IconButton(
                        onClick = onReset,
                        modifier = Modifier
                            .background(OffWhiteSurface, RoundedCornerShape(12.dp))
                            .size(36.dp)
                            .testTag("dashboard_reset_water_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Water Logs", tint = SoftMutedGray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DietAndMealGuide(calc: HealthCalculations) {
    Column {
        Text(
            text = "CUSTOM DIET PLAN & MEALS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SoftMutedGray,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Custom Macro Tracker Visual Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = ShadowColor,
                    spotColor = ShadowColor
                ),
            colors = CardDefaults.cardColors(containerColor = OffWhiteSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Daily Macronutrients Budget",
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Carbs (45%)
                    MacroProgressItem(
                        title = "Carbs",
                        grams = calc.macroCarbsGrams,
                        percent = 45,
                        color = SkyBlue,
                        modifier = Modifier.weight(1f)
                    )

                    // Protein (30%)
                    MacroProgressItem(
                        title = "Protein",
                        grams = calc.macroProteinGrams,
                        percent = 30,
                        color = MintGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Fat (25%)
                    MacroProgressItem(
                        title = "Fat",
                        grams = calc.macroFatGrams,
                        percent = 25,
                        color = SoftCoral,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Meal Recommendations based on Target Calories
        val mealCalorieSplits = listOf(
            Triple("Breakfast", 0.30f, Icons.Default.LightMode),
            Triple("Lunch", 0.35f, Icons.Default.WbSunny),
            Triple("Dinner", 0.25f, Icons.Default.NightsStay),
            Triple("Snacks", 0.10f, Icons.Default.LocalCafe)
        )

        // Standard meal recommendations depending on goal: Lose, Maintain, Gain
        val mealRecommendations = when (calc.bmiCategory) {
            "Underweight" -> mapOf(
                "Breakfast" to "Oatmeal with whole milk, honey, sliced almonds, bananas, and two boiled eggs.",
                "Lunch" to "Grilled Salmon steak served with brown rice, sliced avocado, and creamed spinach.",
                "Dinner" to "High-protein beef sirloin stir fry with sweet potatoes, broccoli, and sesame glaze.",
                "Snacks" to "Mixed Greek yogurt with cashew nuts and a glass of whey protein smoothie."
            )
            "Overweight", "Obese" -> mapOf(
                "Breakfast" to "Egg-white spinach frittata, whole-wheat avocado toast, and black unsweetened coffee.",
                "Lunch" to "Lemon baked cod fillets served with steam-cooked quinoa, light salad, and olive oil.",
                "Dinner" to "Slow-roasted herb-crusted chicken breast, mashed cauliflower, and roasted asparagus.",
                "Snacks" to "Celery stalks dipped in unsalted peanut butter or a handful of fresh blueberries."
            )
            else -> mapOf(
                "Breakfast" to "Scrambled egg wrap, chia seed pudding with fresh strawberries, and green tea.",
                "Lunch" to "Classic Turkey breast club salad with crisp lettuce, cucumbers, cherry tomatoes, and light vinaigrette.",
                "Dinner" to "Lean roasted turkey fillet with brown rice pilaf, sweet green peas, and grilled zucchini.",
                "Snacks" to "A single crisp red apple paired with low-fat cottage cheese."
            )
        }

        mealCalorieSplits.forEach { (mealName, splitRatio, icon) ->
            val mealCalories = (calc.targetCalories * splitRatio).toInt()
            val description = mealRecommendations[mealName] ?: "Balanced lean proteins and leafy greens."

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = ShadowColor,
                        spotColor = ShadowColor
                    ),
                colors = CardDefaults.cardColors(containerColor = OffWhiteSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = mealName,
                            tint = when (mealName) {
                                "Breakfast" -> Color(0xFFF39C12)
                                "Lunch" -> MintGreen
                                "Dinner" -> SkyBlue
                                else -> SoftCoral
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(mealName, fontWeight = FontWeight.Bold, color = DarkCharcoal, fontSize = 16.dp.value.sp)
                            Text("$mealCalories kcal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftMutedGray)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = description,
                            fontSize = 12.sp,
                            color = SoftMutedGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroProgressItem(
    title: String,
    grams: Int,
    percent: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftMutedGray)
        Text("${grams}g", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = DarkCharcoal, modifier = Modifier.padding(top = 4.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(DividerGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percent / 100f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text("$percent%", fontSize = 9.sp, color = SoftMutedGray)
    }
}

package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.presentation.HealthViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(viewModel: HealthViewModel) {
    var step by remember { mutableStateOf(1) }
    val focusManager = LocalFocusManager.current

    // Form inputs
    var name by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") } // "Male", "Female"
    var heightFeetStr by remember { mutableStateOf("") }
    var heightInchesStr by remember { mutableStateOf("") }
    var weightStr by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("Active") } // "Sedentary", "Lightly Active", "Active", "Very Active"
    var goalType by remember { mutableStateOf("Lose") } // "Lose", "Maintain", "Gain"

    val totalSteps = 3

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Image (Hero Banner from generated image)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(OffWhiteSurface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_onboarding_hero_1782973656797),
                    contentDescription = "Onboarding welcome banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..totalSteps) {
                    val activeWeight by animateFloatAsState(targetValue = if (i == step) 2.5f else 1f, label = "indicator")
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .weight(activeWeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (i <= step) MintGreen else SoftMutedGray.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated step-by-step contents
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "step_transition"
            ) { currentStep ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStep) {
                        1 -> {
                            Text(
                                text = "Welcome to Health Tracker",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "Let's personalize your metabolic engine details.",
                                fontSize = 14.sp,
                                color = SoftMutedGray,
                                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("What is your name?") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MintGreen,
                                    focusedLabelColor = MintGreen,
                                    unfocusedBorderColor = DividerGray
                                ),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = ageStr,
                                onValueChange = { if (it.all { char -> char.isDigit() }) ageStr = it },
                                label = { Text("Your age (years)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_age_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MintGreen,
                                    focusedLabelColor = MintGreen,
                                    unfocusedBorderColor = DividerGray
                                ),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Gender",
                                fontWeight = FontWeight.SemiBold,
                                color = DarkCharcoal,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                listOf("Male", "Female").forEach { currentGender ->
                                    val isSelected = gender == currentGender
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) MintGreenLight else OffWhiteSurface)
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSelected) MintGreen else DividerGray,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable {
                                                gender = currentGender
                                                focusManager.clearFocus()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = currentGender,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MintGreen else DarkCharcoal
                                        )
                                    }
                                }
                            }
                        }
                        2 -> {
                            Text(
                                text = "Body Metrics",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "Height and weight are key for BMR/BMI math.",
                                fontSize = 14.sp,
                                color = SoftMutedGray,
                                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = heightFeetStr,
                                    onValueChange = { if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull() != null)) heightFeetStr = it },
                                    label = { Text("Feet (ft)") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("onboarding_height_feet_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MintGreen,
                                        focusedLabelColor = MintGreen,
                                        unfocusedBorderColor = DividerGray
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                OutlinedTextField(
                                    value = heightInchesStr,
                                    onValueChange = { if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull() != null && it.toInt() < 12)) heightInchesStr = it },
                                    label = { Text("Inches (in)") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("onboarding_height_inches_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MintGreen,
                                        focusedLabelColor = MintGreen,
                                        unfocusedBorderColor = DividerGray
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    )
                                )
                            }

                            val feetVal = heightFeetStr.toIntOrNull()
                            val inchesVal = heightInchesStr.toIntOrNull()
                            if (feetVal != null && inchesVal != null) {
                                val totalInches = feetVal * 12 + inchesVal
                                val cmVal = totalInches * 2.54f
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Equivalent to: ${String.format(java.util.Locale.US, "%.1f", cmVal)} cm",
                                    fontSize = 13.sp,
                                    color = MintGreen,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = weightStr,
                                onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) weightStr = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_weight_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MintGreen,
                                    focusedLabelColor = MintGreen,
                                    unfocusedBorderColor = DividerGray
                                ),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                )
                            )
                        }
                        3 -> {
                            Text(
                                text = "Lifestyle & Goals",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "Define your daily movement and objective.",
                                fontSize = 14.sp,
                                color = SoftMutedGray,
                                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                            )

                            Text(
                                text = "Activity Level",
                                fontWeight = FontWeight.SemiBold,
                                color = DarkCharcoal,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val activities = listOf(
                                "Sedentary" to "Little or no exercise",
                                "Lightly Active" to "Light training 1-3d/wk",
                                "Active" to "Moderate training 3-5d/wk",
                                "Very Active" to "Hard exercise 6-7d/wk"
                            )

                            activities.forEach { (title, subtitle) ->
                                val isSelected = activityLevel == title
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { activityLevel = title },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MintGreenLight else OffWhiteSurface
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) MintGreen else DividerGray
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(title, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                            Text(subtitle, fontSize = 12.sp, color = SoftMutedGray)
                                        }
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = MintGreen)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Weight Goal",
                                fontWeight = FontWeight.SemiBold,
                                color = DarkCharcoal,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                listOf("Lose", "Maintain", "Gain").forEach { goal ->
                                    val isSelected = goalType == goal
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) MintGreenLight else OffWhiteSurface)
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSelected) MintGreen else DividerGray,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable { goalType = goal },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$goal Weight",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MintGreen else DarkCharcoal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    TextButton(
                        onClick = { step-- },
                        colors = ButtonDefaults.textButtonColors(contentColor = SoftMutedGray)
                    ) {
                        Text("Back", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                val canGoNext = when (step) {
                    1 -> name.trim().isNotEmpty() && ageStr.toIntOrNull() != null
                    2 -> heightFeetStr.toIntOrNull() != null && heightInchesStr.toIntOrNull() != null && weightStr.toFloatOrNull() != null
                    3 -> true
                    else -> false
                }

                Button(
                    onClick = {
                        if (step < totalSteps) {
                            step++
                        } else {
                            val feetVal = heightFeetStr.toIntOrNull() ?: 0
                            val inchesVal = heightInchesStr.toIntOrNull() ?: 0
                            val cmVal = (feetVal * 12 + inchesVal) * 2.54f
                            viewModel.completeOnboarding(
                                name = name,
                                age = ageStr.toInt(),
                                gender = gender,
                                height = cmVal,
                                weight = weightStr.toFloat(),
                                activityLevel = activityLevel,
                                goalType = goalType
                            )
                        }
                    },
                    enabled = canGoNext,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGreen,
                        disabledContainerColor = DividerGray
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .testTag("onboarding_next_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (step == totalSteps) "Let's Start" else "Continue",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (canGoNext) Color.White else SoftMutedGray
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = if (canGoNext) Color.White else SoftMutedGray
                        )
                    }
                }
            }
        }
    }
}

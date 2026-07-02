package com.example.presentation.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserProfileEntity
import com.example.presentation.HealthViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(viewModel: HealthViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    // Local form states
    var name by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var heightFeetStr by remember { mutableStateOf("") }
    var heightInchesStr by remember { mutableStateOf("") }
    var weightStr by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("Active") }
    var goalType by remember { mutableStateOf("Lose") }

    var showSuccessMessage by remember { mutableStateOf(false) }

    // Synchronize states when database user profile loads
    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            name = profile.name
            ageStr = profile.age.toString()
            gender = profile.gender
            val totalInches = kotlin.math.round(profile.heightCm / 2.54f).toInt()
            heightFeetStr = (totalInches / 12).toString()
            heightInchesStr = (totalInches % 12).toString()
            weightStr = profile.weightKg.toString()
            activityLevel = profile.activityLevel
            goalType = profile.goalType
        }
    }

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
                .padding(bottom = 32.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "YOUR METABOLIC ID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftMutedGray,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Profile Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkCharcoal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Fields Container
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "PERSONAL INFORMATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftMutedGray
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            focusedLabelColor = MintGreen,
                            unfocusedBorderColor = DividerGray
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { if (it.all { char -> char.isDigit() }) ageStr = it },
                            label = { Text("Age (yrs)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_age_input"),
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreen,
                                focusedLabelColor = MintGreen,
                                unfocusedBorderColor = DividerGray
                            )
                        )

                        // Gender picker
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Gender", fontSize = 12.sp, color = SoftMutedGray, modifier = Modifier.padding(bottom = 4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(1.dp, DividerGray, RoundedCornerShape(16.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("Male", "Female").forEach { currentGender ->
                                    val isSelected = gender == currentGender
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(if (isSelected) MintGreen else Color.Transparent)
                                            .clickable { gender = currentGender }
                                            .wrapContentSize(Alignment.Center)
                                    ) {
                                        Text(
                                            text = currentGender,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else DarkCharcoal,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = heightFeetStr,
                                    onValueChange = { if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull() != null)) heightFeetStr = it },
                                    label = { Text("Feet") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_height_feet_input"),
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MintGreen,
                                        focusedLabelColor = MintGreen,
                                        unfocusedBorderColor = DividerGray
                                    )
                                )

                                OutlinedTextField(
                                    value = heightInchesStr,
                                    onValueChange = { if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull() != null && it.toInt() < 12)) heightInchesStr = it },
                                    label = { Text("Inches") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_height_inches_input"),
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MintGreen,
                                        focusedLabelColor = MintGreen,
                                        unfocusedBorderColor = DividerGray
                                    )
                                )
                            }
                            
                            val feetVal = heightFeetStr.toIntOrNull()
                            val inchesVal = heightInchesStr.toIntOrNull()
                            if (feetVal != null && inchesVal != null) {
                                val totalInches = feetVal * 12 + inchesVal
                                val cmVal = totalInches * 2.54f
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f", cmVal)} cm",
                                    fontSize = 11.sp,
                                    color = MintGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = weightStr,
                            onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) weightStr = it },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_weight_input"),
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreen,
                                focusedLabelColor = MintGreen,
                                unfocusedBorderColor = DividerGray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Goal metrics
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "METABOLIC OBJECTIVES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftMutedGray
                    )

                    // Goal Selector
                    Column {
                        Text("Weight Goal", fontSize = 12.sp, color = SoftMutedGray, modifier = Modifier.padding(bottom = 6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, DividerGray, RoundedCornerShape(12.dp))
                        ) {
                            listOf("Lose", "Maintain", "Gain").forEach { goal ->
                                val isSelected = goalType == goal
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (isSelected) SoftCoral else Color.Transparent)
                                        .clickable { goalType = goal }
                                        .wrapContentSize(Alignment.Center)
                                ) {
                                    Text(
                                        text = "$goal Weight",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else DarkCharcoal,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Activity Level
                    Column {
                        Text("Weekly Activity Level", fontSize = 12.sp, color = SoftMutedGray, modifier = Modifier.padding(bottom = 6.dp))
                        val activities = listOf("Sedentary", "Lightly Active", "Active", "Very Active")
                        activities.forEach { level ->
                            val isSelected = activityLevel == level
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { activityLevel = level },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MintGreenLight else Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) MintGreen else DividerGray
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MintGreen else DarkCharcoal
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Active Selection", tint = MintGreen, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save Action Button
            val canSave = name.trim().isNotEmpty() &&
                    ageStr.toIntOrNull() != null &&
                    heightFeetStr.toIntOrNull() != null &&
                    heightInchesStr.toIntOrNull() != null &&
                    weightStr.toFloatOrNull() != null

            Button(
                onClick = {
                    focusManager.clearFocus()
                    val feetVal = heightFeetStr.toIntOrNull() ?: 0
                    val inchesVal = heightInchesStr.toIntOrNull() ?: 0
                    val cmVal = (feetVal * 12 + inchesVal) * 2.54f
                    val profile = UserProfileEntity(
                        id = 1,
                        name = name,
                        age = ageStr.toInt(),
                        gender = gender,
                        heightCm = cmVal,
                        weightKg = weightStr.toFloat(),
                        activityLevel = activityLevel,
                        goalType = goalType,
                        isOnboardingCompleted = true
                    )
                    viewModel.updateProfile(profile)
                    showSuccessMessage = true
                },
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintGreen,
                    disabledContainerColor = DividerGray
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("profile_save_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save Profile", tint = Color.White)
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Success feedback message
            if (showSuccessMessage) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MintGreenLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile saved! Recalculations applied.",
                            color = MintGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Dismiss",
                            color = MintGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable { showSuccessMessage = false }
                        )
                    }
                }
            }
        }
    }
}

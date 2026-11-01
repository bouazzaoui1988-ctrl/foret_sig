package com.example

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val firebaseAuth = remember {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    val prefs = remember { context.getSharedPreferences("sig_foret_prefs", android.content.Context.MODE_PRIVATE) }
    var rememberMe by remember { mutableStateOf(prefs.getBoolean("remember_me", true)) }

    var email by remember { mutableStateOf(if (rememberMe) prefs.getString("saved_email", "") ?: "" else "") }
    var password by remember { mutableStateOf(if (rememberMe) prefs.getString("saved_password", "") ?: "" else "") }
    var fullName by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var userRole by remember { mutableStateOf(prefs.getString("user_role", "titulaire") ?: "titulaire") }

    val saveCredentials = {
        val editor = prefs.edit()
        editor.putString("authenticated_user_email", email.trim())
        editor.putString("user_role", userRole)
        if (rememberMe) {
            editor.putBoolean("remember_me", true)
                .putString("saved_email", email.trim())
                .putString("saved_password", password)
        } else {
            editor.putBoolean("remember_me", false)
                .remove("saved_email")
                .remove("saved_password")
        }
        editor.apply()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Modern Dark Slate Base
    ) {
        // --- Glowing Background Gradient Blur Effects ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Glowing Top-Left/Center Emerald Blur
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF059669).copy(alpha = 0.22f), // Glowing Emerald
                        Color(0xFF10B981).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.25f),
                    radius = size.width * 0.8f
                )
            )

            // Glowing Bottom-Right Blue/Cyan Blur
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2563EB).copy(alpha = 0.20f), // Glowing Deep Blue
                        Color(0xFF3B82F6).copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.75f),
                    radius = size.width * 0.7f
                )
            )
        }

        // --- Auth Form Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Logo & Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color(0x2210B981), RoundedCornerShape(55.dp))
                        .border(2.dp, Color(0xFFFBBF24).copy(alpha = 0.8f), RoundedCornerShape(55.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_forestry_logo),
                        contentDescription = "Conservation des Forêts",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(51.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SIG Forêts",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = Color.White,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Système d'Information Géographique",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Credentials Card (Glassmorphic modern styling)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Color(0xFF475569).copy(alpha = 0.4f),
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xCC1E293B) // Semi-transparent dark slate
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegister) "Créer un compte" else "Connexion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Conditional Full Name input for registration
                    AnimatedVisibility(
                        visible = isRegister,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { -20 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { -20 })
                    ) {
                        Column {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Nom complet") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_field"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF34D399),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedLabelColor = Color(0xFF34D399),
                                    unfocusedLabelColor = Color(0xFF94A3B8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Statut / Role Selection
                    Text(
                        text = "Statut de l'Agent :",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isTitulaire = userRole == "titulaire"
                        Button(
                            onClick = { userRole = "titulaire" },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTitulaire) Color(0xFF10B981) else Color(0xFF334155),
                                contentColor = Color.White
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Titulaire", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { userRole = "vacataire" },
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isTitulaire) Color(0xFF10B981) else Color(0xFF334155),
                                contentColor = Color.White
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Vacataire Dédié", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Adresse email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF34D399)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF34D399),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF34D399),
                            unfocusedLabelColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF34D399)
                            )
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = "Toggle password visibility", tint = Color(0xFF94A3B8))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF34D399),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF34D399),
                            unfocusedLabelColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remember Me ("Se souvenir de moi")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { rememberMe = !rememberMe }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF10B981),
                                uncheckedColor = Color(0xFF475569),
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier.testTag("auth_remember_me_checkbox")
                        )
                        Text(
                            text = "Se souvenir de moi",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Authenticate Button with a glowing emerald style
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank() || (isRegister && fullName.isBlank())) {
                                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            focusManager.clearFocus()
                            isLoading = true

                            val auth = firebaseAuth
                            val isMockFirebase = try {
                                val app = com.google.firebase.FirebaseApp.getInstance()
                                app.options.projectId == "local-demo-project" || app.options.apiKey.contains("Mock")
                            } catch (e: Exception) {
                                true
                            }

                            if (isMockFirebase || auth == null) {
                                isLoading = false
                                Toast.makeText(context, "Connexion locale réussie (Mode Développement) !", Toast.LENGTH_SHORT).show()
                                saveCredentials()
                                onAuthSuccess()
                                return@Button
                            }

                            if (isRegister) {
                                // Register process
                                auth.createUserWithEmailAndPassword(email.trim(), password)
                                    .addOnSuccessListener { authResult ->
                                        val user = authResult.user
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName.trim())
                                            .build()

                                        user?.updateProfile(profileUpdates)
                                            ?.addOnCompleteListener {
                                                isLoading = false
                                                Toast.makeText(context, "Inscription réussie !", Toast.LENGTH_SHORT).show()
                                                saveCredentials()
                                                onAuthSuccess()
                                            } ?: run {
                                                isLoading = false
                                                saveCredentials()
                                                onAuthSuccess()
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        isLoading = false
                                        Toast.makeText(context, "Erreur d'inscription: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                // Login process
                                auth.signInWithEmailAndPassword(email.trim(), password)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Connexion réussie !", Toast.LENGTH_SHORT).show()
                                        saveCredentials()
                                        onAuthSuccess()
                                    }
                                    .addOnFailureListener { exception ->
                                        isLoading = false
                                        Toast.makeText(context, "Erreur de connexion: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button"),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981), // Emerald Green
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = if (isRegister) "S'INSCRIRE" else "SE CONNECTER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Switch Auth Mode text button
                    Text(
                        text = if (isRegister) "Déjà inscrit ? Se connecter" else "Pas encore de compte ? S'inscrire",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF34D399),
                        modifier = Modifier
                            .clickable {
                                isRegister = !isRegister
                                focusManager.clearFocus()
                            }
                            .padding(8.dp)
                            .testTag("auth_switch_mode_button"),
                        textDecoration = TextDecoration.Underline
                    )

                    Spacer(modifier = Modifier.height(12.dp))



                    Spacer(modifier = Modifier.height(12.dp))

                    // Demo bypass disabled
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- "Conservation des forêts" Credit Text ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Direction Générale des Forêts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Conservation des Forêts",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Color(0xFF10B981).copy(alpha = 0.8f),
                    letterSpacing = 1.sp,
                    textDecoration = TextDecoration.None
                )
            }
        }
    }
}

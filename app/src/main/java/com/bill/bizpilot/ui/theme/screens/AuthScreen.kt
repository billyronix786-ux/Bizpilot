package com.bill.bizpilot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bill.bizpilot.ui.theme.BizpilotTheme
import com.bill.bizpilot.ui.theme.viewmodels.MainViewModel

@Composable
fun AuthScreen(viewModel: MainViewModel) {
    AuthScreenContent(
        authError = viewModel.authError,
        onLogin = { username, password -> 
            viewModel.login(username, password)
        },
        onSignup = { username, password -> 
            viewModel.signup(username, password)
        }
    )
}

@Composable
fun AuthScreenContent(
    authError: String = "",
    onLogin: (String, String) -> Unit,
    onSignup: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
    ) {
        // High-end background image
        AsyncImage(
            model = "https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=1200&q=80",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1614850523296-d8c1af93d400?w=400&h=400&fit=crop",
                contentDescription = "Hero Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Cyan.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "BizPilot",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "Strategic Business Intelligence",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Cyan,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(32.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLogin) "Welcome back" else "Create your account",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Cyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Cyan,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        supportingText = {
                            if (authError.isNotEmpty()) {
                                Text(
                                    text = authError,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = authError.isNotEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Cyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Cyan,
                            unfocusedLabelColor = Color.Gray,
                            errorBorderColor = Color.Red,
                            errorLabelColor = Color.Red,
                            errorSupportingTextColor = Color.Red
                        )
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { 
                            if (username.isNotBlank() && password.isNotBlank()) {
                                if (isLogin) onLogin(username, password) else onSignup(username, password)
                            }
                        },
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan, 
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Cyan.copy(alpha = 0.3f),
                            disabledContentColor = Color.Black.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(if (isLogin) "Login" else "Get Started", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))

                    TextButton(onClick = { isLogin = !isLogin }) {
                        Text(
                            if (isLogin) "New here? Sign up" else "Already have an account? Login",
                            color = Color.Cyan
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    BizpilotTheme {
        AuthScreenContent(
            onLogin = { _, _ -> },
            onSignup = { _, _ -> }
        )
    }
}

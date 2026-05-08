package com.bill.bizpilot.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import com.bill.bizpilot.ui.theme.BizpilotTheme
import com.bill.bizpilot.ui.theme.viewmodels.MainViewModel

@Composable
fun CopilotScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    CopilotContent(
        aiResponse = viewModel.aiResponse,
        onAskCopilot = { viewModel.askCopilot(it) },
        onBack = onBack
    )
}

@Composable
fun CopilotContent(
    aiResponse: String,
    onAskCopilot: (String) -> Unit,
    onBack: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var isAsking by remember { mutableStateOf(false) }

    LaunchedEffect(aiResponse) {
        if (aiResponse.isNotEmpty()) {
            isAsking = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Text("AI Copilot", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1675271591211-126ad94e495d?w=400&h=400&fit=crop",
                contentDescription = "AI Assistant",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Cyan.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )
            
            // Pulsing effect placeholder (decorative icon)
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(140.dp),
                tint = Color.Cyan.copy(alpha = 0.1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "How can I help you today?",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Ask about your business performance, sales trends, or expense management.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))
        
        Text("Quick Strategies", color = Color.White, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StrategyChip("Sales Trends", "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=100&h=100&fit=crop") { onAskCopilot("Show me sales trends") }
            StrategyChip("Stock Alerts", "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=100&h=100&fit=crop") { onAskCopilot("Any stock alerts?") }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Ask something", color = Color.Cyan) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.Cyan,
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { 
                if (input.isNotBlank()) {
                    isAsking = true
                    onAskCopilot(input) 
                }
            },
            enabled = !isAsking && input.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
        ) {
            if (isAsking) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
            } else {
                Text("Ask AI", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))

        GlassCard(Modifier.fillMaxWidth()) {
            Text(
                text = aiResponse.ifBlank { "No response yet. Try asking 'How is my business doing?'" },
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StrategyChip(label: String, imageUrl: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(24.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}


// CopilotGlassCard removed, using GlassCard from CommonUI.kt

@Preview(showBackground = true)
@Composable
fun CopilotContentPreview() {
    BizpilotTheme {
        CopilotContent(
            aiResponse = "Your business is doing great! Sales are up by 20% this month.",
            onAskCopilot = {},
            onBack = {}
        )
    }
}

package com.bill.bizpilot.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import com.bill.bizpilot.models.TransactionEntity

@Composable
fun TransactionScreen(transactions: List<TransactionEntity>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
        Text(
            text = "Transaction History",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(16.dp))
        
        // Visual Header for History
        Surface(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.DarkGray
        ) {
            Box {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=600&q=80",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.4f
                )
                Text(
                    "Track every cent of your revenue and spending.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(transactions) { tx ->
                GlassCard(Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Ksh ${tx.amount}", color = Color.White, fontWeight = FontWeight.Bold) },
                        supportingContent = { 
                            Text("${tx.category} • ${tx.type.replaceFirstChar { it.uppercase() }}", color = Color.Gray) 
                        },
                        leadingContent = {
                            val icon = if (tx.type == "income") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
                            val color = if (tx.type == "income") Color.Green else Color.Red
                            Icon(icon, null, tint = color)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    Surface(color = Color(0xFF020617)) {
        TransactionScreen(
            transactions = listOf(
                TransactionEntity(1, 5000.0, "income", "Sales", System.currentTimeMillis(), 1),
                TransactionEntity(2, 2000.0, "expense", "Rent", System.currentTimeMillis(), 1)
            ),
            onBack = {}
        )
    }
}

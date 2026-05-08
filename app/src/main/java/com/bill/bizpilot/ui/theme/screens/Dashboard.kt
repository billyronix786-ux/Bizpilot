package com.bill.bizpilot.ui.theme.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bill.bizpilot.models.TransactionEntity
import com.bill.bizpilot.ui.theme.viewmodels.MainViewModel

@Composable
fun MainDashboard(viewModel: MainViewModel) {
    val transactions = viewModel.transactions
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, type, category ->
                viewModel.addTransaction(amount, type, category)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF020617),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0F172A)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Inventory, null) },
                    label = { Text("Stock") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Groups, null) },
                    label = { Text("Team") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, null) },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.AutoAwesome, null) },
                    label = { Text("AI Copilot") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> DashboardContent(
                    sales = viewModel.totalSales,
                    expenses = viewModel.totalExpenses,
                    payroll = viewModel.totalPayroll,
                    profit = viewModel.profit,
                    userName = viewModel.currentUser?.username ?: "User",
                    onLogout = { viewModel.logout() },
                    onAddClick = { showAddDialog = true },
                    onNavigateToTab = { selectedTab = it }
                )
                1 -> InventoryScreen(viewModel)
                2 -> EmployeeScreen(viewModel)
                3 -> TransactionScreen(
                    transactions = transactions,
                    onBack = { selectedTab = 0 }
                )
                4 -> CopilotScreen(
                    viewModel = viewModel,
                    onBack = { selectedTab = 0 }
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    sales: Double,
    expenses: Double,
    payroll: Double,
    profit: Double,
    userName: String,
    onLogout: () -> Unit,
    onAddClick: () -> Unit,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop",
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Hey 👋", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(userName, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Balance Card with Background "Image" style
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A))
        ) {
            Box {
                // Decorative icon as background image
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).align(Alignment.CenterEnd).offset(x = 40.dp),
                    tint = Color.White.copy(alpha = 0.1f)
                )
                
                Column(Modifier.padding(24.dp)) {
                    Text("Total Balance", color = Color.Cyan, style = MaterialTheme.typography.labelLarge)
                    val animatedProfit by animateFloatAsState(targetValue = profit.toFloat(), label = "")
                    Text(
                        "Ksh ${animatedProfit.toInt()}",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SummaryItem("Sales", sales, Icons.AutoMirrored.Filled.TrendingUp, Color.Green)
                        SummaryItem("Expenses", expenses + payroll, Icons.AutoMirrored.Filled.TrendingDown, Color.Red)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        
        // Large Promo/Feature Image
        Surface(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.DarkGray
        ) {
            Box {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1579532537598-459ecdaf39cc?w=800&q=80",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f
                )
                Column(Modifier.padding(20.dp).align(Alignment.CenterStart)) {
                    Text("AI Consulting", color = Color.Cyan, fontWeight = FontWeight.Bold)
                    Text("Get real-time insights\nfor your business.", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        
        Text("Quick Actions", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard("Add Stock", Icons.Default.AddShoppingCart, Color.Cyan, Modifier.weight(1f)) { onNavigateToTab(1) }
            ActionCard("Hire Team", Icons.Default.PersonAdd, Color.Magenta, Modifier.weight(1f)) { onNavigateToTab(2) }
        }

        Spacer(Modifier.height(20.dp))
        
        Text("Financial Analytics", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        
        ChartCard(sales, expenses + payroll)
        
        Spacer(Modifier.height(20.dp))
        InsightCard(profit)
        
        Spacer(Modifier.height(20.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SupportAgent, null, tint = Color.Cyan)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Business Tip", color = Color.Cyan, style = MaterialTheme.typography.labelLarge)
                    Text("Keep an eye on your expenses this week to maintain healthy margins.", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Add Transaction", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddTransactionDialog(onDismiss: () -> Unit, onConfirm: (Double, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("income") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        titleContentColor = Color.White,
        textContentColor = Color.Gray,
        title = { Text("New Transaction", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount", color = Color.Cyan) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Cyan,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Food, Rent)", color = Color.Cyan) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Cyan,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Column {
                    Text("Type", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = type == "income",
                            onClick = { type = "income" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Cyan)
                        )
                        Text("Income", color = Color.White)
                        Spacer(Modifier.width(16.dp))
                        RadioButton(
                            selected = type == "expense",
                            onClick = { type = "expense" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Red)
                        )
                        Text("Expense", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && category.isNotBlank()) onConfirm(amt, type, category)
                },
                enabled = amount.isNotBlank() && category.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}


@Composable
fun SummaryItem(label: String, value: Double, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Column {
            Text(label, color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
            Text("Ksh ${value.toInt()}", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionCard(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ChartCard(sales: Double, expenses: Double) {
    val maxVal = maxOf(sales, expenses, 1.0)

    GlassCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            AnimatedBar("Sales", sales, maxVal, Color.Cyan)
            AnimatedBar("Expenses", expenses, maxVal, Color.Red)
        }
    }
}

@Composable
fun AnimatedBar(label: String, value: Double, max: Double, color: Color) {
    val target = (value / max).toFloat().coerceIn(0.1f, 1f)
    val animated by animateFloatAsState(targetValue = target, label = "")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height((120 * animated).dp)
                .background(
                    brush = Brush.verticalGradient(listOf(color, color.copy(alpha = 0.3f))),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        Spacer(Modifier.height(8.dp))
        Text(label, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun InsightCard(profit: Double) {
    val message = when {
        profit > 5000 -> "Your business is growing fast 🚀"
        profit > 0 -> "You're making profit, keep pushing!"
        else -> "You're losing money, reduce expenses."
    }
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text("AI Insight 🤖", color = Color.Cyan)
            Spacer(Modifier.height(8.dp))
            Text(message, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardContentPreview() {
    Surface(color = Color(0xFF020617)) {
        DashboardContent(
            sales = 15000.0,
            expenses = 8000.0,
            payroll = 2000.0,
            profit = 5000.0,
            userName = "Preview User",
            onLogout = {},
            onAddClick = {},
            onNavigateToTab = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChartCardPreview() {
    ChartCard(sales = 10000.0, expenses = 5000.0)
}

@Preview(showBackground = true)
@Composable
fun InsightCardPreview() {
    InsightCard(profit = 6000.0)
}

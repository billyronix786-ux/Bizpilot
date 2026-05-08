package com.bill.bizpilot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bill.bizpilot.models.EmployeeEntity
import com.bill.bizpilot.ui.theme.viewmodels.MainViewModel

@Composable
fun EmployeeScreen(viewModel: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddEmployeeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, role, salary, imageUrl ->
                viewModel.addEmployee(name, role, salary, imageUrl)
                showAddDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Team Management", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.PersonAdd, null, tint = Color.Cyan, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payments, null, tint = Color.Cyan)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Monthly Payroll", color = Color.Cyan, style = MaterialTheme.typography.labelLarge)
                    Text("Ksh ${viewModel.totalPayroll.toInt()}", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.employees) { employee ->
                EmployeeItem(employee)
            }
        }
    }
}

@Composable
fun EmployeeItem(employee: EmployeeEntity) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = employee.profileImageUrl.ifEmpty { "https://images.unsplash.com/photo-1633332755192-727a05c4013d?w=200&h=200&fit=crop" },
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(employee.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(employee.role, color = Color.Cyan, style = MaterialTheme.typography.bodySmall)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Ksh ${employee.salary.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
                Text("per month", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        titleContentColor = Color.White,
        title = { Text("Add Employee") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (e.g. Manager)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("Monthly Salary") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Profile Image URL (Optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(name, role, salary.toDoubleOrNull() ?: 0.0, imageUrl)
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)) {
                Text("Hire")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EmployeeItemPreview() {
    Box(modifier = Modifier.background(Color(0xFF020617)).padding(16.dp)) {
        EmployeeItem(
            employee = EmployeeEntity(
                name = "John Doe",
                role = "Lead Developer",
                salary = 120000.0,
                profileImageUrl = "",
                userId = 1
            )
        )
    }
}

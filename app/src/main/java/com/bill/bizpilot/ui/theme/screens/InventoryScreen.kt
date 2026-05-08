package com.bill.bizpilot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.layout.ContentScale
import com.bill.bizpilot.models.ProductEntity
import com.bill.bizpilot.ui.theme.viewmodels.MainViewModel

@Composable
fun InventoryScreen(viewModel: MainViewModel) {
    InventoryScreenContent(
        products = viewModel.products,
        totalStockValue = viewModel.totalStockValue,
        onAddProduct = { name, qty, price, category ->
            viewModel.addProduct(name, qty, price, category)
        }
    )
}

@Composable
fun InventoryScreenContent(
    products: List<ProductEntity>,
    totalStockValue: Double,
    onAddProduct: (String, Int, Double, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, qty, price, category ->
                onAddProduct(name, qty, price, category)
                showAddDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Stock Inventory", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddCircle, null, tint = Color.Cyan, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Inventory, null, tint = Color.Cyan)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Total Stock Value", color = Color.Cyan, style = MaterialTheme.typography.labelLarge)
                    Text("Ksh ${totalStockValue.toInt()}", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products) { product ->
                ProductItem(product)
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductEntity) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200&h=200&fit=crop", // Placeholder product image
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(product.category, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${product.quantity} in stock", color = if (product.quantity < 5) Color.Red else Color.Cyan)
                Text("Ksh ${product.unitPrice.toInt()}/unit", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Double, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        titleContentColor = Color.White,
        title = { Text("Add New Product") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Unit Price") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(name, qty.toIntOrNull() ?: 0, price.toDoubleOrNull() ?: 0.0, category)
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)) {
                Text("Save")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    Surface(color = Color(0xFF020617)) {
        InventoryScreenContent(
            products = listOf(
                ProductEntity(name = "Laptops", quantity = 5, unitPrice = 50000.0, category = "Electronics", userId = 1),
                ProductEntity(name = "Mice", quantity = 2, unitPrice = 1200.0, category = "Accessories", userId = 1),
                ProductEntity(name = "Keyboards", quantity = 15, unitPrice = 2500.0, category = "Accessories", userId = 1)
            ),
            totalStockValue = 290000.0,
            onAddProduct = { _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductItemPreview() {
    Box(modifier = Modifier.background(Color(0xFF020617)).padding(16.dp)) {
        ProductItem(
            product = ProductEntity(
                name = "Sample Product",
                quantity = 10,
                unitPrice = 1500.0,
                category = "Electronics",
                userId = 1
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LowStockProductItemPreview() {
    Box(modifier = Modifier.background(Color(0xFF020617)).padding(16.dp)) {
        ProductItem(
            product = ProductEntity(
                name = "Low Stock Item",
                quantity = 2,
                unitPrice = 500.0,
                category = "Groceries",
                userId = 1
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddProductDialogPreview() {
    AddProductDialog(onDismiss = {}, onConfirm = { _, _, _, _ -> })
}

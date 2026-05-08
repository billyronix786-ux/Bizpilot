package com.bill.bizpilot

import com.bill.bizpilot.models.EmployeeEntity
import com.bill.bizpilot.models.ProductEntity
import com.bill.bizpilot.models.TransactionEntity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseDbManager {
    private val database = FirebaseDatabase.getInstance().reference

    // Sync a transaction to Firebase
    suspend fun syncTransaction(userId: String, transaction: TransactionEntity) {
        database.child("users").child(userId).child("transactions")
            .child(transaction.id.toString()).setValue(transaction).await()
    }

    // Sync a product to Firebase
    suspend fun syncProduct(userId: String, product: ProductEntity) {
        database.child("users").child(userId).child("products")
            .child(product.id.toString()).setValue(product).await()
    }

    // Sync an employee to Firebase
    suspend fun syncEmployee(userId: String, employee: EmployeeEntity) {
        database.child("users").child(userId).child("employees")
            .child(employee.id.toString()).setValue(employee).await()
    }

    // Fetch all data for a user (useful after login on a new device)
    suspend fun fetchUserData(userId: String): Map<String, Any>? {
        val snapshot = database.child("users").child(userId).get().await()
        @Suppress("UNCHECKED_CAST")
        return snapshot.value as? Map<String, Any>
    }
}

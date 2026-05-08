package com.bill.bizpilot.ui.theme.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bill.bizpilot.AiClient
import com.bill.bizpilot.FirebaseDbManager
import com.bill.bizpilot.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val firebaseDb = FirebaseDbManager()
    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        db.userDao(),
        db.transactionDao(),
        db.productDao(),
        db.employeeDao()
    )
    var authError by mutableStateOf("")

    var isLoading by mutableStateOf(true)
        private set

    var currentUser by mutableStateOf<UserEntity?>(null)
        private set

    var transactions by mutableStateOf<List<TransactionEntity>>(emptyList())
        private set

    var products by mutableStateOf<List<ProductEntity>>(emptyList())
        private set

    var employees by mutableStateOf<List<EmployeeEntity>>(emptyList())
        private set

    var aiResponse by mutableStateOf("")
        private set

    var totalSales by mutableDoubleStateOf(0.0)
    var totalExpenses by mutableDoubleStateOf(0.0)
    var totalPayroll by mutableDoubleStateOf(0.0)
    var profit by mutableDoubleStateOf(0.0)
    var totalStockValue by mutableDoubleStateOf(0.0)

    init {
        val firebaseUser = auth.currentUser
        viewModelScope.launch(Dispatchers.IO) {
            if (firebaseUser != null && firebaseUser.email != null) {
                // Try to find the user in Room by email. 
                val user = db.userDao().findByUsername(firebaseUser.email!!)
                withContext(Dispatchers.Main) {
                    currentUser = user
                    if (user != null) {
                        refreshData()
                    }
                    isLoading = false
                }
            } else {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    private fun refreshData() {
        val userId = currentUser?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val txs = repository.getTransactions(userId)
            val prods = repository.getProducts(userId)
            val emps = repository.getEmployees(userId)

            withContext(Dispatchers.Main) {
                transactions = txs
                products = prods
                employees = emps
                calculateMetrics()
            }
        }
    }

    private fun calculateMetrics() {
        totalSales = transactions.filter { it.type == "income" }.sumOf { it.amount }
        totalExpenses = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        totalPayroll = employees.sumOf { it.salary }
        profit = totalSales - (totalExpenses + totalPayroll)
        totalStockValue = products.sumOf { it.quantity * it.unitPrice }
    }

    fun login(email: String, password: String) {
        authError = ""
        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch(Dispatchers.IO) {
                        // Find user in Room based on username (email)
                        var user = repository.login(email, password)
                        if (user == null) {
                            // If not in Room but in Firebase, add to Room
                            repository.signup(email, password)
                            user = repository.login(email, password)
                        }
                        
                        // Initial sync from Firebase if user is found
                        user?.let { roomUser ->
                            val firebaseUser = auth.currentUser
                            if (firebaseUser != null) {
                                syncFromFirebase(firebaseUser.uid, roomUser.id)
                            }
                        }

                        withContext(Dispatchers.Main) {
                            currentUser = user
                            refreshData()
                            isLoading = false
                        }
                    }
                } else {
                    authError = task.exception?.message ?: "Login Failed"
                    isLoading = false
                }
            }
    }

    private suspend fun syncFromFirebase(firebaseUid: String, roomUserId: Int) {
        val data = firebaseDb.fetchUserData(firebaseUid) ?: return
        
        // Simplified sync: Fetch and insert if local lists are empty
        // This avoids duplicates on every login while ensuring data recovery
        val localTxs = repository.getTransactions(roomUserId)
        if (localTxs.isEmpty()) {
            val transactionsMap = data["transactions"] as? Map<*, *>
            transactionsMap?.values?.forEach { txObj ->
                val tx = txObj as? Map<*, *> ?: return@forEach
                repository.addTransaction(
                    amount = (tx["amount"] as? Number)?.toDouble() ?: 0.0,
                    type = tx["type"] as? String ?: "",
                    category = tx["category"] as? String ?: "",
                    userId = roomUserId
                )
            }
        }

        val localProducts = repository.getProducts(roomUserId)
        if (localProducts.isEmpty()) {
            val productsMap = data["products"] as? Map<*, *>
            productsMap?.values?.forEach { pObj ->
                val p = pObj as? Map<*, *> ?: return@forEach
                repository.addProduct(
                    name = p["name"] as? String ?: "",
                    qty = (p["quantity"] as? Number)?.toInt() ?: 0,
                    price = (p["unitPrice"] as? Number)?.toDouble() ?: 0.0,
                    category = p["category"] as? String ?: "",
                    userId = roomUserId
                )
            }
        }

        val localEmployees = repository.getEmployees(roomUserId)
        if (localEmployees.isEmpty()) {
            val employeesMap = data["employees"] as? Map<*, *>
            employeesMap?.values?.forEach { eObj ->
                val e = eObj as? Map<*, *> ?: return@forEach
                repository.addEmployee(
                    name = e["name"] as? String ?: "",
                    role = e["role"] as? String ?: "",
                    salary = (e["salary"] as? Number)?.toDouble() ?: 0.0,
                    imageUrl = e["profileImageUrl"] as? String ?: "",
                    userId = roomUserId
                )
            }
        }
    }

    fun signup(email: String, password: String) {
        authError = ""
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch(Dispatchers.IO) {
                        repository.signup(email, password)
                        val user = db.userDao().login(email, password)
                        withContext(Dispatchers.Main) {
                            currentUser = user
                            refreshData()
                        }
                    }
                } else {
                    authError = task.exception?.message ?: "Signup Failed"
                }
            }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
    }

    fun addTransaction(amount: Double, type: String, category: String) {
        val userId = currentUser?.id ?: return
        val firebaseUser = auth.currentUser
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addTransaction(amount, type, category, userId)
            if (firebaseUser != null) {
                val transaction = TransactionEntity(
                    id = id,
                    amount = amount,
                    type = type,
                    category = category,
                    userId = userId
                )
                firebaseDb.syncTransaction(firebaseUser.uid, transaction)
            }
            refreshData()
        }
    }

    fun addProduct(name: String, qty: Int, price: Double, category: String) {
        val userId = currentUser?.id ?: return
        val firebaseUser = auth.currentUser
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addProduct(name, qty, price, category, userId)
            if (firebaseUser != null) {
                val product = ProductEntity(
                    id = id,
                    name = name,
                    quantity = qty,
                    unitPrice = price,
                    category = category,
                    userId = userId
                )
                firebaseDb.syncProduct(firebaseUser.uid, product)
            }
            refreshData()
        }
    }

    fun addEmployee(name: String, role: String, salary: Double, imageUrl: String) {
        val userId = currentUser?.id ?: return
        val firebaseUser = auth.currentUser
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addEmployee(name, role, salary, imageUrl, userId)
            if (firebaseUser != null) {
                val employee = EmployeeEntity(
                    id = id,
                    name = name,
                    role = role,
                    salary = salary,
                    profileImageUrl = imageUrl,
                    userId = userId
                )
                firebaseDb.syncEmployee(firebaseUser.uid, employee)
            }
            refreshData()
        }
    }

    fun askCopilot(question: String) {
        aiResponse = "Thinking..."
        AiClient.ask(question, totalSales, totalExpenses + totalPayroll) { response ->
            aiResponse = response
        }
    }
}

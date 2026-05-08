package com.bill.bizpilot.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Index
import androidx.room.ForeignKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String, // "income" or "expense"
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: Int
)

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE username = :u AND password = :p LIMIT 1")
    suspend fun login(u: String, p: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :u LIMIT 1")
    suspend fun findByUsername(u: String): UserEntity?
}

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE userId = :userId")
    suspend fun getTransactionsForUser(userId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<TransactionEntity>
}

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val category: String,
    val userId: Int
)

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: ProductEntity): Long

    @androidx.room.Update
    suspend fun update(product: ProductEntity)

    @androidx.room.Delete
    suspend fun delete(product: ProductEntity)

    @Query("SELECT * FROM products WHERE userId = :userId")
    suspend fun getProductsForUser(userId: Int): List<ProductEntity>
}

@Entity(
    tableName = "employees",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String,
    val salary: Double,
    val profileImageUrl: String = "",
    val userId: Int
)

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insert(employee: EmployeeEntity): Long

    @androidx.room.Update
    suspend fun update(employee: EmployeeEntity)

    @androidx.room.Delete
    suspend fun delete(employee: EmployeeEntity)

    @Query("SELECT * FROM employees WHERE userId = :userId")
    suspend fun getEmployeesForUser(userId: Int): List<EmployeeEntity>
}

@Database(entities = [UserEntity::class, TransactionEntity::class, ProductEntity::class, EmployeeEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun productDao(): ProductDao
    abstract fun employeeDao(): EmployeeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bizpilot_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class AppRepository(
    private val userDao: UserDao,
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val employeeDao: EmployeeDao
) {
    suspend fun login(username: String, password: String): UserEntity? {
        return userDao.login(username, password)
    }

    suspend fun signup(username: String, password: String): Int {
        return userDao.insert(UserEntity(username = username, password = password)).toInt()
    }

    suspend fun addTransaction(amount: Double, type: String, category: String, userId: Int): Int {
        return transactionDao.insert(
            TransactionEntity(
                amount = amount,
                type = type,
                category = category,
                userId = userId
            )
        ).toInt()
    }

    suspend fun getTransactions(userId: Int): List<TransactionEntity> {
        return transactionDao.getTransactionsForUser(userId)
    }

    suspend fun addProduct(name: String, qty: Int, price: Double, category: String, userId: Int): Int {
        return productDao.insert(ProductEntity(name = name, quantity = qty, unitPrice = price, category = category, userId = userId)).toInt()
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.delete(product)
    }

    suspend fun getProducts(userId: Int): List<ProductEntity> {
        return productDao.getProductsForUser(userId)

    }

    suspend fun addEmployee(name: String, role: String, salary: Double, imageUrl: String, userId: Int): Int {
        return employeeDao.insert(EmployeeEntity(name = name, role = role, salary = salary, profileImageUrl = imageUrl, userId = userId)).toInt()
    }

    suspend fun getEmployees(userId: Int): List<EmployeeEntity> {
        return employeeDao.getEmployeesForUser(userId)
    }
}

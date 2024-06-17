package com.example.kreonculatorapp.firestore

interface FirestoreInterface {
    suspend fun addProduct(name: String, product: Product)
    suspend fun getProduct(name: String): Product?
    suspend fun getAllProducts(): List<Product>
}
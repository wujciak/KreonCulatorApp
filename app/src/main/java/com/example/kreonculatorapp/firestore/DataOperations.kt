package com.example.kreonculatorapp.firestore

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DataOperations(private val db: FirebaseFirestore) : FirestoreInterface {
    override suspend fun addProduct(name: String, product: Product) {
        try {
            db.collection("products").document(name).set(product).await()
        } catch (e: Exception) {
            // error
        }
    }

    override suspend fun getProduct(name: String): Product? {
        val snapshot = FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo(FieldPath.documentId(), name)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(Product::class.java)
    }

    override suspend fun getAllProducts(): List<Product> {
        return try {
            val querySnapshot = db.collection("products")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
        } catch (e: Exception) {
            // Handle exception
            emptyList() // Return an empty list or handle error case appropriately
        }
    }
}
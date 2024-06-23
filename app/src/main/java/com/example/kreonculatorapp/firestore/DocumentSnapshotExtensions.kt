package com.example.kreonculatorapp.firestore

import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toProduct(): Product? {
    val name = getString("name") ?: return null
    val fat = get("fat") as? Double ?: return null
    return Product(name, fat)
}
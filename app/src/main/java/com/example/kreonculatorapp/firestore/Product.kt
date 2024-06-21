package com.example.kreonculatorapp.firestore

import com.google.firebase.firestore.PropertyName

data class Product (
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("fat") @set:PropertyName("fat") var fat: Number = 0.0
)

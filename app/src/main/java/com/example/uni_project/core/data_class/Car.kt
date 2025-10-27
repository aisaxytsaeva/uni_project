package com.example.uni_project.core.data_class


import com.example.uni_project.R

data class Car(
    val id: String,
    val brand: String,
    val model: String,
    val price: Int,
    val currency: String,
    val fuelType: String,
    val bodyType: String,
    val transmission: String,
    val imageUrl: Int
)
val mockCars = listOf(
    Car(
        id = "1",
        brand = "Toyota",
        model = "Camry",
        price = 5500,
        bodyType = "Sedan",
        currency = "P",
        fuelType = "Бензин",
        transmission =   "A/T",
        imageUrl = R.drawable.car

    ),
    Car(
        id = "2",
        brand = "Honda",
        model = "Civic",
        price = 2500,
        bodyType = "Sedan",
        currency = "P",
        fuelType = "Бензин",
        transmission =   "A/T",
        imageUrl = R.drawable.car
    ),
    Car(
        id = "3",
        brand = "BMW",
        model = "X5",
        price = 12000,
        bodyType = "SUV",
        currency = "P",
        fuelType = "Бензин",
        transmission =   "A/T",
        imageUrl = R.drawable.car

    ),
    Car(
        id = "4",
        brand = "Mercedes",
        model = "E-Class",
        price = 8000,
        bodyType = "Sedan",
        currency = "P",
        fuelType = "Бензин",
        transmission =   "A/T",
        imageUrl = R.drawable.car
    ),
    Car(
        id = "5",
        brand = "Audi",
        model = "A4",
        price = 7000,
        bodyType = "Sedan",
        currency = "P",
        fuelType = "Бензин",
        transmission =   "A/T",
        imageUrl = R.drawable.car
    )
)
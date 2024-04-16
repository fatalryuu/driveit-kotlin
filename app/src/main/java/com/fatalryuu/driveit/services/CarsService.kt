package com.fatalryuu.driveit.services

import com.fatalryuu.driveit.models.Car
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object CarsService {
    private val carsCollection = FirebaseFirestore.getInstance().collection("cars")

    suspend fun getCars(): List<Car> {
        return try {
            val querySnapshot = carsCollection.get().await()
            val carList = ArrayList<Car>()
            for (document in querySnapshot.documents) {
                val car = mapToCar(document)
                carList.add(car)
            }
            carList
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCarReferenceById(carId: String): DocumentReference? {
        return try {
            carsCollection.document(carId).get().await().reference
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun mapToCar(document: DocumentSnapshot): Car {
        val id = document.id
        val name = document.getString("name") ?: ""
        val description = document.getString("description") ?: ""
        val images = document.get("images") as? List<String> ?: emptyList()

        return Car(id, name, description, images)
    }
}

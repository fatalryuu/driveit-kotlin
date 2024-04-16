package com.fatalryuu.driveit.services

import com.fatalryuu.driveit.models.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.fatalryuu.driveit.models.UserDB
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object UserService {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val usersCollection = FirebaseFirestore.getInstance().collection("users")
    private val carsCollection = FirebaseFirestore.getInstance().collection("cars")

    fun createUser(user: FirebaseUser?) {
        user?.let {
            val userDB = UserDB(
                id = user.uid,
                email = user.email ?: ""
            )
            usersCollection.document(userDB.id).set(userDB.toMap())
        }
    }

    fun deleteUser() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            user.delete()
                .addOnSuccessListener {
                    usersCollection.document(user.uid).delete()
                }
        }
    }

    fun updateProfile(userDB: UserDB) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            usersCollection.document(user.uid).set(userDB.toMap(), SetOptions.merge())
        }
    }

    suspend fun getFavouriteCars(): List<Car> {
        val currentUser = firebaseAuth.currentUser
        val favouriteCars = ArrayList<Car>()

        currentUser?.let { user ->
            try {
                val userDocument = usersCollection.document(user.uid).get().await()
                val favourites = userDocument.get("favourites") as? List<DocumentReference>

                favourites?.let { favouriteRefs ->
                    for (favouriteRef in favouriteRefs) {
                        val carDocument = favouriteRef.get().await()
                        val car = CarsService.mapToCar(carDocument)
                        favouriteCars.add(car)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return favouriteCars
    }

    suspend fun isCarInFavorites(carReference: DocumentReference?): Boolean {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            try {
                val userDocument = usersCollection.document(currentUser.uid).get().await()
                val favorites = userDocument.get("favourites") as? List<DocumentReference>
                return favorites?.contains(carReference) ?: false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    suspend fun addCarToFavorites(carReference: DocumentReference?) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null && carReference != null) {
            try {
                usersCollection.document(currentUser.uid)
                    .update("favourites", FieldValue.arrayUnion(carReference))
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun removeCarFromFavorites(carReference: DocumentReference?) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null && carReference != null) {
            try {
                usersCollection.document(currentUser.uid)
                    .update("favourites", FieldValue.arrayRemove(carReference))
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

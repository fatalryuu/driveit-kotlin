package com.fatalryuu.driveit.models

import com.google.firebase.firestore.DocumentReference

data class UserDB(
    val id: String = "",
    val email: String = "",
    var name: String = "",
    var surname: String = "",
    var username: String = "",
    var birthday: String = "",
    var job: String = "",
    var country: String = "",
    var city: String = "",
    var education: String = "",
    var hobby: String = "",
    var social: String = "",
    var favourites: List<DocumentReference> = listOf()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "email" to email,
            "name" to name,
            "surname" to surname,
            "username" to username,
            "birthday" to birthday,
            "job" to job,
            "country" to country,
            "city" to city,
            "education" to education,
            "hobby" to hobby,
            "social" to social,
            "favourites" to favourites
        )
    }
}

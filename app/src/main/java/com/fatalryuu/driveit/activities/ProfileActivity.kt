package com.fatalryuu.driveit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fatalryuu.driveit.R
import com.fatalryuu.driveit.databinding.ActivityProfileBinding
import com.fatalryuu.driveit.models.UserDB
import com.fatalryuu.driveit.services.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email
        binding.email.text = email

        currentUser?.let { user ->
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userDB = document.toObject(UserDB::class.java)
                        userDB?.let {
                            binding.name.setText(it.name)
                            binding.surname.setText(it.surname)
                            binding.username.setText(it.username)
                            binding.birthday.setText(it.birthday)
                            binding.job.setText(it.job)
                            binding.country.setText(it.country)
                            binding.city.setText(it.city)
                            binding.education.setText(it.education)
                            binding.hobby.setText(it.hobby)
                            binding.social.setText(it.social)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }

        binding.edit.setOnClickListener {
            toggleEditing(isEditing)
            isEditing = !isEditing
        }

        binding.logout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.delete.setOnClickListener {
            UserService.deleteUser()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.bottomNavigation.menu.findItem(R.id.menu_profile).isChecked = true

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    true
                }
                R.id.menu_cars -> {
                    startActivity(Intent(this, CarsListActivity::class.java))
                    true
                }
                R.id.menu_favorites -> {
                    startActivity(Intent(this, FavouritesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun toggleEditing(isEditing: Boolean) {
        if (!isEditing) {
            enableInputFields(true)

            binding.edit.text = getString(R.string.save_changes)
        } else {
            enableInputFields(false)

            UserService.updateProfile(gatherUpdatedUserData())

            binding.edit.text = getString(R.string.edit_profile)
        }
    }

    private fun enableInputFields(isEnabled: Boolean) {
        binding.name.isEnabled = isEnabled
        binding.surname.isEnabled = isEnabled
        binding.username.isEnabled = isEnabled
        binding.birthday.isEnabled = isEnabled
        binding.job.isEnabled = isEnabled
        binding.country.isEnabled = isEnabled
        binding.city.isEnabled = isEnabled
        binding.education.isEnabled = isEnabled
        binding.hobby.isEnabled = isEnabled
        binding.social.isEnabled = isEnabled
    }

    private fun gatherUpdatedUserData(): UserDB {
        return UserDB(
            name = binding.name.text.toString(),
            surname = binding.surname.text.toString(),
            username = binding.username.text.toString(),
            birthday = binding.birthday.text.toString(),
            job = binding.job.text.toString(),
            country = binding.country.text.toString(),
            city = binding.city.text.toString(),
            education = binding.education.text.toString(),
            hobby = binding.hobby.text.toString(),
            social = binding.social.text.toString()
        )
    }
}



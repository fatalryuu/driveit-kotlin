package com.fatalryuu.driveit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.fatalryuu.driveit.R
import com.fatalryuu.driveit.databinding.ActivityFavouritesBinding
import com.fatalryuu.driveit.models.Car
import com.fatalryuu.driveit.services.CarsService
import com.fatalryuu.driveit.services.UserService
import com.fatalryuu.driveit.utils.createCarTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavouritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.menu.findItem(R.id.menu_favorites).isChecked = true

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.menu_cars -> {
                    startActivity(Intent(this, CarsListActivity::class.java))
                    true
                }

                R.id.menu_favorites -> {
                    true
                }

                else -> false
            }
        }

        displayCarNames()
    }

    private fun displayCarNames() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val favouriteCars = UserService.getFavouriteCars()
                displayCars(favouriteCars)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun displayCars(filteredCars: List<Car>) {
        val linearLayout = findViewById<LinearLayout>(R.id.main)
        linearLayout.removeAllViews()

        filteredCars.forEach { car ->
            val textView = createCarTextView(car.name)
            textView.setOnClickListener {
                val intent = Intent(this, CarInfoActivity::class.java).apply {
                    putExtra("id", car.id)
                    putExtra("name", car.name)
                    putExtra("description", car.description)
                    putExtra("images", car.images.toTypedArray())
                    putExtra("from", "favourites")
                }
                startActivity(intent)
            }
            linearLayout.addView(textView)
        }
    }
}
package com.fatalryuu.driveit.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import com.fatalryuu.driveit.R
import com.fatalryuu.driveit.databinding.ActivityCarsListBinding
import com.fatalryuu.driveit.models.Car
import com.fatalryuu.driveit.services.CarsService
import com.fatalryuu.driveit.utils.createCarTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CarsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCarsListBinding
    private lateinit var cars: List<Car>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCarsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.menu.findItem(R.id.menu_cars).isChecked = true

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.menu_cars -> {
                    true
                }

                R.id.menu_favorites -> {
                    startActivity(Intent(this, FavouritesActivity::class.java))
                    true
                }

                else -> false
            }
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                filterCars(searchText)
            }
        })

        displayCarNames()
    }

    private fun displayCarNames() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                cars = CarsService.getCars()
                filterCars("") // Display all cars initially
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterCars(searchText: String) {
        val filteredCars = if (searchText.isNotEmpty()) {
            cars.filter { car ->
                car.name.contains(searchText, ignoreCase = true)
            }
        } else {
            cars
        }

        displayFilteredCars(filteredCars)
    }

    private fun displayFilteredCars(filteredCars: List<Car>) {
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
                    putExtra("from", "cars")
                }
                startActivity(intent)
            }
            linearLayout.addView(textView)
        }
    }
}
package com.fatalryuu.driveit.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.fatalryuu.driveit.R
import com.fatalryuu.driveit.databinding.ActivityCarInfoBinding
import com.fatalryuu.driveit.services.CarsService
import com.fatalryuu.driveit.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CarInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCarInfoBinding
    private var isFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCarInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val images = intent.getStringArrayExtra("images")
        val from = intent.getStringExtra("from")

        binding.name.text = name
        binding.description.text = description

        if (id != null) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val car = CarsService.getCarReferenceById(id)
                    isFavourite = UserService.isCarInFavorites(car)
                    binding.button.text =
                        if (isFavourite) getString(R.string.remove_from_favourites) else getString(R.string.add_to_favourites)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.button.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (id != null) {
                    try {
                        val carReference = CarsService.getCarReferenceById(id)
                        if (isFavourite) {
                            UserService.removeCarFromFavorites(carReference)
                        } else {
                            UserService.addCarToFavorites(carReference)
                        }
                        isFavourite = !isFavourite
                        binding.button.text =
                            if (isFavourite) getString(R.string.remove_from_favourites) else getString(
                                R.string.add_to_favourites
                            )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        binding.back.setOnClickListener {
            val intent = if (from == "cars") {
                Intent(this, CarsListActivity::class.java)
            } else {
                Intent(this, FavouritesActivity::class.java)
            }
            startActivity(intent)
        }

        val slides = ArrayList<SlideModel>()

        images?.forEach { imageUrl ->
            slides.add(SlideModel(imageUrl))
        }

        binding.slider.setImageList(slides, ScaleTypes.CENTER_CROP)
    }
}
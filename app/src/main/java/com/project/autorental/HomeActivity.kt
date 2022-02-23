package com.project.autorental

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.project.autorental.car_rental.CarRentalFragment
import com.project.autorental.databinding.ActivityHomeBinding
import com.project.autorental.profile.ProfileFragment
import com.project.autorental.transaction.TransactionFragment

class HomeActivity : AppCompatActivity() {

    private var binding: ActivityHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navView = findViewById<ChipNavigationBar>(R.id.nav_view)

        navView.setItemSelected(R.id.navigation_car_rental, true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CarRentalFragment()).commit()


        bottomMenu(navView)
    }

    @SuppressLint("NonConstantResourceId")
    private fun bottomMenu(navView: ChipNavigationBar) {
        navView.setOnItemSelectedListener { i: Int ->
            var fragment: Fragment? = null
            when (i) {
                R.id.navigation_car_rental -> fragment = CarRentalFragment()
                R.id.navigation_transaction -> fragment = TransactionFragment()
                R.id.navigation_profile -> fragment = ProfileFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    fragment!!
                ).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
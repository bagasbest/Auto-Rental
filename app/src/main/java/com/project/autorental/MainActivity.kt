package com.project.autorental

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.autorental.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Glide.with(this)
            .load(R.drawable.logo)
            .into(binding!!.imageView7)

        // cek apakah user udah pernah login sebelumnya
        checkUserLoginOrNotBefore()

        binding?.button?.setOnClickListener {
            login()
        }

        binding?.register?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun login() {
        val email = binding!!.emailEt.text.toString().trim()
        val password = binding!!.passwordEt.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Email must be filled", Toast.LENGTH_SHORT)
                .show()
            return
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password must be filled", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // login process
        binding!!.progressBar2.visibility = View.VISIBLE
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding!!.progressBar2.visibility = View.GONE
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    binding!!.progressBar2.visibility = View.GONE
                    showFailureDialog()
                }
            }
    }

    private fun checkUserLoginOrNotBefore() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    /// jika gagal login, munculkan alert dialog gagal
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Failure")
            .setMessage("There something wrong when login, Please chec your internet connection and try again")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
package com.project.autorental.car_rental

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.autorental.R
import com.project.autorental.databinding.ActivityCarRentalAddBinding


class CarRentalAddActivity : AppCompatActivity() {

    private var binding : ActivityCarRentalAddBinding? = null
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarRentalAddBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.backButton?.setOnClickListener {
          onBackPressed()
        }

        binding?.ArticleDp?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY)
        }

        binding?.uploadArticle?.setOnClickListener {
            uploadCar()
        }

    }

    private fun uploadCar() {

        val name = binding!!.nameEt.text.toString().trim()
        val type = binding!!.merkEt.text.toString().trim()
        val desc = binding!!.description.text.toString()
        val facility = binding!!.facility.text.toString()
        val price = binding!!.price.text.toString().trim()


        /// ini merpakan validasi kolom inputan, semua kolom wajib diisi
        when {
            name.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Car Name must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            type.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Car Type/Mark/Year must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            desc.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Car Description must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            facility.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Car Facility must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            price.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Car price must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            dp == null -> {
                Toast.makeText(
                    this,
                    "Car image must be added",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }


            // SIMPAN DATA KE DATABASE
            else -> {
                binding!!.progressBar.visibility = View.VISIBLE
                val uid = System.currentTimeMillis().toString()

                val product: MutableMap<String, Any> = HashMap()
                product["name"] = name
                product["description"] = desc
                product["facility"] = facility
                product["type"] = type
                product["price"] = price.toLong()
                product["uid"] = uid
                product["status"] = "ready"
                product["image"] = dp!!
                FirebaseFirestore
                    .getInstance()
                    .collection("car")
                    .document(uid)
                    .set(product)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            binding!!.progressBar.visibility = View.GONE
                            showSuccessDialog()
                        } else {
                            binding!!.progressBar.visibility = View.GONE
                            showFailureDialog()
                        }
                    }
            }
        }

    }


    /// tampilkan dialog box ketika gagal mengupload
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure to upload new car")
            .setMessage("Please, check your internet connection and try again")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// tampilkan dialog box ketika sukses mengupload
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Successfully upload new car")
            .setMessage("New car will be release!")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadArticleDp(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Please wait until process finished...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        val imageFileName = "car/data_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        dp = uri.toString()
                        Glide
                            .with(this)
                            .load(dp)
                            .into(binding!!.ArticleDp)
                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Failure upload car image",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Failure upload car image",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
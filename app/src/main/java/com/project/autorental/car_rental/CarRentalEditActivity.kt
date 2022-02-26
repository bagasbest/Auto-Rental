package com.project.autorental.car_rental

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.autorental.HomeActivity
import com.project.autorental.MainActivity
import com.project.autorental.R
import com.project.autorental.databinding.ActivityCarRentalEditBinding

class CarRentalEditActivity : AppCompatActivity() {

    private var binding: ActivityCarRentalEditBinding? = null
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001
    private var model: CarRentalModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarRentalEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_CAR)
        Glide.with(this)
            .load(model?.image)
            .into(binding!!.ArticleDp)

        binding?.nameEt?.setText(model?.name)
        binding?.merkEt?.setText(model?.type)
        binding?.description?.setText(model?.description)
        binding?.facility?.setText(model?.facility)
        binding?.price?.setText(model?.price.toString())


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
            updateCar()
        }
    }

    private fun updateCar() {

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


            // SIMPAN DATA KE DATABASE
            else -> {
                binding!!.progressBar.visibility = View.VISIBLE

                val product: MutableMap<String, Any> = HashMap()
                product["name"] = name
                product["description"] = desc
                product["facility"] = facility
                product["type"] = type
                product["price"] = price.toLong()
                if(dp != null) {
                    product["image"] = dp!!
                }
                model?.uid?.let {
                    FirebaseFirestore
                        .getInstance()
                        .collection("car")
                        .document(it)
                        .update(product)
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
    }



    /// tampilkan dialog box ketika gagal mengupload
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure to update car")
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
            .setTitle("Successfully update car")
            .setMessage("Success")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
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

    companion object {
        const val EXTRA_CAR = "car"
    }
}
package com.project.autorental.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.autorental.R
import com.project.autorental.databinding.ActivityTransactionDetailBinding
import java.text.DecimalFormat

class TransactionDetailActivity : AppCompatActivity() {

    private var binding: ActivityTransactionDetailBinding? = null
    private var model: TransactionModel? = null
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val formatter = DecimalFormat("#,###")

        checkRole()

        model = intent.getParcelableExtra(EXTRA_TRANSACTION)

        Glide.with(this)
            .load(model?.carImage)
            .into(binding!!.image)

        binding?.carName?.text = "Car Name: ${model?.carName}"
        binding?.carType?.text = "Car Type: ${model?.carType}"
        binding?.customerName?.text = "Customer Name: ${model?.customerName}"
        binding?.customerNik?.text = "Car Name: ${model?.customerNIK}"
        binding?.dateStart?.text = "Start Date: ${model?.dateStart}"
        binding?.dateFinish?.text = "Finish Date: ${model?.dateFinish}"
        binding?.duration?.text = "Duration: ${model?.duration}"
        binding?.pickHour?.text = "Pick Hour: ${model?.pickHour}"
        binding?.status?.text = "Status: ${model?.status}"
        binding?.finalPrice?.text = "Final Price: ${formatter.format(model?.finalPrice)}"

        if(model?.status == "Paid") {
            binding?.status?.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            Glide.with(this)
                .load(model?.paymentProof)
                .into(binding!!.ArticleDp)
        } else {
            binding?.status?.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            binding?.imageHint?.visibility = View.VISIBLE
            if(model?.paymentProof != "") {
                Glide.with(this)
                    .load(model?.paymentProof)
                    .into(binding!!.ArticleDp)
            }
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY)
        }

        binding?.acc?.setOnClickListener {
            accDialog()
        }


    }


    private fun accDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Payment Proof")
            .setMessage("Are you sure want to ACC this payment proof ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YES") { dialogInterface, _ ->
                dialogInterface.dismiss()
                accTransaction()
            }
            .setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun accTransaction() {
        model?.transactionId?.let {
            FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(it)
                .update("status", "Paid")
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "Successfully Acc Payment Proof", Toast.LENGTH_SHORT).show()
                        binding?.status?.text = "Status: Paid"
                        binding?.status?.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                        binding?.acc?.visibility = View.GONE
                    } else {
                        Toast.makeText(this, "Failure Acc Payment Proof", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if("" + it.data?.get("role") == "admin") {
                    binding?.acc?.visibility = View.VISIBLE
                }
            }
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
        val imageFileName = "paymentProof/data_" + System.currentTimeMillis() + ".png"
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
                        saveToDb()
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

    private fun saveToDb() {
        model?.transactionId?.let { it ->
            FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(it)
                .update("paymentProof", dp)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "Successfully upload payment proof", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failure upload payment proof", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_TRANSACTION = "transaction"
    }
}
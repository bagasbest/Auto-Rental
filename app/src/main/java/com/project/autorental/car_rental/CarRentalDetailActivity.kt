package com.project.autorental.car_rental

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.autorental.R
import com.project.autorental.databinding.ActivityCarRentalBinding
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toTimeUnit


class CarRentalDetailActivity : AppCompatActivity() {

    private var binding: ActivityCarRentalBinding? = null
    private var model: CarRentalModel? = null
    private var getCustomerName: String? = null
    private var getCustomerNIK: String? = null
    private var pickHour: String? = null
    private var counter = 0
    val user = FirebaseAuth.getInstance().currentUser


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarRentalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val formatter = DecimalFormat("#,###")

        model = intent.getParcelableExtra(EXTRA_CAR)
        Glide.with(this)
            .load(model?.image)
            .into(binding!!.image)

        checkRole()

        binding?.name?.text = model?.name
        binding?.type?.text = "Type: ${model?.type}"
        binding?.description?.text = model?.description
        binding?.facility?.text = model?.facility
        binding?.price?.text = "Rp. ${formatter.format(model?.price)}"

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


        binding?.rent?.setOnClickListener {
            rentCar()
        }

        binding?.delete?.setOnClickListener {
            showConfirmDialog()
        }

        binding?.edit?.setOnClickListener {

        }
    }

    @OptIn(ExperimentalTime::class)
    @SuppressLint("SimpleDateFormat")
    private fun rentCar() {
        // pilih tanggal peminjaman, pengguna harus memilih tanggal penyewaan dan tanggal pengembalian
        val now = Calendar.getInstance()
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()
            )
            .setSelection(androidx.core.util.Pair.create(now.timeInMillis, now.timeInMillis))
            .build()
        datePicker.show(supportFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {

            // setelah memilih, sistem akan melakukan konversi waktu penyewaan dan waktu pengembalian ke bentuk tanggal, contoh, 19-02-2021
            val prendiRange = datePicker.selection
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val formatFirst = sdf.format(Date(prendiRange?.first.toString().toLong()))
            val formatSecond = sdf.format(Date(prendiRange?.second.toString().toLong()))

            /// untuk penyewaan harian, minimal penyewaan adalah 1 hari
            if (formatFirst == formatSecond) {
                Toast.makeText(this, "Minimum rent a Car 1 Day", Toast.LENGTH_SHORT).show()
                return@addOnPositiveButtonClickListener
            }


            // show time picker, pilih waktu pengambilan barang
            val timePicker =
                MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
            timePicker.show(supportFragmentManager, timePicker.toString())
            timePicker.addOnPositiveButtonClickListener {

                /// tampilkan progress dialog
                val mProgressDialog =
                    ProgressDialog(this)
                mProgressDialog.setMessage("Please wait until process finished...")
                mProgressDialog.setCanceledOnTouchOutside(false)
                mProgressDialog.show()

                // ini merupakan kode untuk mengecek seluruh transaksi, yang berfungsi untuk mengecek apakah barang ini sedang di booking atau tidak
                FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .whereEqualTo("status", "Paid")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            /// cek apakah data transaksi ada atau tidak
                            val size = task.result

                            /// konversi waktu pengambilan barang, contoh waktu konversi: 13:30
                            pickHour = if (timePicker.minute < 10) {
                                timePicker.hour.toString() + ":0" + timePicker.minute
                            } else {
                                timePicker.hour.toString() + ":" + timePicker.minute
                            }

                            val durationEndInMillis = DurationUnit.SECONDS.toTimeUnit().toMillis(
                                DurationUnit.HOURS.toTimeUnit().toSeconds(
                                    timePicker.hour.toLong()
                                ) + DurationUnit.MINUTES.toTimeUnit()
                                    .toSeconds(timePicker.minute.toLong())
                            )



                            /// ambil jam saat ini, digunakan untuk memverifikasi penyewaan
                            val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            val timeNow = df.format(Date())
                            val dateNow = sdf.format(Date())
                            try {
                                val getTimeNow = df.parse(timeNow)
                                val getDateNow = sdf.parse(dateNow)
                                val nowFirst = sdf.parse(formatFirst)

                                if (getDateNow.time == nowFirst.time && getTimeNow.time > durationEndInMillis - 1000 * 60 * 60 * 7) {
                                    mProgressDialog.dismiss()
                                    AlertDialog.Builder(this)
                                        .setTitle("Failure")
                                        .setMessage("Sorry, the product pick-up time has passed, please enter the product pick-up time at least 1 hour ahead of the current hour")
                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                        .setPositiveButton("OK") { dialogInterface, _ ->
                                            mProgressDialog.dismiss()
                                            dialogInterface.dismiss()
                                        }
                                        .show()
                                    return@addOnCompleteListener
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (size.size() > 0) {
                                /// pengecekan pada transaksi, apakah barang yang dipilih sedang di sewa oleh orang lain atau tidak, jika disewa, maka pengguna saat ini tidak bisa menyewa
                                for (document in task.result) {
                                    try {
                                        val listName =
                                            document["name"] as ArrayList<*>
                                        val dateStart: Date =
                                            sdf.parse("" + document["dateStart"])
                                        val dateFinish: Date =
                                            sdf.parse("" + document["dateFinish"])
                                        val nowFirst: Date =
                                            sdf.parse(formatFirst)
                                        val nowSecond: Date =
                                            sdf.parse(formatSecond)
                                        val x: Long =
                                            dateStart.time
                                        val y: Long =
                                            dateFinish.time
                                        val dateStartNow: Long =
                                            nowFirst.time
                                        val dateFinishNow: Long =
                                            nowSecond.time


                                        // cek apakah tanggal sudah di booking atau belum oleh orang lain
                                        if (dateStartNow < x && dateStartNow < y && dateFinishNow < x && dateFinishNow < y
                                            || dateStartNow > x && dateStartNow > y && dateFinishNow > x && dateFinishNow > y
                                        ) {
                                            counter++
                                            if (counter == size.size()) {
                                                counter = 0
                                                mProgressDialog.dismiss()
                                                confirmSewaCarPerDay(
                                                    datePicker,
                                                    pickHour!!,
                                                )
                                            }
                                        } else {
                                            for (i in 0 until listName.size) {
                                                if (model!!.name == listName[i]
                                                ) {
                                                    counter = 0
                                                    mProgressDialog.dismiss()
                                                    Toast.makeText(
                                                        this,
                                                        "Tanggal Sudah Di Booking",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    return@addOnCompleteListener
                                                }
                                            }
                                            counter++
                                            if (counter == size.size()) {
                                                counter = 0
                                                mProgressDialog.dismiss()
                                                confirmSewaCarPerDay(
                                                    datePicker,
                                                    pickHour!!,
                                                )
                                            }
                                        }
                                    } catch (e: ParseException) {
                                        /// gagal mendapatkan data dari database
                                        mProgressDialog.dismiss()
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                mProgressDialog.dismiss()
                                /// jika belum ada transaksi sama sekali, maka cek apakah waktu penyewaan ini sudah sesuai dengan barang pertama di keranjang atau belum

                                pickHour?.let { it1 ->
                                    confirmSewaCarPerDay(
                                        datePicker,
                                        it1,
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

    /// fungsi lanjutan dari fungsi diatas, jika semua nya tervalidasi dan sukses, maka sistem akan menampilkan dialog box, apakah yakin ingin menyewa?
    @SuppressLint("SimpleDateFormat")
    private fun confirmSewaCarPerDay(
        datePicker: MaterialDatePicker<Pair<Long, Long>>,
        pickHour: String,
    ) {
        val prendiRange = datePicker.selection
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val formatFirst: String = sdf.format(Date(prendiRange?.first.toString().toLong()))
        val formatSecond: String = sdf.format(Date(prendiRange?.second.toString().toLong()))
        AlertDialog.Builder(this)
            .setTitle("Confirm Rent A Car")
            .setMessage("Are you sure want to continue Rent A Car ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { _, _ ->
                val diff: Long =
                    prendiRange?.second.toString().toLong() - prendiRange?.first.toString().toLong()
                val diffDays = diff / (24 * 60 * 60 * 1000)
                saveProductToDatabase(
                    formatFirst,
                    formatSecond,
                    diffDays,
                    pickHour
                )
            }
            .setNegativeButton("TIDAK") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    /// fungsi untuk membuat barang di simpan di keranjang   atau transaksi langsung
    private fun saveProductToDatabase(
        first: String,
        second: String,
        difference: Long,
        pickHour: String
    ) {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        val trId = System.currentTimeMillis().toString()

        val transaction: MutableMap<String, Any?> = HashMap()
        transaction["customerId"] = user?.uid
        transaction["customerName"] = getCustomerName
        transaction["customerNIK"] = getCustomerNIK
        transaction["finalPrice"] = model!!.price?.times(difference)
        transaction["dateFinish"] = second
        transaction["dateStart"] = first
        transaction["pickHour"] = pickHour
        transaction["status"] = "Not Paid"
        transaction["transactionId"] = trId
        transaction["CarId"] = model?.uid
        transaction["carName"] = model?.name
        transaction["carType"] = model?.type
        transaction["carImage"] = model?.image
        transaction["duration"] = "$difference Hari"
        transaction["paymentProof"] = ""


        //// simpan data kedalam firebase
        FirebaseFirestore
            .getInstance()
            .collection("transaction")
            .document(trId)
            .set(transaction)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mProgressDialog.dismiss()
                    showSuccessDialog()
                } else {
                    mProgressDialog.dismiss()
                    showFailureDialog()
                }
            }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure Rent A Car")
            .setMessage("Please check your internet connection")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Success Rent A Car")
            .setMessage("Please complete transaction")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }


    private fun showConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete Car")
            .setMessage("Are you sure want to delete this car ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YES") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deleteCar()
            }
            .setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteCar() {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Please wait until process finish...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        model?.uid?.let {
            FirebaseFirestore
                .getInstance()
                .collection("car")
                .document(it)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Success Delete Car",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    } else {
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Failure Delete Car",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        }
    }


    //// pengecekan role dibutuhkan, jika admin, maka admin dapat menghapus & mengedit data kamera
    private fun checkRole() {
        if (user != null) {
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    getCustomerName = "" + documentSnapshot["name"]
                    getCustomerNIK = "" + documentSnapshot["nik"]
                    if ("" + documentSnapshot["role"] == "admin") {
                        binding!!.edit.visibility = View.VISIBLE
                        binding!!.delete.visibility = View.VISIBLE
                    }
                }
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
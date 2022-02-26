package com.project.autorental

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.autorental.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // klik tombol registrasi
        binding?.button2?.setOnClickListener {
            registrateUser()
        }

        /// kembali ke halaman login
        binding!!.login.setOnClickListener { onBackPressed() }

    }

    /// fungsi untuk validasi inputan kolom - kolom registrasi
    private fun registrateUser() {
        val name = binding!!.nameEt.text.toString().trim()
        val username = binding!!.usernameEt.text.toString().trim()
        val phone = binding!!.phoneEt.text.toString().trim()
        val address = binding!!.addressEt.text.toString().trim()
        val email = binding!!.emailEt.text.toString().trim()
        val password = binding!!.passwordEt.text.toString().trim()

        // PILIH JENIS KELAMIN
        val selectId = binding!!.radioGroup2.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectId)
        when {
            name.isEmpty() -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Full name must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            username.isEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Username must be filled", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            phone.isEmpty() -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Phone Number must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            address.isEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Address must be filled", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Email must be filled", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            password.length < 6 -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password minimum 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            radioButton == null -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Gender must be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // simpan biodata kedalam database
            else -> {
                binding!!.progressBar3.visibility = View.VISIBLE
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            val register: MutableMap<String, Any> = HashMap()
                            register["name"] = name
                            register["username"] = username
                            register["phone"] = phone
                            register["address"] = address
                            register["email"] = email
                            register["password"] = password
                            register["gender"] = radioButton.text.toString()
                            register["uid"] = uid
                            register["role"] = "user"
                            FirebaseFirestore
                                .getInstance()
                                .collection("users")
                                .document(uid)
                                .set(register)
                                .addOnCompleteListener { task2: Task<Void?> ->
                                    if (task2.isSuccessful) {
                                        binding!!.progressBar3.visibility = View.GONE
                                        showSuccessDialog()
                                    } else {
                                        binding!!.progressBar3.visibility = View.GONE
                                        showFailureDialog()
                                    }
                                }
                        } else {
                            binding!!.progressBar3.visibility = View.GONE
                            showFailureDialog()
                        }
                    }
            }
        }

    }

    /// munculkan dialog ketika gagal registrasi
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure Registration")
            .setMessage("Please check your internet connection and try again")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// munculkan dialog ketika sukses registrasi
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Success Registration")
            .setMessage("Please login")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
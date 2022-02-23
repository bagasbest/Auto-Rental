package com.project.autorental.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.autorental.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        activity?.let {
            Glide.with(it)
                .load("https://images.unsplash.com/photo-1595439291859-89777a22e3c8?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=10")
                .into(binding!!.imageView2)
        }

        fetchUserData()

        return binding?.root
    }

    private fun fetchUserData() {


        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        // AMBIL DATA PENGGUNA DARI DATABASE, UNTUK DITAMPILKAN SEBAGAI PROFIL
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                val name = "" + documentSnapshot["name"].toString()
                val email = "" + documentSnapshot["email"].toString()
                val phone = "" + documentSnapshot["phone"].toString()
                val gender = "" + documentSnapshot["gender"].toString()
                val address = "" + documentSnapshot["address"].toString()
                val username = "" + documentSnapshot["username"].toString()

                //TERAPKAN PADA UI PROFIL
                binding!!.nameEt.setText(name)
                binding!!.emailEt.setText(email)
                binding!!.phoneEt.setText(phone)
                binding!!.addressEt.setText(address)
                binding!!.usernameEt.setText(username)

                if (gender == "Laki-laki") {
                    binding!!.male.isChecked = true
                } else {
                    binding!!.female.isChecked = true
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.e("Error get profil", e.toString())
                Toast.makeText(activity, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
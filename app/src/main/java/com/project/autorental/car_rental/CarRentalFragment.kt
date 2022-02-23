package com.project.autorental.car_rental

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.autorental.R
import com.project.autorental.databinding.FragmentCarRentalBinding


class CarRentalFragment : Fragment() {

    private var binding: FragmentCarRentalBinding? = null
    private var adapter: CarRentalAdapter? = null
    private var query: String? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCarRentalBinding.inflate(inflater, container, false)
        checkRole()

        val adapter = activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.filter, android.R.layout.simple_list_item_1
            )
        }
        // Specify the layout to use when the list of choices appears
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding!!.statusEt.setAdapter(adapter)
        binding!!.statusEt.setOnItemClickListener { _, _, _, _ ->
            initRecyclerView()
            query = binding!!.statusEt.text.toString()
            initViewModel()
        }


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.add?.setOnClickListener {
            startActivity(Intent(activity, CarRentalAddActivity::class.java))
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
                val role = it.data?.get("role").toString()
                if(role == "admin") {
                    binding?.add?.visibility = View.VISIBLE
                }
            }
    }


    private fun initRecyclerView() {
        binding?.carRv?.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        adapter = CarRentalAdapter()
        binding?.carRv?.adapter = adapter
    }


    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[CarRentalViewModel::class.java]
        binding?.progressBar?.visibility = View.VISIBLE
        when (query) {
            null -> {
                viewModel.setCarList()
            }
            "Filter by Lower Price" -> {
                viewModel.setCarListByLowerPrice()
            }
            "Filter by Higher Price" -> {
                viewModel.setCarListByHigherPrice()
            }
        }
        viewModel.getCarList().observe(viewLifecycleOwner) { car ->
            if (car.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter?.setData(car)
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding?.progressBar?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
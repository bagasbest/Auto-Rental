package com.project.autorental.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.autorental.R
import com.project.autorental.databinding.FragmentTransactionBinding


class TransactionFragment : Fragment() {

    private var binding: FragmentTransactionBinding? = null
    private var adapter: TransactionAdapter? = null
    private var status: String? = null
    private var role: String? = null

    override fun onResume() {
        super.onResume()
        checkRole()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(layoutInflater, container, false)


        val adapter = activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.status, android.R.layout.simple_list_item_1
            )
        }
        // Specify the layout to use when the list of choices appears
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding!!.statusEt.setAdapter(adapter)
        binding!!.statusEt.setOnItemClickListener { _, _, _, _ ->
            initRecyclerView()
            status = binding!!.statusEt.text.toString()
            initViewModel()
        }

        return binding?.root
    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                role = it.data?.get("role").toString()
                initRecyclerView()
                if(role == "admin") {
                    initViewModel()
                } else {
                    initViewModel()
                }
            }
    }


    private fun initRecyclerView() {
        binding?.transactionRv?.layoutManager = LinearLayoutManager(activity)
        adapter = TransactionAdapter()
        binding?.transactionRv?.adapter = adapter
    }


    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        binding?.progressBar?.visibility = View.VISIBLE
        if(role == "admin") {
           if(status == null) {
               viewModel.setAllTransactionList()
           } else {
               viewModel.setAllTransactionListByStatus(status!!)
           }
        } else {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            if(status == null) {
                viewModel.setTransactionListById(uid)
            } else {
                viewModel.setTransactionListByIdAndStatus(uid, status!!)
            }
        }

        viewModel.getTransactionList().observe(viewLifecycleOwner) { transaction ->
            if (transaction.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter?.setData(transaction)
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
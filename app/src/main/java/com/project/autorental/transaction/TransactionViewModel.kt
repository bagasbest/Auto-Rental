package com.project.autorental.transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class TransactionViewModel : ViewModel() {

    private val listTransaction: MutableLiveData<ArrayList<TransactionModel>> = MutableLiveData<ArrayList<TransactionModel>>()
    private val listItem: ArrayList<TransactionModel> = ArrayList()

    private val TAG: String = TransactionViewModel::class.java.simpleName

    fun setAllTransactionList() {
        listItem.clear()
        try {
            FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = TransactionModel()
                            model.carId = "" + document["carId"]
                            model.carImage = "" + document["carImage"]
                            model.carName = "" + document["carName"]
                            model.carType = "" + document["carType"]
                            model.customerId = "" + document["customerId"]
                            model.customerName = "" + document["customerName"]
                            model.customerNIK = "" + document["customerNIK"]
                            model.dateFinish = "" + document["dateFinish"]
                            model.dateStart = "" + document["dateStart"]
                            model.duration = "" + document["duration"]
                            model.finalPrice = document["finalPrice"] as Long
                            model.pickHour = "" + document["pickHour"]
                            model.status = "" + document["status"]
                            model.transactionId = "" + document["transactionId"]
                            model.paymentProof = "" + document["paymentProof"]

                            listItem.add(model)
                        }
                        listTransaction.postValue(listItem)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setAllTransactionListByStatus(status: String) {
        listItem.clear()
        try {
            FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = TransactionModel()
                            model.carId = "" + document["carId"]
                            model.carImage = "" + document["carImage"]
                            model.carName = "" + document["carName"]
                            model.carType = "" + document["carType"]
                            model.customerId = "" + document["customerId"]
                            model.customerName = "" + document["customerName"]
                            model.customerNIK = "" + document["customerNIK"]
                            model.dateFinish = "" + document["dateFinish"]
                            model.dateStart = "" + document["dateStart"]
                            model.duration = "" + document["duration"]
                            model.finalPrice = document["finalPrice"] as Long
                            model.pickHour = "" + document["pickHour"]
                            model.status = "" + document["status"]
                            model.transactionId = "" + document["transactionId"]
                            model.paymentProof = "" + document["paymentProof"]

                            listItem.add(model)
                        }
                        listTransaction.postValue(listItem)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setTransactionListById(customerId: String) {
        listItem.clear()
        try {
            FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = TransactionModel()
                            model.carId = "" + document["carId"]
                            model.carImage = "" + document["carImage"]
                            model.carName = "" + document["carName"]
                            model.carType = "" + document["carType"]
                            model.customerId = "" + document["customerId"]
                            model.customerName = "" + document["customerName"]
                            model.customerNIK = "" + document["customerNIK"]
                            model.dateFinish = "" + document["dateFinish"]
                            model.dateStart = "" + document["dateStart"]
                            model.duration = "" + document["duration"]
                            model.finalPrice = document["finalPrice"] as Long
                            model.pickHour = "" + document["pickHour"]
                            model.status = "" + document["status"]
                            model.transactionId = "" + document["transactionId"]
                            model.paymentProof = "" + document["paymentProof"]

                            listItem.add(model)
                        }
                        listTransaction.postValue(listItem)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setTransactionListByIdAndStatus(customerId: String, status: String) {
        listItem.clear()
        try {
            FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .whereEqualTo("customerId", customerId)
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = TransactionModel()
                            model.carId = "" + document["carId"]
                            model.carImage = "" + document["carImage"]
                            model.carName = "" + document["carName"]
                            model.carType = "" + document["carType"]
                            model.customerId = "" + document["customerId"]
                            model.customerName = "" + document["customerName"]
                            model.customerNIK = "" + document["customerNIK"]
                            model.dateFinish = "" + document["dateFinish"]
                            model.dateStart = "" + document["dateStart"]
                            model.duration = "" + document["duration"]
                            model.finalPrice = document["finalPrice"] as Long
                            model.pickHour = "" + document["pickHour"]
                            model.status = "" + document["status"]
                            model.transactionId = "" + document["transactionId"]
                            model.paymentProof = "" + document["paymentProof"]

                            listItem.add(model)
                        }
                        listTransaction.postValue(listItem)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getTransactionList(): LiveData<ArrayList<TransactionModel>> {
        return listTransaction
    }

}
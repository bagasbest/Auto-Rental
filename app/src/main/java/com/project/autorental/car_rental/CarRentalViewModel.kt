package com.project.autorental.car_rental

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class CarRentalViewModel : ViewModel() {

    private val listCar: MutableLiveData<ArrayList<CarRentalModel>> =
        MutableLiveData<ArrayList<CarRentalModel>>()
    private val listItem: ArrayList<CarRentalModel> = ArrayList()

    private val TAG: String = CarRentalViewModel::class.java.simpleName

    fun setCarList() {
        listItem.clear()
        try {
            FirebaseFirestore
                .getInstance()
                .collection("car")
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = CarRentalModel()
                            model.uid = "" + document["uid"]
                            model.description = "" + document["description"]
                            model.type = "" + document["type"]
                            model.facility = "" + document["facility"]
                            model.image = "" + document["image"]
                            model.name = "" + document["name"]
                            model.status = "" + document["status"]
                            model.price = document["price"] as Long

                            listItem.add(model)
                        }
                        listCar.postValue(listItem)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setCarListByLowerPrice() {
        listItem.clear()
        try {
            FirebaseFirestore
                .getInstance()
                .collection("car")
                .orderBy("price", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = CarRentalModel()
                            model.uid = "" + document["uid"]
                            model.description = "" + document["description"]
                            model.type = "" + document["type"]
                            model.facility = "" + document["facility"]
                            model.image = "" + document["image"]
                            model.name = "" + document["name"]
                            model.status = "" + document["status"]
                            model.price = document["price"] as Long

                            listItem.add(model)
                        }
                        listCar.postValue(listItem)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setCarListByHigherPrice() {
        listItem.clear()
        try {
            FirebaseFirestore
                .getInstance()
                .collection("car")
                .orderBy("price", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = CarRentalModel()
                            model.uid = "" + document["uid"]
                            model.description = "" + document["description"]
                            model.type = "" + document["type"]
                            model.facility = "" + document["facility"]
                            model.image = "" + document["image"]
                            model.name = "" + document["name"]
                            model.status = "" + document["status"]
                            model.price = document["price"] as Long

                            listItem.add(model)
                        }
                        listCar.postValue(listItem)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getCarList(): LiveData<ArrayList<CarRentalModel>> {
        return listCar
    }

}
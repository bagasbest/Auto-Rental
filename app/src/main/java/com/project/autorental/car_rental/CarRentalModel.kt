package com.project.autorental.car_rental

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarRentalModel(
    var uid: String? = null,
    var name: String? = null,
    var description: String? = null,
    var facility: String? = null,
    var type: String? = null,
    var price: Long? = 0L,
    var status: String? = null,
    var image: String? = null,
)  : Parcelable
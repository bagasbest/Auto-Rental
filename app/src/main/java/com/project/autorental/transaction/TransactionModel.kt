package com.project.autorental.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    var transactionId: String? = null,
    var customerId: String? = null,
    var customerName: String? = null,
    var finalPrice: Long? = 0L,
    var dateFinish: String? = null,
    var dateStart: String? = null,
    var pickHour: String? = null,
    var status: String? = null,
    var carId: String? = null,
    var carName: String? = null,
    var carType: String? = null,
    var carImage: String? = null,
    var duration: String? = null,
    var paymentProof: String? = null,
) : Parcelable
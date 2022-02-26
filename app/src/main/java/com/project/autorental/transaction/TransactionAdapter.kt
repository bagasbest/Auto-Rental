package com.project.autorental.transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.autorental.car_rental.CarRentalDetailActivity
import com.project.autorental.car_rental.CarRentalModel
import com.project.autorental.databinding.ItemCarBinding
import com.project.autorental.databinding.ItemTransactionBinding
import java.text.DecimalFormat

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val listTransaction: ArrayList<TransactionModel> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<TransactionModel>?) {
        listTransaction.clear()
        listTransaction.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private var binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: TransactionModel) {
            val formatter = DecimalFormat("#,###")
            with(binding) {


                Glide.with(itemView.context)
                    .load(model.carImage)
                    .into(image)


                carName.text = "Car Name: ${model.carName}"
                customerName.text = "Cust.name: ${model.customerName}"
                rentDate.text = "Rent Date: ${model.dateStart} - ${model.dateFinish}"
                price.text = "RM ${formatter.format(model.finalPrice)}"
                carType.text = "Car Type: ${model.carType}"
                status.text = "Status: ${model.status}"


                if(model.status == "Paid") {
                    status.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark))
                } else {
                    status.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, TransactionDetailActivity::class.java)
                    intent.putExtra(TransactionDetailActivity.EXTRA_TRANSACTION, model)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTransaction[position])
    }

    override fun getItemCount(): Int  = listTransaction.size
}
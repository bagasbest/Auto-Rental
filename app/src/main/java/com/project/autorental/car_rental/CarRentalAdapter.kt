package com.project.autorental.car_rental

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.autorental.databinding.ItemCarBinding
import java.text.DecimalFormat


class CarRentalAdapter : RecyclerView.Adapter<CarRentalAdapter.ViewHolder>() {

    private val listCar: ArrayList<CarRentalModel> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<CarRentalModel>?) {
        listCar.clear()
        listCar.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private var binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: CarRentalModel) {
            val formatter = DecimalFormat("#,###")
            with(binding) {


                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)


                name.text = model.name
                price.text = "Rp. ${formatter.format(model.price)}"
                description.text = model.description
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listCar[position])
    }

    override fun getItemCount(): Int  = listCar.size
}
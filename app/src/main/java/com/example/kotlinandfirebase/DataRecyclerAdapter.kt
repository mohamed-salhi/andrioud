package com.example.kotlinandfirebase

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinandfirebase.databinding.ItemDataBinding

class DataRecyclerAdapter(
    private var listData: List<ModelData>,
    private val onClickListener: OnClickListener?
) :
    RecyclerView.Adapter<DataRecyclerAdapter.DataViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAdapter(id: String) {
        listData = listData.dropWhile { it.id == id }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class DataViewHolder(private val binding: ItemDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ModelData) {
            binding.tvName.text = data.name
            binding.tvAddress.text = data.address
            binding.tvNumber.text = data.number

            binding.ibDelete.setOnClickListener {
                onClickListener?.onClick(data.id)
            }
        }
    }
}
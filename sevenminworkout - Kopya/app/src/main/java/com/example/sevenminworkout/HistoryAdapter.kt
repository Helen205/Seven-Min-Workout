package com.example.sevenminworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sevenminworkout.databinding.ItemHistoryRowBinding


class HistoryAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryRowBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val date: String = items.get(position)

        holder.tvPosition.text = (position + 1).toString()
        holder.tvItem.text = date

        // Listedeki tek/çift konumlara göre arka plan rengi güncelleniyor.
        if (position % 2 == 0) {
            holder.llHistoryItemMain.setBackgroundColor(
                Color.parseColor("#EBEBEB")
            )
        } else {
            holder.llHistoryItemMain.setBackgroundColor(
                Color.parseColor("#FFFFFF")
            )
        }
    }
    /**
     * Listedeki öğe sayısını alır
     */
    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(binding: ItemHistoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        // Her öğeyi eklenecek olan TextView'u tutar
        val llHistoryItemMain = binding.llHistoryItemMain
        val tvItem = binding.tvItem
        val tvPosition = binding.tvPosition
    }
}
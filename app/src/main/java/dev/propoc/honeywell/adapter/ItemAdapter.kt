package dev.propoc.honeywell.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.propoc.honeywell.R
import dev.propoc.honeywell.model.Items

class ItemAdapter(): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.name.text = item.name
        if (item.image != null) {
            holder.imageItem.setImageDrawable(BitmapDrawable(item.image))
        }
        holder.cardView.setCardBackgroundColor(item.colorCoding)
        holder.itemView.setOnClickListener {
            setClickListener?.onItemClicked(position)
        }
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var name: TextView = itemView.findViewById(R.id.nameTv)
        var imageItem: ImageView = itemView.findViewById(R.id.image_item)
        var cardView: CardView = itemView.findViewById(R.id.itemView)
    }

    fun setItemClickListener(listener: ItemClickListener) {
        setClickListener = listener
    }

    interface ItemClickListener {
        fun onItemClicked(position: Int)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Items>(){
        override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
            return  oldItem.name == newItem.name
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    companion object {
        private var setClickListener: ItemClickListener? = null
    }

}
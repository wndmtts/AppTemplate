package com.jailton.apptemplateproject.baseclasses


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jailton.apptemplateproject.R

class StoreAdapter(private val context: Context, private val storeList: List<Item>) :
    RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.storeNameTextView.text = store.name
        holder.storeEmailTextView.text = store.email
        holder.storeEnderecoTextView.text = store.endereco
        // Ajustar a visibilidade do ícone do mapa
        if (store.endereco.isEmpty()) {
            holder.mapIconImageView.visibility = View.GONE
        } else {
            holder.mapIconImageView.visibility = View.VISIBLE
        }
        holder.mapIconImageView.setOnClickListener {
            val endereco = holder.storeEnderecoTextView.text.toString()
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(endereco)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            }
        }


        Glide.with(holder.storeImageView.context)
            .load(store.imageUrl)
            .into(holder.storeImageView)
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImageView: ImageView = itemView.findViewById(R.id.storeImageView)
        val storeNameTextView: TextView = itemView.findViewById(R.id.storeNameTextView)
        val storeEnderecoTextView: TextView = itemView.findViewById(R.id.storeEnderecoTextView)
        val storeEmailTextView: TextView = itemView.findViewById(R.id.storeEmailTextView)
        val mapIconImageView: ImageView = itemView.findViewById(R.id.mapIconImageView)


    }
}

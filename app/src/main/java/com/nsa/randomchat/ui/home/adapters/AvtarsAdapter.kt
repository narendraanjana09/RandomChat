package com.nsa.randomchat.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.nsa.randomchat.R
import com.nsa.randomchat.ui.home.models.AvtarModel
import com.google.android.material.card.MaterialCardView

class AvtarsAdapter(
    val context: Context,
    val list: List<AvtarModel>,
    val onImageClickListener: OnImageClickListener?
): RecyclerView.Adapter<AvtarsAdapter.ViewHolder>() {


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val imageView : ImageView = itemView.findViewById(R.id.image_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.avtar_recyler_item,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            imageView.loadSvg(list[position].link)
            if(list[position].selected){
                ( itemView as MaterialCardView).strokeColor=context.resources.getColor(R.color.teal_700)
            }else{
                ( itemView as MaterialCardView).strokeColor=context.resources.getColor(R.color.white)
            }
            itemView.setOnClickListener {
                onImageClickListener?.onClick(adapterPosition)
            }
        }
    }
    fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }

    override fun getItemCount(): Int = list.size

    interface OnImageClickListener{
        fun onClick(index:Int)
    }

}

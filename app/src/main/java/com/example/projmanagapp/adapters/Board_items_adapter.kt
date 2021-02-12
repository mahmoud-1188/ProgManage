package com.example.projmanagapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projmanagapp.R
import com.example.projmanagapp.models.Boardmodel
import kotlinx.android.synthetic.main.board_items.view.*

class Board_items_adapter (val context:Context, val list :ArrayList<Boardmodel>):RecyclerView.Adapter<Board_items_adapter.viewholder>(){


    private var onclicklistener:OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {

        return viewholder(LayoutInflater.from(context).inflate(R.layout.board_items,parent,false))
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val model = list[position]

        // use glide library to set profile image..
        Glide
            .with(context)
            .load(model.image)
            .circleCrop()
            .placeholder(R.drawable.ic_my_profile)
            .into(holder.itemView.board_item_image)

        holder.itemView.items_bord_name.text = model.name
        holder.itemView.items_created_by.text = "Created by: ${model.createdby}"

        // on click listener...
        holder.itemView.setOnClickListener {

            if (onclicklistener != null){
                onclicklistener!!.onclick(position,model)
            }
        }
    }

    // on click listener interface..
    interface OnClickListener {
        fun onclick(position:Int, model:Boardmodel)
    }

    // create set on click listener method..
    fun setonclicklistener(onclicklistner:OnClickListener){

        this.onclicklistener = onclicklistner
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class viewholder(view: View):RecyclerView.ViewHolder(view){

    }

}
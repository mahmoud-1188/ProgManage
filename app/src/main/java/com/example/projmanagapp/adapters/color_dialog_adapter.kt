package com.example.projmanagapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projmanagapp.R
import kotlinx.android.synthetic.main.item_lable_color.view.*

class color_dialog_adapter (val context:Context,val list:ArrayList<String>,val mselectedcolor:String):RecyclerView.Adapter<color_dialog_adapter.myviewholder>(){

    var onClicklistener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {

        return myviewholder(LayoutInflater.from(context).inflate(R.layout.item_lable_color,parent,false))
    }

    override fun onBindViewHolder(holder: myviewholder, position: Int) {

        val item = list[position]

        if (holder is myviewholder){

            holder.itemView.color_view.setBackgroundColor(Color.parseColor(item))

            if (item == mselectedcolor){

                holder.itemView.cheek_image.visibility = View.VISIBLE
            }else{
                holder.itemView.cheek_image.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onClicklistener != null) {

                    onClicklistener!!.onclick(position,item)
                }
            }
          }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class myviewholder(view:View):RecyclerView.ViewHolder(view){

    }


    interface OnClickListener {

        fun onclick (position:Int, color:String)
    }
}
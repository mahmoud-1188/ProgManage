package com.example.projmanagapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projmanagapp.Activites.task_list_Activity
import com.example.projmanagapp.R
import com.example.projmanagapp.models.SelectMember
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.card_add_member_item.view.*

class cardmemberlistitemadapter(val context:Context,val list:ArrayList<SelectMember>,val assignedto:Boolean):RecyclerView.Adapter<cardmemberlistitemadapter.myviewholder>() {

    private var onclicklistener:OnClickListener? = null

    class myviewholder (view:View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {
        return myviewholder(LayoutInflater.from(context).inflate(R.layout.card_add_member_item,parent,false))
    }

    override fun onBindViewHolder(holder: myviewholder, position: Int) {

        val model = list[position]


        if (position == list.size -1 && assignedto){

            holder.itemView.card_member_add_image.visibility = View.VISIBLE
            holder.itemView.card_member_image.visibility = View.GONE
        }else{

            holder.itemView.card_member_add_image.visibility = View.GONE
            holder.itemView.card_member_image.visibility = View.VISIBLE

            // use glide library to set profile image..
            Glide
                    .with(context)
                    .load(model.image)
                    .circleCrop()
                    .placeholder(R.drawable.ic_my_profile)
                    .into(holder.itemView.card_member_image)
        }


        holder.itemView.setOnClickListener {

            if (onclicklistener != null){

                onclicklistener!!.onclick()
            }
        }

    }

    override fun getItemCount(): Int {

        return list.size
    }

     fun setOnClickListener(onclicklistener:OnClickListener){

         this.onclicklistener = onclicklistener
     }

    interface OnClickListener{

        fun onclick ()
    }
}
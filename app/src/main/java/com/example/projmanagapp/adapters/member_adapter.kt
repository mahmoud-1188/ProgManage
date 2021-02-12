package com.example.projmanagapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projmanagapp.R
import com.example.projmanagapp.models.User
import com.example.projmanagapp.utils.Constant

import kotlinx.android.synthetic.main.member_items.view.*

class member_adapter(val context:Context,val list:ArrayList<User>):RecyclerView.Adapter<member_adapter.viewholder>() {

      private var onClicklistener: OnClickListener? = null

    class viewholder (view:View):RecyclerView.ViewHolder(view){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {

        return viewholder(LayoutInflater.from(context).inflate(R.layout.member_items,parent,false))

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val model = list[position]


        // use glide library to set profile image..
        Glide
            .with(context)
            .load(model.image)
            .circleCrop()
            .placeholder(R.drawable.ic_my_profile)
            .into(holder.itemView.iv_member_image)

        holder.itemView.tv_member_name.text = model.name
        holder.itemView.tv_member_email.text = model.email

        if (model.select){
            holder.itemView.cheek_member_image.visibility = View.VISIBLE
        }else{
            holder.itemView.cheek_member_image.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (onClicklistener != null) {

                if(model.select) {
                    onClicklistener!!.onclick(position, model, Constant.UN_SELECT)
                }else{
                    onClicklistener!!.onclick(position,model, Constant.SELECT)
            }
          }
        }
    }

    override fun getItemCount(): Int {

        return list.size
    }

    fun setOnclicklistener(onclicklistener:OnClickListener){

        this.onClicklistener = onclicklistener
    }
    interface OnClickListener {

        fun onclick(position: Int, model: User,action:String)
    }
}
package com.example.projmanagapp.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projmanagapp.Activites.task_list_Activity
import com.example.projmanagapp.R
import com.example.projmanagapp.models.Card
import com.example.projmanagapp.models.SelectMember
import kotlinx.android.synthetic.main.card_items.view.*

open class card_items_adapter (val context:Context, val cardlist:ArrayList<Card>):RecyclerView.Adapter<card_items_adapter.myviewholder>(){


    private var onclicklistener: OnClickListener? = null

    class myviewholder(view: View) :RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {

       return myviewholder(LayoutInflater.from(context).inflate(R.layout.card_items,parent,false))
    }

    override fun onBindViewHolder(holder: myviewholder, position: Int) {


        val model = cardlist[position]

        if (model.color.isNotEmpty()) {

            holder.itemView.card_view.visibility = View.VISIBLE
            holder.itemView.card_view.setBackgroundColor(Color.parseColor(model.color))
        }else{
            holder.itemView.card_view.visibility = View.GONE
        }


        if ((context as task_list_Activity).mAssignedmemberslist.size > 0){

            val selectedmemberlist:ArrayList<SelectMember> = ArrayList()


            for (i in context.mAssignedmemberslist.indices){
                for (j in model.assignedto){
                    if (context.mAssignedmemberslist[i].id ==j){

                        val selectmember = SelectMember(
                                context.mAssignedmemberslist[i].id,
                                context.mAssignedmemberslist[i].image
                        )
                        selectedmemberlist.add(selectmember)
                    }
                }
            }

            if (selectedmemberlist.size > 0){
                if (selectedmemberlist.size == 1 && selectedmemberlist[0].id == model.createdby){
                    holder.itemView.rv_card_member.visibility = View.GONE

                }else{

                        holder.itemView.rv_card_member.visibility = View.VISIBLE


                        holder.itemView.rv_card_member.layoutManager = GridLayoutManager(context,4)

                        val adapter = cardmemberlistitemadapter(context,selectedmemberlist,false)
                        holder.itemView.rv_card_member.adapter = adapter

                        adapter.setOnClickListener(object :cardmemberlistitemadapter.OnClickListener{
                            override fun onclick() {

                                if (onclicklistener !=null){

                                    onclicklistener!!.onclick(position)
                                }
                            }
                        })
                    }
            }else{

                holder.itemView.rv_card_member.visibility = View.GONE
        }
      }

        holder.itemView.card_name.text = model.name

        holder.itemView.setOnClickListener {

            if (onclicklistener != null){
                onclicklistener!!.onclick(position)
            }
        }
    }


    // on click listener interface..
    interface OnClickListener {
        fun onclick(cardposition:Int)
    }

    // create set on click listener method..
    fun setonclicklistener(onclicklistner:OnClickListener){

        this.onclicklistener = onclicklistner
    }

    override fun getItemCount(): Int {

        return cardlist.size
    }
}
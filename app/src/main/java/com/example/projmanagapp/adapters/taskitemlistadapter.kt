package com.example.projmanagapp.adapters

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projmanagapp.Activites.task_list_Activity
import com.example.projmanagapp.models.task
import com.example.projmanagapp.R
import com.example.projmanagapp.models.Card
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*
import kotlin.collections.ArrayList


open class taskitemlistadapter (private var context:task_list_Activity,private var list:ArrayList<task>):RecyclerView.Adapter<taskitemlistadapter.myviewholder>(){


    private var mdraggerpositionfrom = -1
    private var mtargetpositionto = -1


    class myviewholder(view: View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task,parent,false)

        // determine layout width and heights to display..
        val layoutparams = LinearLayout.
                           LayoutParams((parent.width * 0.7).toInt(),
                           LinearLayout.LayoutParams.WRAP_CONTENT)

        // set margin to the layout...
            layoutparams.setMargins(15.toDp().toPx(),0,40.toDp().toPx(),0)

           view.layoutParams = layoutparams

        return myviewholder(view)
    }

    override fun onBindViewHolder(holder: myviewholder, position: Int) {
        val model = list[position]

        if (holder is myviewholder){
            if (position == list.size -1){ // check if the position equal the last model in the list..if so >
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE // show add task button
                holder.itemView.ll_task_item.visibility = View.GONE // hide task items layout
            }else{

                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }
                // set task title..
                holder.itemView.tv_task_list_title.text = model.title
                //add task button on click listener..
                holder.itemView.tv_add_task_list.setOnClickListener {

                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
            }

                holder.itemView.ib_close_list_name.setOnClickListener {

                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE

            }

               holder.itemView.ib_done_list_name.setOnClickListener {

                val listname = holder.itemView.et_task_list_name.text.toString()

                if (listname.isNotEmpty()){

                        context.createtasklist(listname)


                }else{

                    Toast.makeText(context,"please enter list name",Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.ib_edit_list_name.setOnClickListener {

                holder.itemView.et_edit_task_list_name.setText(model.title)
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_editable_view.setOnClickListener {


                holder.itemView.cv_edit_task_list_name.visibility = View.GONE
                holder.itemView.ll_title_view.visibility = View.VISIBLE
            }

            holder.itemView.ib_done_edit_list_name.setOnClickListener {

                val listname = holder.itemView.et_edit_task_list_name.text.toString()

                if (listname.isNotEmpty()){

                         context.updatetasklist(position,listname,model)


                }else{

                    Toast.makeText(context,"please enter list name",Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.ib_delete_list.setOnClickListener {

                alertdialogfordeletinglist(position,model.title)
            }

            holder.itemView.tv_add_card.setOnClickListener {

                holder.itemView.cv_add_card.visibility = View.VISIBLE
                holder.itemView.tv_add_card.visibility = View.GONE
            }

            holder.itemView.ib_close_card_name.setOnClickListener {

                holder.itemView.cv_add_card.visibility = View.GONE
                holder.itemView.tv_add_card.visibility = View.VISIBLE
            }

            holder.itemView.ib_done_card_name.setOnClickListener {

                val cardname = holder.itemView.et_card_name.text.toString()

                if (cardname.isNotEmpty()){

                    context.createcardlist(position,cardname)

                }else{

                    Toast.makeText(context,"please enter list name",Toast.LENGTH_SHORT).show()
                }
            }
        }

        // prepare recycle view and adapter to display cards...
        holder.itemView.rv_card_list.layoutManager = LinearLayoutManager(context)
        holder.itemView.rv_card_list.setHasFixedSize(true)

        val adapter = card_items_adapter(context,model.card)
        holder.itemView.rv_card_list.adapter = adapter

        adapter.setonclicklistener(object :card_items_adapter.OnClickListener{
            override fun onclick(cardposition: Int) {

                if (context is task_list_Activity){
                    context.cardDetails(position,cardposition)
                }
            }
        })

        // this code for drop and drag function...
        val DividerItemDecoration = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        holder.itemView.rv_card_list.addItemDecoration(DividerItemDecoration)

        val helber = ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP,ItemTouchHelper.DOWN){
            override fun onMove(
                    recyclerView: RecyclerView,
                    dragger: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
            ): Boolean {

                val draggedposision = dragger.adapterPosition
                val targetposition = target.adapterPosition

                if (mdraggerpositionfrom == -1){

                    mdraggerpositionfrom = draggedposision
                }

                mtargetpositionto = targetposition

                Collections.swap(model.card,draggedposision,targetposition)

                adapter.notifyItemMoved(draggedposision,targetposition)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
       }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                if (mdraggerpositionfrom != -1 && mtargetpositionto != -1 &&
                        mdraggerpositionfrom != mtargetpositionto){

                    context.updatecardsintasklist(position,model.card)
                }

                mdraggerpositionfrom = -1
                mtargetpositionto = -1
            }
        })

        helber.attachToRecyclerView(holder.itemView.rv_card_list)
    }


    override fun getItemCount(): Int {
       return list.size
    }

    // delete task list dialog..
    private fun alertdialogfordeletinglist(position:Int,title:String){

        val Bulder = AlertDialog.Builder(context)
        Bulder.setTitle("Alert")
        Bulder.setMessage("are you sure you want to delete $title")
        Bulder.setPositiveButton("Yes"){dialoginterface,whitch ->

            dialoginterface.dismiss()

                context.deletetasklist(position)

        }.setNegativeButton("NO"){
            dialoginterface,whitch ->

            dialoginterface.dismiss()
        }

        val alertdialog = Bulder.create()
        alertdialog.setCancelable(false)
        alertdialog.show()
    }

    // get density pixel from pixel...
    private fun Int.toDp():Int=
        (this / Resources.getSystem().displayMetrics.density).toInt()
    // get pixel from density pixel..to determine how the application will display..
    private fun Int.toPx():Int=
        (this * Resources.getSystem().displayMetrics.density).toInt()
}
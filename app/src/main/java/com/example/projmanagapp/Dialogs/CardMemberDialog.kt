package com.example.projmanagapp.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projmanagapp.R
import com.example.projmanagapp.adapters.member_adapter
import com.example.projmanagapp.models.User
import kotlinx.android.synthetic.main.item_color_dialog.view.*

abstract class CardMemberDialog(context:Context,val members:ArrayList<User>,val titile:String):Dialog(context) {

    private var adapter : member_adapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.item_color_dialog, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        seuptrecycleview(view)


    }

    private fun seuptrecycleview(view:View){

        view.select_txt_dialog.text = titile
        adapter = member_adapter(context,members)

        view.rv_color_dialog.layoutManager = LinearLayoutManager(context)
        view.rv_color_dialog.adapter = adapter

        adapter!!.setOnclicklistener(object :member_adapter.OnClickListener{
            override fun onclick(position: Int, model: User, action: String) {
                dismiss()
                onitemselected(model,action)
            }
        })
    }

    protected abstract fun onitemselected (user:User, action:String)

}
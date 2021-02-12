package com.example.projmanagapp.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projmanagapp.R
import com.example.projmanagapp.adapters.color_dialog_adapter
import kotlinx.android.synthetic.main.item_color_dialog.*
import kotlinx.android.synthetic.main.item_color_dialog.view.*

abstract class LabelColorListDialog (
        context:Context,
        val list:ArrayList<String>,
        val titile :String = "",
        var mselctedcolor:String = ""
):Dialog(context){

    private var adapter :color_dialog_adapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.item_color_dialog,null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        seuptrecycleview(view)

    }


    private fun seuptrecycleview(view:View){

        view.select_txt_dialog.text = titile
        adapter = color_dialog_adapter(context,list,mselctedcolor)
        view.rv_color_dialog.layoutManager = LinearLayoutManager(context)
        view.rv_color_dialog.adapter = adapter

        adapter!!.onClicklistener = object :color_dialog_adapter.OnClickListener{
            override fun onclick(position: Int, color: String) {
                dismiss()
                onitemselected(color)
            }
        }
    }

    protected abstract fun onitemselected(color:String)

}
package com.example.projmanagapp.Activites

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.GridView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projmanagapp.Dialogs.CardMemberDialog
import com.example.projmanagapp.Dialogs.LabelColorListDialog
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import com.example.projmanagapp.adapters.cardmemberlistitemadapter
import com.example.projmanagapp.models.Boardmodel
import com.example.projmanagapp.models.Card
import com.example.projmanagapp.models.SelectMember
import com.example.projmanagapp.models.User
import com.example.projmanagapp.utils.Constant
import kotlinx.android.synthetic.main.activity_card__detials_.*
import kotlinx.android.synthetic.main.activity_task_list_.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class Card_Detials_Activity : BaseActivity() {

    lateinit var mboarddetails :Boardmodel
    private var taskposition = -1
    private var cardposition = -1
    private var mcolorselected = ""
    lateinit var mboardmemberdetailslist :ArrayList<User>
    private var mselecteddueDateMilliseconds:Long = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card__detials_)

        getintentdata()
        settoolbar()

        mcolorselected = mboarddetails.taskList[taskposition].card[cardposition].color
        if (mcolorselected.isNotEmpty()){
            setcolor()
        }

        mselecteddueDateMilliseconds = mboarddetails.taskList[taskposition].card[cardposition].duedat

        if (mselecteddueDateMilliseconds > 0 ){

            val sdf = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val selecteddate = sdf.format(mselecteddueDateMilliseconds)

            Due_Date.text = selecteddate
        }



        Due_Date.setOnClickListener {

            pickdueDateDialog()
        }

        select_member.setOnClickListener {
            choosecardmemberDialog()
        }

        Label_color.setOnClickListener {

            labelcolorlistdialog()
        }

        // set the card name..
        carded_name.setText(mboarddetails.taskList[taskposition].card[cardposition].name)
        carded_name.setSelection(carded_name.text.toString().length)// this line to focus at the end of the text..

        carddetails_update_btn.setOnClickListener {

            if (carded_name.text.toString().isNotEmpty()) {
                ubdatecarddetails()
            }else{
                Toast.makeText(this,"please enter card name ",Toast.LENGTH_SHORT).show()
            }
        }

        setupselectedmemeberlist()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.delet_card_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.delete_card_menu ->{

                alertdaialog(mboarddetails.taskList[taskposition].card[cardposition].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    // date picker dialog...
    @RequiresApi(Build.VERSION_CODES.N)
    private fun pickdueDateDialog(){

        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { view, year, monthofyear, dayofmonth ->

            val sdayofmonth = if (dayofmonth < 10)"0${dayofmonth}" else "$dayofmonth"
            val smonthofyear = if (monthofyear + 1 <10) "0${monthofyear +1}" else "$monthofyear"

            val selectedDate = "$sdayofmonth/$smonthofyear/$year"
            Due_Date.text = selectedDate

            val sdf = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val theDate = sdf.parse(selectedDate)

            mselecteddueDateMilliseconds = theDate!!.time

        },year,month,day)

        dpd.show()

    }

    // method return list of color to select card color field...
    private fun colorlist():ArrayList<String>{
        val colorlist :ArrayList<String> = ArrayList()

        colorlist.add("#0591F8")
        colorlist.add("#7A8089")
        colorlist.add("#3A3345")
        colorlist.add("#EC0606")
        colorlist.add("#FF000000")
        colorlist.add("#FF3700B3")
        colorlist.add("#FF447C03")
        colorlist.add("#F44336")

        return colorlist
    }

    // setup card color function...
    private fun setcolor(){

        Label_color.text = ""
        Label_color.setBackgroundColor(Color.parseColor(mcolorselected))
   }

    // choose card color dialog...
    private fun labelcolorlistdialog(){

        val colorlist :ArrayList<String> = colorlist()

      val listdialog = object :LabelColorListDialog(this,
              colorlist,"Select Color",mcolorselected){
            override fun onitemselected(color: String) {

                mcolorselected = color
                setcolor()
            }
        }

        listdialog.show()
    }


    // card member chooser dialog...
    private fun choosecardmemberDialog() {

        val cardassinedmemberlist = mboarddetails.taskList[taskposition].card[cardposition].assignedto

        if (cardassinedmemberlist.size > 0) {

            for (i in mboardmemberdetailslist.indices) {
                for (j in cardassinedmemberlist) {
                    if (mboardmemberdetailslist[i].id == j)
                        mboardmemberdetailslist[i].select = true
                }
            }

        } else {
            for (i in mboardmemberdetailslist.indices) {
                mboardmemberdetailslist[i].select = false
            }
        }

        val cardmembers = object : CardMemberDialog(this,mboardmemberdetailslist,"Select member"){
            override fun onitemselected(user: User, action: String) {

                if (action == Constant.SELECT){

                    if (!cardassinedmemberlist.contains( user.id)){

                        cardassinedmemberlist.add(user.id)

                    }
                }else{

                    cardassinedmemberlist.remove(user.id)


                    for (i in mboardmemberdetailslist.indices){

                        if (mboardmemberdetailslist[i].id == user.id){
                            mboardmemberdetailslist[i].select = false
                        }
                    }
                }

               setupselectedmemeberlist()
            }
        }
        cardmembers.show()
    }


    // set up card member list and prepare recycle view (card details activity)...
    private fun setupselectedmemeberlist(){

        val cardmemberassinedlist = mboarddetails.taskList[taskposition].card[cardposition].assignedto

        val selectedmemberlist :ArrayList<SelectMember> = ArrayList()

        for (i in mboardmemberdetailslist.indices) {
            for (j in cardmemberassinedlist) {
                if (mboardmemberdetailslist[i].id == j){

                    val selectedmember = SelectMember(
                        mboardmemberdetailslist[i].id,
                        mboardmemberdetailslist[i].image
                )

                    selectedmemberlist.add(selectedmember)
             }
          }
      }

        if (selectedmemberlist.size > 0){

            selectedmemberlist.add(SelectMember("",""))
            select_member.visibility = View.GONE
            rv_select_member.visibility = View.VISIBLE

            val adapter = cardmemberlistitemadapter(this,selectedmemberlist,true)
            rv_select_member.layoutManager = GridLayoutManager(this,6)
            rv_select_member.adapter = adapter

            adapter.setOnClickListener(object :cardmemberlistitemadapter.OnClickListener{
                override fun onclick() {
                    choosecardmemberDialog()
                }
            })

        }else{

            select_member.visibility = View.VISIBLE
            rv_select_member.visibility = View.GONE

        }
    }


    fun addupdatetasklistsuccess(){

        hideprogressdialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    // function to update the card...
    private fun ubdatecarddetails(){

        val card = Card(
                carded_name.text.toString(),
                mboarddetails.taskList[taskposition].card[cardposition].createdby,
                mboarddetails.taskList[taskposition].card[cardposition].assignedto,
                mcolorselected,mselecteddueDateMilliseconds
        )

        mboarddetails.taskList[taskposition].card[cardposition] = card

        val tasklist = mboarddetails.taskList

        tasklist.removeAt(tasklist.size -1)

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().addupdateboardlist(this,mboarddetails)

    }

    // delete card function...
    private fun deletecard(){

        val cards = mboarddetails.taskList[taskposition].card

        cards.removeAt(cardposition)

        val tasklist = mboarddetails.taskList

        tasklist.removeAt(tasklist.size -1)

        tasklist[taskposition].card = cards

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().addupdateboardlist(this,mboarddetails)

    }

    // alert dialog for delete button...
    private fun alertdaialog (title:String){

        val bulder = AlertDialog.Builder(this)
        bulder.setMessage("are you sure you want to delete $title")
                .setTitle("Alert")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("yes"){ dialoginterface,watch ->

                    deletecard()
                    dialoginterface.dismiss()
                }.setNegativeButton("no"){dialoginterface,watch ->

                    dialoginterface.dismiss()
                }

        val alertdialog = bulder.create()

        alertdialog.setCancelable(false)
        alertdialog.show()
    }
        // set tool bare..
        private fun settoolbar() {

            setSupportActionBar(card_details_toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)

            supportActionBar!!.title = mboarddetails.taskList[taskposition].card[cardposition].name
            card_details_toolbar.setNavigationOnClickListener { onBackPressed() }
        }

    // get data from intent..
   private fun getintentdata() {

        if (intent.hasExtra(Constant.BOARD_DETAILS)) {

            mboarddetails = intent.getParcelableExtra<Boardmodel>(Constant.BOARD_DETAILS)!!
        }

        if (intent.hasExtra(Constant.TASK_LIST_POSITION)) {

            taskposition = intent.getIntExtra(Constant.TASK_LIST_POSITION, -1)
        }

        if (intent.hasExtra(Constant.CARD_POSITION)) {

            cardposition = intent.getIntExtra(Constant.CARD_POSITION, -1)
        }

        if (intent.hasExtra(Constant.BORD_MEMBERS_LIST)) {

            mboardmemberdetailslist = intent.getParcelableArrayListExtra(Constant.BORD_MEMBERS_LIST)!!
        }

    }
}
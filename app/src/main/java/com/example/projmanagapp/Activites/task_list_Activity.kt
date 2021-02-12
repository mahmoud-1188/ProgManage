package com.example.projmanagapp.Activites


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import com.example.projmanagapp.adapters.card_items_adapter
import com.example.projmanagapp.adapters.taskitemlistadapter
import com.example.projmanagapp.models.Boardmodel
import com.example.projmanagapp.models.Card
import com.example.projmanagapp.models.User
import com.example.projmanagapp.models.task
import com.example.projmanagapp.utils.Constant
import kotlinx.android.synthetic.main.activity_task_list_.*

class task_list_Activity : BaseActivity() {

    lateinit var mboarddetails :Boardmodel  // variable to receive board..
    lateinit var mdocumentid :String
    lateinit var mAssignedmemberslist :ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list_)


        // receive data from intent..
        if (intent.hasExtra(Constant.DOCUMENT_ID)){

            mdocumentid = intent.getStringExtra(Constant.DOCUMENT_ID)!!
        }

        Log.i("document id",mdocumentid)

        // firebase call...
        showprogressdialog(resources.getString(R.string.dialog_progress))
        FirestoreClass().getBoarddetails(this,mdocumentid)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEMBER_REQUSTE_CODE && resultCode == Activity.RESULT_OK || requestCode == CARD_REQUSTE_CODE){

            // getting data when push member or card details back button and refresh task activity with changes..
            showprogressdialog(resources.getString(R.string.dialog_progress))
            FirestoreClass().getBoarddetails(this,mdocumentid)
        }else{

            Log.e("canceled","Canceled")
        }

    }


    // update tasks lists with new cards position...
    fun updatecardsintasklist(tasklistposition:Int,card:ArrayList<Card>){

        mboarddetails.taskList.removeAt(mboarddetails.taskList.size -1)

        mboarddetails.taskList[tasklistposition].card = card

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().addupdateboardlist(this,mboarddetails)

    }

    fun boardmembersDetialslist(list:ArrayList<User>){

        mAssignedmemberslist = list
        hideprogressdialog()

        val addtasklist = task(resources.getString(R.string.action_add_list))

        // add title to task list..
        mboarddetails.taskList.add(addtasklist)

        // make layout out as horizontal orientation..
        rv_tasklist.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_tasklist.setHasFixedSize(true)

        val adapter = taskitemlistadapter(this,mboarddetails.taskList)
        rv_tasklist.adapter = adapter

    }

    // start card details activity and send data in intent...
    fun cardDetails(tasklistposition:Int,cardposition:Int){

        val intent = Intent(this,Card_Detials_Activity::class.java)
        intent.putExtra(Constant.BOARD_DETAILS,mboarddetails)
        intent.putExtra(Constant.TASK_LIST_POSITION,tasklistposition)
        intent.putExtra(Constant.BORD_MEMBERS_LIST,mAssignedmemberslist)
        intent.putExtra(Constant.CARD_POSITION,cardposition)

        startActivityForResult(intent, CARD_REQUSTE_CODE)
    }

    // inflate menu layout..
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // menu item selected fun...
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.member_menu ->{

                val intent = Intent(this,member_Activity::class.java)
                intent.putExtra(Constant.BOARD_DETAILS,mboarddetails)
                startActivityForResult(intent, MEMBER_REQUSTE_CODE)
            }

        }
        return super.onOptionsItemSelected(item)
    }
    // set tool bare..
    private fun settoolbar(){

        setSupportActionBar(task_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)
        supportActionBar!!.title = mboarddetails.name

        task_toolbar.setNavigationOnClickListener { onBackPressed() }
    }


    // get board tasks details..and prepare the task recycle view to display..
    fun getborddetails(board:Boardmodel){

        mboarddetails = board
        hideprogressdialog()
        settoolbar()

        showprogressdialog(resources.getString(R.string.dialog_progress))
        FirestoreClass().getmembers(this,mboarddetails.assignedto)
    }

    // add update task list success method..
    fun addupdatetasklistsuccess(){

        hideprogressdialog()

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().getBoarddetails(this,mboarddetails.documentid)
    }

    // create task list method..
    fun createtasklist(tasklistname:String){

        val task = task(tasklistname,mboarddetails.createdby)

        mboarddetails.taskList.add(0,task)
        mboarddetails.taskList.removeAt(mboarddetails.taskList.size -1)

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().addupdateboardlist(this,mboarddetails)


    }

    // update task list method..
    fun updatetasklist(position:Int,listname:String,model:task){

        val task = task(listname,model.createdby)

        mboarddetails.taskList[position] = task
        mboarddetails.taskList.removeAt(mboarddetails.taskList.size -1)

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().addupdateboardlist(this,mboarddetails)
    }


    // delete task list method..
    fun deletetasklist(position:Int){

        mboarddetails.taskList.removeAt(position)
        mboarddetails.taskList.removeAt(mboarddetails.taskList.size -1)

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().addupdateboardlist(this,mboarddetails)
    }

     // create card method..
    fun createcardlist(position:Int,cardname:String){

         // remove last item (add list button)..
        mboarddetails.taskList.removeAt(mboarddetails.taskList.size -1)
        val cardassigneduserlist : ArrayList<String> = ArrayList() // create array list for assigned to..
        cardassigneduserlist.add(FirestoreClass().getcurrentuserid()) // add current user to assigned to..
         //assign card variables..
        val card = Card(cardname,FirestoreClass().getcurrentuserid(),cardassigneduserlist)
         //create card path variable..
        val cardlist = mboarddetails.taskList[position].card

        cardlist.add(card)  // add a card to card list ..

         // create a task..
        val task = task(mboarddetails.taskList[position].title,
                        mboarddetails.taskList[position].createdby,
                         cardlist)

         // replace the task with new task..
        mboarddetails.taskList[position] = task

        showprogressdialog(resources.getString(R.string.dialog_progress))

        FirestoreClass().addupdateboardlist(this,mboarddetails) // update the board and recycle view..
    }

    companion object{

        const val MEMBER_REQUSTE_CODE = 14
        const val CARD_REQUSTE_CODE = 15
    }
}
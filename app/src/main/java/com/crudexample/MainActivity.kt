package com.crudexample

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    internal lateinit var editText_fnm: EditText
    internal lateinit var editText_lnm: EditText
    internal lateinit var editText_phoneno: EditText
    internal lateinit var button_add: Button
    internal lateinit var button_clear: Button
    internal lateinit var button_delete: Button
    internal lateinit var listView: ListView
    internal lateinit var databaseAdapter: DatabaseAdapter
    internal var infoStudents = ArrayList<InfoStudent>()
    internal lateinit var c: Cursor
    internal var newpos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseAdapter = DatabaseAdapter(this@MainActivity)
        init()
        fillListView()
        registerForContextMenu(listView)
        /*
        databaseAdapter.openDatabase();

        //SEarch record
        Cursor cursor=databaseAdapter.searchRecord("pradip");
        for (int i = 0; i < cursor.getCount(); i++) {
            String firstname = cursor.getString(cursor.getColumnIndex("firstname"));
            Log.e("name","--------------"+firstname);
            cursor.moveToNext();
        }*/

        //        addStudLikeRecord("1", "110");
        //        addStudLikeRecord("1", "110");
        //        addStudLikeRecord("1", "110");
        //        addStudLikeRecord("2", "111");
        //
        //        addStudCommentRecord("1", "Hello", "1", "110");
        //        addStudCommentRecord("1", "test", "1", "110");
        //        addStudCommentRecord("1", "good", "1", "110");
        //        addStudCommentRecord("2", "super", "1", "111");
        //

        databaseAdapter.openDatabase()

        //SEarch record
        val cursor = databaseAdapter.allStudLikeValues
        for (i in 0 until cursor.count) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            Log.e("like id", "-------------------$id")
            cursor.moveToNext()
        }
        cursor.close()


        val cursor1 = databaseAdapter.countLike("1")
        cursor1.moveToFirst()
        val count = cursor1.count
        Log.e("count", "------------------" + cursor1.getInt(0))


    }

    private fun init() {
        ids()//find view id

        button_add.setOnClickListener {
            addRecord()
            fillListView()
        }
        button_clear.setOnClickListener {
            editText_fnm.setText("")
            editText_lnm.setText("")
            editText_phoneno.setText("")
        }
        button_delete.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete All Records")
            builder.setMessage("Are you sure..")
            builder.setPositiveButton("yes"
            ) { dialog, which ->
                // TODO Auto-generated method stub
                databaseAdapter.openDatabase()
                databaseAdapter.deleteAllRecord()

                databaseAdapter.closeDatabase()

                Toast.makeText(applicationContext,
                        "All Records Deleted", Toast.LENGTH_LONG)
                        .show()
                fillListView()
            }
            builder.setNegativeButton("No"
            ) { dialog, which ->
                // TODO Auto-generated method stub
            }
            val alert = builder.create()
            alert.show()
        }

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            newpos = position
            Log.e("list_position", newpos.toString() + "")
            false
        }
    }

    //Cutom Adapter class Created
    internal inner class CustomAdaper(var mContext: Context, resource: Int, var list: ArrayList<InfoStudent>) : ArrayAdapter<InfoStudent>(mContext, resource, list) {

        override fun getCount(): Int {
            return list.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            convertView = layoutInflater.inflate(R.layout.detail, null)
            val t1 = convertView!!.findViewById<View>(R.id.textView_fnm) as TextView
            val t2 = convertView.findViewById<View>(R.id.textView_lnm) as TextView
            val t3 = convertView.findViewById<View>(R.id.textView_phone) as TextView
            val student = list[position]
            t1.text = student.fname
            t2.text = student.lname
            t3.text = student.phone
            return convertView
        }
    }

    //Find view by id
    fun ids() {
        editText_fnm = findViewById<View>(R.id.editText_fnm) as EditText
        editText_lnm = findViewById<View>(R.id.editText_lnm) as EditText
        editText_phoneno = findViewById<View>(R.id.editText_phoneno) as EditText
        button_add = findViewById<View>(R.id.button_add) as Button
        button_clear = findViewById<View>(R.id.button_clear) as Button
        button_delete = findViewById<View>(R.id.button_delete) as Button
        listView = findViewById<View>(R.id.listView) as ListView
    }

    //Add Record
    fun addRecord() {
        databaseAdapter.openDatabase()
        databaseAdapter.addValues(editText_fnm.text.toString(), editText_lnm.text.toString(), editText_phoneno.text.toString())
        Toast.makeText(applicationContext, "Record Add Successfully", Toast.LENGTH_SHORT).show()
        databaseAdapter.closeDatabase()
    }

    //Add stud like record
    fun addStudLikeRecord(studid: String, uid: String) {
        databaseAdapter.openDatabase()
        databaseAdapter.addStudLike(studid, uid)
        Toast.makeText(applicationContext, "like Add Successfully", Toast.LENGTH_SHORT).show()
        databaseAdapter.closeDatabase()
    }

    //Add stud comment record
    fun addStudCommentRecord(studid: String, comment: String, is_approve: String, uid: String) {
        databaseAdapter.openDatabase()
        databaseAdapter.addStudComment(studid, comment, is_approve, uid)
        Toast.makeText(applicationContext, "comment Add Successfully", Toast.LENGTH_SHORT).show()
        databaseAdapter.closeDatabase()
    }


    //View in listview
    fun fillListView() {
        databaseAdapter.openDatabase()
        c = databaseAdapter.allValues
        Log.e("check_list", "cursor_abpve_for")
        infoStudents = ArrayList()
        for (i in 0 until c.count) {
            Log.e("check_list", "cursor")
            val id = c.getString(c.getColumnIndex("id"))
            val firstname = c.getString(c.getColumnIndex("firstname"))
            val lastname = c.getString(c.getColumnIndex("lastname"))
            val phone = c.getString(c.getColumnIndex("phone_no"))
            infoStudents.add(InfoStudent(firstname, lastname, phone))
            c.moveToNext()
            val customAdaper = CustomAdaper(this, R.layout.detail, infoStudents)
            listView.adapter = customAdaper

        }
        Log.e("infoStudents", infoStudents.size.toString() + "c")
        if (infoStudents.size == 0) {
            infoStudents.clear()
            listView.adapter = null
        }
        databaseAdapter.closeDatabase()

    }

    //Delete One Record
    fun deleteOneRecord(newpos: Int) {
        databaseAdapter.openDatabase()
        c.moveToPosition(newpos)
        val id = c.getString(0)
        databaseAdapter.deleteOneRecord(id)
        databaseAdapter.closeDatabase()
        Toast.makeText(applicationContext, "Delete Successfully", Toast.LENGTH_SHORT).show()
    }

    fun updateDialog(pos: Int) {
        Log.e("posi", pos.toString() + "")
        val up_Dialog = Dialog(this@MainActivity)
        up_Dialog.setTitle("Update Detail")
        up_Dialog.setContentView(R.layout.update_detail)
        up_Dialog.show()
        //Update Dialog Control
        val update_editText_fnm = up_Dialog.findViewById<View>(R.id.update_editText_fnm) as EditText
        val update_editText_lnm = up_Dialog.findViewById<View>(R.id.update_editText_lnm) as EditText
        val update_editText_phone = up_Dialog.findViewById<View>(R.id.update_editText_phone) as EditText
        val update_button = up_Dialog.findViewById<View>(R.id.update_button) as Button
        val update_cancel = up_Dialog.findViewById<View>(R.id.update_cancel) as Button


        databaseAdapter.openDatabase()
        c.moveToPosition(pos)

        val fname = c.getString(1)
        val lname = c.getString(2)
        val phone = c.getString(3)
        Log.e("First name", fname)
        Log.e("Last name", lname)
        Log.e("Phone no", phone)
        update_editText_fnm.setText(fname)
        update_editText_lnm.setText(lname)
        update_editText_phone.setText(phone)
        databaseAdapter.closeDatabase()
        update_button.setOnClickListener {
            val id = c.getString(0)
            databaseAdapter.openDatabase()
            databaseAdapter.updateRecord(id, update_editText_fnm.text.toString(), update_editText_lnm.text.toString(), update_editText_phone.text.toString())
            databaseAdapter.closeDatabase()
            fillListView()

            up_Dialog.dismiss()
            Toast.makeText(this@MainActivity, "Record Updated", Toast.LENGTH_SHORT).show()
        }
        update_cancel.setOnClickListener { up_Dialog.dismiss() }

    }

    //Context Menu
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Select The Action")
        menu.add(0, v.id, 0, "Delete")//groupId, itemId, order, title
        menu.add(0, v.id, 0, "Update")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title === "Delete") {
            deleteOneRecord(newpos)
            fillListView()
        } else if (item.title === "Update") {
            updateDialog(newpos)


            Toast.makeText(applicationContext, "Update Successfully", Toast.LENGTH_SHORT).show()
        } else {
            return false
        }
        return true
    }

}

package com.crudexample

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.util.ArrayList

class DatabaseAdapter internal constructor(context: Context) {

    private var mydb: SQLiteDatabase? = null
    private val helper: DHelper

    //View all stud data
    /* if(c!=null && c.moveToNext())
        {
            Log.e("ID", c.getString(c.getColumnIndex("id")));
            Log.e("FName", c.getString(c.getColumnIndex("firstname")));
            Log.e("Lname", c.getString(c.getColumnIndex("lastname")));
            Log.e("Phone", c.getString(c.getColumnIndex("phone_no")));
        }else{
            Log.e("ELSE", "ELSE");
        }*/ val allValues: Cursor
        get() {
            val columns = arrayOf(ID, FIRSTNAME, LASTNAME, PHONE_NO)
            val c = mydb!!.query(true, TABLE_NAME, columns, null, null, ID, null, null, null)
            c.moveToNext()

            return c

        }

    //View all stud like
    //
    //        if(c!=null && c.moveToNext())
    //        {
    //            Log.e("ID", c.getString(c.getColumnIndex("id")));
    //
    //        }else{
    //            Log.e("ELSE", "ELSE");
    //        }
    val allStudLikeValues: Cursor
        get() {
            val columns = arrayOf(ID, SID, UID)
            val c = mydb!!.query(true, TABLE_NAME_LIKE, columns, null, null, ID, null, null, null)
            c.moveToNext()

            return c

        }

    //View all stud like
    val allStudCommentValues: Cursor
        get() {
            val columns = arrayOf(ID, SID, COMMENT, IS_APPROVE, UID)
            val c = mydb!!.query(true, TABLE_NAME_COMMENT, columns, null, null, ID, null, null, null)
            c.moveToNext()
            return c

        }

    init {
        helper = DHelper(context)
    }

    internal fun openDatabase(): DatabaseAdapter {
        mydb = helper.writableDatabase
        return this
    }

    fun closeDatabase() {
        helper.close()
    }


    //count like

    fun countLike(studid: String): Cursor {
        return mydb!!.rawQuery("select count(*) from stud_like where studid=$studid", null)
    }


    //Insert Record stud
    fun addValues(fnm: String, lnm: String, phone: String) {
        val values = ContentValues()
        values.put(FIRSTNAME, fnm)
        values.put(LASTNAME, lnm)
        values.put(PHONE_NO, phone)
        mydb!!.insert(TABLE_NAME, null, values)
    }


    //Insert record stud like
    fun addStudLike(studid: String, uid: String) {
        val values = ContentValues()
        values.put(SID, studid)
        values.put(UID, uid)
        mydb!!.insert(TABLE_NAME_LIKE, null, values)
    }

    //Insert record stud comment
    fun addStudComment(studid: String, comment: String, is_approve: String, uid: String) {
        val contentValues = ContentValues()
        contentValues.put(SID, studid)
        contentValues.put(COMMENT, comment)
        contentValues.put(IS_APPROVE, is_approve)
        contentValues.put(UID, uid)
        mydb!!.insert(TABLE_NAME_COMMENT, null, contentValues)
    }


    fun searchRecord(search: String): Cursor {
        //        Cursor c=mydb.rawQuery("select * from stud where phone_no="+search+" order by id desc",null);
        val c = mydb!!.query(true, TABLE_NAME, arrayOf(ID, FIRSTNAME, LASTNAME, PHONE_NO), PHONE_NO + "='" + search
                + "'", null, null, null, "id desc", null)

        //        Cursor c=mydb.query(true,TABLE_NAME,new String[] { ID,FIRSTNAME,LASTNAME,PHONE_NO},PHONE_NO+ "='" + search
        //                + "'",new String[] { ID,FIRSTNAME,LASTNAME,PHONE_NO},null,null,"id desc",null);


        c.moveToNext()
        return c
    }

    fun getlist(search: String): ArrayList<String> {

        val alTasklist = ArrayList<String>()

        try {
            val mCursor = mydb!!.query(true, TABLE_NAME,
                    arrayOf(ID, FIRSTNAME, LASTNAME, PHONE_NO), PHONE_NO + "='" + search
                    + "'", arrayOf(ID, FIRSTNAME, LASTNAME, PHONE_NO), null, null, null, null)

            if (mCursor != null) {
                mCursor.moveToFirst()
                for (i in 0 until mCursor.count) {
                    alTasklist.add(mCursor.getString(0))
                    mCursor.moveToNext()
                }
                mCursor.close()
                return alTasklist
            }
            return alTasklist

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return alTasklist

    }


    fun deleteOneRecord(id: String) {
        mydb!!.delete(TABLE_NAME, "$id=$ID", null)
    }

    fun deleteAllRecord() {
        mydb!!.delete(TABLE_NAME, null, null)
    }

    fun updateRecord(id: String, fname: String, lname: String, phone: String) {
        val values = ContentValues()
        values.put(FIRSTNAME, fname)
        values.put(LASTNAME, lname)
        values.put(PHONE_NO, phone)
        mydb!!.update(TABLE_NAME, values, "$id=$ID", null)
    }

    internal inner class DHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            try {
                //stud table
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + FIRSTNAME + " TEXT, "
                        + LASTNAME + " TEXT, "
                        + PHONE_NO + " TEXT ); ")

                //stud like table
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_LIKE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + SID + " TEXT, "
                        + UID + " TEXT ); ")


                //stud comment table
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_COMMENT + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + SID + " TEXT, "
                        + COMMENT + " TEXT, "
                        + IS_APPROVE + " TEXT, "
                        + UID + " TEXT ); ")


            } catch (se: SQLException) {
                Log.e("Sql Exception", se.toString())
            }

        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // on upgrade drop older tables
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_LIKE")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_COMMENT")

            // create new tables
            onCreate(db)
        }
    }

    companion object {
        private val DATABASE_NAME = "db"
        private val DATABASE_VERSION = 4

        //Stud table
        private val TABLE_NAME = "stud"
        private val TABLE_NAME_LIKE = "stud_like"
        private val TABLE_NAME_COMMENT = "stud_comment"
        private val ID = "id"
        private val FIRSTNAME = "firstname"
        private val LASTNAME = "lastname"
        private val PHONE_NO = "phone_no"

        //stud like table
        private val LID = "id"
        private val SID = "studid"
        private val UID = "userid"

        //stud comment table
        private val CID = "id"
        private val COMMENT = "comment"
        private val IS_APPROVE = "is_approve"
    }
}

package com.viratara.android.quickshare

import android.content.Context




class MyPreferences (context: Context){
    val PREFERENCE_NAME = "preference_file"
    val PREFERENCE_ID = "file id"


    val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getFileId() : List<String>{
        val ids : String? = preference.getString(PREFERENCE_ID,"")
        return ids!!.split(",")

    }

    fun setFileId(ids: String) {
        val list = getFileId().plus(ids)
        val csvList : StringBuilder = StringBuilder()
        for (s in list) {
            csvList.append(s)
            csvList.append(",")
        }
        val editor = preference.edit()
        editor.putString(PREFERENCE_ID, csvList.toString())
        editor.apply()

    }

}
package com.viratara.android.quickshare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView

class HistoryActivity : AppCompatActivity() {
    

    private lateinit var listView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val mypreference = MyPreferences(this)
        listView = findViewById<ListView>(R.id.id_list_view)
        val listItems = mypreference.getFileId()
        val adapter = IdAdapter(this, listItems.filter { it.length>1 }.asReversed())
        listView.adapter = adapter
        
    }
}

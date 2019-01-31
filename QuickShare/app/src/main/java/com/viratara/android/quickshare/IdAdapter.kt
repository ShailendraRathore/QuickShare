package com.viratara.android.quickshare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viratara.android.quickshare.R
import android.R.attr.button
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.*
import kotlinx.android.synthetic.main.id_list.view.*


class IdAdapter( context: Context,
                private val dataSource: List<String>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.id_list, parent, false)
        val idText = rowView.findViewById<TextView>(R.id.idText)
        val copyButton = rowView.findViewById<Button>(R.id.copyButton)
        val id = getItem(position).toString()
        idText.text = id
        copyButton.setOnClickListener {

            copyText(id, rowView)

        }

        return rowView
    }

    private fun copyText(string: String,v: View) {
        val context = v.context
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("File Id", string)
        clipboard.primaryClip = clip


    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }




}
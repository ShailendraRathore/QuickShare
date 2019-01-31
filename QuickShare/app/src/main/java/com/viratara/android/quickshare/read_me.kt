package com.viratara.android.quickshare

import android.os.Bundle
import android.app.Activity
import android.graphics.Color
import kotlinx.android.synthetic.main.activity_read_me.*
import github.hotstu.zebratextview.ZebraTextView



class read_me : Activity() {
  
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_me)
        val zebraText = findViewById<ZebraTextView>(R.id.text)
        zebraText.setEvenLineColor(Color.WHITE)
        zebraText.setOddLineColor(Color.parseColor("#3498db"))
    }

}

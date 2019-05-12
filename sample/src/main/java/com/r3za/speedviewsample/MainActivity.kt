package com.r3za.speedviewsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        s1.partColors = arrayListOf(R.color.sample1,R.color.sample2,R.color.sample3,R.color.sample4,R.color.sample5)
        s2.partColors = arrayListOf(R.color.sample10,R.color.sample20,R.color.sample30,R.color.sample40,R.color.sample50)
        s3.partColors = arrayListOf(R.color.sample100,R.color.sample200,R.color.sample300,R.color.sample400,R.color.sample500)
        s4.partColors = arrayListOf(R.color.sample1000,R.color.sample2000,R.color.sample3000,R.color.sample4000,R.color.sample5000)

        btnSubmit.setOnClickListener {
            s1.setViewProgress(etProgress.text.toString().toInt())
            s2.setViewProgress(etProgress.text.toString().toInt())
            s3.setViewProgress(etProgress.text.toString().toInt())
            s4.setViewProgress(etProgress.text.toString().toInt(),false)
            s5.setViewProgress(etProgress.text.toString().toInt())
        }

    }
}

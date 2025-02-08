package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdaccount)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.lblname).text = GlobalClass.NameIco
        findViewById<TextView>(R.id.txtnameAcc).text = GlobalClass.FullName
        findViewById<TextView>(R.id.txtunameAcc).text = GlobalClass.UName
        findViewById<TextView>(R.id.txtemailAcc).text = GlobalClass.Email


        findViewById<ImageView>(R.id.btnback).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.btnlogout).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnsettings).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnchangepin).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, ChangePin::class.java)
            startActivity(intent)
        }
    }
}
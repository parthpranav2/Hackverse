package com.example.hackverse

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChangePin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdchangepin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btnback).setOnClickListener {
            finish()
        }


        findViewById<Button>(R.id.btnchangepin).setOnClickListener {
            val oldpin = findViewById<EditText>(R.id.txtpinold).text.toString()
            val newpin = findViewById<EditText>(R.id.txtpinnew).text.toString()

            // Retrieve employee email from SharedPreferences
            val editor = getSharedPreferences("MY_SETTING", MODE_PRIVATE)
            val Email = editor.getString("Email", null)

            if (Email.isNullOrEmpty()) {
                Toast.makeText(this, "User email not found. Please log in again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dbRef = FirebaseDatabase.getInstance().getReference("User").child(Email)

                    // Query the "Pin" field
                    dbRef.child("Pin").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val Pin = snapshot.getValue(String::class.java)

                                if(Pin==oldpin){
                                    if(oldpin==newpin){
                                        findViewById<TextView>(R.id.txtnotice).setTextColor(Color.parseColor("#EB7167"))
                                        findViewById<EditText>(R.id.txtpinold).background = ContextCompat.getDrawable(this@ChangePin, R.drawable.pinrejection)
                                        findViewById<EditText>(R.id.txtpinnew).background = ContextCompat.getDrawable(this@ChangePin, R.drawable.pinrejection)
                                        findViewById<EditText>(R.id.txtpinnew).text.clear()
                                    }else{
                                        if(newpin!=findViewById<EditText>(R.id.txtpinrnew).text.toString()){
                                            findViewById<LinearLayout>(R.id.linelpinmismatch).visibility= View.VISIBLE
                                            findViewById<EditText>(R.id.txtpinnew).background = ContextCompat.getDrawable(this@ChangePin, R.drawable.pinrejection)
                                            findViewById<EditText>(R.id.txtpinrnew).background = ContextCompat.getDrawable(this@ChangePin, R.drawable.pinrejection)
                                            findViewById<EditText>(R.id.txtpinnew).text.clear()
                                            findViewById<EditText>(R.id.txtpinrnew).text.clear()
                                        }else{

                                            dbRef.child("Pin").setValue(newpin)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            this@ChangePin,
                                                            "Pin updated successfully!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            this@ChangePin,
                                                            "Failed to update pin!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }
                                }else{
                                    findViewById<LinearLayout>(R.id.linelincorrectoldpin).visibility= View.VISIBLE
                                    findViewById<EditText>(R.id.txtpinold).background = ContextCompat.getDrawable(this@ChangePin, R.drawable.pinrejection)
                                    findViewById<EditText>(R.id.txtpinold).text.clear()
                                }


                            } else {
                                Toast.makeText(this@ChangePin, "Pin record not found in the database.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database error
                            Toast.makeText(this@ChangePin, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

    }
}
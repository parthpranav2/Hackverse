package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editor = getSharedPreferences("MY_SETTING", MODE_PRIVATE)
        val empEmail = editor.getString("empEmail", null)

        if (empEmail == null) {
            Toast.makeText(this,"1", Toast.LENGTH_LONG).show()
            redirectToActivity(LoginPage::class.java)
        } else {
            val dbRef = FirebaseDatabase.getInstance().getReference("User").child(empEmail)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val fullname = snapshot.child("empName").getValue(String::class.java) ?: "Unknown"
                        val uName = snapshot.child("empUName").getValue(String::class.java) ?: "Unknown"

                        GlobalClass.Email = empEmail.replace(",", ".") // Replace '.' with ','
                        GlobalClass.FullName = fullname
                        GlobalClass.UName = uName
                        GlobalClass.FirstName = fullname.split(" ").firstOrNull() ?: "Unknown"
                        GlobalClass.NameIco = fullname.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "N/A"

                        redirectToActivity(UserAuthentication::class.java)
                    } else {
                        Toast.makeText(this@MainActivity, "2", Toast.LENGTH_LONG).show()
                        redirectToActivity(LoginPage::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "3", Toast.LENGTH_LONG).show()
                    redirectToActivity(LoginPage::class.java)
                }

            })
        }
    }

    private fun redirectToActivity(activityClass: Class<*>) {
        Handler(mainLooper).postDelayed({
            val intent = Intent(this, activityClass)
            startActivity(intent)
            finish() // Close MainActivity
        }, 1000)
    }
}


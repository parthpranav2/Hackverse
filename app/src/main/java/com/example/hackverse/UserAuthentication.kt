package com.example.hackverse

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import java.net.Authenticator

class UserAuthentication : AppCompatActivity() {

    var ctr = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdpin)

        // Adjust padding for edge-to-edge compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.lblname).text = GlobalClass.NameIco
        findViewById<TextView>(R.id.lblgreet).text = "Hey, ${GlobalClass.FirstName}!"
        findViewById<TextView>(R.id.lblidentifi).text = "Not ${GlobalClass.UName}?"

        findViewById<TextView>(R.id.lblidentifi).setOnClickListener{
            val intent = Intent(this@UserAuthentication, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnsubmit).setOnClickListener {
            Authenticator()
        }

        val txtPin1 = findViewById<TextView>(R.id.txtpin1)

        txtPin1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 4) {
                    Authenticator()  // Call your function when character count is 4
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    fun Authenticator(){
        if (isInternetAvailable(this)) {
            val pinInput = findViewById<EditText>(R.id.txtpin1).text.toString().trim()

            // Check if PIN input is empty
            if (pinInput.isEmpty()) {
                Toast.makeText(this, "Please enter a PIN", Toast.LENGTH_SHORT).show()
                return
            }

            // Retrieve employee email from SharedPreferences
            val editor = getSharedPreferences("MY_SETTING", MODE_PRIVATE)
            val empEmail = editor.getString("empEmail", null)

            if (empEmail.isNullOrEmpty()) {
                Toast.makeText(this, "User email not found. Please log in again.", Toast.LENGTH_SHORT).show()
                return
            }

            val dbRef = FirebaseDatabase.getInstance().getReference("User").child(empEmail)

            // Query the "empPin" field
            dbRef.child("empPin").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val empPin = snapshot.getValue(String::class.java) // Get the value of empPin

                        if (empPin == pinInput) {
                            // Navigate to Home activity
                            findViewById<Button>(R.id.btnsubmit).text="Getting in..."
                            val intent = Intent(this@UserAuthentication, Home::class.java)
                            startActivity(intent)
                            finish() // Optional: Finish current activity
                        } else {
                            findViewById<LinearLayout>(R.id.linelwrongpin).visibility = View.VISIBLE
                            findViewById<EditText>(R.id.txtpin1).text.clear()
                            findViewById<EditText>(R.id.txtpin1).background = ContextCompat.getDrawable(this@UserAuthentication, R.drawable.pinrejection)
                            if(ctr>0){
                                ctr--
                            }else{
                                finishAffinity()
                            }
                            findViewById<TextView>(R.id.txtcounterst).text="Only "+ctr+ " attempts are left."
                        }
                    } else {
                        // No empPin found for the user
                        Toast.makeText(this@UserAuthentication, "PIN not set for this user.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@UserAuthentication, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            val intent = Intent(this, Offline::class.java)
            startActivity(intent)
        }

    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}

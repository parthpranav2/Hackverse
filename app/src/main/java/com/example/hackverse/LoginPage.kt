package com.example.hackverse

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
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

class LoginPage : AppCompatActivity() {
    var verify : Boolean =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdlogin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_frmdlogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnlogin).setOnClickListener {
            if (isInternetAvailable(this)) {
                // Navigate to activity_frmdpin
                val intent = Intent(this, UserAuthentication::class.java)
                verifyuser()
                if(verify) {
                    startActivity(intent)
                }
            } else {
                val intent = Intent(this, Offline::class.java)
                startActivity(intent)
            }

        }

        findViewById<TextView>(R.id.lnklblcreat).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, CreateAccount::class.java)
                startActivity(intent)
        }


        val txtEmail = findViewById<EditText>(R.id.txtemail)
        val lblEmail = findViewById<TextView>(R.id.lblemail)
        txtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || txtEmail.text.toString().isNotEmpty()) {
                lblEmail.visibility = View.VISIBLE
                txtEmail.hint=""
                txtEmail.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                if(txtEmail.text.toString().isNotEmpty()){
                    txtEmail.background = ContextCompat.getDrawable(this, R.drawable.pinacceptance)
                }else{
                    txtEmail.background = ContextCompat.getDrawable(this, R.drawable.pinidle)
                }
            } else {
                lblEmail.visibility = View.INVISIBLE
                txtEmail.hint=" Enter E-mail id"
                txtEmail.background = ContextCompat.getDrawable(this, R.drawable.pinrejection)
            }
        }

        val txtPass = findViewById<EditText>(R.id.txtpass)
        val lblPass = findViewById<TextView>(R.id.lblpass)
        txtPass.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || txtPass.text.toString().isNotEmpty()) {
                lblPass.visibility = View.VISIBLE
                txtPass.hint=""
                txtPass.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                if(txtPass.text.toString().isNotEmpty()){
                    txtPass.background = ContextCompat.getDrawable(this, R.drawable.pinacceptance)
                }else{
                    txtPass.background = ContextCompat.getDrawable(this, R.drawable.pinidle)
                }
            } else {
                lblPass.visibility = View.INVISIBLE
                txtPass.hint=" Enter Password"
                txtPass.background = ContextCompat.getDrawable(this, R.drawable.pinrejection)
            }
        }

    }

    private fun verifyuser() {
        val email = findViewById<EditText>(R.id.txtemail).text.toString()
        val pass = findViewById<EditText>(R.id.txtpass).text.toString()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val sanitizedEmail = email.replace(".", ",") // Replace '.' with ','

        val dbRef = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val storedPassword = snapshot.child("empPassword").getValue(String::class.java)
                    if (storedPassword != null && storedPassword == pass) {
                        val editor = getSharedPreferences("MY_SETTING", MODE_PRIVATE).edit()
                        val fullname = snapshot.child("empName").getValue(String::class.java) ?: "Unknown"
                        val uName = snapshot.child("empUName").getValue(String::class.java) ?: "Unknown"

                        editor.putString("empEmail", sanitizedEmail)
                        editor.apply()

                        GlobalClass.Email = sanitizedEmail.replace(",", ".") // Replace ',' with '.'
                        GlobalClass.FullName = fullname
                        GlobalClass.UName = uName
                        GlobalClass.FirstName = fullname.split(" ").firstOrNull() ?: "Unknown"
                        GlobalClass.NameIco = fullname.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "N/A"
                        verify = true
                    } else {
                        findViewById<LinearLayout>(R.id.linelwrongpass).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.txtnotice).text = "Oh-oh! Invalid Password!"
                        findViewById<EditText>(R.id.txtpass).background = ContextCompat.getDrawable(this@LoginPage, R.drawable.pinrejection)
                        findViewById<EditText>(R.id.txtpass).text.clear()
                        verify=false
                    }
                } else {
                    findViewById<LinearLayout>(R.id.linelwrongpass).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.txtnotice).text = "No account found, Please create one"
                    findViewById<EditText>(R.id.txtpass).background = ContextCompat.getDrawable(this@LoginPage, R.drawable.pinrejection)
                    findViewById<EditText>(R.id.txtemail).background = ContextCompat.getDrawable(this@LoginPage, R.drawable.pinrejection)
                    findViewById<EditText>(R.id.txtemail).text.clear()
                    verify = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginPage, "Error occurred", Toast.LENGTH_LONG).show()
                verify = false
            }
        })
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
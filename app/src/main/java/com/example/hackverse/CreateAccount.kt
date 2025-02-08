package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CreateAccount : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etUName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etGender:TextView
    private lateinit var etPin: EditText

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdcreateaccount)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.linklogin).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        val txtname = findViewById<EditText>(R.id.txtname)
        val lblname = findViewById<TextView>(R.id.lblname)
        val vwname = findViewById<View>(R.id.lnname)
        txtname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || txtname.text.toString().isNotEmpty()) {
                lblname.visibility = View.VISIBLE
                findViewById<EditText>(R.id.txtname).hint=""
                if(txtname.text.toString().isNotEmpty()){
//                    vwname.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                }else{
                    vwname.background = ContextCompat.getDrawable(this, R.drawable.lineidle)
                }
            } else {
                lblname.visibility = View.INVISIBLE
                findViewById<EditText>(R.id.txtname).hint=" Enter name"
                vwname.background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            }
        }

        val txtuname = findViewById<EditText>(R.id.txtuname)
        val lbluname = findViewById<TextView>(R.id.lbluname)
        val vwuname = findViewById<View>(R.id.lnuname)
        txtuname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || txtuname.text.toString().isNotEmpty()) {
                lbluname.visibility = View.VISIBLE
                findViewById<EditText>(R.id.txtuname).hint=""
                if(txtuname.text.toString().isNotEmpty()){
                    vwuname.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                }else{
                    vwuname.background = ContextCompat.getDrawable(this, R.drawable.lineidle)
                }
            } else {
                lbluname.visibility = View.INVISIBLE
                findViewById<EditText>(R.id.txtuname).hint=" Enter user name"
                vwuname.background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            }
        }

        val txtEmail = findViewById<EditText>(R.id.txtemailid)
        val lblEmail = findViewById<TextView>(R.id.lblemailid)
        val vwemail = findViewById<View>(R.id.lnemail)
        txtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || txtEmail.text.toString().isNotEmpty()) {
                lblEmail.visibility = View.VISIBLE
                findViewById<EditText>(R.id.txtemailid).hint=""
                vwemail.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                if(txtEmail.text.toString().isNotEmpty()){
                    vwemail.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                }else{
                    vwemail.background = ContextCompat.getDrawable(this, R.drawable.lineidle)
                }
            } else {
                lblEmail.visibility = View.INVISIBLE
                findViewById<EditText>(R.id.txtemailid).hint=" Enter E-mail id"
                vwemail.background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            }
        }

        val txtPass = findViewById<EditText>(R.id.txtpass)
        val lblPass = findViewById<TextView>(R.id.lblpass)
        val vwpass = findViewById<View>(R.id.lnpass)
        txtPass.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || txtPass.text.toString().isNotEmpty()) {
                lblPass.visibility = View.VISIBLE
                findViewById<EditText>(R.id.txtpass).hint=""
                if(txtPass.text.toString().isNotEmpty()){
                    vwpass.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                }else{
                    vwpass.background = ContextCompat.getDrawable(this, R.drawable.lineidle)
                }
            } else {
                lblPass.visibility = View.INVISIBLE
                findViewById<EditText>(R.id.txtpass).hint=" Enter Password"
                vwpass.background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            }
        }

        val txtRPass = findViewById<EditText>(R.id.txtrepass)
        val lblRPass = findViewById<TextView>(R.id.lblrepass)
        val vwRpass = findViewById<View>(R.id.lnrepass)
        txtRPass.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || txtRPass.text.toString().isNotEmpty()) {
                lblRPass.visibility = View.VISIBLE
                findViewById<EditText>(R.id.txtrepass).hint=""
                if(txtRPass.text.toString().isNotEmpty()){
                    vwRpass.background = ContextCompat.getDrawable(this, R.drawable.lineacceptance)
                }else{
                    vwRpass.background = ContextCompat.getDrawable(this, R.drawable.lineidle)
                }
            } else {
                lblRPass.visibility = View.INVISIBLE
                findViewById<EditText>(R.id.txtrepass).hint=" Re-enter Password"
                vwRpass.background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            }
        }

        etName=findViewById(R.id.txtname)
        etUName=findViewById(R.id.txtuname)
        etEmail=findViewById(R.id.txtemailid)
        etPassword=findViewById(R.id.txtpass)

        if (findViewById<RadioButton>(R.id.rbmale).isChecked) {
            etGender.text="Male"
        } else if (findViewById<RadioButton>(R.id.rbfemale).isChecked) {
            etGender.text="Feale"
        }
        etGender=findViewById(R.id.rbmale)
        etPin=findViewById(R.id.txtpin1)

        dbRef=FirebaseDatabase.getInstance().getReference("User")

        findViewById<Button>(R.id.btnsignup).setOnClickListener {
            // Navigate to activity_frmdpin
            saveUserData()
        }

    }

    private fun saveUserData(){
        val empName=etName.text.toString()
        val empUName=etUName.text.toString()
        val empEmail=etEmail.text.toString()
        val empPassword=etPassword.text.toString()
        val empGender=etGender.text.toString()
        val empPin=etPin.text.toString()

        if(empName.isEmpty()){
            Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show()
            findViewById<View>(R.id.lnname).background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            return
        }
        if(empUName.isEmpty()){
            Toast.makeText(this, "Please enter user name", Toast.LENGTH_LONG).show()
            findViewById<View>(R.id.lnuname).background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            return
        }
        if(empEmail.isEmpty()){
            Toast.makeText(this, "Please enter email id", Toast.LENGTH_LONG).show()
            findViewById<View>(R.id.lnemail).background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            return
        }

        if(empPassword.isEmpty()){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show()
            findViewById<View>(R.id.lnpass).background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            return
        }
        val enteredPassword = findViewById<EditText>(R.id.txtrepass).text.toString()
        if(enteredPassword.isEmpty()){
            Toast.makeText(this, "Re-enter password field empty", Toast.LENGTH_LONG).show()
            findViewById<View>(R.id.lnrepass).background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            return
        }

        if(enteredPassword!=empPassword){
            Toast.makeText(this, "Password not matched in verification", Toast.LENGTH_LONG).show()
            findViewById<View>(R.id.lnrepass).background = ContextCompat.getDrawable(this, R.drawable.linerejection)
            return
        }

        if(empGender.isEmpty()){
            Toast.makeText(this, "Please select gender", Toast.LENGTH_LONG).show()
            return
        }

        if(empPin.isEmpty()){
            Toast.makeText(this, "Please enter Pin", Toast.LENGTH_LONG).show()
            findViewById<EditText>(R.id.txtpin1).background = ContextCompat.getDrawable(this, R.drawable.pinrejection)
            return
        }

        if(empPin==empPassword){
            Toast.makeText(this, "Pin and Password cannot be same", Toast.LENGTH_LONG).show()
            findViewById<EditText>(R.id.txtpin1).background = ContextCompat.getDrawable(this, R.drawable.pinrejection)
            return
        }

        // Sanitize email to make it a valid key
        val sanitizedEmail = empEmail.replace(".", ",") // Replace '.' with ','

        val dbRef = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@CreateAccount, "This email already exists", Toast.LENGTH_LONG).show()
                    findViewById<EditText>(R.id.txtemailid).text.clear()
                    findViewById<View>(R.id.lnemail).background = ContextCompat.getDrawable(this@CreateAccount, R.drawable.linerejection)
                    return
                }else{
                    val user1 = UserModel(empName,empUName,empPassword,empGender,empPin,empEmail)

                    dbRef.setValue(user1)
                        .addOnCompleteListener {
                            Toast.makeText(this@CreateAccount, "Data inserted successfully", Toast.LENGTH_LONG).show()

                            val editor=getSharedPreferences("MY_SETTING", MODE_PRIVATE).edit()
                            editor.putString("empEmail",sanitizedEmail)
                            editor.apply()

                            val fullname = snapshot.child("empName").getValue(String::class.java) ?: "Unknown"
                            val uName = snapshot.child("empUName").getValue(String::class.java) ?: "Unknown"
                            GlobalClass.Email = sanitizedEmail.replace(",", ".") // Replace ',' with '.'
                            GlobalClass.FullName = fullname
                            GlobalClass.UName = uName
                            GlobalClass.FirstName = fullname.split(" ").firstOrNull() ?: "Unknown"
                            GlobalClass.NameIco = fullname.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "N/A"

                            idle()

                        }.addOnFailureListener{ err->
                            Toast.makeText(this@CreateAccount,"Error ${err.message}",Toast.LENGTH_LONG).show()
                        }

                    val intent = Intent(this@CreateAccount, UserAuthentication::class.java)
                    startActivity(intent)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CreateAccount, "Error occured", Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun idle(){
        val vwname = findViewById<View>(R.id.lnname)
        val vwuname = findViewById<View>(R.id.lnuname)
        val vwemail = findViewById<View>(R.id.lnemail)
        val vwpass = findViewById<View>(R.id.lnpass)
        val vwrepass = findViewById<View>(R.id.lnrepass)
        val vwgender = findViewById<RadioGroup>(R.id.rfgender)
        val vwpin = findViewById<EditText>(R.id.txtpin1)

        vwname.background=ContextCompat.getDrawable(this,R.drawable.lineidle)
        vwuname.background=ContextCompat.getDrawable(this,R.drawable.lineidle)
        vwemail.background=ContextCompat.getDrawable(this,R.drawable.lineidle)
        vwpass.background=ContextCompat.getDrawable(this,R.drawable.lineidle)
        vwrepass.background=ContextCompat.getDrawable(this,R.drawable.lineidle)
        vwgender.clearCheck()
        vwpin.background=ContextCompat.getDrawable(this,R.drawable.pinidle)

        etName.text.clear()
        etUName.text.clear()
        etPassword.text.clear()
        etEmail.text.clear()
        findViewById<RadioButton>(R.id.rbmale).isChecked=false
        findViewById<RadioButton>(R.id.rbfemale).isChecked=false
    }
}

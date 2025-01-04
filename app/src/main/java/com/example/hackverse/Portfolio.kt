package com.example.hackverse

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Portfolio : AppCompatActivity() {
    private lateinit var ViewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdmportfolio)

        ViewPager=findViewById(R.id.viewpagerportfolio)
        ViewPager.adapter=PortfolioPagerAdapter(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<TextView>(R.id.lblname).text = GlobalClass.NameIco


        findViewById<LinearLayout>(R.id.tabregisteredevents).setOnClickListener{
            findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_a)
            findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwregisteredevents).visibility= View.VISIBLE

            findViewById<ImageView>(R.id.imgrequests).setImageResource(R.drawable.requests_na)
            findViewById<TextView>(R.id.txtrequests).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtrequestscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwrequests).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgpendingrequests).setImageResource(R.drawable.pendingregistration_na)
            findViewById<TextView>(R.id.txtpendingrequests).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtpendingrequestscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwpendingrequests).visibility= View.INVISIBLE

            ViewPager.currentItem=0

        }

        findViewById<LinearLayout>(R.id.tabrequests).setOnClickListener{
            findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
            findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgrequests).setImageResource(R.drawable.requests_a)
            findViewById<TextView>(R.id.txtrequests).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<TextView>(R.id.txtrequestscount).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwrequests).visibility= View.VISIBLE

            findViewById<ImageView>(R.id.imgpendingrequests).setImageResource(R.drawable.pendingregistration_na)
            findViewById<TextView>(R.id.txtpendingrequests).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtpendingrequestscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwpendingrequests).visibility= View.INVISIBLE

            ViewPager.currentItem=1
        }

        findViewById<LinearLayout>(R.id.tabpendingregistrations).setOnClickListener{
            findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
            findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgrequests).setImageResource(R.drawable.requests_na)
            findViewById<TextView>(R.id.txtrequests).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtrequestscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwrequests).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgpendingrequests).setImageResource(R.drawable.pendingregistration_a)
            findViewById<TextView>(R.id.txtpendingrequests).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<TextView>(R.id.txtpendingrequestscount).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwpendingrequests).visibility= View.VISIBLE

            ViewPager.currentItem=2
        }

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        val userRef = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("InvitedEvents")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val childCount = snapshot.childrenCount
                findViewById<TextView>(R.id.txtrequestscount).text= childCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val user2Ref = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("RegisteredEvents")


        val txtRegisteredEvents = findViewById<TextView>(R.id.txtregisteredeventscount)

        user2Ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    txtRegisteredEvents.text = "0"
                    return
                }

                var Count = 0
                val totalChildren = snapshot.childrenCount.toInt()
                var processedChildren = 0

                for (eventSnapshot in snapshot.children) {
                    val teamId = eventSnapshot.getValue(String::class.java).orEmpty()

                    if (teamId.isNotEmpty()) {
                        val teamRef = FirebaseDatabase.getInstance().getReference("Teams").child(teamId)

                        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(teamSnapshot: DataSnapshot) {
                                val registrationCompleted = teamSnapshot.child("registrationCompleted")
                                    .getValue(Boolean::class.java) ?: true // Default to true if value is null

                                if (registrationCompleted) {
                                    Count++
                                }

                                processedChildren++
                                // Update the TextView after processing all children
                                if (processedChildren == totalChildren) {
                                    txtRegisteredEvents.text = Count.toString()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching team details: ${error.message}")
                            }
                        })
                    } else {
                        processedChildren++
                        // Update the TextView if all children have been processed
                        if (processedChildren == totalChildren) {
                            txtRegisteredEvents.text = Count.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching registered events: ${error.message}")
            }
        })


        val user3Ref = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("RegisteredEvents")

        val txtPendingRegistrations = findViewById<TextView>(R.id.txtpendingrequestscount)

        user3Ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    txtPendingRegistrations.text = "0"
                    return
                }

                var pendingCount = 0
                val totalChildren = snapshot.childrenCount.toInt()
                var processedChildren = 0

                for (eventSnapshot in snapshot.children) {
                    val teamId = eventSnapshot.getValue(String::class.java).orEmpty()

                    if (teamId.isNotEmpty()) {
                        val teamRef = FirebaseDatabase.getInstance().getReference("Teams").child(teamId)

                        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(teamSnapshot: DataSnapshot) {
                                val registrationCompleted = teamSnapshot.child("registrationCompleted")
                                    .getValue(Boolean::class.java) ?: true // Default to true if value is null

                                if (!registrationCompleted) {
                                    pendingCount++
                                }

                                processedChildren++
                                // Update the TextView after processing all children
                                if (processedChildren == totalChildren) {
                                    txtPendingRegistrations.text = pendingCount.toString()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching team details: ${error.message}")
                            }
                        })
                    } else {
                        processedChildren++
                        // Update the TextView if all children have been processed
                        if (processedChildren == totalChildren) {
                            txtPendingRegistrations.text = pendingCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching registered events: ${error.message}")
            }
        })



        findViewById<LinearLayout>(R.id.btnhome).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnactiveevents).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, ActiveEvents::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnaddhackathon).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, AddHackathon::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<RelativeLayout>(R.id.btnusernico).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Account::class.java)
            startActivity(intent)
        }
    }
}
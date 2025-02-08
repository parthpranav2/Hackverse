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

            findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_na)
            findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwinvitation).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_na)
            findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwpendingregistrations).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_na)
            findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwrequest).visibility= View.INVISIBLE

            ViewPager.currentItem=0

        }

        findViewById<LinearLayout>(R.id.tabinvitation).setOnClickListener{
            findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
            findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_a)
            findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwinvitation).visibility= View.VISIBLE

            findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_na)
            findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwpendingregistrations).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_na)
            findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwrequest).visibility= View.INVISIBLE

            ViewPager.currentItem=1
        }

        findViewById<LinearLayout>(R.id.tabpendingregistrations).setOnClickListener{
            findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
            findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_na)
            findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwinvitation).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_a)
            findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwpendingregistrations).visibility= View.VISIBLE

            findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_na)
            findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwrequest).visibility= View.INVISIBLE

            ViewPager.currentItem=2
        }

        findViewById<LinearLayout>(R.id.tabrequest).setOnClickListener{
            findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
            findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_na)
            findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwinvitation).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_na)
            findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwpendingregistrations).visibility= View.INVISIBLE

            findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_a)
            findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwrequest).visibility= View.VISIBLE

            ViewPager.currentItem=3
        }

        val viewpager = findViewById<ViewPager2>(R.id.viewpagerportfolio)
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position){
                    0->{
                        findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_a)
                        findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<View>(R.id.lvwregisteredevents).visibility= View.VISIBLE

                        findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_na)
                        findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwinvitation).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_na)
                        findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwpendingregistrations).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_na)
                        findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwrequest).visibility= View.INVISIBLE
                    }
                    1->{
                        findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
                        findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_a)
                        findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<View>(R.id.lvwinvitation).visibility= View.VISIBLE

                        findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_na)
                        findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwpendingregistrations).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_na)
                        findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwrequest).visibility= View.INVISIBLE
                    }
                    2->{
                        findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
                        findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_na)
                        findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwinvitation).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_a)
                        findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<View>(R.id.lvwpendingregistrations).visibility= View.VISIBLE

                        findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_na)
                        findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwrequest).visibility= View.INVISIBLE
                    }
                    3->{
                        findViewById<ImageView>(R.id.imgregisteredevents).setImageResource(R.drawable.registerede_na)
                        findViewById<TextView>(R.id.txtregisteredevents).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtregisteredeventscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwregisteredevents).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imginvitation).setImageResource(R.drawable.invitations_na)
                        findViewById<TextView>(R.id.txtinvitation).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtinvitationscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwinvitation).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imgpendingregistrations).setImageResource(R.drawable.pendingregistration_na)
                        findViewById<TextView>(R.id.txtpendingregistrations).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtpendingregitrationscount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwpendingregistrations).visibility= View.INVISIBLE

                        findViewById<ImageView>(R.id.imgrequest).setImageResource(R.drawable.requests_a)
                        findViewById<TextView>(R.id.txtrequest).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<TextView>(R.id.txtrequestcount).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<View>(R.id.lvwrequest).visibility= View.VISIBLE

                    }
                }
            }
        })

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        val userRef = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("InvitedEvents")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val childCount = snapshot.childrenCount
                findViewById<TextView>(R.id.txtinvitationscount).text= childCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val user2Ref = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("RegisteredEvents")


        val txtRegisteredEvents = findViewById<TextView>(R.id.txtregisteredeventscount)

        user2Ref.addValueEventListener(object : ValueEventListener {
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

                        teamRef.addValueEventListener(object : ValueEventListener {
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

        val txtPendingRegistrations = findViewById<TextView>(R.id.txtpendingregitrationscount)

        user3Ref.addValueEventListener(object : ValueEventListener {
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

                        teamRef.addValueEventListener(object : ValueEventListener {
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


        val dbCurrentUser = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail).child("TeamsLeaded")
        val txtRequests = findViewById<TextView>(R.id.txtrequestcount)
        var requests = 0
        dbCurrentUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalTeamsToCheck = snapshot.childrenCount.toInt()
                var TeamsChecked = 0

                for (team in snapshot.children){
                    val teamId = team.getValue(String::class.java)
                    if(teamId!=null){
                        TeamsChecked++
                        val dbTeamRef = FirebaseDatabase.getInstance().getReference("Teams").child(teamId).child("Requested")

                        dbTeamRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists() && snapshot.hasChildren()) {
                                    // The node "Requested" contains children
                                    requests++
                                    if(TeamsChecked==totalTeamsToCheck){
                                        txtRequests.text=requests.toString()
                                    }
                                    Log.d("Firebase", "Requested contains children.")
                                } else {
                                    // The node "Requested" is empty or doesn't exist
                                    Log.d("Firebase", "Requested has no children.")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error: ${error.message}")
                            }
                        })

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        findViewById<LinearLayout>(R.id.btnhome).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnchatroom).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, ChatRoomEventList::class.java)
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
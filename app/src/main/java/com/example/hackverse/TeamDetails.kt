package com.example.hackverse

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TeamDetails : AppCompatActivity() {
    private lateinit var EventName:TextView
    private lateinit var MaxTeamSize: TextView // Fixed the capitalization issue
    private lateinit var RecyclerView: RecyclerView
    private lateinit var teamArrayList: ArrayList<UserParsableModel>
    private lateinit var TeamSize: TextView
    private lateinit var TeamName: EditText

    var CurrentTeamSize = 0
    var TeamGlobal = false

    var registeredEMP  : String? = "F"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdteamdetails)

        // Initialize the TextViews
        EventName = findViewById(R.id.txtTDEventName)
        MaxTeamSize = findViewById(R.id.txtTDmaxteamsize)
        RecyclerView = findViewById(R.id.recvwTDTeammates)
        TeamSize = findViewById(R.id.txtTDteamsize)
        TeamName = findViewById(R.id.txtTDTeamName)
        TeamSize.text = CurrentTeamSize.toString()
        // Set text from GlobalClass (make sure these values are initialized)
        EventName.text = GlobalClass.evGName ?: "Default Event Name"  // Safe call with fallback text
        TeamName.setText(GlobalClass.evtmGName ?: "Default Event Name")  // Corrected

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btnTDcloseregistration).setOnClickListener{
            finish()
        }

        handleTeammateEmailClose()

    }

    private fun handleTeammateEmailClose() {
        var maximumTeamMembers = 0
        // Initialize RecyclerView if it's not initialized
        RecyclerView.visibility = View.VISIBLE
        RecyclerView.layoutManager = LinearLayoutManager(this)

        if (!::teamArrayList.isInitialized) {
            teamArrayList = arrayListOf() // Initialize the list if it's not already done
            val adapter = FinalisedTeammatesAdapter(teamArrayList) // Initialize adapter
            RecyclerView.adapter = adapter
        }

        // Reference to the specific event
        val eventRef = FirebaseDatabase.getInstance().getReference("Events").child(GlobalClass.evGId.toString())

        // Fetch the value of evSize
        eventRef.child("evMaxTeamSize").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val evMaxSize = snapshot.getValue(String::class.java) // Assuming evSize is an integer
                if (evMaxSize != null) {
                    findViewById<TextView>(R.id.txtTDmaxteamsize).text = evMaxSize
                    maximumTeamMembers = evMaxSize.toInt()
                    Log.d("EventSize", "Event size is: $evMaxSize")
                } else {
                    Log.e("EventSize", "evSize not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch evSize", error.toException())
            }
        })

        // Retrieve emails from Firebase under the "InGroup" and "Invited" nodes
        val teamRef = FirebaseDatabase.getInstance().getReference("Teams").child(GlobalClass.tmId.toString())
        teamRef.child("leader").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leader = snapshot.getValue(String::class.java)
                if (leader != null) {
                    GlobalClass.teamleaderemail = leader
                    if (leader == GlobalClass.Email) {
                        findViewById<Button>(R.id.butTDInvite).visibility = View.VISIBLE
                        findViewById<Button>(R.id.butTDSaveChanges).visibility = View.VISIBLE
                    }
                    Log.d("TeamLeader", "Leader set to: $leader")
                } else {
                    Log.e("TeamLeader", "Leader value not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch leader", error.toException())
            }
        })

        // Retrieve emails from Firebase under the "InGroup" and "Invited" nodes
        val globalRef = FirebaseDatabase.getInstance().getReference("Teams").child(GlobalClass.tmId.toString())
        globalRef.child("publicVisibility").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val publicVisibility = snapshot.getValue(Boolean::class.java)
                if (publicVisibility != null) {
                    findViewById<Switch>(R.id.TDswtch).isChecked = publicVisibility
                } else {
                    Log.e("TeamLeader", "Public visibility value not found")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch leader", error.toException())
            }
        })

        // Retrieve emails from Firebase under the "InGroup" and "Invited" nodes
        val registeredRef = FirebaseDatabase.getInstance().getReference("Teams").child(GlobalClass.tmId.toString())
        registeredRef.child("registrationCompleted").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registrationCompleted = snapshot.getValue(Boolean::class.java)
                if (registrationCompleted == true) {
                    findViewById<LinearLayout>(R.id.llFinalResColor).setBackgroundColor(Color.parseColor("#1C251F")) // Green
                } else {
                    findViewById<LinearLayout>(R.id.llFinalResColor).setBackgroundColor(Color.parseColor("#251C23")) // Red
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch leader", error.toException())
            }
        })

        // Fetching the "InGroup" emails first
        teamRef.child("InGroup").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val emailId = child.getValue(String::class.java) ?: continue
                    fetchUserDetails(emailId, maximumTeamMembers, "T") // Passed "T" for in-group members
                }

                // After fetching "InGroup" emails, retrieve "Invited" emails
                teamRef.child("Invited").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            val emailId = child.getValue(String::class.java) ?: continue
                            fetchUserDetails(emailId, maximumTeamMembers, "F") // Passed "F" for invited members
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Failed to fetch Invited emails", error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch InGroup emails", error.toException())
            }
        })
    }

    private fun ButtonEnabler(currentCount: Int, MaxCount: Int){
        if (currentCount >= MaxCount) {
            findViewById<Button>(R.id.butTDInvite).visibility = View.GONE
            findViewById<Button>(R.id.butTDSaveChanges).visibility = View.GONE
        } else {
            findViewById<Button>(R.id.butTDInvite).visibility = View.VISIBLE
            findViewById<Button>(R.id.butTDSaveChanges).visibility = View.VISIBLE
        }
    }

    private fun fetchUserDetails(emailId: String, maximumTeamCount: Int, registeredEmpStatus: String) {
        // Sanitizing email to match Firebase keys
        val sanitizedEmail = emailId.replace(".", ",")
        val userRef = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val empName = snapshot.child("empName").value.toString()
                    val empUName = snapshot.child("empUName").value.toString()
                    val empEmail = snapshot.child("empEmail").value.toString()

                    // Create a new UserParsableModel and add it to the list
                    val user = UserParsableModel(empName, empUName, null, null, null, empEmail, registeredEmpStatus)
                    teamArrayList.add(user)

                    // Update the team size and refresh the RecyclerView
                    CurrentTeamSize++
                    ButtonEnabler(CurrentTeamSize, maximumTeamCount)
                    TeamSize.text = CurrentTeamSize.toString()

                    // Notify the adapter about the new data
                    (RecyclerView.adapter as FinalisedTeammatesAdapter).notifyItemInserted(teamArrayList.size - 1)
                } else {
                    Log.e("Firebase", "User not found for email: $emailId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching user details", error.toException())
            }
        })
    }


}
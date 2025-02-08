package com.example.hackverse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GlobalTeamsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var activeteamArrayList: ArrayList<GlobalTeamModel>
    private lateinit var activeteamsAdater: GlobalTeamsAdapter

    var eventName: String? = null
    var eventTheme: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recvactiveteams)
        recyclerView.layoutManager = LinearLayoutManager(context)
        activeteamArrayList = arrayListOf()
        activeteamsAdater = GlobalTeamsAdapter(activeteamArrayList)
        recyclerView.adapter = activeteamsAdater

        val teamRef = FirebaseDatabase.getInstance().getReference("Teams")
        teamRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    activeteamArrayList.clear()

                    for (team in snapshot.children) {
                        val teamKey = team.key
                        if (teamKey != null) {
                            val dbTeamIdRef = FirebaseDatabase.getInstance()
                                .getReference("Teams")
                                .child(teamKey)
                            dbTeamIdRef.child("publicVisibility").get()
                                .addOnSuccessListener { dataSnapshot ->
                                    val isTeamGlobal = dataSnapshot.getValue(Boolean::class.java) ?: false
                                    if (isTeamGlobal) {
                                        dbTeamIdRef.get().addOnSuccessListener { teamSnapshot ->
                                            val leaderId = teamSnapshot.child("leader").getValue(String::class.java)
                                            val eventId = teamSnapshot.child("eventId").getValue(String::class.java)
                                            val teamName = teamSnapshot.child("name").getValue(String::class.java)

                                            if(eventId!=null){
                                                CanIRegister(eventId,teamKey) { iCan ->
                                                    if (iCan) {
                                                        if (leaderId != null && eventId != null) {
                                                            getLeaderName(leaderId) { leaderName ->
                                                                getEventDetails(eventId) { eventDetails ->
                                                                    val (eventName, eventTheme) = eventDetails

                                                                    // Create the GlobalTeamModel
                                                                    val teamModel = GlobalTeamModel(
                                                                        Name = teamName,
                                                                        LeaderId = leaderId,
                                                                        LeaderName = leaderName,
                                                                        TeamId = teamKey,
                                                                        EventId = eventId,
                                                                        EventName = eventName,
                                                                        EventTheme = eventTheme
                                                                    )

                                                                    activeteamArrayList.add(teamModel) // Add to the list
                                                                    recyclerView.post {
                                                                        activeteamsAdater.notifyDataSetChanged()
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                            }

                                        }.addOnFailureListener {
                                            Log.e(
                                                "FirebaseError",
                                                "Failed to fetch team details for $teamKey",
                                                it
                                            )
                                        }
                                    }
                                }.addOnFailureListener {
                                    Log.e(
                                        "FirebaseError",
                                        "Failed to get publicVisibility for team $teamKey",
                                        it
                                    )
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled", error.toException())
            }
        })
    }

    private fun getLeaderName(leaderId: String, onResult: (String?) -> Unit) {
        val sanitizedEmail = leaderId.replace(".", ",") ?: "default_email"

        val dbLeaderName = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)
        dbLeaderName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val empName = snapshot.child("empName").getValue(String::class.java)
                    if (empName != null) {
                        onResult(empName) // Pass leader name to callback
                    } else {
                        Log.d("Firebase", "empName not found")
                        onResult(null)
                    }
                } else {
                    Log.d("Firebase", "User not found")
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled", error.toException())
                onResult(null)
            }
        })
    }

     private fun CanIRegister(eventId: String, teamid: String, onResult: (Boolean) -> Unit) {
        val sanitizedEmail = GlobalClass.Email?.replace(",", ".") ?: "default_email"
        val dbUserName = FirebaseDatabase.getInstance().getReference("Events")
            .child(eventId)
            .child("Registrations")
        val dbTeamRef = FirebaseDatabase.getInstance().getReference("Teams").child(teamid)

        // Check if sanitizedEmail is already in the Registrations for the event
        dbUserName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if email exists in Registrations
                var isEmailInRegistrations = false
                for (childSnapshot in snapshot.children) {
                    val childValue = childSnapshot.getValue(String::class.java)
                    if (childValue == sanitizedEmail) {
                        isEmailInRegistrations = true
                        break // Exit early if email is found
                    }
                }

                // Now check if registrationCompleted is false at the team level
                if (!isEmailInRegistrations) {
                    dbTeamRef.child("registrationCompleted").get().addOnSuccessListener { teamSnapshot ->
                        val registrationCompleted = teamSnapshot.getValue(Boolean::class.java) ?: false
                        if (registrationCompleted == false) {
                            onResult(true)  // Can register, both conditions met
                        } else {
                            onResult(false)  // Registration is already completed for the team
                        }
                    }.addOnFailureListener {
                        Log.e("FirebaseError", "Failed to fetch registrationCompleted for team $teamid", it)
                        onResult(false)  // Handle failure case
                    }
                } else {
                    onResult(false)  // Email is already in the Registrations list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled", error.toException())
                onResult(false)  // Handle failure by returning false
            }
        })
    }




    private fun getEventDetails(eventId: String, onResult: (Pair<String?, String?>) -> Unit) {
        val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId)
        dbEventRef.get().addOnSuccessListener { eventSnapshot ->
            val eventName = eventSnapshot.child("evName").getValue(String::class.java)
            val eventTheme = eventSnapshot.child("evTheme").getValue(String::class.java)

            if (eventName == null || eventTheme == null) {
                Log.d("Firebase", "Event details not found for $eventId")
            }
            onResult(eventName to eventTheme) // Pass event details to callback
        }.addOnFailureListener {
            Log.e("FirebaseError", "Failed to fetch event details for $eventId", it)
            onResult(null to null)
        }
    }

}
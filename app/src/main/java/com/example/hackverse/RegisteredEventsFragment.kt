package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisteredEventsFragment : Fragment() , RegisteredEventsAdapter.OnTeamDetailsClickListener{

    private lateinit var recyclerView : RecyclerView
    private lateinit var teamArrayList : ArrayList<TeamParsableModel>//temporary model
    private lateinit var registeredeventsAdapter : RegisteredEventsAdapter //temporary adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registered_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recvwregisteredevents)
        recyclerView.layoutManager = LinearLayoutManager(context)
        teamArrayList = arrayListOf()
        registeredeventsAdapter = RegisteredEventsAdapter(teamArrayList,this)
        recyclerView.adapter = registeredeventsAdapter

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        if (sanitizedEmail.isEmpty()) {
            Toast.makeText(context, "Email is not available", Toast.LENGTH_SHORT).show()
            return
        }

        val database1 = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("RegisteredEvents")

        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                teamArrayList.clear() // Clear to prevent duplication

                for (eventSnapshot in snapshot.children) {
                    val teamId = eventSnapshot.getValue(String::class.java).orEmpty() // Fetch the value as the team ID

                    Log.d("FirebaseDebug", "Team ID: $teamId") // Log fetched team ID

                    if (teamId.isNotEmpty()) {
                        val teamRef = FirebaseDatabase.getInstance().getReference("Teams").child(teamId)

                        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(teamSnapshot: DataSnapshot) {
                                val registrationCompleted = teamSnapshot.child("registrationCompleted")
                                    .getValue(Boolean::class.java) ?: false // Default to false if value is null

                                if (registrationCompleted) { // Only consider teams with registrationCompleted == true
                                    val teamName = teamSnapshot.child("name").getValue(String::class.java).orEmpty()
                                    val eventId = teamSnapshot.child("eventId").getValue(String::class.java).orEmpty()

                                    Log.d("FirebaseDebug", "Team Name: $teamName, Event ID: $eventId") // Log fetched data

                                    if (teamName.isNotEmpty() && eventId.isNotEmpty()) {
                                        fetchEventDetails(teamName, eventId,teamId)
                                    } else {
                                        Log.d("FirebaseDebug", "Team name or Event ID is missing")
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching team details: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching registered events: ${error.message}")
            }
        })
    }

    private fun fetchEventDetails(teamName: String, eventId: String, teamId: String) {
        val eventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId)

        eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(eventSnapshot: DataSnapshot) {
                val eventName = eventSnapshot.child("evName").getValue(String::class.java).orEmpty()
                val eventTheme = eventSnapshot.child("evTheme").getValue(String::class.java).orEmpty()

                // Create a TeamParsableModel object with the retrieved data
                val teamData = TeamParsableModel(
                    tmName = teamName,
                    tmevName = eventName,
                    tmInviterEmail = null,
                    tmevTheme = eventTheme,

                            tmId = teamId,
                    tmEventId = eventId
                )

                // Add to the list and notify adapter
                teamArrayList.add(teamData)
                registeredeventsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching event details: ${error.message}")
            }
        })
    }

    override fun onTeamDetailsClick(team: TeamParsableModel) {

        GlobalClass.evGName=team.tmevName
        GlobalClass.evtmGName=team.tmName
        GlobalClass.evGId=team.tmEventId
        GlobalClass.tmId=team.tmId


        // Create an Intent to navigate to TeamDetailsActivity
        val intent = Intent(requireContext(), TeamDetails::class.java)
        startActivity(intent)
    }

}
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

class InvitationsFragment : Fragment() , InvitesAdapter.OnTeamDetailsClickListener , InvitesAdapter.OnRejectClickListener , InvitesAdapter.OnAcceptClickListener{

    private lateinit var recyclerView : RecyclerView
    private lateinit var teamArrayList : ArrayList<TeamParsableModel>//temporary model
    private lateinit var requestedteamsAdapter : InvitesAdapter //temporary adapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invitations, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recvwrequests)
        recyclerView.layoutManager = LinearLayoutManager(context)
        teamArrayList = arrayListOf()
        requestedteamsAdapter = InvitesAdapter(teamArrayList,this,this,this)
        recyclerView.adapter = requestedteamsAdapter



        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        if (sanitizedEmail.isEmpty()) {
            Toast.makeText(context, "Email is not available", Toast.LENGTH_SHORT).show()
            return
        }

        val database1 = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("InvitedEvents")

        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                teamArrayList.clear() // Clear the list to avoid duplication

                for (eventSnapshot in snapshot.children) {
                    val invitorEmailId =
                        eventSnapshot.child("InvitorEmailId").getValue(String::class.java)
                    val teamId = eventSnapshot.child("TeamId").getValue(String::class.java)

                    if (!teamId.isNullOrEmpty()) {
                        val eventRef = FirebaseDatabase.getInstance()
                            .getReference("Teams")
                            .child(teamId)

                        eventRef.child("name")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(teamSnapshot: DataSnapshot) {
                                    val teamName = teamSnapshot.getValue(String::class.java)


                                    eventRef.child("eventId")
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(eventSnapshot: DataSnapshot) {
                                                val eventId =
                                                    eventSnapshot.getValue(String::class.java)

                                                if (!eventId.isNullOrEmpty()) {
                                                    val eventThemeRef =
                                                        FirebaseDatabase.getInstance()
                                                            .getReference("Events")
                                                            .child(eventId)

                                                    eventThemeRef.child("evName")
                                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onDataChange(
                                                                eventNameSnapshot: DataSnapshot
                                                            ) {
                                                                val eventName =
                                                                    eventNameSnapshot.getValue(
                                                                        String::class.java
                                                                    )

                                                                eventThemeRef.child("evTheme")
                                                                    .addListenerForSingleValueEvent(
                                                                        object :
                                                                            ValueEventListener {
                                                                            override fun onDataChange(
                                                                                eventThemeSnapshot: DataSnapshot
                                                                            ) {
                                                                                val eventTheme =
                                                                                    eventThemeSnapshot.getValue(
                                                                                        String::class.java
                                                                                    )

                                                                                // Add to teamArrayList
                                                                                val teamData =
                                                                                    TeamParsableModel(
                                                                                        tmName = teamName,
                                                                                        tmevName = eventName,
                                                                                        tmInviterEmail = invitorEmailId,
                                                                                        tmevTheme = eventTheme,

                                                                                        tmId = teamId,
                                                                                        tmEventId = eventId
                                                                                    )
                                                                                teamArrayList.add(
                                                                                    teamData
                                                                                )
                                                                                requestedteamsAdapter.notifyDataSetChanged()
                                                                            }

                                                                            override fun onCancelled(
                                                                                error: DatabaseError
                                                                            ) {

                                                                            }
                                                                        })
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {

                                                            }
                                                        })
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                            }
                                        })
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                    } else {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

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

    override fun onAcceptClick(team: TeamParsableModel) {
        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        // Reference to the InGroup list in the team
        val teamInGroup1Ref = FirebaseDatabase.getInstance()
            .getReference("Teams")
            .child(team.tmId.toString())
            .child("InGroup")

// Add the email to the InGroup list
        teamInGroup1Ref.push().setValue(GlobalClass.Email).addOnSuccessListener {
            Toast.makeText(context, "Added to team's InGroup list: ${team.tmName}", Toast.LENGTH_SHORT).show()

            // After adding, check if there are any more children left in the Invited list
            val teamInvitedRef = FirebaseDatabase.getInstance()
                .getReference("Teams")
                .child(team.tmId.toString())
                .child("Invited")

            teamInvitedRef.orderByValue().equalTo(GlobalClass.Email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Loop through all the children that match the email
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue().addOnSuccessListener {
                            Log.d("Firebase", "Removed from Invited list: ${team.tmName}")
                        }.addOnFailureListener { e ->
                            Log.e("Firebase", "Failed to remove from Invited list", e)
                        }
                    }

                    // After adding the email, check if there are any more children left in the Invited list
                    teamInvitedRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Check if Invited list is empty
                            if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                                // No more elements in Invited, set registrationCompleted to true
                                val registrationCompletedRef = FirebaseDatabase.getInstance()
                                    .getReference("Teams")
                                    .child(team.tmId.toString())
                                    .child("registrationCompleted")

                                registrationCompletedRef.setValue(true)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "Registration completed set to true")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firebase", "Failed to set registration completed", e)
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Failed to query team's Invited list", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to query team's Invited list", Toast.LENGTH_SHORT).show()
                }
            })
        }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add email to InGroup list", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Failed to add email to InGroup list", e)
            }

// Reference to the "Invited" list of the user
        val teamInGroupRef = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("InvitedEvents")

        teamInGroupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    // Check if the current child has a "teamId" that matches the team.tmId
                    val currentTeamId = childSnapshot.child("TeamId").getValue(String::class.java)
                    if (currentTeamId == team.tmId.toString()) {
                        // Remove the item with the matching teamId
                        childSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Log.d("Firebase", "Item with teamID ${team.tmId} removed from Invited list")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Failed to remove item with teamID ${team.tmId} from Invited list", e)
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to query Invited list", error.toException())
            }
        })


        val registrationRef = FirebaseDatabase.getInstance()
            .getReference("Events")
            .child(team.tmEventId.toString())
            .child("Registrations")
        registrationRef.push().setValue(GlobalClass.Email.toString())
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }


        val registeredevents = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("RegisteredEvents")
        registeredevents.push().setValue(team.tmId.toString())
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }

        // Optionally, update the UI
        teamArrayList.remove(team)
        requestedteamsAdapter.notifyDataSetChanged()
    }


    override fun onRejectClick(team: TeamParsableModel) {
        // Sanitize email by replacing dots with commas
        val sanitizedEmail = GlobalClass.Email?.replace(".", ",")?.trim() ?: ""
        if (sanitizedEmail.isEmpty()) {
            Toast.makeText(context, "Email is not available", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to InvitedEvents for the specific user
        val userInvitedEventsRef = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("InvitedEvents")

        // Query all children to find the one with "TeamId" equal to team.tmId
        userInvitedEventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var teamFound = false
                for (childSnapshot in snapshot.children) {
                    val teamId = childSnapshot.child("TeamId").getValue(String::class.java)
                    if (teamId == team.tmId.toString()) {
                        teamFound = true
                        childSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Log.d("Firebase", "Successfully removed team: ${team.tmName} from InvitedEvents")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Failed to remove team: ${e.message}", e)
                            }
                        break
                    }
                }

                if (!teamFound) {
                    Log.w("Firebase", "No matching team found in InvitedEvents with TeamId: ${team.tmId}")
                    Toast.makeText(context, "No matching team found in InvitedEvents", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to query InvitedEvents: ${error.message}", error.toException())
                Toast.makeText(context, "Failed to query InvitedEvents", Toast.LENGTH_SHORT).show()
            }
        })

        // Step 2: Remove the email from the team's Invited list
        val teamInvitedRef = FirebaseDatabase.getInstance()
            .getReference("Teams")
            .child(team.tmId.toString())
            .child("Invited")

        teamInvitedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailFound = false
                for (childSnapshot in snapshot.children) {
                    val childValue = childSnapshot.getValue(String::class.java)
                    if (childValue == GlobalClass.Email) {
                        emailFound = true
                        childSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Log.d("Firebase", "Successfully removed email from Invited")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Failed to remove email from Invited: ${e.message}")
                            }
                        break
                    }
                }
                if (!emailFound) {
                    Log.w("Firebase", "Email not found in Invited list")
                    Toast.makeText(context, "No matching email found in Invited list", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Query cancelled: ${error.message}")
                Toast.makeText(context, "Failed to query Invited list", Toast.LENGTH_SHORT).show()
            }
        })

        // Step 3: Decrease the team size in the database
        val sizeRef = FirebaseDatabase.getInstance()
            .getReference("Teams")
            .child(team.tmId.toString())
            .child("size")

        sizeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the current size value, ensuring it is converted safely to an integer
                val currentSize = snapshot.getValue(String::class.java)?.toIntOrNull() ?: 0 // Default to 0 if null or invalid

                if (currentSize > 0) {
                    // Decrement the size value by 1
                    sizeRef.setValue(currentSize - 1)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Successfully decremented size to ${currentSize - 1}")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Failed to decrement size: ${e.message}")
                        }
                } else {
                    Log.w("Firebase", "Size is already 0, cannot decrement further")
                    Toast.makeText(context, "Size is already 0, cannot decrement further", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch size: ${error.message}")
                Toast.makeText(context, "Failed to fetch size", Toast.LENGTH_SHORT).show()
            }
        })

        // Step 4: Update the UI after removing the team
        teamArrayList.remove(team)
        requestedteamsAdapter.notifyDataSetChanged()
    }



}


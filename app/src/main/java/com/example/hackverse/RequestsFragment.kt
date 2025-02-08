package com.example.hackverse

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

class RequestsFragment : Fragment(), RequestsAdapter.OnRejectClickListener , RequestsAdapter.OnAcceptClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var RequestCandidateArrayList: ArrayList<GlobalTeamModel>
    private lateinit var RequestCandidateAdapter: RequestsAdapter

    private var EventId: String? = null
    private var eventName: String? = null
    private var eventTheme: String? = null
    private var candidateName: String? = null
    private var candidateEmail: String? = null
    private var TeamId: String? = null
    private var teamName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recvwrequests)
        recyclerView.layoutManager = LinearLayoutManager(context)
        RequestCandidateArrayList = arrayListOf()
        RequestCandidateAdapter = RequestsAdapter(RequestCandidateArrayList,this,this)
        recyclerView.adapter = RequestCandidateAdapter

        // Fetch current user data
        fetchUserData()
    }

    private fun fetchUserData() {
        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        val dbCurrentUser = FirebaseDatabase.getInstance().getReference("User")
            .child(sanitizedEmail).child("TeamsLeaded")

        dbCurrentUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Firebase", "Snapshot received: $snapshot")
                for (team in snapshot.children) {
                    val teamId = team.getValue(String::class.java) ?: continue
                    TeamId=teamId
                    fetchTeamDetails(teamId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
            }
        })
    }

    private fun fetchTeamDetails(teamId: String) {
        val dbTeamRef = FirebaseDatabase.getInstance().getReference("Teams").child(teamId)

        dbTeamRef.child("Requested").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(requestedSnapshot: DataSnapshot) {
                if (requestedSnapshot.exists() && requestedSnapshot.hasChildren()) {
                    // Fetch Team Name
                    dbTeamRef.child("name").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            teamName = snapshot.getValue(String::class.java) ?: "Unknown Team"
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Error fetching team name: ${error.message}")
                        }
                    })

                    // Fetch Event Details
                    dbTeamRef.child("eventId").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(eventIdSnapshot: DataSnapshot) {
                            val eventId = eventIdSnapshot.getValue(String::class.java) ?: "Unknown Event"
                            EventId = eventId
                            fetchEventDetails(eventId)

                            // Iterate through requested candidates
                            for (candidate in requestedSnapshot.children) {
                                val userEmail = candidate.getValue(String::class.java) ?: continue
                                fetchCandidateDetails(userEmail,teamId)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Error fetching event ID: ${error.message}")
                        }
                    })
                } else {
                    Log.d("Firebase", "No requests found for team $teamId.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching Requested node: ${error.message}")
            }
        })
    }

    private fun fetchEventDetails(eventId: String) {
        val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId)

        dbEventRef.child("evName").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventName = snapshot.getValue(String::class.java) ?: "Unknown Event"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching event name: ${error.message}")
            }
        })

        dbEventRef.child("evTheme").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventTheme = snapshot.getValue(String::class.java) ?: "Unknown Theme"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching event theme: ${error.message}")
            }
        })
    }

    private fun fetchCandidateDetails(userEmail: String,teamId: String) {
        val sanitizedEmail = userEmail.replace(".", ",")
        candidateEmail=userEmail
        val dbUserRef = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)

        dbUserRef.child("empName").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                candidateName = snapshot.getValue(String::class.java) ?: "Unknown Candidate"
                // Add candidate to the list after fetching all details
                val teamModel = GlobalTeamModel(
                    Name = teamName ?: "Unknown Team",
                    LeaderId = null,
                    LeaderName = null,
                    TeamId = teamId,
                    EventId = EventId ?: "Unknown",
                    EventName = eventName ?: "Unknown Event",
                    EventTheme = eventTheme ?: "Unknown Theme",
                    CandidateEmailId = userEmail,
                    CandidateName = candidateName ?: "Unknown Candidate"
                )

                RequestCandidateArrayList.add(teamModel)
                recyclerView.post { RequestCandidateAdapter.notifyDataSetChanged() }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching candidate name: ${error.message}")
            }
        })
    }


    override fun onRejectClick(item: GlobalTeamModel) {
        val clickedRequest = item
        val dbCandidateRequestRef = FirebaseDatabase.getInstance()
            .getReference("Teams")
            .child(clickedRequest.TeamId.toString())
        val candidateEmail = clickedRequest.CandidateEmailId

        dbCandidateRequestRef.child("Requested").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailFound = false
                for (child in snapshot.children) {
                    // Check if the child value matches the candidate email
                    if (child.value == candidateEmail) {
                        // Delete the child with matching email
                        emailFound = true
                        child.ref.removeValue()
                            .addOnSuccessListener {
                                Log.d("Firebase", "Successfully deleted child with email: $candidateEmail")
                                Toast.makeText(context, "Request rejected for ${clickedRequest.CandidateName}", Toast.LENGTH_SHORT).show()
                                RequestCandidateArrayList.remove(item)
                                RequestCandidateAdapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firebase", "Failed to delete child: ${exception.message}")
                                Toast.makeText(context, "Failed to reject request", Toast.LENGTH_SHORT).show()
                            }
                        break
                    }
                }
                if (!emailFound) {
                    Log.d("Firebase", "No matching email found to reject.")
                    Toast.makeText(context, "No matching request found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading data: ${error.message}")
                Toast.makeText(context, "Failed to reject request: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onAcceptClick(item: GlobalTeamModel) {
        var MaxTeamSize = 0
        var CurrentTeamSize = 0
        var RegistrationStatus = false

        // Flag to track the completion of all Firebase tasks
        var completedTasks = 0

        fun EventSideRegistration() {
            val EventID = item.EventId
            val CandidateEmail = item.CandidateEmailId

            if (EventID != null && CandidateEmail != null) {
                val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(EventID)

                // Create a reference to the "Registrations" node
                val registrationsRef = dbEventRef.child("Registrations")

                // Push the CandidateEmail to "Registrations" with a unique push ID
                val newPushRef = registrationsRef.push()
                newPushRef.setValue(CandidateEmail)
            } else {
                Toast.makeText(context, "EventID or CandidateEmail is null", Toast.LENGTH_SHORT).show()
            }
        }

        fun CandidateSideRegistration() {
            val CandidateEmail = item.CandidateEmailId
            val sanitizedEmail2 = CandidateEmail?.replace(".", ",") ?: ""
            val dbCandidateRef = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail2)
            val TeamID = item.TeamId

            // Check if "RegisteredEvents" child exists
            dbCandidateRef.child("RegisteredEvents").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If the "RegisteredEvents" child doesn't exist, create it
                    if (task.result?.exists() == false) {
                        dbCandidateRef.child("RegisteredEvents").setValue(HashMap<String, Any>())
                    }

                    // Push a new node with the TeamID under "RegisteredEvents"
                    val registeredEventsRef = dbCandidateRef.child("RegisteredEvents")
                    val newPushRef = registeredEventsRef.push() // Push a new unique ID
                    newPushRef.setValue(TeamID)
                } else {
                    Toast.makeText(context, "Error fetching RegisteredEvents: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }



        fun UserCredentialReallocationInTeams() {
            val CandidateEmail = item.CandidateEmailId
            val dbTeamRef = FirebaseDatabase.getInstance().getReference("Teams").child(item.TeamId.toString())
            val globallyAcceptedRef = dbTeamRef.child("GloballyAccepted")
            val inGroup = dbTeamRef.child("InGroup")

// Start by removing the email from "Requested"
            dbTeamRef.child("Requested").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val requestedList = task.result?.children
                    if (requestedList != null) {
                        for (child in requestedList) {
                            val value = child.getValue(String::class.java)
                            if (value == CandidateEmail) {
                                child.ref.removeValue() // Directly remove the child reference
                                break
                            }
                        }
                    } else {
                        Toast.makeText(context, "No data found in Requested", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error fetching data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }

                // Now add the email to "GloballyAccepted"
                val newPushRef = globallyAcceptedRef.push()
                newPushRef.setValue(CandidateEmail)

                val newPushRef1 = inGroup.push()
                newPushRef1.setValue(CandidateEmail)

                dbTeamRef.child("size").setValue((++CurrentTeamSize).toString())

                if(CurrentTeamSize==MaxTeamSize){
                    dbTeamRef.child("publicVisibility").setValue(false)
                    dbTeamRef.child("registrationCompleted").setValue(true)
                }

                EventSideRegistration()
                CandidateSideRegistration()
            }

        }


        // A function to evaluate the final condition after all data is retrieved
        fun evaluateEligibility() {
            if (completedTasks == 3) { // Ensure all tasks are completed
                if (MaxTeamSize > CurrentTeamSize) {
                    if (!RegistrationStatus) {
                        Toast.makeText(context, "Added ${item.CandidateName} to team", Toast.LENGTH_SHORT).show()
                        RequestCandidateArrayList.remove(item)
                        RequestCandidateAdapter.notifyDataSetChanged()
                        UserCredentialReallocationInTeams()
                    } else {
                        Toast.makeText(context, "Not Eligible for registration", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Not Eligible for registration", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Fetch MaxTeamSize
        val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(item.EventId.toString())
        dbEventRef.child("evMaxTeamSize").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val value = task.result?.getValue(String::class.java)
                if (value != null) {
                    try {
                        MaxTeamSize = value.toInt()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Invalid value for Max Team Size", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Max Team Size not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error fetching max team size: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
            completedTasks++
            evaluateEligibility()
        }

        // Fetch CurrentTeamSize
        val dbTeamRef = FirebaseDatabase.getInstance().getReference("Teams").child(item.TeamId.toString())
        dbTeamRef.child("size").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val value = task.result?.getValue(String::class.java)
                if (value != null) {
                    try {
                        CurrentTeamSize = value.toInt()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Invalid value for Team Size", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Current Team Size not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error fetching current team size: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
            completedTasks++
            evaluateEligibility()
        }

        // Fetch RegistrationStatus
        dbTeamRef.child("registrationCompleted").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val value = task.result?.getValue(Boolean::class.java)
                if (value != null) {
                    RegistrationStatus = value
                } else {
                    Toast.makeText(context, "Registration Status not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error fetching registration status: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
            completedTasks++
            evaluateEligibility()
        }
    }

}
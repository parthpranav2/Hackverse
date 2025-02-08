package com.example.hackverse

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GlobalTeamsAdapter(val ActiveTeamList: ArrayList<GlobalTeamModel>):RecyclerView.Adapter<GlobalTeamsAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val vuTeamName : TextView = itemView.findViewById(R.id.txtteamname)
        val vuEventName : TextView = itemView.findViewById(R.id.txteventname)
        val vuLeader : TextView = itemView.findViewById(R.id.txtleader)
        val vuTheme : ImageView = itemView.findViewById(R.id.imgthemeCard)
        val butEventDetails : Button = itemView.findViewById(R.id.butActiveEventDetails)
        val butSendRequest  : Button = itemView.findViewById(R.id.butsendrequest)
        val butTeamDetails : Button = itemView.findViewById(R.id.butActiveTeamDetails)

        val context = itemView.context

        init {
            butEventDetails.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedEvent = ActiveTeamList[position]
                    val evId = clickedEvent.EventId
                    if (evId != null) {
                        val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(evId)

                        dbEventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // Parse data into EventModel
                                    val event = snapshot.getValue(EventModel::class.java)
                                    if (event != null) {
                                        // Pass the parsed event to the BottomSheetFragment
                                        if (context is FragmentActivity) {
                                            val bottomSheetFragment = BSFragmentEventData.newInstance(event)
                                            bottomSheetFragment.show(context.supportFragmentManager, "BottomSheetFragmentTag")
                                        } else {
                                            Toast.makeText(context, "Context is not a FragmentActivity", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Log.d("Firebase", "Data is not compatible with EventModel format")
                                    }
                                } else {
                                    Log.d("Firebase", "No data found at the reference")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Database error: ${error.message}")
                            }
                        })
                    }
                }
            }

            butTeamDetails.setOnClickListener{

            }

            butSendRequest.setOnClickListener{
                val teamId = ActiveTeamList[position].TeamId
                if(teamId!=null){
                    val dbTeamRequest =  FirebaseDatabase.getInstance().getReference("Teams").child(teamId).child("Requested")
                    val uniqueKey = dbTeamRequest.push().key

                    if(uniqueKey!=null){
                        dbTeamRequest.child(uniqueKey).setValue(GlobalClass.Email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    butSendRequest.setBackgroundResource(R.drawable.btnwaiting)
                                    butSendRequest.isEnabled=false
                                    butSendRequest.text = "Waiting"
                                    Toast.makeText(context, "Request Sent Sucessfuly", Toast.LENGTH_LONG).show()
                                    Log.d("Firebase", "TeamIdMain successfully added to TeamsLeaded")
                                } else {
                                    Log.e("Firebase", "Failed to add TeamIdMain: ${task.exception?.message}")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firebase", "Error occurred: ${exception.message}")
                            }
                    }
                }else{
                    Toast.makeText(context, "Error occurred in fetching team details", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerglobalteams_items,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ActiveTeamList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val teamId = ActiveTeamList[position].TeamId
        if(teamId!=null){
            val dbTeamRequest = FirebaseDatabase.getInstance().getReference("Teams").child(teamId)

            dbTeamRequest.child("Requested").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val emailToCheck = GlobalClass.Email
                    var emailExistsInRequested = false

                    if (emailToCheck != null) {
                        // Check if the email exists in "Requested"
                        for (child in snapshot.children) {
                            val value = child.getValue(String::class.java)
                            if (value == emailToCheck) {
                                emailExistsInRequested = true
                                break
                            }
                        }
                    }

                    if (emailExistsInRequested) {
                        // Email found in "Requested"
                        holder.butSendRequest.setBackgroundResource(R.drawable.btnwaiting)
                        holder.butSendRequest.isEnabled = false
                        holder.butSendRequest.text = "Waiting"
                    } else {
                        // Email not found in "Requested", check "GloballyAccepted"
                        dbTeamRequest.child("GloballyAccepted").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var emailExistsInAccepted = false

                                if (emailToCheck != null) {
                                    for (child in snapshot.children) {
                                        val value = child.getValue(String::class.java)
                                        if (value == emailToCheck) {
                                            emailExistsInAccepted = true
                                            break
                                        }
                                    }
                                }

                                if (emailExistsInAccepted) {
                                    // Email found in "GloballyAccepted"
                                    holder.butSendRequest.setBackgroundResource(R.drawable.btnaccepted)
                                    holder.butSendRequest.isEnabled = false
                                    holder.butSendRequest.text = "Accepted"
                                    holder.butSendRequest.setTypeface(holder.butSendRequest.typeface, Typeface.BOLD)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error checking GloballyAccepted: ${error.message}")
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error checking Requested: ${error.message}")
                }
            })

        }

        holder.vuTeamName.text=ActiveTeamList[position].Name
        holder.vuEventName.text=ActiveTeamList[position].EventName
        holder.vuLeader.text=ActiveTeamList[position].LeaderName
        when(ActiveTeamList[position].EventTheme){
            "Social Impact" -> holder.vuTheme.setImageResource(R.drawable.socioimpact)
            "Ai and Machine Learning" -> holder.vuTheme.setImageResource(R.drawable.aiml)
            "Sustainability & Climate Change" -> holder.vuTheme.setImageResource(R.drawable.sustainability)
            "HealthTech" -> holder.vuTheme.setImageResource(R.drawable.healthtech)
            "FinTech" -> holder.vuTheme.setImageResource(R.drawable.fintech)
            "Smart Cities & IoT" -> holder.vuTheme.setImageResource(R.drawable.smartcity)
            "Cybersecurity" -> holder.vuTheme.setImageResource(R.drawable.cybersec)
            "Gaming & Entertainment" -> holder.vuTheme.setImageResource(R.drawable.gaming)
            "Blockchain & Decentralized Systems" -> holder.vuTheme.setImageResource(R.drawable.blockchain)
            "Diversity, Equity & Inclusion" -> holder.vuTheme.setImageResource(R.drawable.diversity)
            "Productivity & Workflow Automation" -> holder.vuTheme.setImageResource(R.drawable.workflowautomation)
            "Data Science & Analytics" -> holder.vuTheme.setImageResource(R.drawable.datasci)
            "Mechathon" -> holder.vuTheme.setImageResource(R.drawable.mechathon)
            "Other" -> holder.vuTheme.setImageResource(R.drawable.others)
            else -> holder.vuTheme.setImageResource(R.drawable.others)
        }

    }

}
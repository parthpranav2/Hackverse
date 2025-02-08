package com.example.hackverse

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisteredEventsAdapter(private val teamList: ArrayList<TeamParsableModel>,private val listener: RegisteredEventsAdapter.OnTeamDetailsClickListener) : RecyclerView.Adapter<RegisteredEventsAdapter.MyViewHolder>() {

    interface OnTeamDetailsClickListener {
        fun onTeamDetailsClick(team: TeamParsableModel) // Correct parameter type
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val vuIdentityIcon : ImageView = itemView.findViewById(R.id.imgREGthemeC)
        val vutmName : TextView = itemView.findViewById(R.id.txtREGteamname)
        val vutmevName : TextView = itemView.findViewById(R.id.txtREGnameofevent)
        val butTeamDetails: View = itemView.findViewById(R.id.butRGTeamDetails)
        val butEventDetails : View = itemView.findViewById(R.id.butRGEventDetails)

        init {
            butEventDetails.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedEvent = teamList[position]
                    val evId = clickedEvent.tmEventId
                    if (evId != null) {
                        val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(evId)

                        dbEventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // Parse data into EventModel
                                    val event = snapshot.getValue(EventModel::class.java)
                                    if (event != null) {
                                        // Pass the parsed event to the BottomSheetFragment
                                        val context = itemView.context
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
        }




    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerregisteredevents_items,parent,false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return teamList.count()
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val team = teamList[position]

        holder.vutmName.text = team.tmName
        holder.vutmevName.text = team.tmevName//temp

        when(team.tmevTheme){
            "Social Impact" -> holder.vuIdentityIcon.setImageResource(R.drawable.socioimpact)
            "Ai and Machine Learning" -> holder.vuIdentityIcon.setImageResource(R.drawable.aiml)
            "Sustainability & Climate Change" -> holder.vuIdentityIcon.setImageResource(R.drawable.sustainability)
            "HealthTech" -> holder.vuIdentityIcon.setImageResource(R.drawable.healthtech)
            "FinTech" -> holder.vuIdentityIcon.setImageResource(R.drawable.fintech)
            "Smart Cities & IoT" -> holder.vuIdentityIcon.setImageResource(R.drawable.smartcity)
            "Cybersecurity" -> holder.vuIdentityIcon.setImageResource(R.drawable.cybersec)
            "Gaming & Entertainment" -> holder.vuIdentityIcon.setImageResource(R.drawable.gaming)
            "Blockchain & Decentralized Systems" -> holder.vuIdentityIcon.setImageResource(R.drawable.blockchain)
            "Diversity, Equity & Inclusion" -> holder.vuIdentityIcon.setImageResource(R.drawable.diversity)
            "Productivity & Workflow Automation" -> holder.vuIdentityIcon.setImageResource(R.drawable.workflowautomation)
            "Data Science & Analytics" -> holder.vuIdentityIcon.setImageResource(R.drawable.datasci)
            "Mechathon" -> holder.vuIdentityIcon.setImageResource(R.drawable.mechathon)
            "Other" -> holder.vuIdentityIcon.setImageResource(R.drawable.others)
            else -> holder.vuIdentityIcon.setImageResource(R.drawable.others)
        }

        // Set click listener for butTeamDetails
        holder.butTeamDetails.setOnClickListener {
            listener.onTeamDetailsClick(team) // Pass the correct object
        }
    }
}
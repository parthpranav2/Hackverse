package com.example.hackverse

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

class RequestsAdapter(private val requestCandidateList: ArrayList<GlobalTeamModel>,
                      private val rejectListener: OnRejectClickListener,
                      private val acceptListner: OnAcceptClickListener
) : RecyclerView.Adapter<RequestsAdapter.MyViewHolder>() {

    interface OnRejectClickListener { // Separate listener for reject
        fun onRejectClick(item: GlobalTeamModel)
    }

    interface OnAcceptClickListener{
        fun onAcceptClick(item: GlobalTeamModel)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vuTheme: ImageView = itemView.findViewById(R.id.imgevTheme)
        val vuIdentityIcon: TextView = itemView.findViewById(R.id.txtnameico)
        val vuCandidateName: TextView = itemView.findViewById(R.id.txtcandidatename)
        val vuCandidateEmail: TextView = itemView.findViewById(R.id.txtcandidateemail)
        val vuEventName: TextView = itemView.findViewById(R.id.txtevname)
        val vuTeamName: TextView = itemView.findViewById(R.id.txttmname)
        val butEventDetails: Button = itemView.findViewById(R.id.butActiveEventDetails)
        val butTeamDetails: Button = itemView.findViewById(R.id.butTeamDetails)
        val butAccept: ImageView = itemView.findViewById(R.id.butAccept)
        val butReject: View = itemView.findViewById(R.id.butReject)

        init{
            butEventDetails.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedEvent = requestCandidateList[position]
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerrequests_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = requestCandidateList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = requestCandidateList[position]
        // Set event theme icon
        holder.vuTheme.setImageResource(
            when (item.EventTheme) {
                "Social Impact" -> R.drawable.socioimpact
                "Ai and Machine Learning" -> R.drawable.aiml
                "Sustainability & Climate Change" -> R.drawable.sustainability
                "HealthTech" -> R.drawable.healthtech
                "FinTech" -> R.drawable.fintech
                "Smart Cities & IoT" -> R.drawable.smartcity
                "Cybersecurity" -> R.drawable.cybersec
                "Gaming & Entertainment" -> R.drawable.gaming
                "Blockchain & Decentralized Systems" -> R.drawable.blockchain
                "Diversity, Equity & Inclusion" -> R.drawable.diversity
                "Productivity & Workflow Automation" -> R.drawable.workflowautomation
                "Data Science & Analytics" -> R.drawable.datasci
                "Mechathon" -> R.drawable.mechathon
                else -> R.drawable.others
            }
        )

        // Set other view properties
        holder.vuIdentityIcon.text = item.CandidateName?.firstOrNull()?.uppercase() ?: ""
        holder.vuCandidateName.text = item.CandidateName
        holder.vuCandidateEmail.text = item.CandidateEmailId
        holder.vuEventName.text = item.EventName
        holder.vuTeamName.text = item.Name

        // Optional buttons (check if they are non-null before setting click listeners)
        holder.butReject.setOnClickListener {
            rejectListener.onRejectClick(item) // Pass the correct object
        }
        holder.butAccept.setOnClickListener{
            acceptListner.onAcceptClick(item)
        }

        holder.butTeamDetails?.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Team Details for ${item.Name}", Toast.LENGTH_SHORT).show()
        }
    }
}

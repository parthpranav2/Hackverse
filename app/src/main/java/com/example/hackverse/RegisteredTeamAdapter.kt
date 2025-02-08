package com.example.hackverse

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RegisteredTeamAdapter(val teamList: ArrayList<TeamModel>):RecyclerView.Adapter<RegisteredTeamAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val vuTeamName : TextView = itemView.findViewById(R.id.txtteamname)
        val vuLeader : TextView = itemView.findViewById(R.id.txtleader)
        val vuTeamSize : TextView = itemView.findViewById(R.id.txtteamsize)
        val vuLeaderIdentity : TextView = itemView.findViewById(R.id.txtleaderidentityiconcard)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerregisteredteams_items,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sanitizedEmail = teamList[position].Leader?.replace(".", ",") ?: "default_email"

        val dbLeaderName = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)

        dbLeaderName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the snapshot exists
                if (snapshot.exists()) {
                    // Get the value of the 'evName' child
                    val empName = snapshot.child("empName").getValue(String::class.java)

                    // Do something with the value of evName
                    if (empName != null) {
                        holder.vuLeader.text=empName
                        holder.vuLeaderIdentity.text = empName.firstOrNull()?.toUpperCase()?.toString() ?: ""
                    } else {
                        Log.d("Firebase", "evName not found")
                    }
                } else {
                    Log.d("Firebase", "User not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled", error.toException())
            }
        })


        holder.vuTeamName.text=teamList[position].Name
        holder.vuTeamSize.text=teamList[position].Size
    }

}
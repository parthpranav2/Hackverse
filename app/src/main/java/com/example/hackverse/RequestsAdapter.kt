package com.example.hackverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequestsAdapter(private val teamList: ArrayList<TeamParsableModel>,
                      private val teamDetailsListener: OnTeamDetailsClickListener,
                      private val rejectListener: OnRejectClickListener,
                      private val acceptListner: OnAcceptClickListener
) : RecyclerView.Adapter<RequestsAdapter.MyViewHolder>() {

    interface OnTeamDetailsClickListener {
        fun onTeamDetailsClick(team: TeamParsableModel)
    }

    interface OnRejectClickListener { // Separate listener for reject
        fun onRejectClick(team: TeamParsableModel)
    }

    interface OnAcceptClickListener{
        fun onAcceptClick(team: TeamParsableModel)
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val vuIdentityIcon : ImageView = itemView.findViewById(R.id.imgRthemeC)
        val vutmName : TextView = itemView.findViewById(R.id.txtRteamname)
        val vutmevName : TextView = itemView.findViewById(R.id.txtRnameofevent)
        val vutminvitor : TextView = itemView.findViewById(R.id.txtRnameofinvitor)
        val btnAccept: View = itemView.findViewById(R.id.butAccept)
        val btnReject: View = itemView.findViewById(R.id.butReject)
        val butTeamDetails: View = itemView.findViewById(R.id.butTeamDetails)

        var vuevId: String? = null
        var vuTeamId: String? = null

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerrequests_items,parent,false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return teamList.count()
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val team = teamList[position]

        holder.vutmName.text = team.tmName
        holder.vutmevName.text = team.tmevName//temp
        holder.vutminvitor.text = team.tmInviterEmail//temp

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
            teamDetailsListener.onTeamDetailsClick(team) // Pass the correct object
        }
        holder.btnReject.setOnClickListener {
            rejectListener.onRejectClick(team) // Pass the correct object
        }
        holder.btnAccept.setOnClickListener{
            acceptListner.onAcceptClick(team)
        }

    }
}
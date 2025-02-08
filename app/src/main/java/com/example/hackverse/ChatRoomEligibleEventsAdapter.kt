package com.example.hackverse

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.integrity.v


class ChatRoomEligibleEventsAdapter(private val eventList: ArrayList <EventModel>) : RecyclerView.Adapter<ChatRoomEligibleEventsAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vuEventName: TextView = itemView.findViewById(R.id.txtevname)
        val vuTheme: ImageView = itemView.findViewById(R.id.imgevtheme)

        var vuevId: String? = null

        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatRoom::class.java)
                intent.putExtra("EVENT_ID", vuevId)
                itemView.context.startActivity(intent)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclereventlistforchatroom_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = eventList[position]
        holder.vuevId= event.evId
        holder.vuEventName.text = event.evName
            when (event.evTheme) {
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

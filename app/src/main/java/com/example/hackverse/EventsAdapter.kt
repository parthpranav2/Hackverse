package com.example.hackverse

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class EventsAdapter(private val eventList: ArrayList <EventModel>) : RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vuName: TextView = itemView.findViewById(R.id.txtnameCard)
        val vuTA: TextView = itemView.findViewById(R.id.txtTACard)
        val vumode: TextView = itemView.findViewById(R.id.txtmodeCard)
        val vussd: TextView = itemView.findViewById(R.id.txtssdCard)
        val vused: TextView = itemView.findViewById(R.id.txtsedCard)
        val vuTheme: ImageView = itemView.findViewById(R.id.imgthemeCard)

        var vuevId: String? = null

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Access the clicked item using the adapter's event list
                    val clickedEvent = eventList[position]

                    // Create an Intent to pass the EventModel object to another activity
                    val context = itemView.context
                    if(context is FragmentActivity){
                        val bottomSheetFragment = BSFragmentEventData.newInstance(clickedEvent)
                        bottomSheetFragment.show(context.supportFragmentManager,"BottomSheetFragmentTag")
                    }else {
                        Toast.makeText(context, "Context is not a FragmentActivity", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleractiveevents_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun updateData(newList: ArrayList<EventModel>) {
        eventList.clear()
        eventList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = eventList[position]

        holder.vuName.text = event.evName
        holder.vuTA.text = event.evTargetAudiance
        holder.vumode.text = event.evMode
        holder.vussd.text = event.evssd
        holder.vused.text = event.evsed

        when (event.evMode) {
            "Online" -> holder.vumode.setTextColor(Color.parseColor("#6FFFB8"))
            "Offline" -> holder.vumode.setTextColor(Color.parseColor("#FF5C5C"))
            "Synergy" -> holder.vumode.setTextColor(Color.parseColor("#81D4FA"))
            else -> holder.vumode.setTextColor(Color.parseColor("#E0E0E0"))
        }

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

        holder.vuevId=event.evId
    }
}

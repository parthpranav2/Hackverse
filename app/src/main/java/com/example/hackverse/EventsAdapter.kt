package com.example.hackverse

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsAdapter(private var fullList: ArrayList<EventModel>) : RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {

    private var eventList: ArrayList<EventModel> = ArrayList(fullList) // Store original unfiltered list

    init {
        eventList.addAll(fullList)
    }

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
                    val clickedEvent = eventList[position]
                    val context = itemView.context
                    if (context is FragmentActivity) {
                        val bottomSheetFragment = BSFragmentEventData.newInstance(clickedEvent)
                        bottomSheetFragment.show(context.supportFragmentManager, "BottomSheetFragmentTag")
                    } else {
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

    /** Updates full list and displayed list */
    fun updateData(newList: ArrayList<EventModel>) {
        eventList.clear()
        eventList.addAll(newList)
        notifyDataSetChanged() // Notify adapter of changes
    }

    fun filterList_activeeventsactivity(){
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val currentDate = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val filteredEvents = when (GlobalClass.FilterActiveEvents) {
            0 -> fullList.filter {
                val eventDate = it.evsed?.let { dateStr -> dateFormat.parse(dateStr) } ?: return@filter false
                val eventTime = it.evset?.let { timeStr -> timeFormat.parse(timeStr) } ?: return@filter false

                val eventCalendar = Calendar.getInstance().apply {
                    time = eventDate
                    set(Calendar.HOUR_OF_DAY, eventTime.hours)
                    set(Calendar.MINUTE, eventTime.minutes)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                eventCalendar.time.after(currentDate) // Future events
            }
            1 -> fullList // No filtering
            2 -> fullList.filter {
                val eventDate = it.evsed?.let { dateStr -> dateFormat.parse(dateStr) } ?: return@filter false
                val eventTime = it.evset?.let { timeStr -> timeFormat.parse(timeStr) } ?: return@filter false

                val eventCalendar = Calendar.getInstance().apply {
                    time = eventDate
                    set(Calendar.HOUR_OF_DAY, eventTime.hours)
                    set(Calendar.MINUTE, eventTime.minutes)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                eventCalendar.time.before(currentDate) // Past events
            }
            3 -> fullList.filter { it.evMode.equals("Offline", ignoreCase = true) }
            4 -> fullList.filter { it.evMode.equals("Online", ignoreCase = true) }
            5 -> fullList.filter { it.evMode.equals("Synergy", ignoreCase = true) }
            else -> fullList
        }

        Log.d("EventsAdapter", "Filtered Events Count: ${filteredEvents.size}")

        eventList.clear()
        eventList.addAll(filteredEvents)
        notifyDataSetChanged()
    }

    fun filterList_addhackathonactivity() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val currentDate = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val filteredEvents = when (GlobalClass.FilterAddHackathon) {
            0 -> fullList.filter {
                val eventDate = it.evsed?.let { dateStr -> dateFormat.parse(dateStr) } ?: return@filter false
                val eventTime = it.evset?.let { timeStr -> timeFormat.parse(timeStr) } ?: return@filter false

                val eventCalendar = Calendar.getInstance().apply {
                    time = eventDate
                    set(Calendar.HOUR_OF_DAY, eventTime.hours)
                    set(Calendar.MINUTE, eventTime.minutes)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                eventCalendar.time.after(currentDate) // Future events
            }
            1 -> fullList // No filtering
            2 -> fullList.filter {
                val eventDate = it.evsed?.let { dateStr -> dateFormat.parse(dateStr) } ?: return@filter false
                val eventTime = it.evset?.let { timeStr -> timeFormat.parse(timeStr) } ?: return@filter false

                val eventCalendar = Calendar.getInstance().apply {
                    time = eventDate
                    set(Calendar.HOUR_OF_DAY, eventTime.hours)
                    set(Calendar.MINUTE, eventTime.minutes)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                eventCalendar.time.before(currentDate) // Past events
            }
            3 -> fullList.filter { it.evMode.equals("Offline", ignoreCase = true) }
            4 -> fullList.filter { it.evMode.equals("Online", ignoreCase = true) }
            5 -> fullList.filter { it.evMode.equals("Synergy", ignoreCase = true) }
            else -> fullList
        }

        eventList.clear()
        eventList.addAll(filteredEvents)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = eventList[position]

        holder.vuName.text = event.evName
        holder.vuTA.text = event.evTargetAudiance
        holder.vumode.text = event.evMode
        holder.vussd.text = event.evssd
        holder.vused.text = event.evsed

        // Change text color based on mode
        holder.vumode.setTextColor(
            when (event.evMode) {
                "Online" -> Color.parseColor("#6FFFB8")
                "Offline" -> Color.parseColor("#FF5C5C")
                "Synergy" -> Color.parseColor("#81D4FA")
                else -> Color.parseColor("#E0E0E0")
            }
        )

        // Set theme images based on event theme
        holder.vuTheme.setImageResource(
            when (event.evTheme) {
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
                "Other" -> R.drawable.others
                else -> R.drawable.others
            }
        )

        holder.vuevId = event.evId
    }
}



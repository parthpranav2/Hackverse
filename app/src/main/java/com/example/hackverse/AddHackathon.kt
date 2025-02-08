package com.example.hackverse

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddHackathon : AppCompatActivity() ,BSFragmentFilter_addhackathon.FilterListener{
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList: ArrayList<EventModel>
    private lateinit var fullList: ArrayList<EventModel> // Store all events for filtering
    private lateinit var database1: DatabaseReference
    private lateinit var database2: DatabaseReference
    private lateinit var adapter: EventsAdapter // Adapter instance

    private var currentFilter: String = "Valid Events" // Store the current filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdmaddhackathon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recvwaddhackathon)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventArrayList = arrayListOf()
        fullList = arrayListOf()
        adapter = EventsAdapter(eventArrayList)
        recyclerView.adapter = adapter

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""

        database2 = FirebaseDatabase.getInstance().getReference("Events")
        database1 = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail).child("EventsCreated")

        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    eventArrayList.clear()
                    fullList.clear()
                    val eventIds = snapshot.children.mapNotNull { it.getValue(String::class.java) }

                    val remainingEvents = eventIds.size
                    var fetchedEvents = 0

                    for (eventId in eventIds) {
                        database2.child(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(eventSnapshot: DataSnapshot) {
                                val event = eventSnapshot.getValue(EventModel::class.java)
                                event?.let {
                                    if (!fullList.contains(it)) {
                                        fullList.add(it)
                                        eventArrayList.add(it)
                                    }
                                }
                                fetchedEvents++

                                // Check if all events are fetched, then apply filter
                                if (fetchedEvents == remainingEvents) {
                                    adapter.updateData(fullList) // Refresh RecyclerView
                                    adapter.filterList_addhackathonactivity() // Apply filter once list is fully populated
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Failed to fetch event details: ${error.message}")
                            }
                        })
                    }
                } else {
                    Log.e("Firebase", "Snapshot is empty or does not exist.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch event IDs: ${error.message}")
            }
        })


        // Add space below the last item in RecyclerView
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.last_item_margin)
        recyclerView.addItemDecoration(LastItemMarginDecoration(marginInPixels))

        findViewById<TextView>(R.id.lblname).text = GlobalClass.NameIco

        findViewById<LinearLayout>(R.id.btnhome).setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnfilter).setOnClickListener {
            val bottomSheet = BSFragmentFilter_addhackathon()
            val bundle = Bundle()
            bundle.putString("selected_filter", currentFilter)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        findViewById<LinearLayout>(R.id.btnchatroom).setOnClickListener {
            startActivity(Intent(this, ChatRoomEventList::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnportfolio).setOnClickListener {
            startActivity(Intent(this, Portfolio::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnactiveevents).setOnClickListener {
            startActivity(Intent(this, ActiveEvents::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<RelativeLayout>(R.id.btnusernico).setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }

        findViewById<LinearLayout>(R.id.btncreateevent).setOnClickListener {
            startActivity(Intent(this, CreateEvent::class.java))
        }
    }

    /*override fun onFilterApplied(filter: Int) {
        GlobalClass.FilterAddHackathon = filter // Store the selected filter globally
        filterList() // Apply the new filter
    }*/

    // This method will be called when the filter is applied in the BottomSheetFragment
    override fun onFilterApplied(filter: Int) {
        GlobalClass.FilterAddHackathon = filter
        adapter.filterList_addhackathonactivity()  // Apply the new filter
    }


    // Callback from Bottom Sheet to apply filter
    /*fun filterList() {
        val filterString = when (GlobalClass.FilterAddHackathon) {
            0 -> "All Events"
            1 -> "Valid Events"
            2 -> "Online"
            3 -> "Offline"
            4 -> "Synergy Events"
            else -> ""
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val currentDate = dateFormat.format(Calendar.getInstance().time)
        val currentTime = timeFormat.format(Calendar.getInstance().time)

        val filteredByDateTime = fullList.filter { event ->
            val eventDate = event.evsed
            val eventTime = event.evset

            if (eventDate.isNullOrEmpty() || eventTime.isNullOrEmpty()) {
                false
            } else {
                eventDate > currentDate || (eventDate == currentDate && eventTime > currentTime)
            }
        }

        eventArrayList.clear()
        eventArrayList.addAll(
            if (GlobalClass.FilterAddHackathon == 1) {
                filteredByDateTime
            } else {
                filteredByDateTime.filter { event ->
                    event.evMode?.contains(filterString, ignoreCase = true) == true ||
                            event.evTheme?.contains(filterString, ignoreCase = true) == true
                }
            }
        )

        adapter.notifyDataSetChanged() // Notify RecyclerView of changes
    }*/

    class LastItemMarginDecoration(private val bottomMargin: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val itemCount = state.itemCount

            if (position == itemCount - 1) {
                outRect.bottom = bottomMargin
            }
        }
    }
}

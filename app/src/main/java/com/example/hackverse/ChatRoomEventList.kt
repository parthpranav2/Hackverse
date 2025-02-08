package com.example.hackverse

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ChatRoomEventList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var EligibleEventArrayList: ArrayList<EventModel>
    private lateinit var eventAdapter: ChatRoomEligibleEventsAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frmdmchatroom) // Set content first
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recvwchatroomeventlist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        EligibleEventArrayList = arrayListOf()
        eventAdapter = ChatRoomEligibleEventsAdapter(EligibleEventArrayList)
        recyclerView.adapter = eventAdapter  //  Set adapter here

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""

        // Function to fetch event name and theme
        fun fetchEventDetails(tmId: String, callback: (String, String, String) -> Unit) {
            val dbTeamRef = FirebaseDatabase.getInstance().getReference("Teams").child(tmId)

            dbTeamRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val evId = snapshot.child("eventId").getValue(String::class.java) ?: "Unknown evId"

                    val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(evId)
                    dbEventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(eventSnapshot: DataSnapshot) {
                            val evName = eventSnapshot.child("evName").getValue(String::class.java) ?: "Unknown Event"
                            val evTheme = eventSnapshot.child("evTheme").getValue(String::class.java) ?: "Unknown Theme"
                            callback(evId, evName, evTheme)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Error fetching event details: ${error.message}")
                            callback(evId, "Unknown Event", "Unknown Theme")
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching team details: ${error.message}")
                    callback("Unknown evId", "Unknown Event", "Unknown Theme")
                }
            })
        }

        // Fetch registered event IDs from Firebase
        database = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail).child("RegisteredEvents")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    EligibleEventArrayList.clear()  // Clear before adding new items
                    val tempList = ArrayList<EventModel>()

                    var processedCount = 0
                    val totalCount = snapshot.childrenCount.toInt()

                    for (dataSnapShot in snapshot.children) {
                        val tmId = dataSnapShot.getValue(String::class.java)

                        tmId?.let { teamId ->
                            fetchEventDetails(teamId) { eventId, eventName, eventTheme ->
                                val eventModel = EventModel(evId = eventId, evName = eventName, evTheme = eventTheme)
                                tempList.add(eventModel)

                                processedCount++
                                if (processedCount == totalCount) {
                                    // Once all events are fetched, update the RecyclerView on UI thread
                                    runOnUiThread {
                                        EligibleEventArrayList.clear()
                                        EligibleEventArrayList.addAll(tempList)
                                        eventAdapter.notifyDataSetChanged()  // 🔥 Notify adapter
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.e("Firebase", "No registered events found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatRoomEventList, error.toString(), Toast.LENGTH_LONG).show()
            }
        })

        // Add margin for last item in RecyclerView
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.last_item_margin)
        recyclerView.addItemDecoration(LastItemMarginDecoration(marginInPixels))

        // UI Click Listeners
        findViewById<TextView>(R.id.lblname).text = GlobalClass.NameIco

        findViewById<LinearLayout>(R.id.btnhome).setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnactiveevents).setOnClickListener {
            startActivity(Intent(this, ActiveEvents::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnportfolio).setOnClickListener {
            startActivity(Intent(this, Portfolio::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnaddhackathon).setOnClickListener {
            startActivity(Intent(this, AddHackathon::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<RelativeLayout>(R.id.btnusernico).setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }
    }

    // Class to add bottom margin for last item in RecyclerView
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

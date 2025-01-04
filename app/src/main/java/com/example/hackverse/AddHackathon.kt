package com.example.hackverse

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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

class AddHackathon : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList: ArrayList<EventModel>
    private lateinit var database1: DatabaseReference
    private lateinit var database2: DatabaseReference

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
        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""

        database2 = FirebaseDatabase.getInstance().getReference("Events")
        database1 = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail).child("EventsCreated")
        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    eventArrayList.clear()
                    val eventIds = snapshot.children.mapNotNull { it.getValue(String::class.java) }

                    for (eventId in eventIds) {
                        database2.child(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(eventSnapshot: DataSnapshot) {
                                val event = eventSnapshot.getValue(EventModel::class.java)
                                event?.let {
                                    if (!eventArrayList.contains(it)) {
                                        eventArrayList.add(it)
                                        recyclerView.adapter = EventsAdapter(eventArrayList)
                                    }
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

        //to create some space below the last item in recycler view so that the info of last item is completely visible and not hindered by floating button
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.last_item_margin)
        recyclerView.addItemDecoration(LastItemMarginDecoration(marginInPixels))


        findViewById<TextView>(R.id.lblname).text = GlobalClass.NameIco

        findViewById<LinearLayout>(R.id.btnhome).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnportfolio).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Portfolio::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnactiveevents).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, ActiveEvents::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<RelativeLayout>(R.id.btnusernico).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Account::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btncreateevent).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, CreateEvent::class.java)
            startActivity(intent)
        }


    }

    class LastItemMarginDecoration(private val bottomMargin: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val itemCount = state.itemCount

            // Add bottom margin only to the last item
            if (position == itemCount - 1) {
                outRect.bottom = bottomMargin
            }
        }
    }
}
package com.example.hackverse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookmarkFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList: ArrayList<EventModel>
    private lateinit var database1: DatabaseReference
    private lateinit var database2: DatabaseReference
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    // This is where the RecyclerView and adapter setup should happen
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView

        // Ensure the RecyclerView is found correctly
        recyclerView = view.findViewById(R.id.recvwbookmarkedevents)
        recyclerView.layoutManager = LinearLayoutManager(context)
        eventArrayList = arrayListOf()
        eventsAdapter = EventsAdapter(eventArrayList)
        recyclerView.adapter = eventsAdapter

        // Get the current user's email
        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        if (sanitizedEmail.isEmpty()) {
            Toast.makeText(context, "Email is not available", Toast.LENGTH_SHORT).show()
            return
        }

        database1 = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail).child("BookmarkedEvents")
        database2 = FirebaseDatabase.getInstance().getReference("Events")
        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    eventArrayList.clear()
                    val eventIds = snapshot.children.mapNotNull { it.key }

                    for (eventId in eventIds) {
                        database2.child(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(eventSnapshot: DataSnapshot) {
                                val event = eventSnapshot.getValue(EventModel::class.java)
                                event?.let {
                                    view.findViewById<TextView>(R.id.txtBookmarkCount)
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


    }

}


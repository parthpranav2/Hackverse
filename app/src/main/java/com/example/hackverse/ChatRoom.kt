package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ChatRoom : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatArayList: ArrayList<ChatRoomModel>
    private lateinit var RequestCandidateAdapter: ChatsAdapter

    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_room)

        // Get the EVENT_ID from the Intent
        eventId = intent.getStringExtra("EVENT_ID")

        // Set up window insets handling as before
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
          // Ensures the most recent message is at the bottom
        recyclerView.layoutManager = layoutManager

        chatArayList = arrayListOf()
        RequestCandidateAdapter = ChatsAdapter(chatArayList,recyclerView)
        recyclerView.adapter = RequestCandidateAdapter

        // Listen for chat messages if eventId is available
        if (eventId != null) {
            listenForChatMessages()
        }

        findViewById<ImageView>(R.id.btnback).setOnClickListener {
            // Navigate to event list screen
            val intent = Intent(this, ChatRoomEventList::class.java)
            startActivity(intent)
        }

        EventDetailsRetriver(eventId)

        findViewById<ImageView>(R.id.butSend).setOnClickListener {
            ChatSaver(eventId)
        }
    }

    fun listenForChatMessages() {
        val MyEmail = GlobalClass.Email
        val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId.toString())

        dbEventRef.child("ChatRoom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val chatList = snapshot.children.toList()

                    // Create a temporary list to hold all chatRoomModels
                    val updatedChatList = ArrayList<ChatRoomModel>()

                    for (i in chatList.indices) {
                        val chatSnapshot = chatList[i]
                        val SenderId = chatSnapshot.child("userId").getValue(String::class.java)
                        val Message = chatSnapshot.child("chat").getValue(String::class.java)
                        val CommitTime = chatSnapshot.child("commitTime").getValue(String::class.java)
                        val CommitDate = chatSnapshot.child("commitDate").getValue(String::class.java)

                        val UserType = if (SenderId == MyEmail) {
                            "Me"
                        } else {
                            "NotMe"
                        }

                        var SuccedingMessageSenderId: String? = null
                        var SuccedingMessageDate: String? = null

                        // Check if there's a succeeding message
                        if (i < chatList.size - 1) {
                            val succeedingChatSnapshot = chatList[i + 1]
                            SuccedingMessageSenderId = succeedingChatSnapshot.child("userId").getValue(String::class.java)
                            SuccedingMessageDate = succeedingChatSnapshot.child("commitDate").getValue(String::class.java)
                        }

                        if (SenderId != null) {
                            getSenderName(SenderId) { SenderName ->
                                // Create a ChatRoomModel instance with the data
                                val chatRoomModel = ChatRoomModel(
                                    SenderId = SenderId,
                                    SenderName = SenderName,
                                    Message = Message,
                                    CommitTime = CommitTime,
                                    CommitDate = CommitDate,
                                    SuccedingMessageSenderId = SuccedingMessageSenderId,
                                    SuccedingMessageDate = SuccedingMessageDate,
                                    UserType = UserType
                                )

                                // Add the chat to the temporary list
                                updatedChatList.add(chatRoomModel)

                                // Once all chats are processed, update the adapter
                                if (updatedChatList.size == chatList.size) {
                                    // Sort the list by CommitDate and CommitTime (oldest first)
                                    updatedChatList.sortWith(compareBy({ it.CommitDate }, { it.CommitTime }))

                                    // Update the original list and notify the adapter
                                    chatArayList.clear()
                                    chatArayList.addAll(updatedChatList)

                                    // Notify the adapter about the update
                                    RequestCandidateAdapter.notifyDataSetChanged()

                                    // Scroll to the top of the RecyclerView
                                    recyclerView.scrollToPosition(0)
                                }
                            }
                        }
                    }
                } else {
                    Log.d("ChatRoom", "No data available in the ChatRoom")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase Error", error.message)
            }
        })
    }


    // Function to get sender name from the user node using sanitized senderId
    fun getSenderName(senderId: String, onComplete: (String) -> Unit) {
        val sanitizedSenderId = senderId.replace(".", ",")
        val dbUserRef = FirebaseDatabase.getInstance().getReference("User").child(sanitizedSenderId)

        dbUserRef.child("empName").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val empName = snapshot.getValue(String::class.java)
                    onComplete(empName ?: "Unknown User")
                } else {
                    onComplete("Unknown User")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase Error", error.message)
                onComplete("Unknown User")
            }
        })
    }

    // Function to retrieve event details
    fun EventDetailsRetriver(evId: String?) {
        if (evId != null) {
            val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(evId)
            val imgTheme = findViewById<ImageView>(R.id.imgTheme)
            dbEventRef.child("evName").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val evName = snapshot.getValue(String::class.java)
                    findViewById<TextView>(R.id.txtevName).text = evName ?: "No value found"
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase Error", error.message)
                }
            })

            dbEventRef.child("evTheme").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val evTheme = snapshot.getValue(String::class.java)
                    when (evTheme) {
                        "Social Impact" -> imgTheme.setImageResource(R.drawable.socioimpact)
                        "Ai and Machine Learning" -> imgTheme.setImageResource(R.drawable.aiml)
                        "Sustainability & Climate Change" -> imgTheme.setImageResource(R.drawable.sustainability)
                        "HealthTech" -> imgTheme.setImageResource(R.drawable.healthtech)
                        "FinTech" -> imgTheme.setImageResource(R.drawable.fintech)
                        "Smart Cities & IoT" -> imgTheme.setImageResource(R.drawable.smartcity)
                        "Cybersecurity" -> imgTheme.setImageResource(R.drawable.cybersec)
                        "Gaming & Entertainment" -> imgTheme.setImageResource(R.drawable.gaming)
                        "Blockchain & Decentralized Systems" -> imgTheme.setImageResource(R.drawable.blockchain)
                        "Diversity, Equity & Inclusion" -> imgTheme.setImageResource(R.drawable.diversity)
                        "Productivity & Workflow Automation" -> imgTheme.setImageResource(R.drawable.workflowautomation)
                        "Data Science & Analytics" -> imgTheme.setImageResource(R.drawable.datasci)
                        "Mechathon" -> imgTheme.setImageResource(R.drawable.mechathon)
                        "Other" -> imgTheme.setImageResource(R.drawable.others)
                        else -> imgTheme.setImageResource(R.drawable.others)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase Error", error.message)
                }
            })
        }
    }

    // Function to save a new chat message
    fun ChatSaver(evId: String?) {
        if (evId != null) {
            val currentDate = Date()
            val dbEventRef = FirebaseDatabase.getInstance().getReference("Events").child(evId)

            val userId = GlobalClass.Email
            val chat = findViewById<EditText>(R.id.chatInput).text.toString()
            val commitDate = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(currentDate)
            val commitTime = SimpleDateFormat("h:mm a", Locale.ENGLISH).format(currentDate)

            dbEventRef.child("ChatRoom").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newChatRef = dbEventRef.child("ChatRoom").push()

                    val chatData = mapOf(
                        "userId" to userId,
                        "chat" to chat,
                        "commitDate" to commitDate,
                        "commitTime" to commitTime
                    )

                    newChatRef.setValue(chatData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Firebase", "Chat data added successfully")
                            findViewById<EditText>(R.id.chatInput).setText("")

                            // Ensure RecyclerView is scrolled to the last item after chat is added
                            recyclerView.post {
                                recyclerView.scrollToPosition(chatArayList.size - 1)
                            }
                        } else {
                            Log.e("Firebase Error", "Failed to add chat data")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase Error", error.message)
                }
            })
        }
    }

}
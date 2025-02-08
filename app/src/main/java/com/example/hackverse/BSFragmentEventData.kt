package com.example.hackverse

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView  // Import TextView
import android.widget.ImageView // Import ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BSFragmentEventData : BottomSheetDialogFragment() {
    private lateinit var recyclerViewComments: RecyclerView
    private lateinit var recyclerViewTeams: RecyclerView
    private lateinit var commentArrayList: ArrayList<CommentModel>
    private lateinit var teamArrayList: ArrayList<TeamModel>
    private lateinit var dbRefE1: DatabaseReference
    private lateinit var dbRefE2: DatabaseReference
    private lateinit var dbRefU1: DatabaseReference//bookmark
    private lateinit var dbRefU2: DatabaseReference//likes
    private lateinit var rootView: View

    private lateinit var evcomment: EditText


    companion object {
        private const val ARG_EVENT = "event"

        fun newInstance(event: EventModel): BSFragmentEventData {
            val fragment = BSFragmentEventData()
            val bundle = Bundle()
            bundle.putParcelable(ARG_EVENT, event)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var event: EventModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottomsheet_fragment_eventinfo, container, false)

        event = arguments?.getParcelable(ARG_EVENT) ?: throw IllegalStateException("EventModel is null")

        // Use findViewById to access the views
        val txtEventName = view.findViewById<TextView>(R.id.txtEventName) ?: return null
        val txtEventName2 = view.findViewById<TextView>(R.id.txtEventName2) ?: return null
        val txtEventDescription = view.findViewById<TextView>(R.id.txtEventDescription) ?: return null
        val txtEventMode = view.findViewById<TextView>(R.id.txtEventMode) ?: return null
        val txtEventTheme = view.findViewById<TextView>(R.id.txtEventTheme) ?: return null
        val txtEventAudience = view.findViewById<TextView>(R.id.txtEventAudience) ?: return null
        val txtEventVenue = view.findViewById<TextView>(R.id.txtEventVenue) ?: return null
        val txtEventssd = view.findViewById<TextView>(R.id.txtEventssd) ?: return null
        val txtEventsed = view.findViewById<TextView>(R.id.txtEventsed) ?: return null
        val txtEventsst = view.findViewById<TextView>(R.id.txtEventsst) ?: return null
        val txtEventset = view.findViewById<TextView>(R.id.txtEventset) ?: return null
        val txtEventTeamSize = view.findViewById<TextView>(R.id.txtEventTeamSize) ?: return null
        val imgEventTheme = view.findViewById<ImageView>(R.id.imgEventTheme)
        val imgPoster = view.findViewById<ImageView>(R.id.imgEventPoster)


        // Set the event data to the views
        txtEventName.text = event.evName
        txtEventName2.text = event.evName
        txtEventDescription.text = event.evDescription
        txtEventMode.text = event.evMode
        when (event.evMode) {
            "Online" ->  txtEventMode.setTextColor(Color.parseColor("#6FFFB8"))
            "Offline" -> txtEventMode.setTextColor(Color.parseColor("#FF5C5C"))
            "Synergy" -> txtEventMode.setTextColor(Color.parseColor("#81D4FA"))
            else -> txtEventMode.setTextColor(Color.parseColor("#E0E0E0"))
        }
        txtEventTheme.text = event.evTheme
        txtEventAudience.text = event.evTargetAudiance
        txtEventVenue.text = event.evVenue
        txtEventssd.text = event.evssd
        txtEventsed.text = event.evsed
        txtEventsst.text = event.evsst
        txtEventset.text = event.evset
        txtEventTeamSize.text = event.evMaxTeamSize

        // Set the event theme image (similar to your code)
        when (event.evTheme) {
            "Social Impact" -> imgEventTheme.setImageResource(R.drawable.socioimpact)
            "Ai and Machine Learning" -> imgEventTheme.setImageResource(R.drawable.aiml)
            "Sustainability & Climate Change" -> imgEventTheme.setImageResource(R.drawable.sustainability)
            "HealthTech" -> imgEventTheme.setImageResource(R.drawable.healthtech)
            "FinTech" -> imgEventTheme.setImageResource(R.drawable.fintech)
            "Smart Cities & IoT" -> imgEventTheme.setImageResource(R.drawable.smartcity)
            "Cybersecurity" -> imgEventTheme.setImageResource(R.drawable.cybersec)
            "Gaming & Entertainment" -> imgEventTheme.setImageResource(R.drawable.gaming)
            "Blockchain & Decentralized Systems" -> imgEventTheme.setImageResource(R.drawable.blockchain)
            "Diversity, Equity & Inclusion" -> imgEventTheme.setImageResource(R.drawable.diversity)
            "Productivity & Workflow Automation" -> imgEventTheme.setImageResource(R.drawable.workflowautomation)
            "Data Science & Analytics" -> imgEventTheme.setImageResource(R.drawable.datasci)
            "Mechathon" -> imgEventTheme.setImageResource(R.drawable.mechathon)
            "Other" -> imgEventTheme.setImageResource(R.drawable.others)
            else -> imgEventTheme.setImageResource(R.drawable.others)
        }

        val posterUrl = event.evPosterURL


        if(posterUrl.isNullOrEmpty()){
            imgPoster.visibility=View.GONE
            view.findViewById<LinearLayout>(R.id.llposter).visibility=View.GONE
            view.findViewById<View>(R.id.lLineposter).visibility=View.GONE
        }else{
            imgPoster.visibility=View.VISIBLE
            view.findViewById<LinearLayout>(R.id.llposter).visibility=View.VISIBLE
            view.findViewById<View>(R.id.lLineposter).visibility=View.VISIBLE
        }

        Glide.with(this)
            .load(posterUrl) // Load the text content of the EditText as a URL
            .fitCenter()
            .placeholder(R.drawable.poster) // Placeholder image
            .error(R.drawable.unlink) // Error image if the URL fails
            .into(imgPoster) // Load the image into the ImageView

        // Initialize the RelativeLayout reference
        val registrationPallet = view.findViewById<RelativeLayout>(R.id.registrationpallet)

        val registrationRef = FirebaseDatabase.getInstance()
            .getReference("Events")
            .child(event.evId.toString())
            .child("Registrations")

        // Add a listener to check if the sanitizedEmail exists as a value for any child
        registrationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailExists = false

                // Iterate through the children to check if any of the values match the sanitizedEmail
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.value == GlobalClass.Email) {
                        emailExists = true
                        break
                    }

                }

                if (emailExists) {
                    // The sanitizedEmail exists as a value in one of the children
                    registrationPallet.visibility = View.GONE
                } else {
                    // The sanitizedEmail does not exist as a value in any of the children
                    registrationPallet.visibility = View.VISIBLE
                }

                val currentDateTime = Calendar.getInstance().time

// Define the date and time format
                val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm", Locale.getDefault())

// Define the target date and time
                val targetDateTime = dateFormat.parse("${event.evsed} ${event.evset}")

// Check if the current date-time matches the target
                if (currentDateTime.after(targetDateTime)) {
                    registrationPallet.visibility = View.GONE
                } else {
                    registrationPallet.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors (e.g., network issues)
                Toast.makeText(requireContext(), "Error checking registration: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })


        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView=view


        recyclerViewComments = view.findViewById(R.id.recyclercomments_items)
        recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())
        commentArrayList = arrayListOf()

        recyclerViewTeams = view.findViewById(R.id.recyclerregisteredteams_items)
        recyclerViewTeams.layoutManager = LinearLayoutManager(requireContext())
        teamArrayList= arrayListOf()


        val butLike = view.findViewById<Button>(R.id.butlike)
        val txtLikes =  view.findViewById<TextView>(R.id.txtlikes)
        var isLiked = false

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: "default_email"


        //add liked event to the user in evId form
        dbRefU2= FirebaseDatabase.getInstance().getReference("User")
            .child(sanitizedEmail)
            .child("LikedEvents")


        dbRefU2.child(event.evId.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLiked = snapshot.exists() // Set isLiked to true if evId exists, false otherwise

                // Update UI or perform other actions based on isLiked value
                if (isLiked) {
                    butLike.background = ContextCompat.getDrawable(requireContext(), R.drawable.like_a)
                    txtLikes.setTextColor(Color.parseColor("#5E7BF0"))
                } else {
                    butLike.background = ContextCompat.getDrawable(requireContext(), R.drawable.like_na)
                    txtLikes.setTextColor(Color.parseColor("#969DA4"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error checking liked status: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })


        dbRefE2 = FirebaseDatabase.getInstance().getReference("Events")
            .child(event.evId.toString())
            .child("LikesCount")

        dbRefE2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Retrieve the value from the snapshot
                    val likesCount = snapshot.getValue(Int::class.java) ?: 0
                    txtLikes.text = likesCount.toString() // Display the value
                } else {
                    // Set to 0 if the node does not exist
                    txtLikes.text = "0"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error (e.g., log it or show a message)
                Log.e("FirebaseCheck", "Error fetching LikesCount: ${error.message}")
                txtLikes.text = "0" // Default to 0 if there's an error
            }
        })


        view.findViewById<Button>(R.id.butRegister).setOnClickListener{
            val intent = Intent(context, Registration::class.java)
            GlobalClass.evGName=event.evName
            GlobalClass.evGTeamSize=event.evMaxTeamSize
            GlobalClass.evGId=event.evId
            startActivity(intent)
        }

        butLike.setOnClickListener {
            val currentLikes = txtLikes.text.toString().toIntOrNull() ?: 0 // Get current likes count
            if (isLiked) {
                // User unlikes the event
                dbRefU2.child(event.evId.toString()).removeValue() // Remove from liked events
                butLike.background = ContextCompat.getDrawable(requireContext(), R.drawable.like_na)
                txtLikes.setTextColor(Color.parseColor("#969DA4"))

                // Decrement the likes count in the database
                dbRefE2.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val likesCount = mutableData.getValue(Int::class.java) ?: 0
                        if (likesCount > 0) {
                            mutableData.value = likesCount - 1
                        }
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (committed) {
                            txtLikes.text = (currentLikes - 1).toString() // Update UI
                        }
                    }
                })

            } else {
                // User likes the event
                dbRefU2.child(event.evId.toString()).setValue(true) // Add to liked events
                butLike.background = ContextCompat.getDrawable(requireContext(), R.drawable.like_a)
                txtLikes.setTextColor(Color.parseColor("#5E7BF0"))

                // Increment the likes count in the database
                dbRefE2.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val likesCount = mutableData.getValue(Int::class.java) ?: 0
                        mutableData.value = likesCount + 1
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (committed) {
                            txtLikes.text = (currentLikes + 1).toString() // Update UI
                        }
                    }
                })
            }
            isLiked = !isLiked // Toggle the isLiked state
        }


        val butBookmark = view.findViewById<Button>(R.id.butbookmark)
        var isBookmark = false

        dbRefU1= FirebaseDatabase.getInstance().getReference("User")
            .child(sanitizedEmail)
            .child("BookmarkedEvents")

        dbRefU1.child(event.evId.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isBookmark = snapshot.exists() // Set isBookmarked to true if evId exists, false otherwise

                // Update UI or perform other actions based on isBookmarked value
                if (isBookmark) {
                    butBookmark.background = ContextCompat.getDrawable(requireContext(), R.drawable.bookmark_a)
                } else {
                    butBookmark.background = ContextCompat.getDrawable(requireContext(), R.drawable.bookmark_na)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error checking bookmark status: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })

        butBookmark.setOnClickListener {
            if (isBookmark) {
                butBookmark.background = ContextCompat.getDrawable(requireContext(), R.drawable.bookmark_na)
                //removes the evId from the list
                dbRefU1.child(event.evId.toString()).removeValue()
            } else {
                butBookmark.background = ContextCompat.getDrawable(requireContext(), R.drawable.bookmark_a)
                //adds the evId to the list
                dbRefU1.child(event.evId.toString()).setValue(true)
            }
            isBookmark = !isBookmark
        }

        val butaddcomment = view.findViewById<ImageView>(R.id.buteventaddcomment)
        evcomment=view.findViewById(R.id.txtEventComment)

        butaddcomment.setOnClickListener{
            if (view.findViewById<EditText>(R.id.txtEventComment).text.isNullOrEmpty()){
                view.findViewById<EditText>(R.id.txtEventComment).background= ContextCompat.getDrawable(requireContext(), R.drawable.pinrejection)
                Toast.makeText(requireContext(), "Please enter a comment in the field", Toast.LENGTH_LONG).show()
            }else{
                view.findViewById<EditText>(R.id.txtEventComment).background= ContextCompat.getDrawable(requireContext(), R.drawable.pinidle)
                saveEventData()
            }
        }

        dbRefE1 = FirebaseDatabase.getInstance().getReference("Events")
            .child(event.evId.toString())
            .child("Comments")

        dbRefE1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Clear the list to avoid duplicate entries on update
                    commentArrayList.clear()

                    for (dataSnapShot in snapshot.children) {
                        val comment = dataSnapShot.getValue(CommentModel::class.java)
                        comment?.let {
                            commentArrayList.add(it)
                        }
                    }

                    // Notify adapter about the updated list
                    recyclerViewComments.adapter = CommentAdapter(commentArrayList)
                    recyclerViewComments.adapter?.notifyDataSetChanged()
                } else {
                    Log.e("Firebase", "No comments found for this event.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_LONG).show()
            }
        })

        val dbRefE1 = FirebaseDatabase.getInstance().getReference("Events")
            .child(event.evId.toString())
            .child("Teams")

        val adapter = RegisteredTeamAdapter(teamArrayList)
        recyclerViewTeams.adapter = adapter

        dbRefE1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    teamArrayList.clear()

                    for (team in snapshot.children) {
                        val teamKey = team.key // Get the key of the current child
                        if (teamKey != null) {
                            val dbTeamIdRef = FirebaseDatabase.getInstance().getReference("Teams")
                                .child(teamKey)

                            dbTeamIdRef.child("registrationCompleted").get().addOnSuccessListener { dataSnapshot ->
                                val isRegistrationCompleted = dataSnapshot.getValue(Boolean::class.java) ?: false
                                if (isRegistrationCompleted) {
                                    // Fetch the full team object
                                    dbTeamIdRef.get().addOnSuccessListener { teamSnapshot ->
                                        val team1 = teamSnapshot.getValue(TeamModel::class.java)
                                        team1?.let {
                                            teamArrayList.add(it)
                                            recyclerViewTeams.post { adapter.notifyDataSetChanged() }
                                        }
                                    }.addOnFailureListener {
                                        Log.e("FirebaseError", "Failed to fetch team details for $teamKey", it)
                                    }
                                }
                            }.addOnFailureListener {
                                Log.e("FirebaseError", "Failed to get registrationCompleted for team $teamKey", it)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled", error.toException())
            }
        })



    }

    private fun saveEventData(){
        val txtCommentEditText = evcomment.text.toString()


        if (txtCommentEditText.isEmpty()) {
            Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_LONG).show()
            return
        }

        val commentId = dbRefE1.push().key!! // Generate unique key
        val comment = CommentModel(txtCommentEditText, GlobalClass.NameIco, GlobalClass.UName) // Create model object

        dbRefE1.child(commentId).setValue(comment)
            .addOnCompleteListener {
                Toast.makeText(requireContext(), "Comment added successfully", Toast.LENGTH_LONG).show()
                evcomment.setText("")
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_LONG).show()
            }
    }
}

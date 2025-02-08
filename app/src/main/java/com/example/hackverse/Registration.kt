package com.example.hackverse

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Registration : AppCompatActivity(), BSFragmentTeammateEmail.OnTeammateEmailClosedListener,  BSFragmentRegisterTeam.OnRegisterTeamClosedListener{
    private lateinit var EventName: TextView
    private lateinit var MaxTeamSize: TextView // Fixed the capitalization issue
    private lateinit var RecyclerView: RecyclerView
    private lateinit var teamArrayList: ArrayList<UserParsableModel>
    private lateinit var dbRef1: DatabaseReference
    private lateinit var dbRef2: DatabaseReference
    private lateinit var TeamSize: TextView
    private lateinit var butInvite: Button
    private lateinit var butRegister: Button
    private lateinit var TeamName: EditText

    var CurrentTeamSize = 0
    var TeamGlobal = false

    lateinit var TeamIdMain :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdregistration)

        // Initialize the TextViews
        EventName = findViewById(R.id.txtEventName)
        MaxTeamSize = findViewById(R.id.txtmaxteamsize)
        RecyclerView = findViewById(R.id.recvwTeammates)
        TeamSize = findViewById(R.id.txtteamsize)
        butInvite = findViewById(R.id.butInvite)
        butRegister = findViewById(R.id.btnRegisterTeam)
        TeamName = findViewById(R.id.txtTeamName)

        TeamSize.text = CurrentTeamSize.toString()

        // Set text from GlobalClass (make sure these values are initialized)
        EventName.text = GlobalClass.evGName ?: "Default Event Name"  // Safe call with fallback text
        MaxTeamSize.text = GlobalClass.evGTeamSize?.toString() ?: "Default Team Size"  // Safe call with fallback text


        // Set padding based on system bars (status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        GlobalClass.parsableemail=GlobalClass.Email
        GlobalClass.teamleaderemail=GlobalClass.Email
        handleTeammateEmailClose()

        butInvite.setOnClickListener{
            val bottomsheetfragment = BSFragmentTeammateEmail()
            bottomsheetfragment.show(supportFragmentManager,bottomsheetfragment.tag)
        }

        findViewById<Switch>(R.id.swtch).setOnCheckedChangeListener { _, isChecked ->
            val maxTeamSizeInt = GlobalClass.evGTeamSize?.toIntOrNull() // Convert to Int safely
            val currentTeamSizeInt = CurrentTeamSize

            if (isChecked) {
                if (currentTeamSizeInt != null && maxTeamSizeInt != null) {
                    if (currentTeamSizeInt < maxTeamSizeInt) {
                        TeamGlobal = true
                    } else {
                        findViewById<Switch>(R.id.swtch).isChecked = false
                        Toast.makeText(this, "Max team size reached", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    findViewById<Switch>(R.id.swtch).isChecked = false
                    Toast.makeText(this, "Invalid team size or max team size.", Toast.LENGTH_SHORT).show()
                }
            } else {
                TeamGlobal = false
            }
        }


        findViewById<ImageView>(R.id.btncloseregistration).setOnClickListener{
            val bottomsheetfragment = BSFragmentDiscardTeam()
            bottomsheetfragment.show(supportFragmentManager,bottomsheetfragment.tag)
        }
        butRegister.setOnClickListener{
            if(TeamName.text.isNullOrEmpty()){
                TeamName.background= ContextCompat.getDrawable(this, R.drawable.pinrejection)
                Toast.makeText(this, "Please enter a valid team name", Toast.LENGTH_SHORT).show()
            }else{
                val bottomsheetfragment = BSFragmentRegisterTeam()
                bottomsheetfragment.show(supportFragmentManager,bottomsheetfragment.tag)
            }
        }
    }

    override fun onRegisterTeamClosed(){
        dbRef2=FirebaseDatabase.getInstance().getReference("Teams")
        RegisterTeam()

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""

        val dbLeaderRef = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)

// Directly reference "TeamsLeaded"
        val teamsLeadedRef = dbLeaderRef.child("TeamsLeaded")

// Generate a unique key for the new entry
        val uniqueKey = teamsLeadedRef.push().key

        if (uniqueKey != null) {
            teamsLeadedRef.child(uniqueKey).setValue(TeamIdMain)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "TeamIdMain successfully added to TeamsLeaded")
                    } else {
                        Log.e("Firebase", "Failed to add TeamIdMain: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error occurred: ${exception.message}")
                }
        } else {
            Log.e("Firebase", "Failed to generate a unique key")
        }

    }
    // Correct method name
    override fun onTeammateEmailClosed() {
        handleTeammateEmailClose()
    }

    private fun handleTeammateEmailClose() {
        if (GlobalClass.parsableemail.isNullOrEmpty()) {
            return
        }

        RecyclerView.visibility = View.VISIBLE
        RecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the teamArrayList and the Adapter if not already done
        if (!::teamArrayList.isInitialized) {
            teamArrayList = arrayListOf()
            val adapter = TeammatesAdapter(teamArrayList)
            RecyclerView.adapter = adapter
        }

        // Sanitize the email for consistency
        val sanitizedEmail = GlobalClass.parsableemail?.replace(".", ",") ?: ""

        // Check if the email already exists in the teamArrayList
        val isDuplicate = teamArrayList.any { it.empEmail == GlobalClass.parsableemail }
        if (isDuplicate) {
            // Show a toast message for duplicate email
            Toast.makeText(this, "This email is already in the list!", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed to add the new email if it's not a duplicate
        dbRef1 = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)
        dbRef1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val empName = snapshot.child("empName").value.toString()
                    val empUName = snapshot.child("empUName").value.toString()
                    val empEmail = snapshot.child("empEmail").value.toString()

                    // Add the new UserParsableModel to the teamArrayList
                    val newUser = UserParsableModel(empName, empUName, null, null, null, empEmail)
                    teamArrayList.add(newUser)
                    CurrentTeamSize++
                    TeamSize.text = CurrentTeamSize.toString()

                    // Hide the invite button and reset the switch if the max size is reached
                    if (CurrentTeamSize.toString() == MaxTeamSize.text) {
                        butInvite.visibility = View.GONE
                        findViewById<Switch>(R.id.swtch).isChecked = false
                        TeamGlobal = false
                    }

                    // Notify the adapter of the new item
                    (RecyclerView.adapter as TeammatesAdapter).notifyItemInserted(teamArrayList.size - 1)
                } else {
                    // Handle case when user data doesn't exist
                    Log.e("Firebase", "User not found!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data", error.toException())
            }
        })
    }



    private fun RegisterTeam(){

        val tmName = TeamName.text.toString()
        val tmPublicVisibility = TeamGlobal
        val tmId = dbRef2.push().key!!
        val tmSize = TeamSize.text.toString()
        val tmLeader = GlobalClass.teamleaderemail
        val tmEventId = GlobalClass.evGId
        val tmRegistrationCompleted = false

        TeamIdMain=tmId

        var done = false

        val team = TeamModel(tmName,tmPublicVisibility,tmSize,tmLeader,tmEventId,tmRegistrationCompleted)
        dbRef2.child(tmId).setValue(team)
            .addOnCompleteListener{

            }.addOnFailureListener{

            }

        //adding team to the list of active teams in event
        dbRef2 = FirebaseDatabase.getInstance().getReference("Events").child(GlobalClass.evGId.toString()).child("Teams")
        val teamData = mapOf(
            tmId to tmId
        )
        dbRef2.setValue(teamData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Team added successfully with new ID!")
            } else {
                Log.e("Firebase", "Error adding team: ${task.exception?.message}")
            }
        }

        // Iterate through RecyclerView items to check the color of "teammembertagcardtag"
        for (i in 0 until RecyclerView.childCount) {
            val childView = RecyclerView.getChildAt(i)

            // Get the background color of "teammembertagcardtag"
            val cardTagView = childView.findViewById<RelativeLayout>(R.id.teammembertagcardtag)
            val cardTagColor = (cardTagView.background as? ColorDrawable)?.color
            // Get the email value from "txtuseremailcardG"
            val vuEmailTextView = childView.findViewById<TextView>(R.id.txtuseremailcardG)
            val email = vuEmailTextView.text.toString()
            val eventID = GlobalClass.evGId.toString()
            // Determine which Firebase reference to use based on the background color
            val InitialAllocation = if (cardTagColor == Color.parseColor("#EB7167")) {
                FirebaseDatabase.getInstance()
                    .getReference("Teams")
                    .child(tmId)
                    .child("Invited")
            } else {
                FirebaseDatabase.getInstance()
                    .getReference("Teams")
                    .child(tmId)
                    .child("InGroup")
            }

            if (cardTagColor == Color.parseColor("#EB7167")) {
                // Adding team to individual emails which are invited
                val sanitizedEmail = email.replace(".", ",") // Replace '.' with ',' or another allowed character

                // Reference to the Firebase location
                val registeredevents = FirebaseDatabase.getInstance()
                    .getReference("User")
                    .child(sanitizedEmail)
                    .child("InvitedEvents")

                // Create the data to save
                val teamData1 = mapOf(
                    "TeamId" to tmId,
                    "InvitorEmailId" to GlobalClass.Email
                )

                // Generate a push ID and save the data under it
                val newPushId = registeredevents.push().key // Create a unique push ID
                registeredevents.child(newPushId!!).setValue(teamData1)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Firebase", "Team added successfully with new push ID: $newPushId")
                        } else {
                            Log.e("Firebase", "Error adding team: ${task.exception?.message}")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseError", "Failed to add team data: ${e.message}", e)
                    }

            }else{

                // Adding individuals to registrations in the event which are registered

                //(bottom one method is most successfull method for storing / writing data in firebase database where a id is alloted to the element
                val registrationRef = FirebaseDatabase.getInstance()
                    .getReference("Events")
                    .child(eventID)
                    .child("Registrations")
                registrationRef.push().setValue(email)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Email $email added successfully to the appropriate group.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseError", "Failed to add email: $email", e)
                    }

                //adding team to individual emails which are registered
                val sanitizedEmail = email.replace(".", ",") // Replace '.' with ',' or another allowed character
                val registeredevents = FirebaseDatabase.getInstance()
                    .getReference("User")
                    .child(sanitizedEmail)
                    .child("RegisteredEvents")
                registeredevents.push().setValue(tmId)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Email $email added successfully to the appropriate group.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseError", "Failed to add email: $email", e)
                    }

            }

            // Push the email to Firebase
            InitialAllocation.push().setValue(email)
                .addOnSuccessListener {
                    done = true
                    Log.d("Firebase", "Email $email added successfully to the appropriate group.")
                }
                .addOnFailureListener { e ->
                    done = false
                    Toast.makeText(this, "Team Registration Failed", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseError", "Failed to add email: $email", e)
                }
        }

        if(done){
            Toast.makeText(this, "Team Registered Sucessfully", Toast.LENGTH_SHORT).show()
        }


    }

    // Function to remove a teammate
    fun removeTeammate(position: Int) {
        if (position in teamArrayList.indices) {
            teamArrayList.removeAt(position)
            CurrentTeamSize--
            TeamSize.text = CurrentTeamSize.toString()

            // Notify the adapter of the removal
            (RecyclerView.adapter as TeammatesAdapter).notifyItemRemoved(position)

            // If the team size reaches the max, show the invite button again
            if (CurrentTeamSize < MaxTeamSize.text.toString().toInt()) {
                butInvite.visibility = View.VISIBLE
            }
        }
    }

}

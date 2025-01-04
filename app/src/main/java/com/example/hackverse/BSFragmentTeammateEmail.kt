package com.example.hackverse

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*

class BSFragmentTeammateEmail : BottomSheetDialogFragment() {

    private lateinit var email: EditText
    private lateinit var dbRef1: DatabaseReference
    var UserVerified = 0 //0->verification pending , 1-> eligible ,2-> not eligible

    // Define the interface
    interface OnTeammateEmailClosedListener {
        fun onTeammateEmailClosed()
    }

    // Reference to the listener
    private var listener: OnTeammateEmailClosedListener? = null

    companion object {
        fun newInstance(event: EventModel): BSFragmentTeammateEmail {
            val fragment = BSFragmentTeammateEmail()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the context implements the interface
        if (context is OnTeammateEmailClosedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnTeammateEmailClosedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Trigger the callback when the bottom sheet is dismissed
        listener?.onTeammateEmailClosed()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottomsheet_fragment_playeremail, container, false)

        // Initialize the EditText and Button views
        email = view.findViewById(R.id.txtemailtoregister)
        GlobalClass.parsableemail=null
        val verifyButton = view.findViewById<Button>(R.id.butverify)
        val inviteButton = view.findViewById<Button>(R.id.butsendinvitation)

        // Set the click listener for the verify button
        verifyButton.setOnClickListener {
            if(email.text.isNullOrEmpty()){
                email.background = ContextCompat.getDrawable(requireContext(), R.drawable.pinrejection)
                Toast.makeText(requireContext(), "Please enter email id", Toast.LENGTH_SHORT).show()
            }else{
                UserVerification(view)
                if(UserVerified==1){
                    Toast.makeText(requireContext(), "User Verified", Toast.LENGTH_SHORT).show()
                }
            }
        }

        inviteButton.setOnClickListener{
            if(UserVerified==1){
                InviteUser(view)
            }else{
                email.background = ContextCompat.getDrawable(requireContext(), R.drawable.pinrejection)
                Toast.makeText(requireContext(), "User Verification Pending", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun UserVerification(view: View){
        // Sanitize the email input (replace '.' with ',')
        val sanitizedEmail = email.text.toString().replace(".", ",")
        dbRef1 = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail)

        // Check if the email exists in the database
        dbRef1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Email exists
                    email.background = ContextCompat.getDrawable(requireContext(), R.drawable.pinidle)
                    view.findViewById<TextView>(R.id.txtplayeremailnotice).text="The user with above email Id does not exist"
                    view.findViewById<TextView>(R.id.txtplayeremailnotice).visibility = View.GONE

                    dbRef1 = FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail).child("RegisteredEvents").child(GlobalClass.evGId.toString())
                    dbRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                email.background = ContextCompat.getDrawable(requireContext(), R.drawable.pinrejection)
                                view.findViewById<TextView>(R.id.txtplayeremailnotice).text="The user with above email Id is already registered for the same event"
                                view.findViewById<TextView>(R.id.txtplayeremailnotice).visibility = View.VISIBLE
                                UserVerified = 2

                            } else {
                                email.background = ContextCompat.getDrawable(requireContext(), R.drawable.pinacceptance)
                                view.findViewById<TextView>(R.id.txtplayeremailnotice).text="The user with above email Id is already registered for the same event"
                                view.findViewById<TextView>(R.id.txtplayeremailnotice).visibility = View.GONE

                                UserVerified = 1
                                Toast.makeText(requireContext(), "The user can be added to the team", Toast.LENGTH_SHORT).show()

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any potential errors
                            Log.e("FirebaseError", "Error checking node existence", error.toException())
                        }
                    })

                } else {
                    // Email does not exist
                    email.background = ContextCompat.getDrawable(requireContext(), R.drawable.pinrejection)
                    view.findViewById<TextView>(R.id.txtplayeremailnotice).text="The user with above email Id does not exist"
                    view.findViewById<TextView>(R.id.txtplayeremailnotice).visibility = View.VISIBLE

                    UserVerified = 2
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                println("Error occurred: ${error.message}")
            }
        })
    }

    private fun InviteUser(view: View){
        if(UserVerified==1){
            GlobalClass.parsableemail=email.text.toString()
        }else{
            GlobalClass.parsableemail=null
        }
        dismiss()
    }
}

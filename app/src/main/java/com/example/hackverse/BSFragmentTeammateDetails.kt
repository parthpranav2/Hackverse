package com.example.hackverse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView  // Import TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BSFragmentTeammateDetails : BottomSheetDialogFragment() {

    companion object {
        const val ARG_USER = "user"
        const val ARG_POSITION = "position"

        fun newInstance(user: UserParsableModel, position : Int): BSFragmentTeammateDetails {
            val fragment = BSFragmentTeammateDetails()
            val bundle = Bundle()
            bundle.putParcelable(ARG_USER, user)  // Put the UserParsableModel as Parcelable
            bundle.putInt(ARG_POSITION, position)  // Pass position
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var user: UserParsableModel
    private var position: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.bottomsheet_fragment_playerdetails, container, false)

        // Retrieve the passed UserParsableModel
        user = arguments?.getParcelable(ARG_USER) ?: throw IllegalStateException("UserParsableModel is null")
        position = arguments?.getInt(ARG_POSITION) ?: -1

        // Use findViewById to access the views and populate them with data
        val txtName = view.findViewById<TextView>(R.id.txtPDName)
        val txtGender = view.findViewById<TextView>(R.id.txtPDGender)
        txtName.text = user.empName
        txtGender.text = user.empGender

        if(user.empEmail==GlobalClass.Email.toString()){
            view.findViewById<Button>(R.id.butRemoveTeammate).visibility=View.GONE
        }

        // Set up the "Remove" button click listener
        view.findViewById<Button>(R.id.butRemoveTeammate).setOnClickListener {
            if (position != -1) {
                // Call the function in Registration to remove the user
                (activity as? Registration)?.removeTeammate(position)
            }
            dismiss()

        }

        return view
    }
}


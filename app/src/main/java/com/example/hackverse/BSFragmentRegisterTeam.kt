package com.example.hackverse

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BSFragmentRegisterTeam : BottomSheetDialogFragment() {
    private lateinit var confirmation: EditText

    // Define the interface
    interface OnRegisterTeamClosedListener {
        fun onRegisterTeamClosed()
    }

    // Reference to the listener
    private var listener: OnRegisterTeamClosedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the context implements the interface
        if (context is OnRegisterTeamClosedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnRegisterTeamClosedListenerListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottomsheet_fragment_teamregister_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views here
        confirmation = view.findViewById(R.id.txtConfirmation1)

        view.findViewById<Button>(R.id.butcontinue1)?.setOnClickListener {
            if (confirmation.text.toString() == "confirm") {
                // Close the BottomSheetFragment
                dismiss()

                listener?.onRegisterTeamClosed()
                // Close the Registration activity
                activity?.let { parentActivity ->
                    if (parentActivity is Registration) {
                        parentActivity.finish()
                    }
                }
            } else {
                // Show the prompt and change background
                view.findViewById<TextView>(R.id.txtprompt1)?.visibility = View.VISIBLE
                view.findViewById<EditText>(R.id.txtConfirmation1)?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.pinrejection)
            }
        }
    }
}

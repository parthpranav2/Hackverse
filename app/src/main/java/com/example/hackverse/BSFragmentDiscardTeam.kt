package com.example.hackverse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BSFragmentDiscardTeam : BottomSheetDialogFragment() {

    private lateinit var confirmation: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.bottomsheet_fragment_teamregistercancel_warning,
            container,
            false
        )

        confirmation = view.findViewById(R.id.txtConfirmation)

        view.findViewById<Button>(R.id.butcontinue).setOnClickListener {
            if (confirmation.text.toString() == "confirm") {
                // Close the BottomSheetFragment
                dismiss()

                // Close the Registration activity
                activity?.let { parentActivity ->
                    if (parentActivity is Registration) {
                        parentActivity.finish()
                    }
                }
            }else{
                view.findViewById<TextView>(R.id.txtprompt).visibility=View.VISIBLE
                view.findViewById<EditText>(R.id.txtConfirmation1).background = ContextCompat.getDrawable(requireContext(), R.drawable.pinrejection)
            }
        }

        return view
    }
}

package com.example.hackverse

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BSFragmentFilter_activeevents : BottomSheetDialogFragment() {

    interface FilterListener {
        fun onFilterApplied(filter: Int)
    }

    private var listener: FilterListener? = null
    private var selectedPosition: Int = 1  // Default to "All Events"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Check if the context implements the FilterListener interface
        if (context is FilterListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement FilterListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_fragment_filter_activeevents, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the selected filter from arguments
        selectedPosition = GlobalClass.FilterActiveEvents

        val listView = view.findViewById<ListView>(R.id.listfilters)
        val items = arrayOf("Valid Events", "All Events", "Completed Events", "Offline Events", "Online Events", "Synergy Events")

        val arrayAdapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.listview_filter_items,  // Custom layout for styling
            R.id.txtlistitem,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_filter_items, parent, false)
                val textView = view.findViewById<TextView>(R.id.txtlistitem)

                textView.text = getItem(position)  // Set the text from the dataset

                // Set the color for the selected filter
                if (position == selectedPosition) {
                    textView.setTextColor(Color.parseColor("#5E7BF0"))  // Selected color
                } else {
                    textView.setTextColor(Color.parseColor("#FFFFFF"))  // Default color
                }

                return view
            }
        }

        listView.adapter = arrayAdapter

        // Automatically select the previously selected filter
        listView.setSelection(selectedPosition)

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position  // Update selected position
            GlobalClass.FilterActiveEvents = position
            arrayAdapter.notifyDataSetChanged() // Refresh list to show color change

            // Trigger the listener callback
            listener?.onFilterApplied(selectedPosition)  // Apply the selected filter
            dismiss()  // Close Bottom Sheet
        }
    }
}




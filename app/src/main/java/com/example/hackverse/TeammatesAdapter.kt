package com.example.hackverse

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class TeammatesAdapter(private val teamList: ArrayList<UserParsableModel>) : RecyclerView.Adapter<TeammatesAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val vuIdentityIcon : TextView = itemView.findViewById(R.id.txtidentityiconcardG)
        val vuName : TextView = itemView.findViewById(R.id.txtnamecardG)
        val vuUserName : TextView = itemView.findViewById(R.id.txtusernamecardG)
        val vuEmail : TextView = itemView.findViewById(R.id.txtuseremailcardG)

        val vuCardBody : RelativeLayout = itemView.findViewById(R.id.teammembertagcardbody)
        val vuCardTag : RelativeLayout = itemView.findViewById(R.id.teammembertagcardtag)

        val vuLeader : TextView = itemView.findViewById(R.id.txtLeaderTag)


        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Access the clicked item using the adapter's event list
                    val clickedEvent = teamList[position]

                    // Create an Intent to pass the EventModel object to another activity
                    val context = itemView.context
                    if(context is FragmentActivity){
                        val bottomSheetFragment = BSFragmentTeammateDetails.newInstance(clickedEvent,position)
                        bottomSheetFragment.show(context.supportFragmentManager,"BottomSheetFragmentTag")
                    }else {
                        Toast.makeText(context, "Context is not a FragmentActivity", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.recycleringroup_items,parent,false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return teamList.count()
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.vuIdentityIcon.text = teamList[position].empName?.firstOrNull()?.uppercase() ?: ""
        holder.vuName.text = teamList[position].empName
        holder.vuUserName.text = teamList[position].empUName
        //parse each elemental user email while adding it to the list
        holder.vuEmail.text = teamList[position].empEmail

        if(teamList[position].empName==GlobalClass.FullName){
            holder.vuCardTag.setBackgroundColor(Color.parseColor("#4AA46D"))
            holder.vuCardBody.setBackgroundColor(Color.parseColor("#DEF8EC"))
        }else{
            holder.vuCardTag.setBackgroundColor(Color.parseColor("#EB7167"))
            holder.vuCardBody.setBackgroundColor(Color.parseColor("#F8D1D1"))
        }

        if(teamList[position].empEmail==GlobalClass.teamleaderemail){
            holder.vuLeader.visibility=View.VISIBLE
        }else{
            holder.vuLeader.visibility=View.GONE
        }

    }
}
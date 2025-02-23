package com.example.hackverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatsAdapter(
    private val chatList: ArrayList<ChatRoomModel>,
    private val recyclerView: RecyclerView  // Pass RecyclerView here
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_DATE_CHANGE = 0
        const val VIEW_TYPE_ME = 1
        const val VIEW_TYPE_NOT_ME = 2
    }

    class MeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.txtmessage)
        val committime: TextView = itemView.findViewById(R.id.txtcommittime)
        val card: RelativeLayout = itemView.findViewById(R.id.recevercard)
    }

    class NotMeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.txtmessage)
        val committime: TextView = itemView.findViewById(R.id.txtcommittime)
        val name: TextView = itemView.findViewById(R.id.txtsendername)
        val nameIco: TextView = itemView.findViewById(R.id.lblnameico)
        val Ico: RelativeLayout = itemView.findViewById(R.id.rellayoutIco)
        val card: RelativeLayout = itemView.findViewById(R.id.sendercard)
    }

    class DateChangeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date : TextView = itemView.findViewById(R.id.txtdate)
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList.isEmpty()) return VIEW_TYPE_ME  // Default to VIEW_TYPE_ME to avoid crash
        return if (chatList[position].UserType == "Me") VIEW_TYPE_ME else VIEW_TYPE_NOT_ME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_DATE_CHANGE ->{
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerchats_datechange_items, parent, false)
                MeViewHolder(itemView)
            }

            VIEW_TYPE_ME -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerchats_recever_items, parent, false)
                MeViewHolder(itemView)
            }

            VIEW_TYPE_NOT_ME -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerchats_sender_items, parent, false)
                NotMeViewHolder(itemView)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    var extraPosition = 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (chatList.isEmpty()) return  // Prevent crashes if empty list
        val currentItem = chatList[position + extraPosition]

        // Check if it's the first message OR if the date has changed from the previous message
        /*val isFirstMessage = position == 0
        val previousDate = if (position > 0) chatList[position - 1].CommitDate else null
        val shouldShowDateBubble = isFirstMessage || previousDate != currentItem.SuccedingMessageDate

        if (shouldShowDateBubble) {
            when (holder) {
                is DateChangeViewHolder -> {
                    holder.date.text = currentItem.CommitDate
                    extraPosition++
                }
            }
        }*/

        when (holder) {

            is MeViewHolder -> {
                val currentItem = chatList[position+extraPosition]
                holder.message.text = currentItem.Message
                holder.committime.text = currentItem.CommitTime

                if (position + 1 < chatList.size && chatList[position].SenderId == chatList[position+1].SenderId) {
                    holder.card.setBackgroundResource(R.drawable.chatbubble_notlast_recever)
                }
            }

            is NotMeViewHolder -> {
                val currentItem = chatList[position]
                holder.message.text = currentItem.Message
                holder.committime.text = currentItem.CommitTime
                holder.name.text = currentItem.SenderName
                holder.nameIco.text = currentItem.SenderName?.firstOrNull()?.uppercase()

                if (position > 0 && chatList[position - 1].SenderId == currentItem.SenderId) {
                    holder.name.visibility = View.GONE
                } else {
                    holder.name.visibility = View.VISIBLE
                }


                if (position + 1 < chatList.size && chatList[position].SenderId == chatList[position+1].SenderId) {
                    holder.card.setBackgroundResource(R.drawable.chatbubble_notlast_sender)
                    holder.Ico.visibility = View.INVISIBLE
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size  // Return the size of the chat list
    }

    // Method to add a new chat message and notify the adapter
    fun addChatMessage(newChat: ChatRoomModel) {
        chatList.add(0, newChat) // Add the new message at the top
        notifyItemInserted(0)  // Notify the adapter that the new item is inserted at the top

        // Ensure RecyclerView scrolls to the top
        recyclerView.scrollToPosition(0)
    }
}

package com.example.hackverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CommentAdapter(val commentList: ArrayList<CommentModel>):RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val vuIdentityIcon : TextView = itemView.findViewById(R.id.txtidentityiconcard)
        val vuUserName : TextView = itemView.findViewById(R.id.txtusernamecard)
        val vucomment : TextView = itemView.findViewById(R.id.txtcommentcard)
        var clientID: String? = null //the commenter id (node in events->Comments)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.recyclercomments_items,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.vucomment.text=commentList[position].comment
        holder.vuIdentityIcon.text=commentList[position].identifyalpha
        holder.vuUserName.text=commentList[position].username
    }

}
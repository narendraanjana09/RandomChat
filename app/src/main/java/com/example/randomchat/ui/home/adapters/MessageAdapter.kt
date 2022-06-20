package com.example.randomchat.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.randomchat.R
import com.example.randomchat.ui.home.models.ChatsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(
    val context: Context,
    val list: List<ChatsModel>
): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1
    private var fuser: FirebaseUser? = null

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val message_tv : TextView = itemView.findViewById(R.id.message_tv)
        val time_tv : TextView = itemView.findViewById(R.id.time_tv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType==MSG_TYPE_LEFT){
        val view= LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left,parent,false)
        return ViewHolder(view)
        }else{
            val view= LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right,parent,false)
            return ViewHolder(view)
        }
    }
    fun convertTime(time: Long): String? {
        val formatter = SimpleDateFormat("hh:mm a")
        return formatter.format(Date(time))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat=list[position]
        holder.apply {
            time_tv.text=convertTime(chat.messageTime)
            message_tv.text=chat.messageText
        }

    }
    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (list[position].messageUser==fuser?.uid.toString()) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    override fun getItemCount(): Int = list.size

}

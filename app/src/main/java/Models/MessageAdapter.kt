package Models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcompanion.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val messages: List<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_ME = 1
    private val VIEW_OTHER = 2

    private val currentUserId =
        FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_ME
        } else {
            VIEW_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_ME) {
            val view = inflater.inflate(R.layout.item_message_me, parent, false)
            MeViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_message_other, parent, false)
            OtherViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val time = formatTime(message.timestamp)

        if (holder is MeViewHolder) {
            holder.tvMessage.text = message.text
            holder.tvTime.text = time
        } else if (holder is OtherViewHolder) {
            holder.tvUser.text = message.senderName
            holder.tvMessage.text = message.text
            holder.tvTime.text = time
        }
    }

    override fun getItemCount(): Int = messages.size

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    class MeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    class OtherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUser: TextView = view.findViewById(R.id.tvUser)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }
}
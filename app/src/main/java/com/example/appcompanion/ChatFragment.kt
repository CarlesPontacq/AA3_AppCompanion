package com.example.appcompanion

import Models.Message
import Models.MessageAdapter
import Models.User
import Models.UserAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.Query

class ChatFragment : Fragment() {
    private lateinit var database: DatabaseReference

    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val rvMessages = view.findViewById<RecyclerView>(R.id.rvMessages)
        val etMessage = view.findViewById<EditText>(R.id.etMessage)
        val btnSend = view.findViewById<Button>(R.id.btnSend)

        adapter = MessageAdapter(messages)
        rvMessages.layoutManager = LinearLayoutManager(requireContext())
        rvMessages.adapter = adapter

        val databaseUrl = "https://appcompanionpokemontcg-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseUrl).getReference("messages")

        //Enviar mensaje
        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if(text.isNotEmpty()){
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "anon"
                var username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anon"
                if(username.isNullOrBlank()){
                    username = "Anon"
                }

                Log.d("Chat test", "username")

                val message = Message(
                    senderId = uid,
                    senderName = username,
                    text = text,
                    timestamp = System.currentTimeMillis()
                )

                database.push().setValue(message)
                etMessage.text.clear()
            }
        }

        database.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if(message != null){
                    messages.add(message)
                    adapter.notifyItemInserted(messages.size - 1)
                    rvMessages.scrollToPosition(messages.size - 1)
                    Log.d("Chat test",
                        "User: ${message.senderId}, Message: ${message.text} - From: ${message.senderName}")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val newUser = snapshot.child("user").getValue(String::class.java)
                val newMessage = snapshot.child("message").getValue(String::class.java)

                val oldSnapshot = previousChildName?.let { database.child(it).get().result}
                val oldUser = oldSnapshot?.child("user")?.getValue(String::class.java)
                val oldMessage = oldSnapshot?.child("message")?.getValue(String::class.java)

                Log.d("Chat test", "Changed - Old User: $oldUser, Old Message: $oldMessage")
                Log.d("Chat test", "Changed - New User: $newUser, New Message: $newMessage")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val user = snapshot.child("user").getValue(String::class.java)
                val message = snapshot.child("message").getValue(String::class.java)

                Log.d("Chat test", "Removed - User: $user, Message: $message")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val movedKey = snapshot.key

                Log.d("Chat test", "Moved - From: $previousChildName, To: $movedKey")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Chat test", "Cancelled - Error: ${error.message}")
            }
        })

        return view
    }
}
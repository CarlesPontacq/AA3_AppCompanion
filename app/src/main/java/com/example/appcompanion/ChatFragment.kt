package com.example.appcompanion

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val databaseUrl = "https://appcompanionpokemontcg-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseUrl).getReference("messages")

        /*
        //Enviar mensaje
        val dataId = database.push().key

        val messageData = mapOf(
            "user" to "Carles",
            "message" to "Funciona"
        )

        if(dataId != null){
            database.child(dataId).setValue(messageData)
                .addOnSuccessListener{result ->
                    Log.d("Chat test", "Insert correcto")
                }
                .addOnFailureListener { exception ->
                    Log.d("Chat test", "Error ${exception.message}")
                }
        }
        */

        /*
        //Recibir mensajes
        val query: Query = database.orderByChild("user").equalTo("Carles")

        query.get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    for (dataSnapshot in snapshot.children){
                        val message = dataSnapshot.child("message").getValue(String::class.java)
                        Log.d("Chat test", "Message: $message")
                    }
                }
                else {
                    Log.d("Chat test", "No messages found for user Jose")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Chat test", "Error: ${exception.message}")
            }
        */

        database.addChildEventListener(createChildListenerEvent())

        return view
    }

    private fun createChildListenerEvent(): ChildEventListener{
        return object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                database.ref.get().addOnSuccessListener { fullSnapshot ->
                    for(child in fullSnapshot.children){
                        val u = child.child("user").getValue(String::class.java)
                        val m = child.child("message").getValue(String::class.java)
                        Log.d("Chat test", "User: $u, Message: $m")
                    }
                }
                    .addOnFailureListener { e ->
                        Log.d("Chat test", "Error fetching full collection: ${e.message}")
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
        }
    }
}
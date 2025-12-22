package com.example.appcompanion

import Models.User
import Models.UserAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    private lateinit var recycler: RecyclerView
    private val users = mutableListOf<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val databaseUrl = "https://appcompanionpokemontcg-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseUrl).getReference("users")

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recycler = view.findViewById(R.id.usersRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = UserAdapter(users) { selectedUser ->
            openChatWith(selectedUser)
        }

        recycler.adapter = adapter

        loadUsers()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.chat_navigation)
    }

    private fun loadUsers() {
        Log.d("PokemonCard", "Loading users")

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if(currentUid == null){
            Log.e("PokemonCard", "User not authentificated")
            return
        }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()

                for (userSnap in snapshot.children) {
                    val user = userSnap.getValue(User::class.java)
                    if (user != null && user.uid != currentUid) {
                        users.add(user)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PokemonCard", error.toString())
            }
        })
    }

    private fun openChatWith(user: User) {
        /*
        val fragment = ChatConversationFragment.newInstance(user.uid, user.username)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()

         */
    }
}
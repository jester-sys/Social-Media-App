package com.krishna.stitch.UI


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.Adapter.UserAdapter
import com.krishna.stitch.databinding.FragmentSearchBinding
import com.krishna.stitch.model.User


//class SearchFragment : Fragment() {
//
//    private lateinit var binding: FragmentSearchBinding
//    private val list = ArrayList<User>()
//    private var auth: FirebaseAuth? = null
//    private var database: FirebaseDatabase? = null
//    private var adapter: UserAdapter? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentSearchBinding.inflate(inflater, container, false)
//
//        auth = FirebaseAuth.getInstance()
//        database = FirebaseDatabase.getInstance()
//
//        adapter = UserAdapter(requireContext(), list)
//        val layoutManager = LinearLayoutManager(requireContext())
//        binding.usersRV.layoutManager = layoutManager
//
//        if (binding.usersRV != null) {
//            binding.usersRV.adapter = adapter
//        }
//
//        database?.getReference()?.child("Users")?.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                list.clear()
//                for (dataSnapshot in snapshot.children) {
//                    val user = dataSnapshot.getValue(User::class.java)
//                    user?.UserID = dataSnapshot.key
//
//                    if (dataSnapshot.key != FirebaseAuth.getInstance().uid) {
//                        user?.let { list.add(it) }
//                    }
//                }
//
//                adapter?.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//
//        })
//
//        return binding.root
//    }
//}

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val list = ArrayList<User>()
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var adapter: UserAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        adapter = UserAdapter(requireContext(), list)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.usersRV.layoutManager = layoutManager
        binding.usersRV.adapter = adapter

        database?.getReference()?.child("Users")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.UserID = dataSnapshot.key

                    if (dataSnapshot.key != FirebaseAuth.getInstance().uid) {
                        user?.let { list.add(it) }
                    }
                }

                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        return binding.root
    }

    private fun filter(query: String?) {
        val filteredList = ArrayList<User>()

        if (!query.isNullOrEmpty()) {
            for (user in list) {
                val fullName = user.name + " " + user.lastName
                if (fullName.contains(query, ignoreCase = true)) {
                    filteredList.add(user)
                }
            }
        } else {
            filteredList.addAll(list)
        }

        adapter?.filterList(filteredList)
    }
}

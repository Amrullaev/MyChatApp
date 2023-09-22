package com.example.mychatapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mychatapp.R
import com.example.mychatapp.adapter.ProfileAdapter
import com.example.mychatapp.databinding.FragmentHomeBinding
import com.example.mychatapp.domain.UserData
import com.example.mychatapp.ui.login.LoginActivity
import com.example.mychatapp.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var list: ArrayList<UserData>
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        list = ArrayList()
        Constant.uid = auth.currentUser?.uid.toString()

        profileAdapter = ProfileAdapter(list, object : ProfileAdapter.OnClickListener {
            override fun onClick(user: UserData) {
                val bundle = Bundle()
                bundle.putSerializable("name", user)
                findNavController().navigate(R.id.action_homeFragment_to_chatFragment, bundle)
            }

        })

        binding.rv.adapter = profileAdapter


        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                val children = snapshot.children
                children.forEach {
                    val value = it.getValue(UserData::class.java)

                    if (value != null && auth.uid != value.uid) {
                        list.add(value)
                    }

                }

                profileAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
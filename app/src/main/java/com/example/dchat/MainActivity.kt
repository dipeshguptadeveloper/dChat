package com.example.dchat

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dchat.adapter.UserAdapter
import com.example.dchat.databinding.ActivityMainBinding
import com.example.dchat.databinding.ActivityOtpactivityBinding
import com.example.dchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var users: ArrayList<User>? = null
    var userAdapter: UserAdapter? = null
    var dialog: ProgressDialog? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = ProgressDialog(this@MainActivity)
        dialog?.setMessage("Uploading Image...")
        dialog?.setCancelable(false)

        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        users = ArrayList<User>()
        userAdapter = UserAdapter(this@MainActivity, users!!)
        val layoutManager = GridLayoutManager(this@MainActivity, 2)

        binding!!.mRec.layoutManager = layoutManager
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        binding!!.mRec.adapter = userAdapter
        database!!.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    users!!.clear()
                    for (snapshot1 in snapshot.children) {
                        val user: User? = snapshot1.getValue(User::class.java)
                        if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) users!!.add(user)
                    }

                    userAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Offline")
    }
}
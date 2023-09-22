package com.example.mychatapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mychatapp.MainActivity
import com.example.mychatapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.mychatapp.databinding.ActivityLoginBinding
import com.example.mychatapp.domain.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        reference = firebaseDatabase.getReference("users")

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, options)
        binding.signInBtn.setOnClickListener {
            val intent = client.signInIntent
            startActivityForResult(intent, 10001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        val userData =
            UserData(account.displayName, account.id, account.email, account.photoUrl.toString())


        reference.child(userData.uid ?: "").setValue(userData).addOnSuccessListener {

            val intent = Intent(
                this, MainActivity::class.java
            )
            startActivity(intent)

        }



        auth.signInWithCredential(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {

                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
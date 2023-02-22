package com.sturec.sturecteacher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val reference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener{
            val schoolCode = binding.schoolCodeEditText.text.toString()
            val mail = binding.mailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            reference.child("schools").child(schoolCode).child("Teacher").get()
                .addOnSuccessListener {
                    var a = false
                    for(i in it.children)
                    {
                        if(i.value==mail)
                        {
                            a = true
                        }
                    }
                    if(!a)
                    {
                        Toast.makeText(
                            this,
                            "Teacher not found for this school",
                            Toast.LENGTH_LONG
                        ).show()
                    }else
                    {
                        val instance = FirebaseAuth.getInstance()
                        Log.e("login", mail+password)
                        instance.signInWithEmailAndPassword(mail,password)
                            .addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener{
                                Toast.makeText(
                                    this,
                                    "Could not log you in: ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
        }


    }
}
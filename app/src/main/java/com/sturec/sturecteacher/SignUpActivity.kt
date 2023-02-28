package com.sturec.sturecteacher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.databinding.ActivityLoginBinding
import com.sturec.sturecteacher.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private val reference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupToLoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signUpButton.setOnClickListener{
            val schoolCode = binding.signupSchoolCodeEditText.text.toString()
            val mail = binding.signupMailEditText.text.toString()
            val password = binding.signupPasswordEditText.text.toString()

            reference.child("schools").child(schoolCode).child("Teacher")
                .child("list").get()
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
                        instance.createUserWithEmailAndPassword(mail, password)
                            .addOnSuccessListener {
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener{exception->
                                Toast.makeText(
                                    this,
                                    "Could not log you in: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
        }
    }
}
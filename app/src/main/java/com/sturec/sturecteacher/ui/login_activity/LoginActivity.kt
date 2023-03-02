package com.sturec.sturecteacher.ui.login_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.ui.sign_up_activity.SignUpActivity
import com.sturec.sturecteacher.data.user_data.UserDataEntity
import com.sturec.sturecteacher.databinding.ActivityLoginBinding
import com.sturec.sturecteacher.ui.home_activity.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val reference = FirebaseDatabase.getInstance().reference

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null)
        {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.loginToSignupButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.button.setOnClickListener{
            val schoolCode = binding.schoolCodeEditText.text.toString()
            val mail = binding.mailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            reference.child("schools").child(schoolCode).child("Teacher")
                .child("list").get()
                .addOnSuccessListener {
                    var a = false
//                    Log.e("teachers", it.children.toString())
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
                        instance.signInWithEmailAndPassword(mail,password)
                            .addOnSuccessListener {
                                lifecycleScope.launch{
                                    viewModel.userDataRepositoryImpl.addUserData(UserDataEntity(schoolCode, 1))
                                }
                                val intent = Intent(this, HomeActivity::class.java)
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
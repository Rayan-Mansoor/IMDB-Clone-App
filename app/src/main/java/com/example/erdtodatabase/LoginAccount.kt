package com.example.erdtodatabase

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.room.Room
import com.example.erdtodatabase.databinding.ActivityLoginAccountBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginAccount : AppCompatActivity() {
    private lateinit var binding : ActivityLoginAccountBinding
    private var isInfoValid : Boolean = false
    private lateinit var db : AppDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.myEmail.hint = "User ID"
        binding.myPassword.hint = "Password"

        binding.myEmail.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus){
                binding.textInputLayout5.error = null
            }
            else if (!hasFocus){
                if (binding.myEmail.text.toString()[0] != '@'){
                    isInfoValid = false
                    binding.textInputLayout5.error = "Invalid Email"
                }
                else{
                    isInfoValid = true
                }
            }
        }

        binding.myEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.myEmail.hint = null
                if (binding.myEmail.text.isNullOrEmpty()){
                    binding.myEmail.hint = "Email Address"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        binding.myPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.myPassword.hint = null
                if (binding.myPassword.text.isNullOrEmpty()){
                    binding.myPassword.hint = "Password"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        binding.loginAccBtn.setOnClickListener {
            binding.myEmail.clearFocus()
            loginAcc()
        }

        binding.createAccActivityBtn.setOnClickListener {
            switchToCreateAcc()
        }

    }

    private fun switchToCreateAcc() {
        startActivity(Intent(this,CreateAccount::class.java))
        finish()
    }

    private fun loginAcc() {
        db = Room.databaseBuilder(applicationContext,AppDB::class.java,"movie-database").build()
        db.openHelper.writableDatabase

        setProgressBar(true)
        GlobalScope.launch {
            val obtainedUser = db.AppDAO().getUser(binding.myEmail.text.toString(),binding.myPassword.text.toString())
            withContext(Dispatchers.Main){
                setProgressBar(false)
                if (obtainedUser.isNotEmpty()){
                    Log.d("LoginAccount",obtainedUser[0].UID)
                    val sharedPref = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putBoolean("logged_in", true)
                    editor.putString("User_name",obtainedUser[0].UID)
                    editor.apply()

                    startActivity(Intent(this@LoginAccount,MainActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this@LoginAccount,"Invalid Credentials",Toast.LENGTH_SHORT).show()
                }
            }



        }
    }

    private fun setProgressBar(progress : Boolean){
        if (progress){
            binding.progressBar.visibility = View.VISIBLE
        }
        else
            binding.progressBar.visibility = View.INVISIBLE
    }
}
package com.mkalachova.trelloclone.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mkalachova.trelloclone.R
import com.mkalachova.trelloclone.firebase.FirebaseAuthClass
import com.mkalachova.trelloclone.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.mkalachova.trelloclone.models.User

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupActionBar()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }

        btn_sign_up.setOnClickListener {
            val name: String = et_name.text.toString().trim { it <= ' ' }
            val email: String = et_email_signup.text.toString().trim { it <= ' ' }
            val password: String = et_password_signup.text.toString()

            if(validateForm(name, email, password)) {
                registerUser(name, email, password)
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirebaseAuthClass.authInstance
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid, name, registeredEmail)
                    FirestoreClass().registerUser(this, user)
                    hideProgressDialog()
                    finish()
                } else {
                    if(task.exception!!.message!!.isNullOrEmpty()) {
                        showErrorSnackBar(
                            getResources().getString(R.string.registration_failed)
                        )
                    } else {
                        showErrorSnackBar(task.exception!!.message!!)
                    }
                    hideProgressDialog()
                }
            }
    }

    fun validateForm(name: String, email: String, password: String) : Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar(getResources().getString(R.string.please_enter_name))
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(getResources().getString(R.string.please_enter_email))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(getResources().getString(R.string.please_enter_password))
                false
            }
            else -> {
                true
            }
        }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "Registered user",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
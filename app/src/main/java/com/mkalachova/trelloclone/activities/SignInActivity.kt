package com.mkalachova.trelloclone.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mkalachova.trelloclone.R
import kotlinx.android.synthetic.main.activity_sign_in.*
import models.User
import timber.log.Timber

class SignInActivity : BaseActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

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

        btn_sign_in.setOnClickListener {
            val email = et_email_signin.text.toString().trim { it <= ' '}
            val password = et_password_signin.text.toString()

            if(validateForm(email, password)) {
                auth = FirebaseAuth.getInstance()
                signIn(email, password)
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun signIn(email: String, password: String) {
        showProgressDialog(resources.getString(R.string.please_wait))
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressDialog()

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(baseContext, "Authentication successful!",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Timber.w("signInWithEmail:failure ${task.exception}")
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signInSuccesss(user: User) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun validateForm(email: String, password: String) : Boolean {
        return when {
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
}
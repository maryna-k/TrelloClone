package com.mkalachova.trelloclone.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mkalachova.trelloclone.R
import firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_my_profile.*
import models.User
import utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        iv_profile_user_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_update.setOnClickListener {
            if(mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
            }
            uploadUserImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this,
                "Permissions denied",
                Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK &&
            requestCode == PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_user_image)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showImageChooser() {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        toolbar_my_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUserDataInUI(user: User) {
        mUserDetails = user

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

        et_name.setText(user.name)
        et_email.setText(user.email)
        if(user.mobile != 0L) {
            et_mobile.setText(user.mobile.toString())
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        var changesMade = false

        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageUrl
            changesMade = true
        }
        if(et_name.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = et_name.text.toString()
            changesMade = true
        }
        if(et_mobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()
            changesMade = true
        }

        if(changesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" +
                        System.currentTimeMillis() +
                        "." +
                        getFileExtension(mSelectedImageFileUri)
            )
            storageRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL",
                            taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                    mProfileImageUrl = uri.toString()

                    updateUserProfileData()
                    hideProgressDialog()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
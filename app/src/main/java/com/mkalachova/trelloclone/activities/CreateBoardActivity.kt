package com.mkalachova.trelloclone.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mkalachova.trelloclone.R
import com.mkalachova.trelloclone.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_create_board.*
import com.mkalachova.trelloclone.models.Board
import com.mkalachova.trelloclone.utils.Constants
import com.mkalachova.trelloclone.utils.EspressoIdlingResource
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var selectedImageFileUri: Uri? = null
    private lateinit var userName: String
    private var boardImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        setupActionBar()

        if(intent.hasExtra((Constants.NAME))) {
            userName = intent.getStringExtra(Constants.NAME)!!
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_board_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        toolbar_create_board_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        iv_board_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_create.setOnClickListener {
            if(selectedImageFileUri != null) {
                uploadBoardImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    fun boardCreatedSuccessfully() {
        EspressoIdlingResource.increment()
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
        EspressoIdlingResource.decrement()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
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
            requestCode == Constants.PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null) {
            selectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(selectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(iv_board_image)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createBoard() {
        val assignedUsersArrayList: ArrayList<String> = java.util.ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())

        var board = Board(
            et_board_name.text.toString(),
            boardImageUrl,
            userName,
            assignedUsersArrayList
        )

        FirestoreClass.createBoard(this, board)
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if(selectedImageFileUri != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE" +
                        System.currentTimeMillis() +
                        "." +
                        Constants.getFileExtension(this, selectedImageFileUri)
            )
            storageRef.putFile(selectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                    boardImageUrl = uri.toString()

                    createBoard()
                    hideProgressDialog()
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }
}
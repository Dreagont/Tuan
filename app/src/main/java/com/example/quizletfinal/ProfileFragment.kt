package com.example.quizletfinal

import android.app.Activity
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private var username : String? = null
    private var userImage : String? = null
    lateinit var profilePicture : ImageView
    private var firebaseStorage: FirebaseStorage? = null

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null


    private var saveStorageReference: StorageReference? = null
    private var loadStorageReference:StorageReference? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        val emailTextView = view.findViewById<TextView>(R.id.txtEmail)
        val usernameTextView = view.findViewById<TextView>(R.id.txtUserNameChange)
        val btnChangePassword = view.findViewById<View>(R.id.btnChangePassword)
        val btnLogout = view.findViewById<View>(R.id.btnLogout)
        profilePicture =view.findViewById(R.id.profilePicture)

        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
        val email = sharedPreferences.getString("Email", "No Email")
        username = sharedPreferences.getString("username", "No Username")
        userImage = sharedPreferences.getString("image", "No Email")


        Toast.makeText(requireContext(),"user name: " + username.toString(),Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(),"user image: " + userImage.toString(),Toast.LENGTH_SHORT).show()

        profilePicture.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }
        firebaseStorage = FirebaseStorage.getInstance()

        saveStorageReference = firebaseStorage?.getReference("profile_pictures/")
        val loadStorageReference = firebaseStorage?.getReference("profile_pictures/$userImage")

        loadStorageReference?.downloadUrl?.addOnSuccessListener { uri ->
            val imagePath = uri.toString()

            Picasso.get().load(imagePath).placeholder(R.drawable.baseline_downloading_24).into(profilePicture)
        }

        emailTextView.text = email
        usernameTextView.text = username

        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            profilePicture.setImageURI(data?.data)
            val imageName = "$username.jpg"
            val imageRef = saveStorageReference?.child(imageName)

            val uploadTask = imageRef?.putFile(uri)

            if (uploadTask != null) {
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    updateImageUrl(username)
                    Toast.makeText(requireActivity(), "Profile picture uploaded successfully!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(requireActivity(), "Failed to upload profile picture: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateImageUrl(username: String?) {
        if (username != null) {
            FirebaseDatabase.getInstance().getReference("users").child(username).child("profileImage").setValue(
                "$username.jpg"
            )
        }
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.custom_change_password_dialog, null)

        val currentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
        val newPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val confirmNewPassword = view.findViewById<EditText>(R.id.etConfirmNewPassword)

        builder.setView(view)
            .setTitle("Change Password")
            .setPositiveButton("Change") { _, _ ->
                if (newPassword.text.toString() == confirmNewPassword.text.toString()) {
                    changePassword(currentPassword.text.toString(), newPassword.text.toString())
                } else {
                    Toast.makeText(context, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error changing password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Error auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }
}
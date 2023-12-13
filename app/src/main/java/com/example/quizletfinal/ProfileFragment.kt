package com.example.quizletfinal

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
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

        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
        val email = sharedPreferences.getString("Email", "No Email")
        val username = sharedPreferences.getString("Username", "No Username")

        emailTextView.text = email
        usernameTextView.text = username

        Toast.makeText(requireContext(), username, Toast.LENGTH_SHORT).show()

        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }


    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val currentPassword = EditText(requireContext())
        currentPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        currentPassword.hint = "Current Password"
        layout.addView(currentPassword)

        val newPassword = EditText(requireContext())
        newPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPassword.hint = "New Password"
        layout.addView(newPassword)

        val confirmNewPassword = EditText(requireContext())
        confirmNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmNewPassword.hint = "Confirm New Password"
        layout.addView(confirmNewPassword)

        builder.setView(layout)
        builder.setTitle("Change Password")
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
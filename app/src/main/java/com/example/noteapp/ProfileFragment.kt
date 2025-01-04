package com.example.noteapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileFragment : Fragment() {

    lateinit var profileName: TextView
    lateinit var profileEmail: TextView
    lateinit var profileVersion: TextView
    lateinit var logoutButton: Button
    lateinit var imgProfile: ImageView
    lateinit var sharedPreferences: SharedPreferences

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        profileVersion = view.findViewById(R.id.profileVersion)
        logoutButton = view.findViewById(R.id.logoutButton)
        imgProfile = view.findViewById(R.id.imgProfile)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Load saved profile image
        loadProfileImage()

        // Get Profile Data from SharedPreferences
        val userName = sharedPreferences.getString("USER_NAME", "Guest") ?: "Guest"
        val userEmail = sharedPreferences.getString("USER_EMAIL", "email@example.com") ?: "email@example.com"

        // Set Profile Data
        profileName.text = userName
        profileEmail.text = userEmail
        profileVersion.text = "v1.0.0"

        // Set onClickListener for Profile Image
        imgProfile.setOnClickListener {
            showProfileOptionsDialog()
        }

        // Logout Button Listener
        logoutButton.setOnClickListener {
            logout()
        }

        return view
    }

    // Menampilkan dialog opsi (Edit Profile atau Lihat Foto Profil)
    private fun showProfileOptionsDialog() {
        val options = arrayOf("Edit Profile", "Lihat Foto Profil")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Opsi")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery() // Opsi Edit Profile
                    1 -> viewProfileImage()     // Opsi Lihat Foto Profil
                }
            }
        builder.create().show()
    }

    // Membuka galeri untuk memilih foto
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Menampilkan foto profil di dialog layar penuh
    private fun viewProfileImage() {
        val imageString = sharedPreferences.getString("PROFILE_IMAGE", null)
        if (imageString != null) {
            val imageBytes = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val dialog = AlertDialog.Builder(requireContext())
            val imageView = ImageView(requireContext()).apply {
                setImageBitmap(bitmap)
                adjustViewBounds = true
            }
            dialog.setView(imageView)
            dialog.setPositiveButton("Tutup") { d, _ -> d.dismiss() }
            dialog.create().show()
        } else {
            Toast.makeText(requireContext(), "Tidak ada foto profil yang tersedia.", Toast.LENGTH_SHORT).show()
        }
    }

    // Menyimpan foto profil ke SharedPreferences
    private fun saveProfileImage(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageBytes = stream.toByteArray()
        val imageString = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)

        with(sharedPreferences.edit()) {
            putString("PROFILE_IMAGE", imageString)
            apply()
        }

        Toast.makeText(requireContext(), "Foto profil berhasil diubah!", Toast.LENGTH_SHORT).show()
    }

    // Memuat foto profil dari SharedPreferences
    private fun loadProfileImage() {
        val imageString = sharedPreferences.getString("PROFILE_IMAGE", null)
        imageString?.let {
            val imageBytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imgProfile.setImageBitmap(bitmap)
        }
    }

    // Logout dan hapus data pengguna dari SharedPreferences
    private fun logout() {
        with(sharedPreferences.edit()) {
            remove("USER_ID")
            remove("USER_NAME")
            remove("USER_EMAIL")
            remove("PROFILE_IMAGE")
            apply()
        }

        Toast.makeText(context, "Anda telah logout!", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    // Handle hasil dari pemilihan gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(it)
                val selectedImage = BitmapFactory.decodeStream(inputStream)
                imgProfile.setImageBitmap(selectedImage)
                saveProfileImage(selectedImage)
            }
        }
    }
}

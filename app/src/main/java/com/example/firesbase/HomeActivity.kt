package com.example.firesbase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firesbase.databinding.ActivityAuthBinding
import com.example.firesbase.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings


enum class ProviderType {
    BASIC,
    GOOGLE
}
class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?:"")

        //Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setup(email: String, provider: String){
        title = "inicio"
        binding.emailTextView.text = email
        binding.providerTextView.text = provider

        binding.CerrarButton.setOnClickListener(){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        binding.saveButton.setOnClickListener(){
            db.collection("users").document(email).set(
                hashMapOf("provider" to provider,
                "address" to binding.addresstextView.text.toString(),
                "phone" to binding.phoneTextView.text.toString())
            )
        }

        binding.getButton.setOnClickListener(){
            db.collection("users").document(email).get().addOnSuccessListener {
                binding.addresstextView.setText(it.get("address") as String?)
                binding.phoneTextView.setText(it.get("phone")as String?)
            }
        }

        binding.deletButton.setOnClickListener(){
            db.collection("users").document(email).delete()
        }
    }
}
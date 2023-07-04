package com.example.firesbase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.firesbase.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider



class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val GOOGLE_SING_IN = 200
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //analy
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completada")
        analytics.logEvent("InitScreen", bundle)

        //inicializar
        mAuth = FirebaseAuth.getInstance()

        //setup perron
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()

        binding.authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs:SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email:String? = prefs.getString("email", null)
        val provider:String? = prefs.getString("provider", null)

        if (email != null && provider != null){
            binding.authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }

    }

    private fun setup() {
        title = "autenticacion"
            binding.CerrarButton.setOnClickListener{
                if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {

                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(binding.emailEditText.text.toString(),
                            binding.passwordEditText.text.toString()).addOnCompleteListener {

                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?:"", ProviderType.BASIC)
                        }
                        else{ showAlert()}
                        }
                    }
                }


        binding.logInButton.setOnClickListener{
                if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {

                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(binding.emailEditText.text.toString(),
                            binding.passwordEditText.text.toString()).addOnCompleteListener {

                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?:"", ProviderType.BASIC)
                        }
                        else{ showAlert()}
                    }
                }
            }

        binding.googlebutton.setOnClickListener{
            // configuracion

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            val signInIntent = googleClient.signInIntent
            googleClient.signOut()

            startActivityForResult(signInIntent, GOOGLE_SING_IN)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido un error la autenticar el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
     private fun showHome(email: String, provider: ProviderType){

         val homeIntent = Intent(this, HomeActivity::class.java).apply {
             putExtra("email", email)
             putExtra("provider", provider.name)
         }
         startActivity(homeIntent)
     }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SING_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                if(account != null){
                    Log.d("Tag", "firebasegoogleid $account.id")
                            firebaseAuthWithGoogle(account.idToken!!)
                }else{
                    Toast.makeText(this, "correo no existe",Toast.LENGTH_LONG).show()
                }
            }catch (e:ApiException){
                Log.d("Tag","google sign in failed $e")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    Log.d("Tag","signInWithCredential:sucess")
                    val user = mAuth.currentUser?.email.toString()
                    login(user)
                }else{
                    Log.w("Tag", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "correo no existe",Toast.LENGTH_LONG).show()
                }
            }
    }


}




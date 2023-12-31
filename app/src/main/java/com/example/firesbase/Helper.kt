package com.example.firesbase

import android.content.Intent
import android.content.Context
import com.google.firebase.auth.FirebaseAuth

fun Context.login(email: String){
    val intent = Intent(this,HomeActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}
fun Context.logout(){
    FirebaseAuth.getInstance().signOut()
    val intent = Intent(this,AuthActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}
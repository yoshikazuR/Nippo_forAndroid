package jp.techacademy.yoshikazu.takahashi.nippoforandroid

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var mDataBaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mDataBaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                val userRef = mDataBaseReference.child(UsersPATH).child(user!!.uid)
                progressBar.visibility = View.GONE
                finish()
            } else {
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, getString(R.string.login_failure_message), Snackbar.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
        }

        title = getString(R.string.login_title)

        loginButton.setOnClickListener { v ->
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.length != 0 && password.length >= 6) {
                login(email, password)
            } else {
                Snackbar.make(v, getString(R.string.login_error_message), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        // プログレスバーを表示する
        progressBar.visibility = View.VISIBLE

        // ログインする
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
    }
}
package kumeda.cloudfirestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
    }

    fun loginLoginClicked(view: View) {
        val email = loginEmailTxt.text.toString()
        val password = logintPasswordTxt.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener{exception ->
                Log.e("Exception:", "Could not sign in user - ${exception.localizedMessage} ")
            }

    }
    fun loginCreateClicked(view: View) {
        val createIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createIntent)

    }
}

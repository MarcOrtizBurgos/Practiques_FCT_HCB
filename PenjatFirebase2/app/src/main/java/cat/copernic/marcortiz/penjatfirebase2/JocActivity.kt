package cat.copernic.marcortiz.penjatfirebase2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_joc.*
import kotlinx.android.synthetic.main.fragment_penjat.*

class JocActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joc)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        setup(email ?: "")
    }

    private fun setup(email : String){
        title = "Joc"
        textUsuari2.text = email

        buttonTanca2.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            moveTaskToBack(true)
            val mainIntent : Intent = Intent(this,MainActivity::class.java)
            startActivity(mainIntent)
        }
    }
}
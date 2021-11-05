package cat.copernic.marcortiz.penjatfirebase2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_penjat.*

class FragmentPenjat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_penjat)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        val nouIntent : Intent = Intent(this,JocActivity::class.java).apply {
            putExtra("email",email)
        }


        buttonN.setOnClickListener{
            startActivity(nouIntent)
        }

        buttonA.setOnClickListener{
            startActivity(nouIntent)
        }

        setup(email ?: "")
    }

    private fun setup(email : String){
        title = "Penjat"
        textUsuari.text = email

        buttonTanca.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}
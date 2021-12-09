package cat.copernic.marcortiz.penjatfirebase2.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cat.copernic.marcortiz.penjatfirebase2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_fail.*
import kotlinx.android.synthetic.main.fragment_fail.buttonTanca3
import kotlinx.android.synthetic.main.fragment_win.*



class FailActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_fail)

        title = "Fail"

        val bundle = intent.extras
        val user = bundle?.getString("user").toString()
        val paraula = bundle?.getString("paraula").toString()
        val punts = bundle?.getString("punts").toString()
        val text = getString(R.string.he_perdut_al_penjat, user + paraula)

        textViewParaula1.text = getString(R.string.paraula_era, paraula)
        textViewPunts2.text = getString(R.string.punts, punts)

        buttonCompartir1.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,text)
            //intent.data = Uri.parse(imageViewWin.drawable.toString())
            startActivity(Intent.createChooser(intent,"Penjat"))
        }

        buttonTanca3.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            moveTaskToBack(true)
            val mainIntent: Intent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }
}

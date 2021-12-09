package cat.copernic.marcortiz.penjatfirebase2.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_win.*
import cat.copernic.marcortiz.penjatfirebase2.R


class WinActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_win)

        title = "Win"

        val bundle = intent.extras
        val user = bundle?.getString("user").toString()
        val paraula = bundle?.getString("paraula").toString()
        val punts = bundle?.getString("punts").toString()
        val text = getString(R.string.he_guanyat_al_penjat_fet_per_marc, user, paraula)

        textViewParaula.text = getString(R.string.paraula_era, paraula)
        textViewPunts.text = getString(R.string.punts, punts)

        buttonCompartir.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, text)
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
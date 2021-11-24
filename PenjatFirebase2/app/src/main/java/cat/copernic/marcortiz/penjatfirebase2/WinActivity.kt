package cat.copernic.marcortiz.penjatfirebase2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_win.*
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import android.graphics.drawable.BitmapDrawable





class WinActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_win)

        buttonCompartir.setOnClickListener {
            /*
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "He guanyat en el Penjat fet per Marc Ortiz Burgos"
            )
            startActivity(Intent.createChooser(intent, "Share with"))*/
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "He guanyat en el Penjat fet per Marc Ortiz Burgos")
            //intent.data = Uri.parse(imageViewWin.drawable.toString())
            startActivity(Intent.createChooser(intent,"Penjat"))

        }

        title = "Win"
        buttonTanca3.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}
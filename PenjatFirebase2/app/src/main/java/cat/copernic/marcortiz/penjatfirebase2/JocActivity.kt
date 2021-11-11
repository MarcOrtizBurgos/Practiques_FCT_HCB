package cat.copernic.marcortiz.penjatfirebase2

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_joc.*

class JocActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joc)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val partida = bundle?.getString("partida")
        setup(email ?: "",partida ?: "")
    }

    val db = Firebase.firestore

    private fun setup(email : String,partida : String){
        title = "Joc"
        textUsuari2.text = email
        //textpartida.text = partida.toString()

        db.collection("jocs")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //Log.d(TAG, "${document.id} => ${document.data}")
                    if (document.id.equals(partida)){
                        textpartida.setText(document.id)
                        intents.setText(document.data["intents"].toString())
                        acerts.setText(document.data["acerts"].toString())
                    }
                    Log.d("MainActivity", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


        buttonEnvia.setOnClickListener{
            db.collection("user")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        //Log.d(TAG, "${document.id} => ${document.data}")
                        Log.d("MainActivity", "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }

        buttonTanca2.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            moveTaskToBack(true)
            val mainIntent : Intent = Intent(this,MainActivity::class.java)
            startActivity(mainIntent)
        }
    }

}
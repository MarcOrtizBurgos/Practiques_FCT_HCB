package cat.copernic.marcortiz.penjatfirebase2

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.fragment_penjat.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.collections.HashMap

class FragmentPenjat : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_penjat)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        var partida = ""

        buttonN.setOnClickListener {
            partida = editpartida.text.toString()
            create(partida, email.toString(), partida.toString(), email.toString())
        }

        buttonA.setOnClickListener {
            partida = editpartida.text.toString()
            db.collection("jocs")
                .get()
                .addOnSuccessListener { doc ->
                    var bol = false
                    for (i in doc) {
                        if (i.id.toString().equals(editpartida.text.toString()) && i.get("state")
                                .toString().equals("false")
                        ) {
                            bol = true
                        }
                    }
                    if (bol) {
                        val nouIntent: Intent = Intent(this, JocActivity::class.java).apply {
                            putExtra("email", email)
                            putExtra("partida", partida)
                        }
                        startActivity(nouIntent)
                    } else {
                        alert("Error", "Aquesta partida ja esta finalitzada")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }

        buttonR.setOnClickListener {
            val nouIntent: Intent = Intent(this, RankingActivity::class.java).apply {}
            startActivity(nouIntent)
        }

        setup(email ?: "")
    }

    private fun setup(email: String) {
        title = "Penjat"
        textUsuari.text = email

        buttonTanca.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    //Busca una paraula random de l'array
    private fun paraulaRandom(paraules: String): String? {
        val replace = paraules.replace("[", "")
        val replace1 = replace.replace("]", "")
        val myList = Arrays.asList(replace1.split(", ").toTypedArray())
        val rand = Random()
        val num: Int = rand.nextInt(6)
        return myList[0][num]
    }

    //Crea una nova partida a la base de dades
    @Throws(InterruptedException::class, ExecutionException::class)
    private fun create(id: String?, usuari: String?, partida: String, email: String) {
        db.collection("jocs")
            .get()
            .addOnSuccessListener { doc ->
                var bol = false
                for (i in doc) {
                    if (i.id.toString().equals(editpartida.text.toString())) {
                        bol = true
                    }
                }

                if (!bol) {
                    db.collection("plantilla").document("penjat")
                        .get()
                        .addOnSuccessListener { result ->
                            val docRef: DocumentReference = db.collection("jocs").document(id ?: "")
                            val data: MutableMap<String, Any?> = HashMap()
                            data["acerts"] = result["acerts"].hashCode()
                            data["adivinar"] = ""
                            data["intents"] = result["intents"].hashCode()
                            data["paraula"] =
                                paraulaRandom(result["paraules"].toString()).toString()
                            data["state"] = false
                            data["estatjoc"] = ""
                            data["usuari"] = usuari ?: ""
                            docRef.set(data)

                            val nouIntent: Intent = Intent(this, JocActivity::class.java).apply {
                                putExtra("email", email)
                                putExtra("partida", partida)
                            }

                            startActivity(nouIntent)
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error getting documents.", exception)
                        }
                } else {
                    alert("Error", "Aquesta partida ja esta feta")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun alert(title: String, alert: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(alert)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
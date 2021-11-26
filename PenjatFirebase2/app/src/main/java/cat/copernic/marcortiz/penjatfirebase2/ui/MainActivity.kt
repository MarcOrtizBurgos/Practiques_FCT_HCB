package cat.copernic.marcortiz.penjatfirebase2.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cat.copernic.marcortiz.penjatfirebase2.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_login.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de firebase completa")
        analytics.logEvent("InitScreen", bundle)

        setup()
    }

    private fun setup() {
        title = "Autentificació"
        buttonRegistre.setOnClickListener {
            if (editUsuari.text.isNotEmpty() && !editClau.text.isNullOrEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        editUsuari.text.toString(),
                        editClau.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val ahora = System.currentTimeMillis()
                            val fecha = Date(ahora)
                            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                            val salida: String = df.format(fecha)

                            val docRef: DocumentReference = db.collection("users").document(editUsuari.text.toString())
                            val data: MutableMap<String, Any?> = HashMap()
                            data["id"] = editUsuari.text.toString().split("@")[0]
                            data["punts"] = 0
                            data["date"] = salida
                            docRef.set(data)

                            showPenjat(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
            }
        }

        buttonLogin.setOnClickListener {
            if (editUsuari.text.isNotEmpty() && !editClau.text.isNullOrEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        editUsuari.text.toString(),
                        editClau.text.toString()
                    ).addOnCompleteListener() {
                        if (it.isSuccessful) {
                            showPenjat(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se a produit un error autenticant l'usuari")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showPenjat(email: String) {
        val penjatIntent: Intent = Intent(this, FragmentPenjat::class.java).apply {
            putExtra("email", email)
        }
        startActivity(penjatIntent)
    }

}
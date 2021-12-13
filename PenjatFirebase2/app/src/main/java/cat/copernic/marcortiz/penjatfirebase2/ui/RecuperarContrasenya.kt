package cat.copernic.marcortiz.penjatfirebase2.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cat.copernic.marcortiz.penjatfirebase2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_recuperar_contrasenya.*
import java.util.regex.Pattern

class RecuperarContrasenya : AppCompatActivity() {

    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasenya)

        buttonContinuar.setOnClickListener {
            if (dataValid(
                    editClau.text.toString(),
                    editRepeteixClau.text.toString(),
                    editUsuari.text.toString()
                )
            ) {
                val psswd = editClau.text.toString()
                val email = editUsuari.text.toString()
                changePassword(email, psswd)
            }
        }
    }

    private fun changePassword(email: String, psswd: String) {
        var bool = false
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == email) {
                    bool = true
                    val password = document.get("password").toString()
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        email,
                        password,
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val currentUser = auth.currentUser
                            currentUser?.updatePassword(psswd)?.addOnSuccessListener  {
                                auth.signOut()
                                Toast.makeText(
                                    this,
                                    getString(R.string.s_ha_canbiar_la_contrasenya),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            val pas = hashMapOf("password" to psswd)
                            db.collection("users").document(email).update(
                                pas as Map<String, Any>
                            )
                            showMain()
                            finish()
                        } else {
                            showAlert(getString(R.string.error_en_inici_de_sessio))
                        }
                    }
                }
            }
            if (!bool) {
                showAlert(getString(R.string.L_usuari_no_esta_registrat))
            }
        }

    }

    private fun dataValid(psswd1: String, psswd2: String, email: String): Boolean {
        var bool = true
        var errorMessage = ""

        if (!checkEmailFormat(email)) {
            errorMessage += getString(R.string.format_email_incorrecte)
            bool = false
        }

        if (psswd1.isEmpty()) {
            errorMessage += getString(R.string.falta_introduir_la_contrasenya)
            bool = false
        }
        if (psswd2.isEmpty()) {
            errorMessage += getString(R.string.falta_introduir_contrasenya_repetida)
            bool = false
        }

        if (psswd1.isNotEmpty() && psswd2.isNotEmpty()) {
            if (psswd1 != psswd2) {
                errorMessage += getString(R.string.les_contrasenyes_no_coincideixen)
                bool = false
            }
        }

        if (errorMessage != "") {
            showAlert(errorMessage)
        }
        return bool
    }

    private fun checkEmailFormat(email: String): Boolean {
        val email_address_pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return email_address_pattern.matcher(email).matches()
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.error))
        builder.setMessage(msg)
        builder.setPositiveButton(getString(R.string.acceptar), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

}
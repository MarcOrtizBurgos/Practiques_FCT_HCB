package cat.copernic.marcortiz.penjatfirebase2.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
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
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de firebase completa")
        analytics.logEvent("InitScreen", bundle)

        listenerbuttonidioma()

        setup()
    }
    private fun listenerbuttonidioma(){
        buttonCat.setOnClickListener {
            idioma("ca","ES")
            val intent = Intent(this, MainActivity::class.java).apply {
            }
            finish()
            startActivity(intent)
        }
        buttonEsp.setOnClickListener {
            idioma("es","ES")
            val intent = Intent(this, MainActivity::class.java).apply {
            }
            finish()
            startActivity(intent)
        }
        buttonEn.setOnClickListener {
            idioma("en","")
            val intent = Intent(this, MainActivity::class.java).apply {
            }
            finish()
            startActivity(intent)
        }
    }

    private fun idioma(language:String,country:String){
        val localizacion = Locale(language, country)
        Locale.setDefault(localizacion)
        val config = Configuration()
        config.locale = localizacion
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setup() {
        title = getString(R.string.autentificacio)
        buttonRegistre.setOnClickListener {

            if(datavalids(
                    editUsuari.text.toString(),
                    editClau.text.toString()
            )){
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
                            data["email"] = editUsuari.text.toString()
                            data["password"] = editClau.text.toString()
                            docRef.set(data)

                            showPenjat(it.result?.user?.email ?: "")
                        } else {
                            showAlert(getString(R.string.error_amb_el_registre), getString(R.string.s_ha_produit_un_error_en_intentar_registrar_aquest_usuari) +"\n"+
                                    getString(R.string.consideri_la_possibilitat_que_aquest_usuari_ja_estigui_registrat))
                        }
                    }
            }
        }

        buttonLogin.setOnClickListener {
            if (editUsuari.text.isNotEmpty() && !editClau.text.isNullOrEmpty()) {
                if(checkEmailFormat(editUsuari.text.toString())){
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(
                            editUsuari.text.toString(),
                            editClau.text.toString()
                        ).addOnCompleteListener() {
                            if (it.isSuccessful) {
                                showPenjat(it.result?.user?.email ?: "")
                            } else {
                                showAlert(getString(R.string.error_en_inici_de_sessio),getString(
                                          R.string.s_ha_produit_un_error_en_iniciar_sessio
                                        + R.string.consideri_la_possibilitat_que_aquest_usuari_no_estigui_registrar))
                            }
                        }
                }else{
                    showAlert(getString(R.string.error),  getString(R.string.el_valor_introduit_en_el_camp_usuari_no_es_un_email))
                }

            }else{
                showAlert(getString(R.string.error), getString(R.string.els_camps_estan_buits))
            }
        }

        buttonRecuperaContrasenya.setOnClickListener {
            showRecuperaContrasenya()
        }
    }

    private fun showAlert(title: String, msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(R.string.acceptar, null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showPenjat(email: String) {
        val penjatIntent: Intent = Intent(this, FragmentPenjat::class.java).apply {
            putExtra("email", email)
        }
        startActivity(penjatIntent)
    }

    private fun showRecuperaContrasenya(){
        val recuperaContrasenya: Intent = Intent(this, RecuperarContrasenya::class.java)
        startActivity(recuperaContrasenya)
    }

    private fun datavalids(email: String, password: String): Boolean {
        var errorMessage = ""
        var bool = true

        if (email.isEmpty()) {
            errorMessage += getString(R.string.falta_introduir_el_email)
            bool = false
        } else if (!checkEmailFormat(email)) {
            errorMessage += getString(R.string.format_email_incorrecte)
            bool = false
        }

        if (password.isEmpty()) {
            errorMessage += getString(R.string.falta_introduir_la_contrasenya)
            bool = false
        }

        if (password.length < 6) {
            errorMessage += getString(R.string.la_contrasenya_ha_de_Ser_minim_de_sis_carcters)
            bool = false
        }

        if (errorMessage != "") {
            showAlert(getString(R.string.error), errorMessage)
        }

        return bool

    }

    private fun checkEmailFormat(email: String): Boolean {
        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

}
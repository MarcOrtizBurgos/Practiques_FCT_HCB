package cat.copernic.marcortiz.penjatfirebase2.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.marcortiz.penjatfirebase2.R
import cat.copernic.marcortiz.penjatfirebase2.adapters.RankingAdapter
import cat.copernic.marcortiz.penjatfirebase2.models.User
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_ranking.*
import kotlin.collections.ArrayList


class RankingActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var usuaris: ArrayList<User>
    private lateinit var myAdapter: RankingAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.fragment_ranking)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuaris = arrayListOf()
        myAdapter = RankingAdapter(usuaris)

        recyclerView.adapter = myAdapter

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    eventChngeListener("punts")
                } else if (position == 1) {
                    eventChngeListener("date")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


    }

    fun eventChngeListener(str: String) {
        usuaris.clear()
        val docRef = db.collection("users")
        docRef.orderBy(str, Query.Direction.DESCENDING).limit(6)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.get("id").toString().isNotEmpty() && document.get("date")
                            .toString().isNotEmpty()
                        && document.get("punts").toString().isNotEmpty()
                    ) {
                        usuaris.add(
                            User(
                                document.get("id").toString(),
                                document.get("date").toString(),
                                document.get("punts").toString()
                            )
                        )
                    }
                }
                myAdapter = RankingAdapter(usuaris)
                recyclerView.adapter = myAdapter
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }
}







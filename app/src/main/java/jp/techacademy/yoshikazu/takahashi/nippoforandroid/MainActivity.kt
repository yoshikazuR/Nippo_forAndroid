package jp.techacademy.yoshikazu.takahashi.nippoforandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Repo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var mReportArrayList = mutableMapOf<String, Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        title = "Nippo Viewer"

        val user = FirebaseAuth.getInstance().currentUser

        if(user == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }else {
            listView.setOnItemClickListener { adapterView, _, position, _ ->
                val reportTitle = adapterView.getItemAtPosition(position) as String
                val reportBody = mReportArrayList.get(reportTitle.toString())
                Log.d("TEST", reportTitle.toString())
                Log.d("TEST", reportBody!!.body)
                val intent = Intent(applicationContext, ReportView::class.java)
                intent.putExtra("reportBody", reportBody!!.body)
                    .putExtra("reportTitle", reportTitle.toString())
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        val array = mutableListOf<String>()
        val listView = findViewById<ListView>(R.id.listView)
        if(user != null) {
            Log.d("TEST", "READ")
            val db = Firebase.firestore
            db.collection(user.uid).get()
                .addOnSuccessListener {result ->
                    for (document in result) {
                        if (document.id !=  "template") {
                            val report: Report =
                                Report(document.id.replace("_", "/"), document.data["value"].toString())
//                        Log.d("TEST", "${report.title} => ${report.body}")
                            mReportArrayList.put(report.title, report)
                            array.add(report.title)
                        }
                    }
                    array.sortWith(compareBy<String> {Integer.parseInt(it.split("/")[0])}.thenBy {Integer.parseInt(it.split("/")[1])}.thenBy { Integer.parseInt(it.split("/")[2].split("(")[0]) })
                    array.reverse()
                    val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array)
                    listView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.d("TEST", "get failed with ", exception)
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val rootLayout: View = findViewById(android.R.id.content)

        if (id == R.id.action_logout) {
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                Snackbar.make(rootLayout, getString(R.string.no_login_user), Snackbar.LENGTH_LONG).show()
            } else {
                FirebaseAuth.getInstance().signOut()
                Snackbar.make(rootLayout, getString(R.string.logout_complete_message), Snackbar.LENGTH_LONG).show()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
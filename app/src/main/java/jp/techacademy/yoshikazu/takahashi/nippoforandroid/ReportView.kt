package jp.techacademy.yoshikazu.takahashi.nippoforandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_report_view.*

class ReportView : AppCompatActivity() {
    private var reportTitle:String = ""
    private var reportBody:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_view)

        val extras = intent.extras
        reportTitle = extras!!.get("reportTitle") as String
        reportBody = extras!!.get("reportBody") as String
        title = reportTitle


        textView.text = reportBody
    }
}
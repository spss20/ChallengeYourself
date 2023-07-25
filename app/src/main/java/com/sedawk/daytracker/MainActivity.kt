package com.sedawk.daytracker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var start: Button
    private lateinit var sessionManager: SessionManager
    private lateinit var challengeNameTv: TextView
    private lateinit var counterLayout: LinearLayout
    private lateinit var daysTv: TextView
    private lateinit var hoursTv: TextView
    private lateinit var minutesTv: TextView
    private lateinit var secondsTv: TextView
    private lateinit var setting: ImageView
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        mHandler = Handler(Looper.getMainLooper())
        start = findViewById(R.id.start_btn)
        setting = findViewById(R.id.setting)
        challengeNameTv = findViewById(R.id.challenge_name_tv)
        counterLayout = findViewById(R.id.counter_ly)
        daysTv = findViewById(R.id.day_tv)
        hoursTv = findViewById(R.id.hour_tv)
        minutesTv = findViewById(R.id.minute_tv)
        secondsTv = findViewById(R.id.seconds_tv)
        start.setOnClickListener { showChallengeDialog() }
        setting.setOnClickListener { showSettingsDialog() }
        if (sessionManager.name != null) {
            startCounter()
        } else {
            start.visibility = View.VISIBLE
            counterLayout.visibility = View.GONE
            setting.visibility = View.GONE
            challengeNameTv.text = getString(R.string.challenge_title_placeholder)
        }
    }

    private fun startCounter() {
        challengeNameTv.text = sessionManager.name
        start.visibility = View.GONE
        counterLayout.visibility = View.VISIBLE
        setting.visibility = View.VISIBLE
        val challengeTimestamp = sessionManager.timestamp
        printTime(challengeTimestamp)
        val diff = System.currentTimeMillis() - sessionManager.timestamp
        val diffDays = diff / (24 * 60 * 60 * 1000)
        Log.v(TAG, "Diff Days: $diffDays")
        daysTv.text = getString(R.string.day_format, diffDays + 1)
        mHandler.post(updateDateTime)
        val intent = Intent(this@MainActivity, MyWidgetAlarmReceiver::class.java)
        sendBroadcast(intent)
    }

    private var updateDateTime: Runnable = object : Runnable {
        override fun run() {
            val calendar = Calendar.getInstance()
            val currentHour = calendar[Calendar.HOUR_OF_DAY]
            val currentMinute = calendar[Calendar.MINUTE]
            val currentSecond = calendar[Calendar.SECOND]
            var remainingSeconds =
                (23 - currentHour) * 60 * 60 + (59 - currentMinute) * 60 + (59 - currentSecond)
            val remainingHours = remainingSeconds / (60 * 60) // Calculate remaining hours
            remainingSeconds %= 60 * 60
            val remainingMinutes = remainingSeconds / 60 // Calculate remaining minutes
            remainingSeconds %= 60
            hoursTv.text = remainingHours.toString()
            minutesTv.text = remainingMinutes.toString()
            secondsTv.text = remainingSeconds.toString()
            mHandler.postDelayed(this, 1000)
        }
    }

    private fun showChallengeDialog() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_challenge, null)
        val challengeNameEt: EditText = view.findViewById(R.id.challenge_name)
        val startChallenge: Button = view.findViewById(R.id.start_challenge)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        startChallenge.setOnClickListener {
            val challengeName: String = challengeNameEt.text.toString()
            if (challengeName.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "Challenge Name cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startChallenge(challengeName)
                dialog.dismiss()
            }
        }
    }

    private fun showSettingsDialog() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
        val startCounterOn = view.findViewById<Button>(R.id.start_counter_on)
        val reset = view.findViewById<Button>(R.id.reset)
        val cancel = view.findViewById<Button>(R.id.cancel)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        startCounterOn.setOnClickListener {
            dialog.dismiss()
            showDateDialog()
        }
        reset.setOnClickListener {
            dialog.dismiss()
            reset()
        }
        cancel.setOnClickListener { dialog.dismiss() }
    }

    private fun reset() {
        mHandler.removeCallbacks(updateDateTime)
        sessionManager.name = null
        sessionManager.timestamp = -1
        start.visibility = View.VISIBLE
        counterLayout.visibility = View.GONE
        setting.visibility = View.GONE
        challengeNameTv.text = getString(R.string.challenge_title_placeholder)
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar[year, month, dayOfMonth, 0, 0] = 0
                sessionManager.timestamp = selectedCalendar.timeInMillis
                val diff = System.currentTimeMillis() - selectedCalendar.timeInMillis
                val diffDays = diff / (24 * 60 * 60 * 1000)
                daysTv.text = getString(R.string.day_format, diffDays + 1)
                val intent = Intent(this@MainActivity, MyWidgetAlarmReceiver::class.java)
                sendBroadcast(intent)
                Toast.makeText(this, "Day Counter Started", Toast.LENGTH_SHORT).show()
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }

    private fun startChallenge(challengeName: String) {
        sessionManager.name = challengeName
        val currentDate = Calendar.getInstance()
        currentDate[Calendar.HOUR_OF_DAY] = 0
        currentDate[Calendar.MINUTE] = 0
        currentDate[Calendar.SECOND] = 0
        printTime(currentDate.timeInMillis)
        sessionManager.timestamp = currentDate.timeInMillis
        startCounter()
    }

    private fun printTime(timestamp: Long) {
        val df = SimpleDateFormat("yyyy.MM.dd G HH:mm:ss", Locale.getDefault())
        val date = df.format(Date(timestamp))
        Log.d(TAG, "Date: $date")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
package com.sedawk.daytracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button start;
    SessionManager sessionManager;
    TextView challengeNameTv;
    LinearLayout counterLayout;
    TextView daysTv;
    TextView hoursTv;
    TextView minutesTv;
    TextView secondsTv;
    ImageView setting;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        mHandler = new Handler(Looper.getMainLooper());

        start = findViewById(R.id.start_btn);
        setting = findViewById(R.id.setting);
        challengeNameTv = findViewById(R.id.challenge_name_tv);
        counterLayout = findViewById(R.id.counter_ly);
        daysTv = findViewById(R.id.day_tv);
        hoursTv = findViewById(R.id.hour_tv);
        minutesTv = findViewById(R.id.minute_tv);
        secondsTv = findViewById(R.id.seconds_tv);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChallengeDialog();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsDialog();
            }
        });

        if(sessionManager.getName() != null){
            startCounter();
        } else  {
            start.setVisibility(View.VISIBLE);
            counterLayout.setVisibility(View.GONE);
            setting.setVisibility(View.GONE);
            challengeNameTv.setText(getString(R.string.challenge_title_placeholder));
        }
    }

    private void startCounter() {
        challengeNameTv.setText(sessionManager.getName());
        start.setVisibility(View.GONE);
        counterLayout.setVisibility(View.VISIBLE);
        setting.setVisibility(View.VISIBLE);

        long challengeTimestamp = sessionManager.getTimestamp();
        printTime(challengeTimestamp);

        long diff = (System.currentTimeMillis() - sessionManager.getTimestamp());
        long diffDays = (diff / (24 * 60 * 60 * 1000));

        Log.v(TAG , "Diff Days: "  + diffDays);
        daysTv.setText(getString(R.string.day_format , diffDays + 1));

        mHandler.post(updateDateTime);

        Intent intent = new Intent(MainActivity.this, MyWidgetAlarmReceiver.class);
        sendBroadcast(intent);
    }

    Runnable updateDateTime = new Runnable() {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);
            int currentSecond = calendar.get(Calendar.SECOND);

            int remainingSeconds = (23 - currentHour) * 60 * 60 // Calculate remaining seconds in hours
                    + (59 - currentMinute) * 60   // Calculate remaining seconds in minutes
                    + (59 - currentSecond);       // Calculate remaining seconds in seconds

            int remainingHours = remainingSeconds / (60 * 60);   // Calculate remaining hours
            remainingSeconds %= (60 * 60);

            int remainingMinutes = remainingSeconds / 60;        // Calculate remaining minutes
            remainingSeconds %= 60;

            hoursTv.setText(String.valueOf(remainingHours));
            minutesTv.setText(String.valueOf(remainingMinutes));
            secondsTv.setText(String.valueOf(remainingSeconds));

            mHandler.postDelayed(this , 1000);
        }
    };

    private void showChallengeDialog() {
        View view  = LayoutInflater.from(this).inflate(R.layout.dialog_challenge , null);
        EditText challengeNameEt = view.findViewById(R.id.challenge_name);
        Button startChallenge = view.findViewById(R.id.start_challenge);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                        .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        startChallenge.setOnClickListener(view1 -> {
            String challengeName = challengeNameEt.getText().toString();
            if(challengeName.isEmpty()){
                Toast.makeText(MainActivity.this, "Challenge Name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                startChallenge(challengeName);
                dialog.dismiss();
            }
        });
    }

    private void showSettingsDialog(){
        View view  = LayoutInflater.from(this).inflate(R.layout.dialog_settings , null);
        Button startCounterOn = view.findViewById(R.id.start_counter_on);
        Button reset = view.findViewById(R.id.reset);
        Button cancel = view.findViewById(R.id.cancel);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        startCounterOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showDateDialog();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                reset();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void reset() {
        mHandler.removeCallbacks(updateDateTime);

        sessionManager.setName(null);
        sessionManager.setTimestamp(-1);

        start.setVisibility(View.VISIBLE);
        counterLayout.setVisibility(View.GONE);
        setting.setVisibility(View.GONE);
        challengeNameTv.setText(getString(R.string.challenge_title_placeholder));
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year , month , dayOfMonth , 0 , 0, 0);
                    sessionManager.setTimestamp(selectedCalendar.getTimeInMillis());

                    long diff = (System.currentTimeMillis() - selectedCalendar.getTimeInMillis());
                    long diffDays = (diff / (24 * 60 * 60 * 1000));

                    daysTv.setText(getString(R.string.day_format , diffDays + 1));
                    Intent intent = new Intent(MainActivity.this, MyWidgetAlarmReceiver.class);
                    sendBroadcast(intent);
                    Toast.makeText(this, "Day Counter Started", Toast.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void startChallenge(String challengeName){
        sessionManager.setName(challengeName);

        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY , 0);
        currentDate.set(Calendar.MINUTE , 0);
        currentDate.set(Calendar.SECOND , 0);

        printTime(currentDate.getTimeInMillis());
        sessionManager.setTimestamp(currentDate.getTimeInMillis());

        startCounter();
    }

    private void printTime(long timestamp){
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd G HH:mm:ss");
        String date = df.format(new Date(timestamp));
        Log.d(TAG , "Date: " + date);
    }
}
package com.example.notificationscheduler;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private JobScheduler jobScheduler;
    public static final int JOB_ID = 0;
    //Switches for setting job options
    private Switch mDeviceIdleSwitch;
    private Switch mDeviceChargingSwitch;
    //Override deadline seekbar
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDeviceIdleSwitch = findViewById(R.id.idleSwitch);
        mDeviceChargingSwitch = findViewById(R.id.chargingSwitch);
        mSeekBar = findViewById(R.id.seekBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        }

        final TextView seekBarProgress = findViewById(R.id.seekBarProgess);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i > 0) {
                    seekBarProgress.setText(i + " s");
                } else {
                    seekBarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public void scheduleJob(View view) {
        RadioGroup networkOptions = findViewById(R.id.networkOptions);
        int selectedNetworkID = networkOptions.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
        int seekBarInt = mSeekBar.getProgress();
        boolean seekBarSet = seekBarInt > 0;

        switch (selectedNetworkID) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                    .setRequiredNetworkType(selectedNetworkOption)
                    .setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked())
                    .setRequiresCharging(mDeviceChargingSwitch.isChecked());

            if (seekBarSet) {
                builder.setOverrideDeadline(seekBarInt * 1000);
            }
            boolean constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE
                    || mDeviceChargingSwitch.isChecked() || mDeviceIdleSwitch.isChecked()
                    || seekBarSet;
            if (constraintSet) {
                JobInfo jobInfo = builder.build();
                jobScheduler.schedule(jobInfo);
                Toast.makeText(this, "Job Scheduled, job will run when " +
                        "the constraints are met.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please set at least one constraint",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void cancelJob(View view) {
        if (jobScheduler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                jobScheduler.cancelAll();
            }
            jobScheduler = null;
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}

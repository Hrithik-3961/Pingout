package com.example.pingout;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

interface TimerOverCallback {
    void onTimerOver();
}

public class ThreadHandler {

    Context context;

    ThreadHandler(Context ctx) {
        context = ctx;
    }

    void startThread() {
        ComponentName componentName = new ComponentName(context, TimerService.class);
        JobInfo jobInfo = new JobInfo.Builder(100, componentName)
                .build();
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo);
    }

    void stopThread() {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancelAll();
    }

    boolean isRunning() {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        return scheduler.getAllPendingJobs().size() > 0;
    }

}

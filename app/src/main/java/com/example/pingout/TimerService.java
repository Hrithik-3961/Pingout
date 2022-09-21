package com.example.pingout;

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.util.Log;

import java.util.List;


public class TimerService extends JobService {
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> {
            for(int i = 0; i < 6; i++) {
                if(jobCancelled)
                    return;
                Log.d("myTag", "onStartJob: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            boolean isForeground = myProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;

            if(isForeground) {
                ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
                assert activityManager != null;
                List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
                String topClassName = tasks.get(0).getTaskInfo().topActivity.getShortClassName().substring(1);

                if (HomeActivity.mListener != null && topClassName.equals("HomeActivity"))
                    HomeActivity.mListener.onTimerOver();

                if (ChatActivity.mListener != null && topClassName.equals("ChatActivity"))
                    ChatActivity.mListener.onTimerOver();
            }
            jobFinished(jobParameters, false);
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancelled = true;
        return true;
    }
}

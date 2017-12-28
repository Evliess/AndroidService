package play.wait.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by guijj on 9/25/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private PowerManager.WakeLock wakeLock;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PLAY.WAIT.tag");
            System.out.println("WakeUP is null");
            wakeLock.acquire();
        } else {
            System.out.println("WakeUP is not null");
            wakeLock.acquire();
        }
    }
}

package play.wait.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import play.wait.servicetest.MainActivity;


/**
 * Created by guijj on 9/20/2017.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    private final String actionBoot = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(actionBoot)){
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    }

}

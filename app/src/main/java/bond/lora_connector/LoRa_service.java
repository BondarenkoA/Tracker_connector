package bond.lora_connector;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import bond.lora_connector.logger.Log;

/**
 * Created by bond on 24.11.2017.
 * Служба для связи с трекером по блютус, и передачей данных в андрозик
 */

public class LoRa_service extends Service {

    private static final String TAG = "LoRa_service";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Test", "Service: onCreate");

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_lora_notif_38);
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        startForeground(777, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Test", "Service: onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Test", "Service: onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("Test", "Service: onTaskRemoved");
    }
}

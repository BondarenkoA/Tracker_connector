package bond.lora_connector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import bond.lora_connector.logger.Log;

/**
 * Created by bond on 24.11.2017.
 * Служба для связи с трекером по блютус, и передачей данных в андрозик
 */

public class LoRa_service extends Service {

    private static final String TAG = "LoRa_service";

    NotificationManager mNM;

    Messenger mClient;

    final tracker_handler m_tracker_handler = new tracker_handler();

    private BT_tracker m_BT_tracker = new BT_tracker(m_tracker_handler);



    /**
     * Handler of messages from host-tracker.
     */
    class tracker_handler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG.STR_FROM_TRACKER:
                    Log.i(TAG, "MESSAGE_READ");
                    if(mClient != null) {
                        try {
                            Message out_msg = Message.obtain(null, MSG.STR_FROM_TRACKER, msg.obj);
                            mClient.send(out_msg);
                        } catch (RemoteException e) {
                            // There is nothing special we need to do if the service has crashed.
                        }
                    }
                    break;
                case MSG.BT_IS_OFF:
                case MSG.BT_IS_ON:
                case MSG.TRACKER_NOT_CONNECTED:
                case MSG.TRACKER_CONNECTED:
                    try {
                        Message out_msg = Message.obtain(msg);
                        mClient.send(out_msg);
                    } catch (RemoteException e) {
                        // There is nothing special we need to do if the service has crashed.
                    }

                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }



    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG.REGISTER_CLIENT:
                    Log.i(TAG, "MSG_REGISTER_CLIENT");
                    mClient  = msg.replyTo;

                    try {
                        Message out_msg = Message.obtain(null, MSG.STR_FROM_TRACKER, new String("Клиент подключен!\n"));
                        mClient.send(out_msg);
                    } catch (RemoteException e) {
                        // There is nothing special we need to do if the service has crashed.
                    }

                    if(m_BT_tracker.getState() != BT_tracker.STATE_CONNECTED){
                        m_BT_tracker.set_BT_address("30:14:09:30:11:18");
                        m_BT_tracker.connect();
                    }

                    break;
                case MSG.STOP_SERVICE:
                    Log.i(TAG, "MSG_STOP_SERVICE");

                    m_BT_tracker.stop();
                    stopSelf();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service: onCreate");

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
        Log.i(TAG, "Service: onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service: onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "Service: onTaskRemoved");
    }
}

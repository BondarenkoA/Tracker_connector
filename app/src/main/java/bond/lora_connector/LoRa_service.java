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

    static final int MSG_REGISTER_CLIENT = 1; //register a client
    static final int MSG_STOP_SERVICE = 2;
    static final int MSG_STR_TO_TRACKER = 3;
    static final int MSG_STR_FROM_TRACKER = 4;
    static final int MSG_SET_TRACKER_BT_ADR = 5;

    /**
     * Handler of messages from host-tracker.
     */
    class tracker_handler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case constants.MESSAGE_READ:
                    Log.i(TAG, "MESSAGE_READ");
                    try {
                        Message out_msg = Message.obtain(null, LoRa_service.MSG_STR_FROM_TRACKER, msg.obj);
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
                case MSG_REGISTER_CLIENT:
                    Log.i(TAG, "MSG_REGISTER_CLIENT");
                    mClient  = msg.replyTo;

                    try {
                        Message out_msg = Message.obtain(null, LoRa_service.MSG_STR_FROM_TRACKER, new String("Клиент подключен!\n"));
                        mClient.send(out_msg);
                    } catch (RemoteException e) {
                        // There is nothing special we need to do if the service has crashed.
                    }

                    break;
                case MSG_STOP_SERVICE:
                    Log.i(TAG, "MSG_STOP_SERVICE");
                    stopSelf();
                    break;
                /*case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    for (int i=mClients.size()-1; i>=0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null,
                                    MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;*/
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


        if(m_BT_tracker.getState() != BT_tracker.STATE_CONNECTED){
            m_BT_tracker.set_BT_address("30:14:09:30:11:18");
            m_BT_tracker.connect();
        }

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

package bond.lora_connector;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import bond.lora_connector.logger.Log;
import bond.lora_connector.logger.LogWrapper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Messenger mService = null;/** Messenger for communicating with service. */

    boolean mIsBound;/** Flag indicating whether we have called bind on the service. */


    // Layout Views
    private TextView m_text_status;
    private TextView m_text_spp;
    private Switch m_btn_svc;

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG.STR_FROM_TRACKER:
                    Log.i(TAG, "MSG.STR_FROM_TRACKER");
                    m_text_spp.append((String)msg.obj);
                    break;
                case MSG.BT_IS_OFF:
                    Log.i(TAG, "MSG.BT_IS_OFF");
                    m_text_status.append("Bluetooth выключен\n");
                    break;
                case MSG.BT_IS_ON:
                    Log.i(TAG, "MSG.BT_IS_ON");
                    m_text_status.append("Bluetooth включен\n");
                    break;
                case MSG.TRACKER_NOT_CONNECTED:
                    Log.i(TAG, "MSG.TRACKER_NOT_CONNECTED");
                    m_text_status.append("Нет соединения с трекером\n");
                    break;
                case MSG.TRACKER_CONNECTED:
                    Log.i(TAG, "MSG.TRACKER_CONNECTED");
                    m_text_status.append("Трекер подключен\n");
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

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.

            Log.i(TAG, "onServiceConnected");

            mService = new Messenger(service);
            m_text_status.append("Attached.\n");
            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null, MSG.REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                /*msg = Message.obtain(null,
                        MessengerService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);*/
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), "remote_service_connected",
                    Toast.LENGTH_SHORT).show();
        }
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            Log.i(TAG, "onServiceDisconnected");

            mService = null;
            m_text_status.append("Disconnected.\n");
            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), "remote_service_disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.

        Log.i(TAG, "doBindService");

        startService(new Intent(this, LoRa_service.class));

        bindService(new Intent(this,
                LoRa_service.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        m_text_status.append("Binding...\n");
    }

    void doStopService() {

        Log.i(TAG, "doStopService");

        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {

                //if (mIsBound) unbindService(mConnection);

                try {
                    Message msg = Message.obtain(null, MSG.STOP_SERVICE);
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            m_text_status.append("Service Stoping...\n");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initializeLogging();

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_text_status = (TextView) findViewById(R.id.text_status);
        m_text_status.setMovementMethod(new ScrollingMovementMethod());
        m_text_status.append("Start\n");

        m_text_spp = (TextView) findViewById(R.id.text_spp);
        m_text_spp.setMovementMethod(new ScrollingMovementMethod());

        m_btn_svc = (Switch) findViewById(R.id.btn_toggle_srvc);

        m_btn_svc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "m_btn_svc onClick");

                //TextView text_status = (TextView) findViewById(R.id.text_status);

                if(m_btn_svc.isChecked()) {
                    m_text_status.append("Start Service...\n");
                    m_btn_svc.setText("Остановить службу");
                    doBindService();
                }
                else {
                    m_text_status.append("Stop Service...\n");
                    m_btn_svc.setText("Запустить службу");
                    doStopService();
                }

            }
        });

        m_btn_svc.setChecked(isMyServiceRunning(LoRa_service.class));

    };

    /** Create a chain of targets that will receive log data */

    public void initializeLogging() {
        // Wraps Android's native log framework.
        /*LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
*/

        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        Log.i(TAG, "Log Ready");
    }

}

package bond.lora_connector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import bond.lora_connector.logger.Log;
import bond.lora_connector.logger.LogWrapper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Layout Views
    private TextView m_text_status;
    private Switch m_btn_svc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initializeLogging();

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_text_status = (TextView) findViewById(R.id.text_status);
        m_text_status.setText("Start");

        m_btn_svc = (Switch) findViewById(R.id.btn_toggle_srvc);

        m_btn_svc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "m_btn_svc onClick");
                TextView text_status = (TextView) findViewById(R.id.text_status);

                if(((Switch)view).isChecked())
                    text_status.setText("Start");
                else
                    text_status.setText("Stop");

            }
        });
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

package androzic.plugin.tactical_map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import bond.xxxx_connector.R;

/**
 * Created by bond on 29.11.2017.
 */

public class TacticalActivity extends AppCompatActivity {
    private static final String TAG = "TacticalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tactical_main);
    }

}

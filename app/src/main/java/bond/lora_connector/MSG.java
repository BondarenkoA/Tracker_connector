package bond.lora_connector;

/**
 * Created by bond on 26.11.2017.
 */

public interface MSG {

    // Message types sent from the BluetoothChatService Handler
    //public static final int MESSAGE_READ = 2;


    int REGISTER_CLIENT = 1;
    int STOP_SERVICE = 2;
    int STR_TO_TRACKER = 3;
    int STR_FROM_TRACKER = 4;
    int SET_TRACKER_BT_ADR = 5;
    int BT_IS_OFF = 6;
    int BT_IS_ON = 7;
    int TRACKER_NOT_CONNECTED = 8;
    int TRACKER_CONNECTED = 9;
};

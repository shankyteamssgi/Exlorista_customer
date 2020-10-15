package e.a.exlorista_customer;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData().get(auxiliary.FCM_RECEIVEDMESSAGE));
    }

    @Override
    public void onNewToken(String token) {
        Log.i("TOKEN","refreshed token -> "+token);
        /*
        auxiliaryfcmmanager.sendTokenToServer(auxiliary.SERVER_URL+"/fcmTokenManagement.php"
                ,auxiliary.DUMMYVAL_CUSTID
                ,token); doesn't work here for unknown reasons*/
    }

    private void sendNotification(String s) {
    }
}

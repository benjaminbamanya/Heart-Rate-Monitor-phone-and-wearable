package healthmonitor.ehealth.faithworks.watchapplink;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;

//import android.support.v7.app.ActionBarActivity;
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    boolean flag = false;
    String number = "0";

    boolean sendFlag;
    private TextView textView;
    private FirebaseAppIndex firebaseAppIndex;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (textView != null) {
                textView.setText(Integer.toString(msg.what));
                if ((msg.what > 80 || msg.what < 78) && !flag) {
                    flag = true;
                    SmsManager smsManager = SmsManager.getDefault();

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            Log.e("permission", "Permission already granted.");
                            smsManager.sendTextMessage(number, null, "Alert! The owner of the watch might be having a heart attack! The heart beat rate is " + Integer.toString(msg.what), null, null);


                        } else {
                            requestPermission();
                        }
                    }
                    //smsManager.sendTextMessage(number, null, "Alert! The owner of the watch might be having a heart attack! The heart beat rate is "+Integer.toString(msg.what), null, null);
                }
            }

        }
    };

    //deprecated //private GoogleApiClient client;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);


        firebaseAppIndex = FirebaseAppIndex.getInstance();
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.heartbeat);
        Button dugme = (Button) findViewById(R.id.send_sms);
        Button dugme2 = (Button) findViewById(R.id.save);
        Button dugme3 = (Button) findViewById(R.id.change);
        Button dugme4 = (Button) findViewById(R.id.button4);
        final EditText girdi = (EditText) findViewById(R.id.editText);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }


            } else {


                requestPermission();
            }
        }


        dugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "Alert! The owner of the watch might need help!", null, null);


            }
        });
        dugme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = girdi.getText().toString();
                girdi.setEnabled(false);
            }
        });
        dugme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                girdi.setEnabled(true);
            }
        });
        dugme4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
            }
        });
        //Deprecated //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataLayerListenerService.setHandler(handler);
    }

    @Override
    protected void onPause() {
        DataLayerListenerService.setHandler(null);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        //deprecated //client.connect();



        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.example.mhc.sensorapp/http/host/path")
        ); */
        //deprecated// AppIndex.AppIndexApi.start(client, viewAction);

        Indexable recipe = new Indexable.Builder()
                .setName("Main Page")
                .setUrl("android-app://com.example.mhc.sensorapp/http/host/path")
                .build();


        firebaseAppIndex.getInstance().update(recipe);
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        super.onStop();

     /*   Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),

                Uri.parse("android-app://healthmonitor.ehealth.faithworks.watchapplink/http/host/path")
        );




*/
        FirebaseUserActions.getInstance().end(getAction());

        //deprecated //AppIndex.AppIndexApi.end(client, viewAction);
        //deprecated //client.disconnect();
    }

    private Action getAction() {
        return Actions.newView("Main Page", "android-app://healthmonitor.ehealth.faithworks.watchapplink/http/host/path");
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this,
                            "Permission denied", Toast.LENGTH_LONG).show();
                    sendFlag = false;

                }
                break;
        }
    }


}


package healthmonitor.ehealth.faithworks.watchapplink;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class mHealth extends Application {

    private static mHealth sInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        sInstance = this;
    }

}
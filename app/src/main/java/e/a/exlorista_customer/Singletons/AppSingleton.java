package e.a.exlorista_customer.Singletons;

import android.app.Application;
import android.content.Context;

public class AppSingleton extends Application {

    private static AppSingleton instance;

    public static AppSingleton getInstance(){
        if(instance==null){
            instance=new AppSingleton();
        }
        return instance;
    }

    public static Context getContext(){
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

}

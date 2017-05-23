package pro.dreamcode.ideascollector;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import pro.dreamcode.ideascollector.adapters.Filter;

/**
 * Created by migue on 26/04/2017.
 */

public class AppIdeasCollector extends Application {

    private RealmConfiguration realmConf;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        realmConf = new RealmConfiguration.Builder()
                .name("myIdeas.realm")
                .build();
        Realm.setDefaultConfiguration(realmConf);
    }

    public static void save(Context context, int filterOption){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("filter", filterOption);
        editor.apply();;
    }

    public static int load(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int filterOption = preferences.getInt("filter",  Filter.LEAST_TIME_LEFT);
        return filterOption;
    }
}

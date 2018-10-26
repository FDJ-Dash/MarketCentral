package com.freetime_software.marketcentral;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.Locale;

public class MainActivityLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

//        /**
//         * Sets the locale to {@link Locale#ENGLISH}
//         * if the device locale is not {@link Locale#GERMAN}.
//         */
//        public void setLocale() {
//            if (BuildConfig.FLAVOR.equals("flavorWithSwapedLocale")) {
//                Resources res = getResources();
//                DisplayMetrics dm = res.getDisplayMetrics();
//                Configuration conf = res.getConfiguration();
//                if (!conf.locale.equals(Locale.ENGLISH)) {
//                    conf.locale = Locale.SPANISH;
//                    res.updateConfiguration(conf, dm);
//                }
//            }
//        }
    }
}

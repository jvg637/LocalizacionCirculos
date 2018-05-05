package org.example.proyectobase;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by jvg63 on 09/04/2017.
 */

public class Preferencias extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
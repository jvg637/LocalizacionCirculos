package org.example.proyectobase.utils;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextSpeechVelocity {

    public TextToSpeech tts;

    public void inicializaVoz(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    String languageToLoad = "es"; /* your language*/
                    Locale locale = new Locale(languageToLoad);
                    int result = tts.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    } else {
                        tts.setPitch(1.3f);
                        tts.setSpeechRate(1f);
                    }
                }
            }
        });
    }

    public void salidaNumeroAltavoz(String velocidad) {
        //////////Log.d("LAMADA", "VOZ");
        if (velocidad.length() > 0) {
            if (!tts.isSpeaking()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(velocidad, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    tts.speak(velocidad, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }
}

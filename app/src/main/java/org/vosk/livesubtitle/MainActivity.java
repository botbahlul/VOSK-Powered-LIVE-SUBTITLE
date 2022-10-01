package org.vosk.livesubtitle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.util.DisplayMetrics;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;

public class MainActivity extends BaseActivity {

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static AudioManager audio;
    public static int mStreamVolume;
    private final ArrayList<String> arraylist_models = new ArrayList<>();
    private final ArrayList<String> arraylist_src = new ArrayList<>();
    private final ArrayList<String> arraylist_dst = new ArrayList<>();
    private final ArrayList<String> arraylist_src_languages = new ArrayList<>();
    private final ArrayList<String> arraylist_dst_languages = new ArrayList<>();
    private final Map<String, String> map_model_country = new HashMap<>();
    private final Map<String, String> map_src_country = new HashMap<>();
    private final Map<String, String> map_dst_country = new HashMap<>();
    private Spinner spinner_src_languages;
    private TextView textview_src;
    private Spinner spinner_dst_languages;
    private TextView textview_dst;
    @SuppressLint("StaticFieldLeak")
    public static TextView textview_recognizing;
    @SuppressLint("StaticFieldLeak")
    public static TextView textview_overlaying;
    @SuppressLint("StaticFieldLeak")
    public static TextView textview_debug;
    @SuppressLint("StaticFieldLeak")
    public static TextView textview_debug2;
    @SuppressLint("StaticFieldLeak")
    public static EditText voice_text;
    private DisplayMetrics display;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindowManager().getDefaultDisplay().getMetrics(display);
            float d = display.density;
            DISPLAY_METRIC.DISPLAY_WIDTH = display.widthPixels;
            DISPLAY_METRIC.DISPLAY_HEIGHT = display.heightPixels;
            DISPLAY_METRIC.DISPLAY_DENSITY = d;
            String string_overlaying = "Overlaying=" + OVERLAYING_STATUS.OVERLAYING;
            textview_overlaying.setText(string_overlaying);
            if (OVERLAYING_STATUS.OVERLAYING) {
                stop_create_overlay_mic_button();
                start_create_overlay_mic_button();
                stop_create_overlay_translation_text();
                start_create_overlay_translation_text();
                if (TRANSLATION_TEXT.STRING.length()>0) {
                    create_overlay_translation_text.overlay_translation_text.setText(TRANSLATION_TEXT.STRING);
                }
            }
            if (RECOGNIZING_STATUS.RECOGNIZING) {
                stop_vosk_voice_recognizer();
                start_vosk_voice_recognizer();
            }
            String string_recognizing = "Recognizing=" + RECOGNIZING_STATUS.RECOGNIZING;
            textview_recognizing.setText(string_recognizing);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getWindowManager().getDefaultDisplay().getMetrics(display);
            float d = display.density;
            DISPLAY_METRIC.DISPLAY_WIDTH = display.widthPixels;
            DISPLAY_METRIC.DISPLAY_HEIGHT = display.heightPixels;
            DISPLAY_METRIC.DISPLAY_DENSITY = d;
            String string_overlaying = "Overlaying=" + OVERLAYING_STATUS.OVERLAYING;
            textview_overlaying.setText(string_overlaying);
            if (OVERLAYING_STATUS.OVERLAYING) {
                stop_create_overlay_mic_button();
                start_create_overlay_mic_button();
                stop_create_overlay_translation_text();
                start_create_overlay_translation_text();
                if (TRANSLATION_TEXT.STRING.length()>0) {
                    create_overlay_translation_text.overlay_translation_text.setText(TRANSLATION_TEXT.STRING);
                }
            }
            if (RECOGNIZING_STATUS.RECOGNIZING) {
                stop_vosk_voice_recognizer();
                start_vosk_voice_recognizer();
            }
            String string_recognizing = "Recognizing=" + RECOGNIZING_STATUS.RECOGNIZING;
            textview_recognizing.setText(string_recognizing);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        arraylist_models.add("en-US");
        arraylist_models.add("zh-CN");
        arraylist_models.add("ru-RU");
        arraylist_models.add("fr-FR");
        arraylist_models.add("de-DE");
        arraylist_models.add("es-ES");
        arraylist_models.add("pt-PT");
        arraylist_models.add("tr-TR");
        arraylist_models.add("vi-VN");
        arraylist_models.add("it-IT");
        arraylist_models.add("nl-NL");
        arraylist_models.add("ca-ES");
        arraylist_models.add("fa-IR");
        arraylist_models.add("uk-UA");
        arraylist_models.add("kk-KZ");
        arraylist_models.add("sv-SE");
        arraylist_models.add("ja-JP");
        arraylist_models.add("eo-EO");
        arraylist_models.add("hi-IN");
        arraylist_models.add("cs-CZ");
        arraylist_models.add("pl-PL");

        arraylist_src.add("en");
        arraylist_src.add("zh");
        arraylist_src.add("ru");
        arraylist_src.add("fr");
        arraylist_src.add("de");
        arraylist_src.add("es");
        arraylist_src.add("pt");
        arraylist_src.add("tr");
        arraylist_src.add("vi");
        arraylist_src.add("it");
        arraylist_src.add("nl");
        arraylist_src.add("ca");
        arraylist_src.add("fa");
        arraylist_src.add("uk");
        arraylist_src.add("kk");
        arraylist_src.add("sv");
        arraylist_src.add("ja");
        arraylist_src.add("eo");
        arraylist_src.add("hi");
        arraylist_src.add("cs");
        arraylist_src.add("pl");

        arraylist_src_languages.add("English");
        arraylist_src_languages.add("Chinese");
        arraylist_src_languages.add("Russian");
        arraylist_src_languages.add("French");
        arraylist_src_languages.add("German");
        arraylist_src_languages.add("Spanish");
        arraylist_src_languages.add("Portuguese");
        arraylist_src_languages.add("Turkish");
        arraylist_src_languages.add("Vietnamese");
        arraylist_src_languages.add("Italian");
        arraylist_src_languages.add("Dutch");
        arraylist_src_languages.add("Catalan");
        arraylist_src_languages.add("Persian");
        arraylist_src_languages.add("Ukrainian");
        arraylist_src_languages.add("Kazakh");
        arraylist_src_languages.add("Swedish");
        arraylist_src_languages.add("Japanese");
        arraylist_src_languages.add("Esperanto");
        arraylist_src_languages.add("Hindi");
        arraylist_src_languages.add("Czech");
        arraylist_src_languages.add("Polish");

        for (int i=0;i<arraylist_src_languages.size();i++) {
            map_model_country.put(arraylist_src_languages.get(i), arraylist_models.get(i));
        }

        for (int i=0;i<arraylist_src_languages.size();i++) {
            map_src_country.put(arraylist_src_languages.get(i), arraylist_src.get(i));
        }

        arraylist_dst.add("af");
        arraylist_dst.add("ar");
        arraylist_dst.add("be");
        arraylist_dst.add("bg");
        arraylist_dst.add("bn");
        arraylist_dst.add("ca");
        arraylist_dst.add("cs");
        arraylist_dst.add("cy");
        arraylist_dst.add("da");
        arraylist_dst.add("de");
        arraylist_dst.add("el");
        arraylist_dst.add("en");
        arraylist_dst.add("eo");
        arraylist_dst.add("es");
        arraylist_dst.add("et");
        arraylist_dst.add("fa");
        arraylist_dst.add("fi");
        arraylist_dst.add("fr");
        arraylist_dst.add("ga");
        arraylist_dst.add("gl");
        arraylist_dst.add("gu");
        arraylist_dst.add("he");
        arraylist_dst.add("hi");
        arraylist_dst.add("hr");
        arraylist_dst.add("ht");
        arraylist_dst.add("hu");
        arraylist_dst.add("id");
        arraylist_dst.add("is");
        arraylist_dst.add("it");
        arraylist_dst.add("ja");
        arraylist_dst.add("ka");
        arraylist_dst.add("kn");
        arraylist_dst.add("ko");
        arraylist_dst.add("lt");
        arraylist_dst.add("lv");
        arraylist_dst.add("mk");
        arraylist_dst.add("mr");
        arraylist_dst.add("ms");
        arraylist_dst.add("mt");
        arraylist_dst.add("nl");
        arraylist_dst.add("no");
        arraylist_dst.add("pl");
        arraylist_dst.add("pt");
        arraylist_dst.add("ro");
        arraylist_dst.add("ru");
        arraylist_dst.add("sk");
        arraylist_dst.add("sl");
        arraylist_dst.add("sq");
        arraylist_dst.add("sv");
        arraylist_dst.add("sw");
        arraylist_dst.add("ta");
        arraylist_dst.add("te");
        arraylist_dst.add("th");
        arraylist_dst.add("tl");
        arraylist_dst.add("tr");
        arraylist_dst.add("uk");
        arraylist_dst.add("ur");
        arraylist_dst.add("vi");
        arraylist_dst.add("zh");

        arraylist_dst_languages.add("Afrikaans");
        arraylist_dst_languages.add("Arabic");
        arraylist_dst_languages.add("Belarusian");
        arraylist_dst_languages.add("Bulgarian");
        arraylist_dst_languages.add("Bengali");
        arraylist_dst_languages.add("Catalan");
        arraylist_dst_languages.add("Czech");
        arraylist_dst_languages.add("Welsh");
        arraylist_dst_languages.add("Danish");
        arraylist_dst_languages.add("German");
        arraylist_dst_languages.add("Greek");
        arraylist_dst_languages.add("English");
        arraylist_dst_languages.add("Esperanto");
        arraylist_dst_languages.add("Spanish");
        arraylist_dst_languages.add("Estonian");
        arraylist_dst_languages.add("Persian");
        arraylist_dst_languages.add("Finnish");
        arraylist_dst_languages.add("French");
        arraylist_dst_languages.add("Irish");
        arraylist_dst_languages.add("Galician");
        arraylist_dst_languages.add("Gujarati");
        arraylist_dst_languages.add("Hebrew");
        arraylist_dst_languages.add("Hindi");
        arraylist_dst_languages.add("Croatian");
        arraylist_dst_languages.add("Haitian");
        arraylist_dst_languages.add("Hungarian");
        arraylist_dst_languages.add("Indonesian");
        arraylist_dst_languages.add("Icelandic");
        arraylist_dst_languages.add("Italian");
        arraylist_dst_languages.add("Japanese");
        arraylist_dst_languages.add("Georgian");
        arraylist_dst_languages.add("Kannada");
        arraylist_dst_languages.add("Korean");
        arraylist_dst_languages.add("Lithuania");
        arraylist_dst_languages.add("Latvian");
        arraylist_dst_languages.add("Macedonian");
        arraylist_dst_languages.add("Marathi");
        arraylist_dst_languages.add("Malay");
        arraylist_dst_languages.add("Maltese");
        arraylist_dst_languages.add("Dutch");
        arraylist_dst_languages.add("Norwegia");
        arraylist_dst_languages.add("Polish");
        arraylist_dst_languages.add("Portuguese");
        arraylist_dst_languages.add("Romanian");
        arraylist_dst_languages.add("Russian");
        arraylist_dst_languages.add("Slovak");
        arraylist_dst_languages.add("Slovenian");
        arraylist_dst_languages.add("Albanian");
        arraylist_dst_languages.add("Swedish");
        arraylist_dst_languages.add("Swahili");
        arraylist_dst_languages.add("Tamil");
        arraylist_dst_languages.add("Telugu");
        arraylist_dst_languages.add("Thai");
        arraylist_dst_languages.add("Tagalog");
        arraylist_dst_languages.add("Turkish");
        arraylist_dst_languages.add("Ukrainian");
        arraylist_dst_languages.add("Urdu");
        arraylist_dst_languages.add("Vietnamese");
        arraylist_dst_languages.add("Chinese");

        for (int i=0;i<arraylist_dst_languages.size();i++) {
            map_dst_country.put(arraylist_dst_languages.get(i), arraylist_dst.get(i));
        }

        spinner_src_languages = findViewById(R.id.spinner_src_languages);
        setup_src_spinner(arraylist_src_languages);
        textview_src = findViewById(R.id.textview_src);
        spinner_dst_languages = findViewById(R.id.spinner_dst_languages);
        setup_dst_spinner(arraylist_dst_languages);
        textview_dst = findViewById(R.id.textview_dst);
        textview_recognizing = findViewById(R.id.textview_recognizing);
        textview_overlaying = findViewById(R.id.textview_overlaying);
        Button button_toggle_overlay = findViewById(R.id.button_toggle_overlay);
        textview_debug = findViewById(R.id.textview_debug);
        textview_debug2 = findViewById(R.id.textview_debug2);
        voice_text = findViewById(R.id.voice_text);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mStreamVolume = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        VOICE_TEXT.STRING = "";
        TRANSLATION_TEXT.STRING = "";

        display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        float d = display.density;
        DISPLAY_METRIC.DISPLAY_WIDTH = display.widthPixels;
        DISPLAY_METRIC.DISPLAY_HEIGHT = display.heightPixels;
        DISPLAY_METRIC.DISPLAY_DENSITY = d;

        RECOGNIZING_STATUS.RECOGNIZING = false;
        String string_recognizing = "Recognizing=" + RECOGNIZING_STATUS.RECOGNIZING;
        textview_recognizing.setText(string_recognizing);

        OVERLAYING_STATUS.OVERLAYING = false;
        String string_overlaying = "Overlaying=" + OVERLAYING_STATUS.OVERLAYING;
        textview_overlaying.setText(string_overlaying);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        }

        checkRecordAudioPermission();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        spinner_src_languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stop_vosk_voice_recognizer();
                if (RECOGNIZING_STATUS.RECOGNIZING) start_vosk_voice_recognizer();
                stop_create_overlay_translation_text();
                if (OVERLAYING_STATUS.OVERLAYING) start_create_overlay_translation_text();
                String src_country = spinner_src_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.MODEL = map_model_country.get(src_country);
                LANGUAGE_PREFS.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE_PREFS.SRC);
                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE_PREFS.DST);
                check_dictionary();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                String src_country = spinner_src_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.MODEL = map_model_country.get(src_country);
                LANGUAGE_PREFS.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE_PREFS.SRC);
                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE_PREFS.DST);
            }
        });

        spinner_dst_languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stop_vosk_voice_recognizer();
                if (RECOGNIZING_STATUS.RECOGNIZING) start_vosk_voice_recognizer();
                stop_create_overlay_translation_text();
                if (OVERLAYING_STATUS.OVERLAYING) start_create_overlay_translation_text();
                String src_country = spinner_src_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE_PREFS.SRC);
                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE_PREFS.DST);
                check_dictionary();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                String src_country = spinner_src_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE_PREFS.SRC);
                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE_PREFS.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE_PREFS.DST);
            }
        });

        button_toggle_overlay.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    checkDrawOverlayPermission();
                    OVERLAYING_STATUS.OVERLAYING = !OVERLAYING_STATUS.OVERLAYING;
                    String string_overlaying = "Overlaying=" + OVERLAYING_STATUS.OVERLAYING;
                    textview_overlaying.setText(string_overlaying);
                    if (OVERLAYING_STATUS.OVERLAYING) {
                        start_create_overlay_mic_button();
                        start_create_overlay_translation_text();
                    } else {
                        VOICE_TEXT.STRING = "";
                        TRANSLATION_TEXT.STRING = "";
                        voice_text.setText("");
                        create_overlay_translation_text.overlay_translation_text.setText("");
                        stop_create_overlay_translation_text();
                        stop_create_overlay_mic_button();
                        stop_vosk_voice_recognizer();
                        RECOGNIZING_STATUS.RECOGNIZING = false;
                        String string_recognizing = "Recognizing=" + RECOGNIZING_STATUS.RECOGNIZING;
                        textview_recognizing.setText(string_recognizing);
                    }
                }
                return false;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop_create_overlay_translation_text();
        stop_create_overlay_mic_button();
        stop_vosk_voice_recognizer();
    }

    public void setup_src_spinner(ArrayList<String> supported_languages)
    {
        Collections.sort(supported_languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_textview_align, supported_languages);
        adapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner_src_languages.setAdapter(adapter);
        spinner_src_languages.setSelection(supported_languages.indexOf("English"));
    }

    public void setup_dst_spinner(ArrayList<String> supported_languages)
    {
        Collections.sort(supported_languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_textview_align, supported_languages);
        adapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner_dst_languages.setAdapter(adapter);
        spinner_dst_languages.setSelection(supported_languages.indexOf("Indonesian"));
    }

    private void checkRecordAudioPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
    }

    private void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }
    }

    /*private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }*/

    private void start_create_overlay_mic_button() {
        Intent i = new Intent(this, create_overlay_mic_button.class);
        startService(i);
    }

    private void stop_create_overlay_mic_button() {
        stopService(new Intent(this, create_overlay_mic_button.class));
    }

    private void start_create_overlay_translation_text() {
        Intent i = new Intent(this, create_overlay_translation_text.class);
        startService(i);
    }

    private void stop_create_overlay_translation_text() {
        stopService(new Intent(this, create_overlay_translation_text.class));
    }

    private void start_vosk_voice_recognizer() {
        Intent i = new Intent(this, VoskVoiceRecognizer.class);
        startService(i);
    }

    private void stop_vosk_voice_recognizer() {
        stopService(new Intent(this, VoskVoiceRecognizer.class));
    }

    private void check_dictionary() {
        String string1 = "/no_backup/com.google.mlkit.translate.models/" + textview_src.getText() + "_" + textview_dst.getText();
        String string2 = "/no_backup/com.google.mlkit.translate.models/" + textview_dst.getText() + "_" + textview_src.getText();
        File mdir1 = new File(Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + string1);
        File mdir2 = new File(Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + string2);
        String downloaded_status_message;
        if (mdir1.exists() || mdir2.exists()) {
            DICTIONARY_MODEL_DOWNLOAD_STATUS.DOWNLOADED = true;
            downloaded_status_message = "Dictionary is ready to use";
            textview_debug2.setText(downloaded_status_message);
        }

        if (!(mdir1.exists())) {
            if (!(mdir2.exists())) {
                DICTIONARY_MODEL_DOWNLOAD_STATUS.DOWNLOADED = false;
                downloaded_status_message = "Preparing dictionary, please be patient";
                textview_debug2.setText(downloaded_status_message);
            }
        }
    }

}

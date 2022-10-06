package org.vosk.livesubtitle;

import static android.text.TextUtils.substring;

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
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.util.DisplayMetrics;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;

public class MainActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static AudioManager audio;
    public static int mStreamVolume;
    private final ArrayList<String> arraylist_models = new ArrayList<>();
    private final ArrayList<String> arraylist_models_URL = new ArrayList<>();
    private final ArrayList<String> arraylist_src = new ArrayList<>();
    private final ArrayList<String> arraylist_dst = new ArrayList<>();
    private final ArrayList<String> arraylist_src_languages = new ArrayList<>();
    private final ArrayList<String> arraylist_dst_languages = new ArrayList<>();
    private final Map<String, String> map_model_country = new HashMap<>();
    private final Map<String, String> map_src_country = new HashMap<>();
    private final Map<String, String> map_dst_country = new HashMap<>();
    private final Map<String, String> map_country_models_URL = new HashMap<>();
    private Spinner spinner_src_languages;
    private Button button_delete_model;
    private Button button_download_model;
    private ProgressBar mProgressBar;
    private TextView textview_modelURL;
    private TextView textview_model_zip_file;
    private TextView textview_filesize;
    private TextView textview_bytesdownloaded;
    private TextView textview_downloaded_status;
    private TextView textview_extracted_folder;
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
    private Button button_toggle_overlay;

    private String string_en_src_folder;
    private String string_en_dst_folder;
    private String string_src_en_folder;
    private String string_dst_en_folder;
    private File file_en_src_folder;
    private File file_en_dst_folder;
    private File file_src_en_folder;
    private File file_dst_en_folder;
    private String mlkit_status_message = "";

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
                if (TRANSLATION_TEXT.STRING.length() > 0) {
                    create_overlay_translation_text.overlay_translation_text.setText(TRANSLATION_TEXT.STRING);
                }
            }
            if (RECOGNIZING_STATUS.RECOGNIZING) {
                stop_vosk_voice_recognizer();
                start_vosk_voice_recognizer();
            }
            String string_recognizing = "Recognizing=" + RECOGNIZING_STATUS.RECOGNIZING;
            textview_recognizing.setText(string_recognizing);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
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
                if (TRANSLATION_TEXT.STRING.length() > 0) {
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

        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-cn-0.22.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-ru-0.22.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-de-0.15.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-es-0.42.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-pt-0.3.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-tr-0.3.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-vn-0.3.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-it-0.22.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-nl-0.22.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-ca-0.4.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-fa-0.5.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-uk-v3-small.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-kz-0.15.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-sv-rhasspy-0.15.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-ja-0.22.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-eo-0.42.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-hi-0.22.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-cs-0.4-rhasspy.zip");
        arraylist_models_URL.add("https://alphacephei.com/vosk/models/vosk-model-small-pl-0.22.zip");

        for (int i = 0; i < arraylist_src_languages.size(); i++) {
            map_model_country.put(arraylist_src_languages.get(i), arraylist_models.get(i));
        }

        for (int i = 0; i < arraylist_src_languages.size(); i++) {
            map_src_country.put(arraylist_src_languages.get(i), arraylist_src.get(i));
        }

        for (int i = 0; i < arraylist_src_languages.size(); i++) {
            map_country_models_URL.put(arraylist_src_languages.get(i), arraylist_models_URL.get(i));
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

        for (int i = 0; i < arraylist_dst_languages.size(); i++) {
            map_dst_country.put(arraylist_dst_languages.get(i), arraylist_dst.get(i));
        }

        spinner_src_languages = findViewById(R.id.spinner_src_languages);
        setup_src_spinner(arraylist_src_languages);
        button_delete_model = findViewById(R.id.button_delete_model);
        button_download_model = findViewById(R.id.button_download_model);
        mProgressBar = findViewById(R.id.mProgressBar);
        textview_modelURL = findViewById(R.id.textview_modelURL);
        textview_model_zip_file = findViewById(R.id.textview_model_zip_file);
        textview_filesize = findViewById(R.id.textview_filesize);
        textview_bytesdownloaded = findViewById(R.id.textview_bytesdownloaded);
        textview_downloaded_status = findViewById(R.id.textview_downloaded_status);
        textview_extracted_folder = findViewById(R.id.textview_extracted_folder);
        textview_src = findViewById(R.id.textview_src);
        spinner_dst_languages = findViewById(R.id.spinner_dst_languages);
        setup_dst_spinner(arraylist_dst_languages);
        textview_dst = findViewById(R.id.textview_dst);
        textview_recognizing = findViewById(R.id.textview_recognizing);
        textview_overlaying = findViewById(R.id.textview_overlaying);
        button_toggle_overlay = findViewById(R.id.button_toggle_overlay);
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

        VOSK_MODEL.DOWNLOADED = false;
        MLKIT_DICTIONARY.READY = false;

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
                LANGUAGE.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE.SRC);

                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE.DST);

                VOSK_MODEL.ISO_CODE = map_model_country.get(src_country);
                VOSK_MODEL.URL_ADDRESS = map_country_models_URL.get(src_country);
                if (VOSK_MODEL.URL_ADDRESS != null) {
                    VOSK_MODEL.ZIP_FILENAME = substring(VOSK_MODEL.URL_ADDRESS, VOSK_MODEL.URL_ADDRESS.lastIndexOf('/') + 1, VOSK_MODEL.URL_ADDRESS.length());
                }
                VOSK_MODEL.ZIP_FILE_COMPLETE_PATH = getExternalFilesDir(null).getAbsolutePath() + "/" + VOSK_MODEL.ZIP_FILENAME;
                VOSK_MODEL.EXTRACTED_PATH = getExternalFilesDir(null).getAbsolutePath() + "/" + "downloaded";
                String string_url = "Model URL = " + VOSK_MODEL.URL_ADDRESS;
                textview_modelURL.setText(string_url);
                String string_zip_path = "Save as = " + VOSK_MODEL.ZIP_FILE_COMPLETE_PATH;
                textview_model_zip_file.setText(string_zip_path);
                check_vosk_downloaded_model(VOSK_MODEL.ISO_CODE);

                string_en_src_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + "en" + "_" + textview_src.getText();
                string_en_dst_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + "en" + "_" + textview_dst.getText();
                string_src_en_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + textview_src.getText() + "_" + "en" ;
                string_dst_en_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + textview_dst.getText() + "_" + "en" ;
                file_en_src_folder = new File(string_en_src_folder);
                file_en_dst_folder = new File(string_en_dst_folder);
                file_src_en_folder = new File(string_src_en_folder);
                file_dst_en_folder = new File(string_dst_en_folder);
                check_mlkit_dictionary();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                String src_country = spinner_src_languages.getSelectedItem().toString();
                LANGUAGE.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE.SRC);

                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE.DST);

                VOSK_MODEL.ISO_CODE = map_model_country.get(src_country);
                VOSK_MODEL.URL_ADDRESS = map_country_models_URL.get(src_country);
                if (VOSK_MODEL.URL_ADDRESS != null) {
                    VOSK_MODEL.ZIP_FILENAME = substring(VOSK_MODEL.URL_ADDRESS, VOSK_MODEL.URL_ADDRESS.lastIndexOf('/') + 1, VOSK_MODEL.URL_ADDRESS.length());
                }
                VOSK_MODEL.ZIP_FILE_COMPLETE_PATH = getExternalFilesDir(null).getAbsolutePath() + "/" + VOSK_MODEL.ZIP_FILENAME;
                VOSK_MODEL.EXTRACTED_PATH = getExternalFilesDir(null).getAbsolutePath() + "/" + "downloaded";
                String string_url = "Model URL = " + VOSK_MODEL.URL_ADDRESS;
                textview_modelURL.setText(string_url);
                String string_zip_path = "Save as = " + VOSK_MODEL.ZIP_FILE_COMPLETE_PATH;
                textview_model_zip_file.setText(string_zip_path);
                check_vosk_downloaded_model(VOSK_MODEL.ISO_CODE);
            }
        });

        spinner_dst_languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stop_vosk_voice_recognizer();
                if (RECOGNIZING_STATUS.RECOGNIZING) start_vosk_voice_recognizer();

                stop_create_overlay_translation_text();
                if (OVERLAYING_STATUS.OVERLAYING) start_create_overlay_translation_text();

                String src_country = spinner_src_languages.getSelectedItem().toString();
                LANGUAGE.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE.SRC);
                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE.DST);

                string_en_src_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + "en" + "_" + textview_src.getText();
                string_en_dst_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + "en" + "_" + textview_dst.getText();
                string_src_en_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + textview_src.getText() + "_" + "en" ;
                string_dst_en_folder = Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName() + "/no_backup/com.google.mlkit.translate.models/" + textview_dst.getText() + "_" + "en" ;
                file_en_src_folder = new File(string_en_src_folder);
                file_en_dst_folder = new File(string_en_dst_folder);
                file_src_en_folder = new File(string_src_en_folder);
                file_dst_en_folder = new File(string_dst_en_folder);
                check_mlkit_dictionary();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                String src_country = spinner_src_languages.getSelectedItem().toString();
                LANGUAGE.SRC = map_src_country.get(src_country);
                textview_src.setText(LANGUAGE.SRC);

                String dst_country = spinner_dst_languages.getSelectedItem().toString();
                LANGUAGE.DST = map_dst_country.get(dst_country);
                textview_dst.setText(LANGUAGE.DST);
            }
        });

        button_delete_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File ddir = new File(VOSK_MODEL.USED_PATH);
                if (ddir.exists()) {
                    deleteRecursively(ddir);
                    String msg = ddir + "deleted";
                    toast(msg);
                }
                check_vosk_downloaded_model(VOSK_MODEL.ISO_CODE);
            }
        });

        button_download_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!VOSK_MODEL.DOWNLOADED) {
                    File edir = new File(getApplicationContext().getExternalFilesDir(null), "downloaded");
                    if (!(edir.exists())) {
                        edir.mkdir();
                    }

                    mProgressBar.setVisibility(View.VISIBLE);
                    textview_filesize.setVisibility(View.VISIBLE);
                    textview_bytesdownloaded.setVisibility(View.VISIBLE);

                    new Thread(new Runnable() {
                        public void run() {
                            DownloadModel(VOSK_MODEL.URL_ADDRESS);
                        }
                    }).start();

                } else {
                    String msg = "Model has been downloaded, no need to download it again";
                    toast(msg);
                }
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

    public void setup_src_spinner(ArrayList<String> supported_languages) {
        Collections.sort(supported_languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_textview_align, supported_languages);
        adapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner_src_languages.setAdapter(adapter);
        spinner_src_languages.setSelection(supported_languages.indexOf("English"));
    }

    public void setup_dst_spinner(ArrayList<String> supported_languages) {
        Collections.sort(supported_languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_textview_align, supported_languages);
        adapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner_dst_languages.setAdapter(adapter);
        spinner_dst_languages.setSelection(supported_languages.indexOf("Indonesian"));
    }

    private void checkRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
    }

    private void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }
    }

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

    private void check_mlkit_dictionary() {
        if (Objects.equals(textview_src.getText(), textview_dst.getText())) {
            MLKIT_DICTIONARY.READY = true;
            mlkit_status_message = "";
        }
        if (Objects.equals(textview_src.getText(), "en")) {
            if (file_en_dst_folder.exists() || file_dst_en_folder.exists()) {
                MLKIT_DICTIONARY.READY = true;
                mlkit_status_message = "Dictionary is ready";
            } else {
                MLKIT_DICTIONARY.READY = false;
                mlkit_status_message = "Dictionary is not ready";
            }
        }
        if (Objects.equals(textview_dst.getText(), "en")) {
            if (file_en_src_folder.exists() || file_src_en_folder.exists()) {
                MLKIT_DICTIONARY.READY = true;
                mlkit_status_message = "Dictionary is ready";
            } else {
                MLKIT_DICTIONARY.READY = false;
                mlkit_status_message = "Dictionary is not ready";
            }
        }
        if (!(Objects.equals(textview_src.getText(), "en")) && !(Objects.equals(textview_dst.getText(), "en"))) {
            if ((file_en_src_folder.exists() || file_src_en_folder.exists()) && (file_en_dst_folder.exists()) || file_dst_en_folder.exists()) {
                MLKIT_DICTIONARY.READY = true;
                mlkit_status_message = "Dictionary is ready";
            }
            else if ((file_en_src_folder.exists() || file_src_en_folder.exists()) && !file_dst_en_folder.exists() && !file_en_dst_folder.exists()) {
                MLKIT_DICTIONARY.READY = false;
                mlkit_status_message = "Dictionary is not ready";
            }
            else if ((file_en_dst_folder.exists() || file_dst_en_folder.exists()) && !file_src_en_folder.exists() && !file_en_src_folder.exists()) {
                MLKIT_DICTIONARY.READY = false;
                mlkit_status_message = "Dictionary is not ready";
            }
            else if (!file_en_src_folder.exists() && !file_en_dst_folder.exists() && !file_src_en_folder.exists() && !file_dst_en_folder.exists()) {
                MLKIT_DICTIONARY.READY = false;
                mlkit_status_message = "Dictionary is not ready";
            }
        }
        textview_debug2.setText(mlkit_status_message);
    }

    public void DownloadModel (String models_URL) {
        File dir = new File(String.valueOf(this.getExternalFilesDir(null)));
        if(!(dir.exists())){
            dir.mkdir();
        }

        File edir = new File(this.getExternalFilesDir(null), "downloaded");
        if(!(edir.exists())){
            edir.mkdir();
        }

        ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            int count;

            @Override
            public void run() {
                try {
                    URL url = new URL(models_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        String r ="Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                        textview_debug.setText(r);
                    } else {
                        String r = "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                        textview_debug.setText(r);
                        if (connection.getContentLength() > 0) {
                            int lengthOfFile = connection.getContentLength();
                            String string_filesize = "File size=" + lengthOfFile + " bytes";
                            textview_filesize.setText(string_filesize);
                            InputStream input = connection.getInputStream();
                            OutputStream output = new FileOutputStream(VOSK_MODEL.ZIP_FILE_COMPLETE_PATH);

                            byte[] data = new byte[1024];
                            long total = 0;
                            while ((count = input.read(data)) != -1) {
                                total += count;
                                String string_bytes_received = "Bytes received=" + total + " bytes";
                                textview_bytesdownloaded.setText(string_bytes_received);
                                mProgressBar.setIndeterminate(false);
                                mProgressBar.setMax(100);
                                if (lengthOfFile > 0) {
                                    publishProgress((int) (total * 100 / lengthOfFile));
                                }
                                output.write(data, 0, count);
                            }
                            output.flush();
                            output.close();
                            input.close();
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //UI Thread work here
                            mProgressBar.setVisibility(View.GONE);
                            textview_filesize.setVisibility(View.GONE);
                            textview_bytesdownloaded.setVisibility(View.GONE);
                            VOSK_MODEL.DOWNLOADED = true;
                            Decompress df = new Decompress(VOSK_MODEL.ZIP_FILE_COMPLETE_PATH, VOSK_MODEL.EXTRACTED_PATH);
                            df.unzip();
                            File oldfolder = new File(VOSK_MODEL.EXTRACTED_PATH, VOSK_MODEL.ZIP_FILENAME.replace(".zip", ""));
                            File newfolder = new File(VOSK_MODEL.EXTRACTED_PATH, VOSK_MODEL.ISO_CODE);
                            oldfolder.renameTo(newfolder);
                            File ddir = new File(VOSK_MODEL.ZIP_FILE_COMPLETE_PATH);
                            deleteRecursively(ddir);
                            VOSK_MODEL.USED_PATH = VOSK_MODEL.EXTRACTED_PATH + "/" + VOSK_MODEL.ISO_CODE;
                            String r = "";
                            textview_debug.setText(r);
                            check_vosk_downloaded_model(VOSK_MODEL.ISO_CODE);
                        }
                    });
                } catch (Exception e) {
                    check_vosk_downloaded_model(VOSK_MODEL.ISO_CODE);
                    toast(e.getMessage());
                    //textview_debug.setText(e.getMessage());
                }
            }
        });
    }

    private void publishProgress(Integer... progress) {
        mProgressBar.setProgress(progress[0]);
    }

    private void check_vosk_downloaded_model(String string_model) {
        File edir = new File(VOSK_MODEL.EXTRACTED_PATH + "/" + string_model);
        if (Objects.equals(VOSK_MODEL.ISO_CODE, "en-US")) {
            button_delete_model.setVisibility(View.GONE);
            button_download_model.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            textview_modelURL.setVisibility(View.GONE);
            textview_model_zip_file.setVisibility(View.GONE);
            textview_filesize.setVisibility(View.GONE);
            textview_bytesdownloaded.setVisibility(View.GONE);
            textview_downloaded_status.setVisibility(View.GONE);
        } else {
            if (edir.exists()) {
                VOSK_MODEL.DOWNLOADED = true;
                VOSK_MODEL.USED_PATH = VOSK_MODEL.EXTRACTED_PATH + "/" + string_model;
                String downloaded_status = "Model has been downloaded";
                textview_downloaded_status.setText(downloaded_status);
                button_delete_model.setVisibility(View.VISIBLE);
                button_download_model.setVisibility(View.GONE);
                textview_modelURL.setVisibility(View.GONE);
                textview_model_zip_file.setVisibility(View.GONE);
                textview_filesize.setVisibility(View.GONE);
                textview_bytesdownloaded.setVisibility(View.GONE);
                textview_downloaded_status.setVisibility(View.GONE);
                textview_extracted_folder.setVisibility(View.VISIBLE);
                textview_extracted_folder.setText(VOSK_MODEL.USED_PATH);
            } else {
                VOSK_MODEL.DOWNLOADED = false;
                VOSK_MODEL.USED_PATH = "";
                String downloaded_status = "Model is not downloaded yet";
                textview_downloaded_status.setText(downloaded_status);
                button_delete_model.setVisibility(View.GONE);
                button_download_model.setVisibility(View.VISIBLE);
                textview_modelURL.setVisibility(View.VISIBLE);
                textview_model_zip_file.setVisibility(View.VISIBLE);
                textview_extracted_folder.setVisibility(View.VISIBLE);
                textview_extracted_folder.setText(VOSK_MODEL.USED_PATH);
            }
        }
    }

    void deleteRecursively(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles()))
                deleteRecursively(child);
        fileOrDirectory.delete();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}


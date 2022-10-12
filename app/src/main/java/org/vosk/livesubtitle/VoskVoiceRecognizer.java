package org.vosk.livesubtitle;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class VoskVoiceRecognizer extends Service implements RecognitionListener {
    public VoskVoiceRecognizer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private Translator translator;
    private String results;
    private String downloaded_status_message;

    @Override
    public void onCreate() {
        super.onCreate();
        LibVosk.setLogLevel(LogLevel.INFO);

        int h;
        if (Objects.equals(LANGUAGE.SRC, "ja") || Objects.equals(LANGUAGE.SRC, "zh-Hans") || Objects.equals(LANGUAGE.SRC, "zh-Hant")) {
            h = 122;
        }
        else {
            h = 109;
        }
        MainActivity.voice_text.setHeight((int) (h * getResources().getDisplayMetrics().density));

        initModel(LANGUAGE.MODEL);
        if (RECOGNIZING_STATUS.RECOGNIZING) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (VOICE_TEXT.STRING != null) {
                        get_translation(VOICE_TEXT.STRING, LANGUAGE.SRC, LANGUAGE.DST);
                    }
                }
            },0,2000);
            if (VOICE_TEXT.STRING.length() > 0) {
                MainActivity.textview_debug2.setText(downloaded_status_message);
            }
        } else {
            if (translator != null) translator.close();
        }
    }

    private void initModel(String string_model) {
        StorageService.unpack(this, string_model, "model",
                (model) -> {
                    this.model = model;
                    setUiState(STATE_READY);
                    recognizeMicrophone();
                },
                (exception) -> setErrorState("Failed to unpack the model" + exception.getMessage()));
    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (RECOGNIZING_STATUS.RECOGNIZING) {
                speechService.startListening(this);
            } else {
                if (speechService != null) {
                    speechService.stop();
                    speechService.shutdown();
                }
                if (speechStreamService != null) {
                    speechStreamService.stop();
                }
                if (translator != null) translator.close();
                if (translator != null) {
                    translator.close();
                }
                stopSelf();
            }
        }

        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if (RECOGNIZING_STATUS.RECOGNIZING) {
                speechService.startListening(this);
            } else {
                if (speechService != null) {
                    speechService.stop();
                    speechService.shutdown();
                }
                if (speechStreamService != null) {
                    speechStreamService.stop();
                }
                if (translator != null) translator.close();
                if (translator != null) {
                    translator.close();
                }
                stopSelf();
            }
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }
        if (speechStreamService != null) {
            speechStreamService.stop();
        }
        if (translator != null) translator.close();
    }

    @Override
    public void onPartialResult(String hypothesis) {
        if (hypothesis != null) {
            results = (((((hypothesis.replace("text", ""))
                    .replace("{", ""))
                    .replace("}", ""))
                    .replace(":", ""))
                    .replace("partial", ""))
                    .replace("\"", "");
        }
        if (RECOGNIZING_STATUS.RECOGNIZING) {
            VOICE_TEXT.STRING = results.toLowerCase(Locale.forLanguageTag(LANGUAGE.SRC));
            MainActivity.voice_text.setText(VOICE_TEXT.STRING);
            MainActivity.voice_text.setSelection(MainActivity.voice_text.getText().length());
        }
        else {
            VOICE_TEXT.STRING = "";
            MainActivity.voice_text.setText("");
        }
    }

    @Override
    public void onResult(String hypothesis) {
        /*if (hypothesis != null) {
            results = (((((hypothesis.replace("text", ""))
                    .replace("{", ""))
                    .replace("}", ""))
                    .replace(":", ""))
                    .replace("partial", ""))
                    .replace("\"", "");
        }
        if (RECOGNIZING_STATUS.RECOGNIZING) {
            VOICE_TEXT.STRING = results.toLowerCase(Locale.forLanguageTag(LANGUAGE.SRC));
            MainActivity.voice_text.setText(VOICE_TEXT.STRING);
            MainActivity.voice_text.setSelection(MainActivity.voice_text.getText().length());
        }
        else {
            VOICE_TEXT.STRING = "";
            MainActivity.voice_text.setText("");
        }*/
    }

    @Override
    public void onFinalResult(String hypothesis) {
        /*if (hypothesis != null) {
            results = (((((hypothesis.replace("text", ""))
                    .replace("{", ""))
                    .replace("}", ""))
                    .replace(":", ""))
                    .replace("partial", ""))
                    .replace("\"", "");
        }
        if (RECOGNIZING_STATUS.RECOGNIZING) {
            VOICE_TEXT.STRING = results.toLowerCase(Locale.forLanguageTag(LANGUAGE.SRC));
            MainActivity.voice_text.setText(VOICE_TEXT.STRING);
            MainActivity.voice_text.setSelection(MainActivity.voice_text.getText().length());
        }
        else {
            VOICE_TEXT.STRING = "";
            MainActivity.voice_text.setText("");
        }*/
        /*setUiState(STATE_DONE);
        if (speechStreamService != null) {
            speechStreamService = null;
        }*/
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
        speechService.startListening(this);
    }

    @Override
    public void onTimeout() {
        setUiState(STATE_DONE);
        speechService.startListening(this);
    }

    private void setUiState(int state) {
        switch (state) {
            case STATE_START:
                MainActivity.textview_debug.setText(R.string.preparing);
                MainActivity.textview_debug.setMovementMethod(new ScrollingMovementMethod());
                break;
            case STATE_READY:
                MainActivity.textview_debug.setText(R.string.ready);
                break;
            case STATE_DONE:
                break;
            case STATE_FILE:
                MainActivity.textview_debug.setText(getString(R.string.starting));
                break;
            case STATE_MIC:
                MainActivity.textview_debug.setText(R.string.say_something);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private void setErrorState(String message) {

        MainActivity.textview_debug.setText(message);
        speechService.startListening(this);
    }

    private void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE);
            speechService.stop();
            speechService = null;
            String string_recognizing = "Recognizing=" + RECOGNIZING_STATUS.RECOGNIZING;
            MainActivity.textview_recognizing.setText(string_recognizing);
            String string_overlaying = "Overlaying=" + OVERLAYING_STATUS.OVERLAYING;
            MainActivity.textview_overlaying.setText(string_overlaying);
            string_overlaying = "Overlaying=" + OVERLAYING_STATUS.OVERLAYING;
            MainActivity.textview_overlaying.setText(string_overlaying);
        } else {
            setUiState(STATE_MIC);
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void get_translation(String text, String textFrom, String textTo) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(textFrom)
                .setTargetLanguage(textTo)
                .build();

        translator = Translation.getClient(options);

        if (!MLKIT_DICTIONARY.READY) {
            DownloadConditions conditions = new DownloadConditions.Builder().build();
            translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(unused -> MLKIT_DICTIONARY.READY = true)
                    .addOnFailureListener(e -> {});
        }

        if (MLKIT_DICTIONARY.READY) {
            downloaded_status_message = "Dictionary is ready";
            MainActivity.textview_debug2.setText(downloaded_status_message);
            translator.translate(text).addOnSuccessListener(s -> {
                TRANSLATION_TEXT.STRING = s;
                if (RECOGNIZING_STATUS.RECOGNIZING) {
                    if (TRANSLATION_TEXT.STRING.length() == 0) {
                        create_overlay_translation_text.overlay_translation_text.setVisibility(View.INVISIBLE);
                        create_overlay_translation_text.overlay_translation_text_container.setVisibility(View.INVISIBLE);
                    } else {
                        create_overlay_translation_text.overlay_translation_text_container.setVisibility(View.VISIBLE);
                        create_overlay_translation_text.overlay_translation_text.setVisibility(View.VISIBLE);
                        create_overlay_translation_text.overlay_translation_text.setText(TRANSLATION_TEXT.STRING);
                        create_overlay_translation_text.overlay_translation_text.setSelection(create_overlay_translation_text.overlay_translation_text.getText().length());
                    }
                } else {
                    create_overlay_translation_text.overlay_translation_text.setVisibility(View.INVISIBLE);
                    create_overlay_translation_text.overlay_translation_text_container.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(e -> {});
        }
    }

    private void toast(String message) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

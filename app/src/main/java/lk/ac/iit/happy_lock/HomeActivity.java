package lk.ac.iit.happy_lock;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapzen.speakerbox.Speakerbox;
import com.skyfishjy.library.RippleBackground;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;
import com.vikramezhil.droidspeech.OnDSPermissionsListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Dulkith on 5/29/18.
 */

public class HomeActivity extends Fragment implements View.OnClickListener, OnDSListener, OnDSPermissionsListener {

    private TextView voiceInput;
    private ImageButton start;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private RippleBackground rippleBackground;
    private DroidSpeech droidSpeech;
    Speakerbox speakerbox;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Vibrator vibrator;

    List<String>
            doorLock = new ArrayList<>(Arrays.asList("close the door","close door","lock door", "lock", "lock my door", "දොර අගුලු දාන්න","දොර වහන්න","கதவை மூடு","பூட்டு கதவு")),
            doorUnlock= new ArrayList<>(Arrays.asList("open the door","open door","unlock door", "unlock", "unlock my door", "දොර අගුලු අරින්න","දොර අරින්න","கதவைத் திற","திறக்க கதவு")),
            bulb1On = new ArrayList<>(Arrays.asList("turn on light one","turn on light 1","on light one","on light 1","turn on bulb one","turn on bulb 1","on bulb one","on bulb 1","turn on first bulb","පලවෙනි බල්බ් එක ඔන් කරන්න","පලවෙනි බල්බ් එක on කරන්න","පලවෙනි ලයිට් එක ඔන් කරන්න","පලවෙනි ලයිට් එක on කරන්න")),
            bulb1Off = new ArrayList<>(Arrays.asList("turn off light one","turn off light 1","off light one","off light 1","turn off bulb one","turn off bulb 1","off bulb one","off bulb 1","turn off first bulb","පලවෙනි බල්බ් එක ඔෆ් කරන්න","පලවෙනි බල්බ් එක off කරන්න","පලවෙනි ලයිට් එක ඔෆ් කරන්න","පලවෙනි ලයිට් එක off කරන්න")),
            bulb2On = new ArrayList<>(Arrays.asList("turn on light one","turn on light 2","on light two","on light 2","turn on bulb two","turn on bulb 2","on bulb two","on bulb 2","turn on second bulb","දෙවෙනි බල්බ් එක ඔන් කරන්න","දෙවෙනි බල්බ් එක on කරන්න","දෙවෙනි ලයිට් එක ඔන් කරන්න","දෙවෙනි ලයිට් එක on කරන්න","දෙවැනි බල්බ් එක ඔන් කරන්න","දෙවැනි බල්බ් එක on කරන්න","දෙවැනි ලයිට් එක ඔන් කරන්න","දෙවැනි ලයිට් එක on කරන්න")),
            bulb2Off = new ArrayList<>(Arrays.asList("turn on light one","turn off light 2","off light two","off light 2","turn off bulb two","turn off bulb 2","off bulb two","off bulb 2","turn off second bulb","දෙවෙනි බල්බ් එක ඔෆ් කරන්න","දෙවෙනි බල්බ් එක off කරන්න","දෙවෙනි ලයිට් එක ඔෆ් කරන්න","දෙවෙනි ලයිට් එක off කරන්න","දෙවැනි බල්බ් එක ඔෆ් කරන්න","දෙවැනි බල්බ් එක off කරන්න","දෙවැනි ලයිට් එක ඔෆ් කරන්න","දෙවැනි ලයිට් එක off කරන්න"));

    int stopListening = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home_layout, container, false);

        voiceInput = v.findViewById(R.id.voiceInput);
        start = v.findViewById(R.id.btnSpeak);
        rippleBackground = v.findViewById(R.id.content_speak);
        rippleBackground.startRippleAnimation();

        // Initializing the droid speech and setting the listener
        droidSpeech = new DroidSpeech(getContext(), null);
        droidSpeech.setOnDroidSpeechListener(this);
        droidSpeech.setShowRecognitionProgressView(true);
        droidSpeech.setOneStepResultVerify(false);
        droidSpeech.setRecognitionProgressMsgColor(Color.WHITE);
        droidSpeech.setOneStepVerifyConfirmTextColor(Color.WHITE);
        droidSpeech.setOneStepVerifyRetryTextColor(Color.WHITE);
        droidSpeech.setListeningMsg("Happy lock listening...");

        int[] colorPallets = new int[]{
                Color.parseColor("#D02D2B"),
                Color.parseColor("#D02D2B"),
                Color.parseColor("#D02D2B"),
                Color.parseColor("#D02D2B"),
                Color.parseColor("#D02D2B")};

        // Setting random color pallets to the recognition progress view
        droidSpeech.setRecognitionProgressViewColors(colorPallets);

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        speakerbox = new Speakerbox(getActivity().getApplication());
        // speakerbox.play("Welcome to happy lock.");

        //droidSpeech.startDroidSpeechRecognition();

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                vibrator.vibrate(100);
                droidSpeech.startDroidSpeechRecognition();
                stopListening = 0;
            }
        });

//        tts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    tts.setLanguage(Locale.UK);
//                }
//            }
//        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        super.onDestroy();


    }

    // MARK: OnClickListener Method

    @Override
    public void onClick(View view) {
//        switch (view.getId())
//        {
//            case R.id.start:
//
//                // Starting droid speech
//               // droidSpeech.startDroidSpeechRecognition();
//
//                // Setting the view visibilities when droid speech is running
//                //start.setVisibility(View.GONE);
//                //stop.setVisibility(View.VISIBLE);
//
//                break;
//
//            case R.id.stop:
//
//                // Closing droid speech
//                droidSpeech.closeDroidSpeechOperations();
//
//                //stop.setVisibility(View.GONE);
//                start.setVisibility(View.VISIBLE);
//
//                break;
//        }
    }


    // MARK: DroidSpeechListener Methods

    @Override
    public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {

        //Log.i(TAG, "Current speech language = " + currentSpeechLanguage);
        //Log.i(TAG, "Supported speech languages = " + supportedSpeechLanguages.toString());

        if (supportedSpeechLanguages.contains("si-LK")) {
            // Setting the droid speech preferred language as tamil if found
            droidSpeech.setPreferredLanguage("si-LK");
            //droidSpeech.setPreferredLanguage("en-UK");

            // Setting the confirm and retry text in tamil
            droidSpeech.setOneStepVerifyConfirmText("Confirm");
            droidSpeech.setOneStepVerifyRetryText("Try again");
        }
    }

    @Override
    public void onDroidSpeechRmsChanged(float rmsChangedValue) {
        // Log.i(TAG, "Rms change value = " + rmsChangedValue);
    }

    @Override
    public void onDroidSpeechLiveResult(String liveSpeechResult) {
        //Log.i(TAG, "Live speech result = " + liveSpeechResult);
    }

    @Override
    public void onDroidSpeechFinalResult(String finalSpeechResult) {
        // Setting the final speech result
        // this.voiceInput.setText(finalSpeechResult);

        voiceControl(finalSpeechResult);

        droidSpeech.closeDroidSpeechOperations();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if (stopListening!=1){
                   vibrator.vibrate(60);
                   droidSpeech.startDroidSpeechRecognition();
               }
            }
        }, 1500);

        if (droidSpeech.getContinuousSpeechRecognition()) {
            int[] colorPallets = new int[]{
                    Color.parseColor("#D02D2B"),
                    Color.parseColor("#D02D2B"),
                    Color.parseColor("#D02D2B"),
                    Color.parseColor("#D02D2B"),
                    Color.parseColor("#D02D2B")};

            droidSpeech.setRecognitionProgressViewColors(colorPallets);
        } else {
            //stop.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDroidSpeechClosedByUser() {
        // stop.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDroidSpeechError(String errorMsg) {
        // Speech error
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    // MARK: DroidSpeechPermissionsListener Method

    @Override
    public void onDroidSpeechAudioPermissionStatus(boolean audioPermissionGiven, String errorMsgIfAny) {
        if (audioPermissionGiven) {
            start.post(new Runnable() {
                @Override
                public void run() {
                    // Start listening
                    start.performClick();
                }
            });
        } else {
            if (errorMsgIfAny != null) {
                // Permissions error
                Toast.makeText(getContext(), errorMsgIfAny, Toast.LENGTH_LONG).show();
            }
        }
    }

    // DUKA

    private void voiceControl(String finalSpeechResult) {

        if (doorLock.contains(finalSpeechResult)) {
            DatabaseReference myRef = database.getReference("DOOR_STATUS");
            myRef.setValue(1);
            speakerbox.play("door locked.");

        } else if (doorUnlock.contains(finalSpeechResult)) {
            DatabaseReference myRef = database.getReference("DOOR_STATUS");
            myRef.setValue(0);
            speakerbox.play("door unlocked.");

        } else if (bulb1On.contains(finalSpeechResult)) {
            DatabaseReference myRef = database.getReference("BULB_1_STATUS");
            myRef.setValue(1);
            speakerbox.play("bulb one on.");

        } else if (bulb2On.contains(finalSpeechResult)) {
            DatabaseReference myRef = database.getReference("BULB_2_STATUS");
            myRef.setValue(1);
            speakerbox.play("bulb two on.");

        } else if (bulb1Off.contains(finalSpeechResult)) {
            DatabaseReference myRef = database.getReference("BULB_1_STATUS");
            myRef.setValue(0);
            speakerbox.play("bulb two off.");

        } else if (bulb2Off.contains(finalSpeechResult)) {
            DatabaseReference myRef = database.getReference("BULB_2_STATUS");
            myRef.setValue(0);
            speakerbox.play("bulb two off.");

        }else if (finalSpeechResult.equals("stop listening")){
            stopListening = 1;
            speakerbox.play("good bye.");

        } else {
            speakerbox.play("wrong command");
        }
    }
}


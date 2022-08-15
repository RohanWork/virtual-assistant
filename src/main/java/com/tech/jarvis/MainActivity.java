package com.tech.jarvis;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class MainActivity extends AppCompatActivity {

    TextView textView, textView1;
    RelativeLayout relativeLayout;
    SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;
    Intent intent;
    Database database;
    //edu.cmu.pocketsphinx.SpeechRecognizer recognizer;
    final MediaPlayer[] mediaPlayer = new MediaPlayer[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS};
        relativeLayout = findViewById(R.id.relative);

        textView1 = findViewById(R.id.text2);

        database = new Database(this);
        database.defaultValue();

        if(!checkPermission(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                try {
                    GifImageView imageView = new GifImageView(MainActivity.this);
                    GifDrawable gifFromResource = new GifDrawable( getResources(), R.drawable.voice);
                    imageView.setImageDrawable(gifFromResource);
                    relativeLayout.removeAllViews();
                    RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(layoutParams1);
                    relativeLayout.addView(imageView);
                } catch (IOException e) {
                }
                //recognizer.stop();
                textToSpeech.stop();
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                relativeLayout.removeAllViews();
            }

            @Override
            public void onError(int error) {
                relativeLayout.removeAllViews();
                textView.setText("JARVIS");
                //FireRecognition();
            }

            @Override
            public void onResults(Bundle results) {
                relativeLayout.removeAllViews();
                ArrayList<String> result = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
                String str = result.get(0);
                Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
                if(str.contains("your name")) {
                    myName();
                }
                if(str.contains("flash")) {
                    if(str.contains("on")) {
                        flashLight(true);
                    }
                    else if(str.contains("off")) {
                        flashLight(false);
                    }
                }
                if(str.contains("date")){
                    dateShow();
                }
                if(str.contains("time")){
                    timeShow();
                }
                if(str.contains("call")){
                    call(str);
                }
                if(str.contains("text") || str.contains("sms") || str.contains("message")) {
                    if(str.contains("WhatsApp")) {
                        if(!checkMobileDataIsEnabled(MainActivity.this)) {
                            Toast.makeText(MainActivity.this,"Mobile Data is off",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            whatsappMessage(str);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        message(str);
                    }
                }
                if(str.contains(("Bluetooth"))) {
                    if(str.contains("on")) {
                        bluetooth(true);
                    }
                    else if(str.contains("off")) {
                        bluetooth(false);
                    }
                }
                if(str.contains(("Wi-Fi"))) {
                    if(str.contains("on")) {
                        wifi(true);
                    }
                    else if(str.contains("off")) {
                        wifi(false);
                    }
                }
                if(str.contains("open")) {
                    startApplication(str);
                }
                if(str.contains("normal mode")) {
                    ringerMode(0);
                }
                if(str.contains("silent mode")) {
                    ringerMode(1);
                }
                if(str.contains("vibrate mode")) {
                    ringerMode(2);
                }
                if(str.contains("web")) {
                    if(!checkMobileDataIsEnabled(MainActivity.this)) {
                        Toast.makeText(MainActivity.this,"Mobile Data is off",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    googleSearch(str);
                }
                if(str.toLowerCase().contains("videos")) {
                    if(!checkMobileDataIsEnabled(MainActivity.this)) {
                        Toast.makeText(MainActivity.this,"Mobile Data is off",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    youtubeSearch(str);
                }
                if(str.contains("joke")) {
                    joke();
                }
                if(str.contains("turn") && !str.contains("flash")) {
                    if(str.contains("on")) {
                        LightControl(str, true);
                    }
                    else if (str.contains("off") || (str.contains("of"))){
                        LightControl(str, false);
                    }
                }
                if(str.contains("map")) {
                    if(!checkMobileDataIsEnabled(MainActivity.this)) {
                        Toast.makeText(MainActivity.this,"Mobile Data is off",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    googleMap(str);
                }
                if(str.contains("news")) {
                    if(!checkMobileDataIsEnabled(MainActivity.this)) {
                        Toast.makeText(MainActivity.this,"Mobile Data is off",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    news();
                }
                if(str.contains("weather")) {
                    if(!checkMobileDataIsEnabled(MainActivity.this)) {
                        Toast.makeText(MainActivity.this,"Mobile Data is off",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    weather(str);
                }
                if(str.contains("music")) {
                    musicPlayer();
                }
                textView.setText("JARVIS");
                //FireRecognition();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        textView = findViewById(R.id.text1);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRecognizer.stopListening();
                speechRecognizer.startListening(intent);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechRecognizer.stopListening();
                speechRecognizer.startListening(intent);
            }
        });
        //continuosSpeech();

    }

    /*public void continuosSpeech() {

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getApplicationContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                } else {
                    FireRecognition();
                }
            }
        }.execute();
    }

    @Override
    public void onStop(){
        super.onStop();
        recognizer.removeListener(this);
    }


    public void FireRecognition(){
        recognizer.startListening("digits");
    }

    @Override
    public void onBeginningOfSpeech() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndOfSpeech() {
        // TODO Auto-generated method stub

    }

    private void setupRecognizer(File assetsDir) throws IOException {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                .setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
                .setRawLogDir(assetsDir).setKeywordThreshold(1e-40f)
                .getRecognizer();
        recognizer.addListener(this);


        File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
        recognizer.addGrammarSearch("digits", digitsGrammar);

    }

    @Override
    public void onResult(Hypothesis hup) {
    }

    @Override
    public void onError(Exception e) {
    }

    @Override
    public void onTimeout() {
        FireRecognition();
    }

    @Override
    public void onPartialResult(Hypothesis arg0) {
        if(arg0 == null){ return; }
        String comando = arg0.getHypstr();
        if(comando.equalsIgnoreCase("jarvis")) {
            speechRecognizer.startListening(intent);
        }
    }*/

    public void click(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }


    private boolean checkPermission(Context context, String... permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void myName() {
        textToSpeech.speak("Hi, I am jarvis.", TextToSpeech.QUEUE_FLUSH, null);
        relativeLayout.removeAllViews();
        TextView textView1 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams1.setMargins(20, 200, 20, 20);
        textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
        textView1.setLayoutParams(layoutParams1);
        textView1.setText("Myself");
        textView1.setTextColor(Color.BLACK);
        textView1.setTextSize(25);
        relativeLayout.addView(textView1);
        TextView textView2 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams2.setMargins(20, 0, 20, 20);
        textView2.setLayoutParams(layoutParams2);
        textView2.setText("I am Jarvis.");
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(25);
        relativeLayout.addView(textView2);
    }

    private void flashLight(boolean value){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if(!value){
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
                textToSpeech.speak("Flashlight turned off", TextToSpeech.QUEUE_FLUSH, null);
                relativeLayout.removeAllViews();
                TextView textView1 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams1.setMargins(20, 200, 20, 20);
                textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                textView1.setLayoutParams(layoutParams1);
                textView1.setText("Flashlight");
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(25);
                relativeLayout.addView(textView1);
                TextView textView2 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams2.setMargins(20, 0, 20, 20);
                textView2.setLayoutParams(layoutParams2);
                textView2.setText("Flashlight turned off");
                textView2.setTextColor(Color.WHITE);
                textView2.setTextSize(25);
                relativeLayout.addView(textView2);
            } catch (CameraAccessException e) {
            }
        }
        else {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, true);
                textToSpeech.speak("Flashlight turned on", TextToSpeech.QUEUE_FLUSH, null);
                relativeLayout.removeAllViews();
                TextView textView1 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams1.setMargins(20, 200, 20, 20);
                textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                textView1.setLayoutParams(layoutParams1);
                textView1.setText("Flashlight");
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(25);
                relativeLayout.addView(textView1);
                TextView textView2 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams2.setMargins(20, 0, 20, 20);
                textView2.setLayoutParams(layoutParams2);
                textView2.setText("Flashlight turned on");
                textView2.setTextColor(Color.WHITE);
                textView2.setTextSize(25);
                relativeLayout.addView(textView2);
            } catch (CameraAccessException e) {
            }
        }
    }

    public void dateShow(){
        String date = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(new Date());
        textToSpeech.speak("It's " + date, TextToSpeech.QUEUE_FLUSH, null);
        relativeLayout.removeAllViews();
        TextView textView1 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams1.setMargins(20, 200, 20, 20);
        textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
        textView1.setLayoutParams(layoutParams1);
        textView1.setText("Date");
        textView1.setTextColor(Color.BLACK);
        textView1.setTextSize(25);
        relativeLayout.addView(textView1);
        TextView textView2 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams2.setMargins(20, 0, 20, 20);
        textView2.setLayoutParams(layoutParams2);
        textView2.setText(date);
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(25);
        relativeLayout.addView(textView2);
    }

    private void timeShow(){
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        textToSpeech.speak("It's " + time, TextToSpeech.QUEUE_FLUSH, null);
        relativeLayout.removeAllViews();
        TextView textView1 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams1.setMargins(20, 200, 20, 20);
        textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
        textView1.setLayoutParams(layoutParams1);
        textView1.setText("Time");
        textView1.setTextColor(Color.BLACK);
        textView1.setTextSize(25);
        relativeLayout.addView(textView1);
        TextView textView2 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams2.setMargins(20, 0, 20, 20);
        textView2.setLayoutParams(layoutParams2);
        textView2.setText(time);
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(25);
        relativeLayout.addView(textView2);
    }

    private void call (String str) {
        String name = str.toLowerCase();
        Intent i = new Intent(Intent.ACTION_CALL);
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        while(c.moveToNext()) {
            String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(name.contains(contactName.toLowerCase())){
                startActivity(i.setData(Uri.parse("tel:" + phoneNumber )));
                break;
            }
        }
    }

    private void bluetooth(boolean value) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(value){
            if(!(mBluetoothAdapter.isEnabled())) {
                mBluetoothAdapter.enable();
                textToSpeech.speak("Bluetooth turned on", TextToSpeech.QUEUE_FLUSH, null);
                relativeLayout.removeAllViews();
                TextView textView1 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams1.setMargins(20, 200, 20, 20);
                textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                textView1.setLayoutParams(layoutParams1);
                textView1.setText("Bluetooth");
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(25);
                relativeLayout.addView(textView1);
                TextView textView2 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams2.setMargins(20, 0, 20, 20);
                textView2.setLayoutParams(layoutParams2);
                textView2.setText("Bluetooth Enabled");
                textView2.setTextColor(Color.WHITE);
                textView2.setTextSize(25);
                relativeLayout.addView(textView2);
            }
        }
        else{
            if(mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
                textToSpeech.speak("Bluetooth turned off ", TextToSpeech.QUEUE_FLUSH, null);
                relativeLayout.removeAllViews();
                TextView textView1 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams1.setMargins(20, 200, 20, 20);
                textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                textView1.setLayoutParams(layoutParams1);
                textView1.setText("Bluetooth");
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(25);
                relativeLayout.addView(textView1);
                TextView textView2 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams2.setMargins(20, 0, 20, 20);
                textView2.setLayoutParams(layoutParams2);
                textView2.setText("Bluetooth Disabled");
                textView2.setTextColor(Color.WHITE);
                textView2.setTextSize(25);
                relativeLayout.addView(textView2);
            }
        }
    }

    private void wifi(boolean value) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (value) {
            if(!(wifiManager.isWifiEnabled())) {
                wifiManager.setWifiEnabled(true);
                textToSpeech.speak("WiFi turned on", TextToSpeech.QUEUE_FLUSH, null);
                relativeLayout.removeAllViews();
                TextView textView1 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams1.setMargins(20, 200, 20, 20);
                textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                textView1.setLayoutParams(layoutParams1);
                textView1.setText("Wi-Fi");
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(25);
                relativeLayout.addView(textView1);
                TextView textView2 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams2.setMargins(20, 0, 20, 20);
                textView2.setLayoutParams(layoutParams2);
                textView2.setText("Wi-Fi Enabled");
                textView2.setTextColor(Color.WHITE);
                textView2.setTextSize(25);
                relativeLayout.addView(textView2);
            }
        }
        else{
            if(wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
                textToSpeech.speak("WiFi turned off", TextToSpeech.QUEUE_FLUSH, null);
                relativeLayout.removeAllViews();
                TextView textView1 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams1.setMargins(20, 200, 20, 20);
                textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                textView1.setLayoutParams(layoutParams1);
                textView1.setText("Wi-Fi");
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(25);
                relativeLayout.addView(textView1);
                TextView textView2 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams2.setMargins(20, 0, 20, 20);
                textView2.setLayoutParams(layoutParams2);
                textView2.setText("Wi-Fi Disabled");
                textView2.setTextColor(Color.WHITE);
                textView2.setTextSize(25);
                relativeLayout.addView(textView2);
            }
        }
    }

    private void message(String str) {
        String name = str.toLowerCase();
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME );
        while(c.moveToNext()) {
            String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(name.contains(contactName.toLowerCase())) {
                String message = name.substring(name.lastIndexOf(contactName.toLowerCase())+contactName.length()+1);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                textToSpeech.speak("Message sent", TextToSpeech.QUEUE_FLUSH, null);
                relativeLayout.removeAllViews();
                TextView textView1 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams1.setMargins(20, 200, 20, 20);
                textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                textView1.setLayoutParams(layoutParams1);
                textView1.setText("Message");
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(25);
                relativeLayout.addView(textView1);
                TextView textView2 = new TextView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams2.setMargins(20, 0, 20, 20);
                textView2.setGravity(Gravity.CENTER);
                textView2.setLayoutParams(layoutParams2);
                textView2.setText("To\n" + contactName + "\n\nMessage\n" + message);
                textView2.setTextColor(Color.WHITE);
                textView2.setTextSize(25);
                relativeLayout.addView(textView2);
                break;
            }
        }
    }

    private void startApplication(String str){
        String url = str.substring(str.indexOf("open") + 5);
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.contains(url.toLowerCase())) {
                Intent intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
                startActivity(intent);
                break;
            }
        }
    }

    private void ringerMode(int mode) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        if(mode == 0) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            textToSpeech.speak("Your phone is restored to normal mode", TextToSpeech.QUEUE_FLUSH, null);
            relativeLayout.removeAllViews();
            TextView textView1 = new TextView(MainActivity.this);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams1.setMargins(20, 200, 20, 20);
            textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Ringer mode");
            textView1.setTextColor(Color.BLACK);
            textView1.setTextSize(25);
            relativeLayout.addView(textView1);
            TextView textView2 = new TextView(MainActivity.this);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams2.setMargins(20, 0, 20, 20);
            textView2.setLayoutParams(layoutParams2);
            textView2.setText("Phone is on normal mode");
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(25);
            relativeLayout.addView(textView2);
        }
        if(mode == 1) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            textToSpeech.speak("Your phone is on silent mode", TextToSpeech.QUEUE_FLUSH, null);
            relativeLayout.removeAllViews();
            TextView textView1 = new TextView(MainActivity.this);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams1.setMargins(20, 200, 20, 20);
            textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Ringer mode");
            textView1.setTextColor(Color.BLACK);
            textView1.setTextSize(25);
            relativeLayout.addView(textView1);
            TextView textView2 = new TextView(MainActivity.this);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams2.setMargins(20, 0, 20, 20);
            textView2.setLayoutParams(layoutParams2);
            textView2.setText("Phone is on silent mode");
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(25);
            relativeLayout.addView(textView2);
        }
        if(mode == 2) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            textToSpeech.speak("Your phone is on vibrate mode", TextToSpeech.QUEUE_FLUSH, null);
            relativeLayout.removeAllViews();
            TextView textView1 = new TextView(MainActivity.this);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams1.setMargins(20, 200, 20, 20);
            textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Ringer mode");
            textView1.setTextColor(Color.BLACK);
            textView1.setTextSize(25);
            relativeLayout.addView(textView1);
            TextView textView2 = new TextView(MainActivity.this);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams2.setMargins(20, 0, 20, 20);
            textView2.setLayoutParams(layoutParams2);
            textView2.setText("Phone is on vibrate mode");
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(25);
            relativeLayout.addView(textView2);
        }
    }

    private void googleSearch(String str) {
        String url = str.substring(str.indexOf("web") + 3);
        relativeLayout.removeAllViews();
        WebView webView = new WebView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(layoutParams1);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl("http://www.google.com/#q=" + url);
        relativeLayout.addView(webView);
    }

    private void youtubeSearch(String str) {
        String url = str.substring(str.indexOf("for") + 3);
        url = url.replace(' ', '+');
        relativeLayout.removeAllViews();
        WebView webView = new WebView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(layoutParams1);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl("https://www.youtube.com/results?search_query=" + url);
        relativeLayout.addView(webView);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void whatsappMessage(String str) throws UnsupportedEncodingException {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        while(c.moveToNext()) {
            String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(str.contains(contactName)){
                String message = str.substring(str.lastIndexOf(contactName)+contactName.length()+1);
                String url = "https://api.whatsapp.com/send?phone=91" + phoneNumber + "&text=" + URLEncoder.encode(message, "UTF-8");
                intent.setPackage("com.whatsapp");
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        }
    }

    private void joke() {
        Cursor res = database.getAllData("select * from Parameters");
        ArrayList<String> arrayList = new ArrayList<>();
        while (res.moveToNext()) {
            if (res.getString(0).equals("joke")) {
                arrayList.add(res.getString(1));
            }
        }
        int random = (new Random().nextInt(5));
        textToSpeech.speak(arrayList.get(random), TextToSpeech.QUEUE_FLUSH, null);
        relativeLayout.removeAllViews();
        TextView textView1 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams1.setMargins(20, 200, 20, 20);
        textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
        textView1.setLayoutParams(layoutParams1);
        textView1.setText("Jokes");
        textView1.setTextColor(Color.BLACK);
        textView1.setTextSize(25);
        relativeLayout.addView(textView1);
        TextView textView2 = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams2.setMargins(50, 0, 50, 50);
        textView2.setLayoutParams(layoutParams2);
        textView2.setText(arrayList.get(random));
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(25);
        relativeLayout.addView(textView2);
    }

    private void LightControl(String str, Boolean value) {
        if (value) {
            String light = str.substring(str.indexOf("on") + 2);
            String number = null;
            Cursor res = database.getAllData("select * from Parameters");
            while (res.moveToNext()) {
                if (res.getString(0).equals("simnumber")) {
                    number = res.getString(1);
                    break;
                }
            }
            res = database.getAllData("select * from Parameters");
            while (res.moveToNext()) {
                if (res.getString(1).equals(light.trim())) {
                    String actual = res.getString(0);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, "#A." + actual + " on*", null, null);
                    textToSpeech.speak(light + " turned on", TextToSpeech.QUEUE_FLUSH, null);
                    relativeLayout.removeAllViews();
                    TextView textView1 = new TextView(MainActivity.this);
                    RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    layoutParams1.setMargins(20, 200, 20, 20);
                    textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                    textView1.setLayoutParams(layoutParams1);
                    textView1.setText("Home Light");
                    textView1.setTextColor(Color.BLACK);
                    textView1.setTextSize(25);
                    relativeLayout.addView(textView1);
                    TextView textView2 = new TextView(MainActivity.this);
                    RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layoutParams2.setMargins(50, 0, 50, 50);
                    textView2.setLayoutParams(layoutParams2);
                    textView2.setText(light + " turned on.");
                    textView2.setTextColor(Color.WHITE);
                    textView2.setTextSize(25);
                    relativeLayout.addView(textView2);
                }
            }
        }
        if (!value) {
            String light;
            if (str.contains("off")) {
                light = str.substring(str.indexOf("off") + 3);
            } else {
                light = str.substring(str.indexOf("of") + 2);
            }
            String number = null;
            Cursor res = database.getAllData("select * from Parameters");
            while (res.moveToNext()) {
                if (res.getString(0).equals("simnumber")) {
                    number = res.getString(1);
                    break;
                }
            }
            res = database.getAllData("select * from Parameters");
            while (res.moveToNext()) {
                if (res.getString(1).equals(light.trim())) {
                    String actual = res.getString(0);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, "#A." + actual + " off*", null, null);
                    textToSpeech.speak(light + " turned off", TextToSpeech.QUEUE_FLUSH, null);
                    relativeLayout.removeAllViews();
                    TextView textView1 = new TextView(MainActivity.this);
                    RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    layoutParams1.setMargins(20, 200, 20, 20);
                    textView1.setBackground(ContextCompat.getDrawable(this, R.drawable.com_bg));
                    textView1.setLayoutParams(layoutParams1);
                    textView1.setText("Home Light");
                    textView1.setTextColor(Color.BLACK);
                    textView1.setTextSize(25);
                    relativeLayout.addView(textView1);
                    TextView textView2 = new TextView(MainActivity.this);
                    RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layoutParams2.setMargins(50, 0, 50, 50);
                    textView2.setLayoutParams(layoutParams2);
                    textView2.setText(light + " turned off.");
                    textView2.setTextColor(Color.WHITE);
                    textView2.setTextSize(25);
                    relativeLayout.addView(textView2);
                }
            }
        }
    }

    private void googleMap(String str) {
        if (str.contains("location")) {
            String url = str.substring(str.indexOf("of") + 2);
            relativeLayout.removeAllViews();
            WebView webView = new WebView(MainActivity.this);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            webView.setLayoutParams(layoutParams1);
            webView.setWebViewClient(new MyBrowser());
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl("http://www.google.com/maps/place/" + url);
            relativeLayout.addView(webView);
        } else {
            if (str.contains("direction")) {
                String source = str.substring(str.indexOf("from") + 4, str.indexOf("to"));
                String destination = str.substring(str.indexOf("to") + 2);
                relativeLayout.removeAllViews();
                WebView webView = new WebView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                webView.setLayoutParams(layoutParams1);
                webView.setWebViewClient(new MyBrowser());
                webView.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webView.loadUrl("http://www.google.com/maps/dir/" + source + "/" + destination);
                relativeLayout.addView(webView);
            }
        }
    }

    private void news() {
        relativeLayout.removeAllViews();
        WebView webView = new WebView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(layoutParams1);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl("http://www.google.com/news");
        relativeLayout.addView(webView);
    }

    private void weather(String str) {
        String search = str.substring(str.indexOf("weather"));
        relativeLayout.removeAllViews();
        WebView webView = new WebView(MainActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(layoutParams1);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl("http://www.google.com/#q=" + search);
        relativeLayout.addView(webView);
    }

    private boolean checkMobileDataIsEnabled(Context context){
        boolean mobileYN = false;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mobileYN = Settings.Global.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
            int dataState = tel.getDataState();
            if(dataState != TelephonyManager.DATA_DISCONNECTED){
                mobileYN = true;
            }

        }

        return mobileYN;
    }

    public void musicPlayer() {
        final List<String> list = new ArrayList<>();
        ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        relativeLayout.removeAllViews();
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(0,0,0,150);
        ListView listView = new ListView(MainActivity.this);
        listView.setBackgroundColor(Color.WHITE);
        listView.setLayoutParams(layoutParams1);
        listView.setAdapter(listAdapter);
        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i].getName());
        }
        int resID = getResources().getIdentifier(list.get(0),"raw",getPackageName());
        mediaPlayer[0] = MediaPlayer.create(MainActivity.this,resID);
        mediaPlayer[0].start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mediaPlayer[0] != null) {
                    mediaPlayer[0].release();
                }

                int resID = getResources().getIdentifier(list.get(i),"raw",getPackageName());
                mediaPlayer[0] = MediaPlayer.create(MainActivity.this,resID);
                mediaPlayer[0].start();
            }
        });
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 150);
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        Button button = new Button(MainActivity.this);
        button.setLayoutParams(layoutParams2);
        button.setText("Pause");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer[0].stop();
            }
        });
        relativeLayout.addView(button);
        relativeLayout.addView(listView);
    }
}

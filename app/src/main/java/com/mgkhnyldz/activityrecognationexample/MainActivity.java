package com.mgkhnyldz.activityrecognationexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String FILE_NAME = "location_histories";
    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    // static final long  DETECTION_INTERVAL_IN_MILLISECONDS = 30 * 1000;
    public static final int CONFIDENCE = 70;
    private static final int PERMISSION_CODE = 1001;
    private static final int PERMISSION_CODE_LOCATION = 1001;

    private ConstraintLayout constraintLayout;
    private LinearLayout linearLayout;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView txtActivity, txtTransition, boylamTxt, enlemTxt, historyTxt;
    private Button btnStartTrcking, btnStopTracking, btnHistory, btnBack;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        historyTxt.setMovementMethod(new ScrollingMovementMethod());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MainActivity.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int transition = intent.getIntExtra("transition", 0);
                    handleUserActivity(type, transition);
                }
            }
        };


        ButtonStart();
        ButtonStop();
        ButtonHistory();
        ButtonBack();
    }

    //button işlemleri
    private void ButtonBack() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void ButtonHistory() {
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintLayout.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

                FileInputStream fis = null;

                try {
                    fis = openFileInput(FILE_NAME);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    StringBuilder stringBuilder = new StringBuilder();
                    String text;

                    while ((text = bufferedReader.readLine()) != null) {
                        stringBuilder.append(text).append("\n");
                    }

                    historyTxt.setText(stringBuilder.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    private void ButtonStart() {


        btnStartTrcking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 29) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) +
                            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);
                    } else {
                        startTracking();
                    }
                } else {
                    startTracking();
                }
            }
        });
    }

    private void ButtonStop() {
        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking();
            }
        });

    }

    //kullanıcı lokasyonunu alma
    private void LokasyonIslemleri(final String durum) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE_LOCATION);

        } else {


            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    enlemTxt.setText(String.format("%s", location.getLatitude()));
                    boylamTxt.setText(String.format("%s", location.getLongitude()));

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date(location.getTime());
                    String formattedDate = dateFormat.format(date);


                    if (!txtActivity.getText().equals("Durma")) {
                        saveToFile(formattedDate, location, durum);

                    }



                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);

//            if (locationManager != null) {
//                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                if (lastKnownLocation != null) {
//                    enlemTxt.setText(String.format("%s", lastKnownLocation.getLatitude()));
//                    boylamTxt.setText(String.format("%s", lastKnownLocation.getLongitude()));
//                }
//
//            }


            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                GPSCheck();

            }

        }
    }

    //kullanıcı lokasyonu tarihi ve durumu ile dosyaya kaydeder
    private void saveToFile(String formattedDate, Location location, String durum) {
        String textTarih = formattedDate;
        String tarih = "Tarih : ";
        String lokasyon = " Lokasyon : ";
        String bosluk = " ";
        String state = "Durum : ";

        double enlem = location.getLatitude();
        double boylam = location.getLongitude();
        DecimalFormat numberFormat = new DecimalFormat("#.00000");
        String yeniEnlem = numberFormat.format(enlem);
        String yeniBoylam = numberFormat.format(boylam);


        FileOutputStream fos = null;

        try {

            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write(tarih.getBytes());
            fos.write(textTarih.getBytes());
            fos.write('\n');
            fos.write(lokasyon.getBytes());
            fos.write(yeniEnlem.getBytes());
            fos.write(bosluk.getBytes());
            fos.write(yeniBoylam.getBytes());
            fos.write('\n');
            fos.write(state.getBytes());
            fos.write(durum.getBytes());
            fos.write('\n');


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    //konum durumunu kontrol eder
    private void GPSCheck() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }


    //view elemanlarını initialize eder
    private void init() {
        constraintLayout = findViewById(R.id.container);
        linearLayout = findViewById(R.id.linearLayout);
        historyTxt = findViewById(R.id.linearLayoutTxtView);
        boylamTxt = findViewById(R.id.boylamTxt);
        enlemTxt = findViewById(R.id.enlemTxt);
        txtActivity = findViewById(R.id.txt_activity);
        txtTransition = findViewById(R.id.txt_confidence);
        btnStartTrcking = findViewById(R.id.btn_start_tracking);
        btnStopTracking = findViewById(R.id.btn_stop_tracking);
        btnHistory = findViewById(R.id.historyBtn);
        btnBack = findViewById(R.id.backBtn);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted for location", Toast.LENGTH_LONG).show();
                startTracking();

            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted for recognition", Toast.LENGTH_LONG).show();
                startTracking();

            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }

        }

    }


    //kullanıcının durumunu ekranda belirtir
    private void handleUserActivity(int type, int transition) {
        String label = "";


        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                txtActivity.setText(label);
                LokasyonIslemleri(label);
                break;
            }

            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                txtActivity.setText(label);
                break;
            }

            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                txtActivity.setText(label);
                LokasyonIslemleri(label);
                break;
            }

        }





    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(MainActivity.BROADCAST_DETECTED_ACTIVITY));
    }

    //broadcast unregister
    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    //service başlatma ve sonlandırma
    private void startTracking() {
        Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void stopTracking() {
        Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }

}

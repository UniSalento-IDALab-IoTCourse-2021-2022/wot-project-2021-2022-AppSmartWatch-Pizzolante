package com.example.worksafewatch;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

public class DangerListenActivity extends Activity {

    private String machineryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_listen);

        machineryId = getIntent().getExtras().getString("MACHINERY_ID");

        // Definisco il topic
        String topic = "worksafe/dangers";

        // Creo il client MQTT
        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(),
                "tcp://test.mosquitto.org",
                clientId);

        // Mi connetto e mi sottoscrivo
        MqttMachinistConnect(client,topic);

        // Definisco il pulsante stop
        Button stop_scan_button = findViewById(R.id.stopButton);

        // Definisco e aggancio l'Action Listener al pulsante stopScanButton
        stop_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tolgo la sottoscrizione
                MqttUnsubscribe(client,topic);
                // Mi disconnetto
                MqttDisconnect(client);
                // Torno alla schermata precedente
                Intent previous = new Intent(DangerListenActivity.this, MachinistActivity.class);
                startActivity(previous);
            }
        });

    }

    // Metodo che effettua la connessione al brocker MQTT
    public void MqttMachinistConnect(MqttAndroidClient client, String topic) {

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(DangerListenActivity.this, "Connected to "+client.getServerURI(),
                            Toast.LENGTH_LONG).show();
                    // Una volta connesso correttamente, mi sottoscrivo
                    MqttSubscribe(client,topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(DangerListenActivity.this, "CONNECTION FAILED",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Metodo che sottoscrive il client al topic /worksafe/risks e imposta una callback
    public void MqttSubscribe(MqttAndroidClient client, String topic) {

        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(DangerListenActivity.this, "Correctly subscribed to "+topic,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(DangerListenActivity.this, "SUBSCRIPTION FAILED",
                            Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //Definisco la callback sul client
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //Toast.makeText(DangerListenActivity.this, "Message: "+message+"\narrived to topic "+topic,
                //Toast.LENGTH_SHORT).show();

                // Estraggo il messaggio e lo trasformo in tipo String
                String beaconId = new String(message.getPayload());

                // Controllo che l'ID del beacon presente nel messaggio sia uguale a quello del mio macchinario
                if(machineryId.equals(beaconId)){
                    notifyRiskMachinist("Un worker è vicino al tuo macchinario!");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    // Metodo che effettua la disconnessione al brocker MQTT
    public void MqttDisconnect(MqttAndroidClient client) {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(DangerListenActivity.this, "Disconnected from "+client.getServerURI(),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(DangerListenActivity.this, "DISCONNECTION FAILED",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Metodo che elimina la sottoscrizione al topic /worksafe/risks
    public void MqttUnsubscribe(MqttAndroidClient client, String topic) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(DangerListenActivity.this, "Correctly unsubscribed to "+topic,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(DangerListenActivity.this, "UNSUBSCRIPTION FAILED",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    // Metodo che serve per inviare una notifica con un messaggio
    private void notifyRiskMachinist(String message) {
        Notification.Builder b = new Notification.Builder(this);

        //FIX android O bug Notification add setChannelId("shipnow-message")
        NotificationChannel mChannel = null;
        b.setSmallIcon(R.drawable.worker_worker_icon_with_png_and_vector_format_for_free_481601) // vector (doesn't work with png as well)
                .setContentText("Pericolo!\nUn worker è vicino al tuo macchinario!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("1", "WorkSafe",NotificationManager.IMPORTANCE_HIGH);
            b.setChannelId("1");
        }

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(1, b.build());
    }

}

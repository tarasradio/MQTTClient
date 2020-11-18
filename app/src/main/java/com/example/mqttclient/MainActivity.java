package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.*;
import org.eclipse.paho.client.mqttv3.*;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private EditText textMessage, subscribeTopic, unSubscribeTopic, incText;
    private Button publishMessage, subscribe, unSubscribe, check;

    private MqttAndroidClient client = null;

    private String topic;

    private String clientId;

    private void reConnect()
    {
        client = new MqttAndroidClient(this.getApplicationContext(),
                "tcp://broker.hivemq.com:1883", clientId);
        try {
            //client.unsubscribe(topic);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String _topic, MqttMessage message) throws Exception {
                    String m = message.toString();
                    Toast toast = Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Успешно подключились к серверу", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    int qos = 0;
                    IMqttToken subToken = null;
                    try {
                        subToken = client.subscribe(topic, qos);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    subToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Успешно подключились к топику", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Ошибка подключения к топику", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Ошибка подключения к серверу", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMessage = (EditText) findViewById(R.id.textMessage);
        publishMessage = (Button) findViewById(R.id.publishMessage);

        subscribe = (Button) findViewById(R.id.subscribe);
        subscribeTopic = (EditText) findViewById(R.id.subscibeTopic);

        clientId = MqttClient.generateClientId();

        topic = "testtopic/AAE230";

        reConnect();

        publishMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] encodedPayload = textMessage.getText().toString().getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //client.unsubscribe(topic);
                    topic = subscribeTopic.getText().toString();
                    reConnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
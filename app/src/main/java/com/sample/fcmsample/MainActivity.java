package com.sample.fcmsample;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    String TAG = getClass().toString();

    TextView tvLog;
    TextView tvRecevie;
    TextView tvSend;
    EditText etSendMsg;
    Button btnSend;
    String regId;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        init();
    }

    void bindView()
    {
        tvLog = findViewById(R.id.tvLog);
        tvRecevie = findViewById(R.id.tvReceive);
        tvSend = findViewById(R.id.tvSend);
        etSendMsg = findViewById(R.id.etSendMsg);
        btnSend = findViewById(R.id.btnSend);
    }

    void init() {
        getRegistrationID();
        runtimeEnableAutoInit();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = etSendMsg.getText().toString().trim();
                send(sendMsg);
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());
        processIntent(getIntent());
    }

    //메인엑티비티가 떠있을떄
    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
        super.onNewIntent(intent);
    }

    public void getRegistrationID() {
        /*
        regId = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG,regId);*/

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.e(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void send(String input) {
        JSONObject requestObj = new JSONObject();
        try {
            //우선순위 높음
            requestObj.put("priority", "high");

            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            dataObj.put("subject", input);
            dataObj.put("message", input);

            requestObj.put("data", dataObj);

            /*
            JSONObject notificationData = new JSONObject();
            notificationData.put("title" , "노티 타이틀 노티 타이틀");
            notificationData.put("body" , "노티 바디 바디 바디");
            notificationData.put("image","https://namu.wiki/w/%ED%8C%8C%EC%9D%BC:Hello%20Summer_Photo%203_%EB%82%98%EC%9D%80_1.jpg");*/

            //requestObj.put("notification",notificationData);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, regId); //수신자의 등록아이디를 넣어준다.

            requestObj.put("registration_ids", jsonArray);

            Log.e(TAG,requestObj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendData(requestObj, new SendResponsListener() {
            @Override
            public void onRequestStarted() {
                //println();
            }

            @Override
            public void OnRequestComplete() {
                println("OnRequestComplete() 호출됨");
            }

            @Override
            public void onRequestErrer(VolleyError error) {
                println("onRequestErrer() 호출됨");
            }
        });
    }

    public void sendData(JSONObject requestData, final SendResponsListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        listener.OnRequestComplete();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onRequestErrer(error);
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json"; //json 형태로 주고받겠다.
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //해더정보를 넣어 줄때
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=AAAA_hrYyGY:APA91bFBy8QIoJNe3XfSuEPi7tETgMHulMaIryMm4n71onU0LIu2SwqvmYuh2U7uV6ZoKoKvO2PGVxyu8Mx1xdCY_C75RyqFlYOw_nWRFA3jdMHtBb8fGwVQX8rVWoKYfsHcaRjKEGnE");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //파라미터를 넣어줄떄
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }

    public interface SendResponsListener {
        public void onRequestStarted();

        public void OnRequestComplete();

        public void onRequestErrer(VolleyError error);

    }
    public void processIntent(Intent intent) {

        if (intent != null) {
            String from = intent.getStringExtra("from");
            String contents = intent.getStringExtra("content");

            println("DATA : " + from + " , " + contents);

            tvRecevie.append("DATA : " + contents + "\n");
        } else {
            println("intent is null");
        }
    }

    public void println(String data) {
        tvLog.append(data + "\n");
    }

    /** FCM function **/
    public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }

    public void deviceGroupUpstream() {
        // [START fcm_device_group_upstream]
        String to = "a_unique_key"; // the notification key
        AtomicInteger msgId = new AtomicInteger();
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(to)
                .setMessageId(String.valueOf(msgId.get()))
                .addData("hello", "world")
                .build());
        // [END fcm_device_group_upstream]
    }

    // [START fcm_get_account]
    public String getAccount() {
        // This call requires the Android GET_ACCOUNTS permission
        Account[] accounts = AccountManager.get(this /* activity */).
                getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0].name;
    }
    // [END fcm_get_account]

    public void getAuthToken() {
        // [START fcm_get_token]
        String accountName = getAccount();

        // Initialize the scope using the client ID you got from the Console.
        final String scope = "audience:server:client_id:"
                + "1262xxx48712-9qs6n32447mcj9dirtnkyrejt82saa52.apps.googleusercontent.com";

        String idToken = null;
        try {
           // idToken = GoogleAuthUtil.getToken(this, accountName, scope);
        } catch (Exception e) {
            Log.w(TAG, "Exception while getting idToken: " + e);
        }
        // [END fcm_get_token]
    }

    // [START fcm_add_to_group]
    public String addToGroup(
            String senderId, String userEmail, String registrationId, String idToken)
            throws IOException, JSONException {
        URL url = new URL("https://fcm.googleapis.com/fcm/googlenotification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "add");
        data.put("notification_key_name", userEmail);
        data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));
        data.put("id_token", idToken);

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        return response.getString("notification_key");
    }
    // [END fcm_add_to_group]

    public void removeFromGroup(String userEmail, String registrationId, String idToken) throws JSONException {
        // [START fcm_remove_from_group]
        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "remove");
        data.put("notification_key_name", userEmail);
        data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));
        data.put("id_token", idToken);
        // [END fcm_remove_from_group]
    }

    public void sendUpstream() {
        final String SENDER_ID = "YOUR_SENDER_ID";
        final int messageId = 0; // Increment for each
        // [START fcm_send_upstream]
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(messageId))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());
        // [END fcm_send_upstream]
    }

}

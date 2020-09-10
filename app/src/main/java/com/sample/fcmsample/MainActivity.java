package com.sample.fcmsample;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        tvLog = findViewById(R.id.tvLog);
        tvRecevie = findViewById(R.id.tvReceive);
        tvSend = findViewById(R.id.tvSend);
        etSendMsg = findViewById(R.id.etSendMsg);
        btnSend = findViewById(R.id.btnSend);

        init();
    }

    void init() {
        getRegistrationID();

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
        regId = FirebaseInstanceId.getInstance().getToken();
        println(regId);
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

            JSONObject notificationData = new JSONObject();
            notificationData.put("title" , "노티 타이틀 노티 타이틀");
            notificationData.put("body" , "노티 바디 바디 바디");

            requestObj.put("notification",notificationData);

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
}

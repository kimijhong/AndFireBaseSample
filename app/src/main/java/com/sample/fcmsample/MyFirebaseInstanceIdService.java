package com.sample.fcmsample;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService{
    private static final String TAG = "MyFirebaseInstanceIdService";


    //등록아이디가 갱신이 되었을떄
    /**
        나에게 메세지를 보낼 사람은 토큰id 값을 알아야 보낼수 있다. 보통 회원가입하면서 서버에 저장하거나 상태편한테 알려주도록
        작성한다.
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();


        Log.d(TAG,"onTokenRefresh() 호출됨");
    }
}

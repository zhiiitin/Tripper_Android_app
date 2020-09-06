package com.example.tripper_android_app.setting.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tripper_android_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Google_Login_Fragment extends Fragment {
    private static final String TAG = "TAG_ResultFragment";
    private Activity activity;
    private TextView textView ,tvWelcome;
    private FirebaseAuth auth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google__login_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textView = view.findViewById(R.id.textView);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        Button btSignOut = view.findViewById(R.id.btSignOutGoogle);
        btSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut() {
        // 登出Firebase帳號
        auth.signOut();

        // 登出Google帳號
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // 由google-services.json轉出
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(activity, options);
        client.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Navigation.findNavController(textView).popBackStack();
                Log.d(TAG, "Signed out");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 檢查user是否已經登入，是則FirebaseUser物件不為null
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Navigation.findNavController(textView).popBackStack();
        } else {
            String text = "您的Firebase UID: " + user.getUid();
            textView.setText(text);
        }


    }
}
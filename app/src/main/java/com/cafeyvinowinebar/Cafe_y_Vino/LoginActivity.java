package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

/**
 * The user logins with the FirebaseAuth and goes to the MainActivity
 * There is an option to get a new password if the user has forgotten his current one
 */

public class LoginActivity extends AppCompatActivity {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseMessaging fMessaging = FirebaseMessaging.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private EditText edtEmail, edtPass;
    private ProgressBar progressBar;
    private final Handler handler = new Handler();

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        boolean isDarkThemeOn = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPass = findViewById(R.id.edtPassLogin);
        Button btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar2);
        CheckBox checkBox = findViewById(R.id.checkBox);
        Button btnRestablecer = findViewById(R.id.btnRestablecer);

        btnLogin.setOnClickListener(v -> {

            if (Utils.isConnected(getBaseContext())) {

                String email = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError(getString(R.string.error_email));
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    edtPass.setError(getString(R.string.error_password));
                    return;
                }
                if (pass.length() < 6) {
                    edtPass.setError(getString(R.string.error_password_length));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(App.executor, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                                fMessaging.getToken().addOnSuccessListener(App.executor, s -> {
                                    assert user != null;
                                    String userId = user.getUid();
                                    fStore.collection("usuarios").document(userId).get().addOnSuccessListener(App.executor, snapshot -> {
                                        String token = snapshot.getString(Utils.KEY_TOKEN);
                                        assert token != null;

                                        // we check if the current FirebaseMessaging token for the user
                                        // is the same as the token stored in the FirestoreDB
                                        // to make sure it's up to date
                                        if (!token.equals(s)) {
                                            snapshot.getReference().update(Utils.KEY_TOKEN, s);
                                        }
                                    });
                                });
                                handler.post(() -> {
                                    Toast.makeText(LoginActivity.this, getString(R.string.login_inicio), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(MainActivity.newIntent(getBaseContext()));
                                });

                            } else {
                                handler.post(() -> {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(getBaseContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                        Toast.makeText(getBaseContext(), R.string.wrong_email, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        });

            } else {
                Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        btnRestablecer.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View view = getLayoutInflater().inflate(R.layout.user_data_et, null);
            EditText et = view.findViewById(R.id.edtUserEt);
            et.setHint(getString(R.string.su_email));
            builder.setTitle(getString(R.string.dialog_reset_email_title));
            builder.setMessage(getString(R.string.no_email));
            builder.setView(view);
            builder.setPositiveButton(getString(R.string.send_email), (dialog, which) -> {
                String eMail = et.getText().toString().trim();
                if (TextUtils.isEmpty(eMail)) {
                    et.setError(getString(R.string.no_email));
                    return;
                }
                fAuth.sendPasswordResetEmail(eMail).addOnCompleteListener(App.executor, task -> {
                    if (task.isSuccessful()) {
                        handler.post(() -> Toast.makeText(LoginActivity.this, getString(R.string.sent_email_toast), Toast.LENGTH_SHORT).show());
                    } else {
                        handler.post(() -> {
                            if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                                Toast.makeText(getBaseContext(), R.string.wrong_email, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            });
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
            if (isDarkThemeOn) {
                Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setTextColor(getColor(R.color.soft_light_teal));
                Button btnNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                btnNegative.setTextColor(getColor(R.color.soft_light_teal));
            }
        });
    }
}
package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The user creates a new profile with the FirebaseAuth and goes to the MainActivity
 * A document for the user with their metadata is created in the FirestoreDB
 */

public class RegistrationActivity extends AppCompatActivity {

    private final FirebaseMessaging fMessaging = FirebaseMessaging.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private EditText edtName, edtPhone, edtEmail, edtPass, edtFecha;
    private Button btnReg;
    private ProgressBar progressBar;
    private CheckBox checkBox;
    private String userId;
    private Handler handler;

    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        initViews();

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });


        btnReg.setOnClickListener(v -> {

            if (Utils.isConnected(getBaseContext())) {

                String name = edtName.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                String fecha = edtFecha.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtName.setError(getString(R.string.error_nombre));
                    return;

                // the name 'client' is reserved for the orders and bills that are created by the administration in a custom manner
                } else if (name.equals("Cliente") || name.equals("cliente") || name.equals("CLIENTE")) {
                    edtName.setError(getString(R.string.client_name_error));
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    edtPhone.setError(getString(R.string.error_telefono));
                    return;
                }
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
                if (TextUtils.isEmpty(fecha)) {
                    edtFecha.setError(getString(R.string.error_fecha_nacimiento));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(App.executor, task -> {
                            if (task.isSuccessful()) {
                                fMessaging.getToken().addOnSuccessListener(App.executor, s -> {
                                    userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                    DocumentReference documentReference = fStore.collection("usuarios").document(userId);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put(Utils.KEY_NOMBRE, name);
                                    user.put(Utils.TELEFONO_ACCENT, phone);
                                    user.put(Utils.IS_PRESENT, false);
                                    user.put(Utils.KEY_MESA, "00");
                                    user.put(Utils.KEY_TOKEN, s);
                                    user.put(Utils.KEY_BONOS, 0);
                                    user.put(Utils.KEY_FECHA_DE_NACIMIENTO, fecha);
                                    user.put(Utils.EMAIL, fAuth.getCurrentUser().getEmail());
                                    documentReference.set(user).addOnSuccessListener(aVoid -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        startActivity(MainActivity.newIntent(getBaseContext()));
                                    });

                                });
                            } else {
                                handler.post(() -> {
                                    Toast.makeText(RegistrationActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);

                                });
                            }
                        });

            } else {
                Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initViews() {
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnReg = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        checkBox = findViewById(R.id.checkBox);
        edtFecha = findViewById(R.id.edtFechaNacimiento);
        handler = new Handler();
    }

}
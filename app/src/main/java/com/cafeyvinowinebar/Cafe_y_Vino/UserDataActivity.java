package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

/**
 * Displays the user's name, phone number, and email; with the possibility to update this data
 * The data is updated with inner static classes that implement Runnable
 */

public class UserDataActivity extends AppCompatActivity {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private Button btnLogOut, btnNombre, btnEmail, btnTelefono;
    private TextView txtInfoName, txtInfoEmail, txtInfoTelefono;
    private String userName, userEmail, userTelefono;
    FirebaseUser user;
    DocumentReference userDoc;
    Handler mainHandler;

    public static Intent newIntent(Context context) {
        return new Intent(context, UserDataActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        init();

        btnLogOut.setOnClickListener(v -> {

            if (Utils.isConnected(getBaseContext())) {

                fAuth.signOut();
                Intent intent = new Intent(getBaseContext(), WalkthroughActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });

        btnTelefono.setOnClickListener(v -> {

            if (Utils.isConnected(getBaseContext())) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.user_data_et, null);
                EditText et = view.findViewById(R.id.edtUserEt);
                et.setHint(getString(R.string.new_phone));
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(view);
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.cambiar), (dialog, which) -> {

                    App.executor.submit(new TelefonoChanger(et, mainHandler, getBaseContext(), userDoc));

                });

                builder.create().show();

            } else {
                Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });

        btnNombre.setOnClickListener(v -> {

            if (Utils.isConnected(getBaseContext())) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.user_data_et, null);
                EditText et = view.findViewById(R.id.edtUserEt);
                et.setHint(getString(R.string.new_name));
                builder.setView(view);
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.cambiar), (dialog, which) -> {

                    App.executor.submit(new NameChanger(et, mainHandler, getBaseContext(), userDoc));

                });
                builder.create().show();

            } else {
                Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });

        btnEmail.setOnClickListener(v -> {

            if (Utils.isConnected(getBaseContext())) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.user_data_et, null);
                EditText et = view.findViewById(R.id.edtUserEt);
                et.setHint(getString(R.string.new_email));
                et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(view);
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.cambiar), (dialog, which) -> {

                    App.executor.submit(new EmailChanger(et, user, getBaseContext(), mainHandler));

                });
                builder.create().show();

            } else {
                Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        userDoc.addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                return;
            }
            assert value != null;
            userTelefono = value.getString(Utils.TELEFONO_ACCENT);
            userName = value.getString(Utils.KEY_NOMBRE);
            userEmail = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
            txtInfoEmail.setText(userEmail);
            txtInfoName.setText(userName);
            txtInfoTelefono.setText(userTelefono);
        });
    }

    private void init() {
        mainHandler = new Handler();
        btnNombre = findViewById(R.id.btnNombre);
        btnEmail = findViewById(R.id.btnEmail);
        btnTelefono = findViewById(R.id.btnTelefono);
        btnLogOut = findViewById(R.id.btnLogOut);
        txtInfoEmail = findViewById(R.id.txtInfoEmail);
        txtInfoName = findViewById(R.id.txtInfoName);
        txtInfoTelefono = findViewById(R.id.txtInfoTelefono);
        user = fAuth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        userDoc = fStore.collection("usuarios")
                .document(userId);
    }

    static class EmailChanger implements Runnable {

        private final EditText et;
        private final FirebaseUser user;
        private final Context context;
        private final Handler mainHandler;

        public EmailChanger(EditText et, FirebaseUser user, Context context, Handler mainHandler) {
            this.et = et;
            this.user = user;
            this.context = context;
            this.mainHandler = mainHandler;
        }

        @Override
        public void run() {
            String email = et.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.user_empty_field_email), Toast.LENGTH_SHORT).show());
            } else {
                user.updateEmail(email)
                        .addOnFailureListener(e -> mainHandler.post(() ->
                                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()));
            }
        }
    }

    static class NameChanger implements Runnable {

        private final EditText et;
        private final Handler mainHandler;
        private final Context context;
        private final DocumentReference userDoc;

        public NameChanger(EditText et, Handler mainHandler, Context context, DocumentReference userDoc) {
            this.et = et;
            this.mainHandler = mainHandler;
            this.context = context;
            this.userDoc = userDoc;
        }

        @Override
        public void run() {
            String name = et.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.user_empty_field_name), Toast.LENGTH_SHORT).show());
            } else {
                userDoc.update(Utils.KEY_NOMBRE, name);
            }
        }
    }

    static class TelefonoChanger implements Runnable {

        private final EditText et;
        private final Handler mainHandler;
        private final Context context;
        private final DocumentReference userDoc;

        public TelefonoChanger(EditText et, Handler mainHandler, Context context, DocumentReference userDoc) {
            this.et = et;
            this.mainHandler = mainHandler;
            this.context = context;
            this.userDoc = userDoc;
        }

        @Override
        public void run() {
            String telefono = et.getText().toString().trim();
            if (TextUtils.isEmpty(telefono)) {
                mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.user_empty_field_phone), Toast.LENGTH_SHORT).show());

            } else {
                userDoc.update(Utils.TELEFONO_ACCENT, telefono);
            }
        }
    }

}
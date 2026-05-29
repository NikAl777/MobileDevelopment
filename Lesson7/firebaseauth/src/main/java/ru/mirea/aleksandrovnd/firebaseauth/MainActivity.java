package ru.mirea.aleksandrovnd.firebaseauth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mirea.aleksandrovnd.firebaseauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            updateUI(user);
        };

        binding.createAccountButton.setOnClickListener(view -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            createAccount(email, password);
        });

        binding.signInButton.setOnClickListener(view -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            signIn(email, password);
        });

        binding.signOutButton.setOnClickListener(view -> signOut());

        binding.verifyEmailButton.setOnClickListener(view -> sendEmailVerification());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth != null && authStateListener != null) {
            mAuth.addAuthStateListener(authStateListener);
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuth != null && authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Введите email");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Некорректный email");
            valid = false;
        } else {
            binding.emailEditText.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Введите пароль");
            valid = false;
        } else if (password.length() < 6) {
            binding.passwordEditText.setError("Минимум 6 символов");
            valid = false;
        } else {
            binding.passwordEditText.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount: " + email);

        if (!validateForm(email, password)) {
            return;
        }

        binding.createAccountButton.setEnabled(false);
        binding.signInButton.setEnabled(false);
        binding.statusTextView.setText("Регистрация...");
        binding.detailTextView.setText("Ожидание ответа Firebase...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.createAccountButton.setEnabled(true);
                    binding.signInButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Toast.makeText(
                                MainActivity.this,
                                "Аккаунт создан",
                                Toast.LENGTH_SHORT
                        ).show();

                        updateUI(user);

                    } else {
                        Exception exception = task.getException();

                        String message = exception != null
                                ? exception.getMessage()
                                : "Ошибка регистрации";

                        binding.statusTextView.setText("Ошибка регистрации");
                        binding.detailTextView.setText(message);

                        Toast.makeText(
                                MainActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        updateUI(null);
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn: " + email);

        if (!validateForm(email, password)) {
            return;
        }

        binding.signInButton.setEnabled(false);
        binding.createAccountButton.setEnabled(false);
        binding.statusTextView.setText("Вход...");
        binding.detailTextView.setText("Ожидание ответа Firebase...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.signInButton.setEnabled(true);
                    binding.createAccountButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Toast.makeText(
                                MainActivity.this,
                                "Вход выполнен",
                                Toast.LENGTH_SHORT
                        ).show();

                        updateUI(user);

                    } else {
                        Exception exception = task.getException();

                        String message = exception != null
                                ? exception.getMessage()
                                : "Ошибка входа";

                        binding.statusTextView.setText(R.string.auth_failed);
                        binding.detailTextView.setText(message);

                        Toast.makeText(
                                MainActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();

        Toast.makeText(
                MainActivity.this,
                "Выход выполнен",
                Toast.LENGTH_SHORT
        ).show();

        updateUI(null);
    }

    private void sendEmailVerification() {
        binding.verifyEmailButton.setEnabled(false);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            binding.verifyEmailButton.setEnabled(true);
            updateUI(null);
            return;
        }

        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    binding.verifyEmailButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(
                                MainActivity.this,
                                "Письмо отправлено на " + user.getEmail(),
                                Toast.LENGTH_LONG
                        ).show();

                    } else {
                        Exception exception = task.getException();

                        String message = exception != null
                                ? exception.getMessage()
                                : "Не удалось отправить письмо";

                        Toast.makeText(
                                MainActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            binding.statusTextView.setText(
                    getString(
                            R.string.emailpassword_status_fmt,
                            user.getEmail(),
                            user.isEmailVerified()
                    )
            );

            binding.detailTextView.setText(
                    getString(R.string.firebase_status_fmt, user.getUid())
            );

            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);
            binding.signedInButtons.setVisibility(View.VISIBLE);

            binding.verifyEmailButton.setEnabled(!user.isEmailVerified());

        } else {
            binding.statusTextView.setText(R.string.signed_out);
            binding.detailTextView.setText("Firebase UID: —");

            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);
            binding.signedInButtons.setVisibility(View.GONE);

            binding.signInButton.setEnabled(true);
            binding.createAccountButton.setEnabled(true);
        }
    }
}
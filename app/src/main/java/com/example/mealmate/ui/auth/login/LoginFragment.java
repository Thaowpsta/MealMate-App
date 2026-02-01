package com.example.mealmate.ui.auth.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.main.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class LoginFragment extends Fragment implements LoginContract.View {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button guestButton;
//    private ProgressBar progressBar;
    private CallbackManager callbackManager; // Facebook
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private LoginPresenter presenter;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new LoginPresenter(this, new UserRepository(requireContext()));

        callbackManager = CallbackManager.Factory.create();
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        presenter.onGoogleResultReceived(result.getData());
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.email);
        passwordInput = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login);
        guestButton = view.findViewById(R.id.guest);
        TextView signUpText = view.findViewById(R.id.sign_up);
//        progressBar = view.findViewById(R.id.progress_bar);
        ImageView googleBtn = view.findViewById(R.id.btn_google);
        ImageView facebookBtn = view.findViewById(R.id.btn_facebook);

        signUpText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        signUpText.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment)
        );


        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            presenter.login(email, password);
        });

        guestButton.setOnClickListener(v -> presenter.loginGuest());

        googleBtn.setOnClickListener(v -> presenter.onGoogleSignInClicked(requireActivity()));

        facebookBtn.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile")));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                presenter.onFacebookTokenReceived(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Facebook Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                onLoginError(error.getMessage());
            }
        });
    }

    @Override
    public void launchGoogleSignIn(Intent intent) {
        googleSignInLauncher.launch(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showProgress() {
//        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (loginButton != null) loginButton.setEnabled(false);
        if (guestButton != null) guestButton.setEnabled(false);
    }

    @Override
    public void hideProgress() {
//        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (loginButton != null) loginButton.setEnabled(true);
        if (guestButton != null) guestButton.setEnabled(true);
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }

    }

    @Override
    public void onLoginError(String error) {
        Toast.makeText(getContext(), "Login Failed: " + error, Toast.LENGTH_SHORT).show();
        Log.i("Facebook", error);
    }

    @Override
    public void showEmailError(String error) {
        emailInput.setError(error);
        emailInput.requestFocus();
    }

    @Override
    public void showPasswordError(String error) {
        passwordInput.setError(error);
        passwordInput.requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
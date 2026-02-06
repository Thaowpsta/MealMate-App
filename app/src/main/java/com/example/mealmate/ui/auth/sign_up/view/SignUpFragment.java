package com.example.mealmate.ui.auth.sign_up.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mealmate.R;
import com.example.mealmate.data.repositories.UserRepository;
import com.example.mealmate.ui.auth.sign_up.presenter.SignUpPresenterImp;
import com.example.mealmate.ui.main.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class SignUpFragment extends Fragment implements SignUpView{

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button signUpButton;
    private ProgressBar progressBar;
    private Button guestButton;

    private SignUpPresenterImp presenter;
    private CallbackManager callbackManager; // Facebook
    private ActivityResultLauncher<Intent> googleSignInLauncher;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new SignUpPresenterImp(this, new UserRepository(requireContext()));

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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameInput = view.findViewById(R.id.name);
        emailInput = view.findViewById(R.id.email);
        passwordInput = view.findViewById(R.id.password);
//        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        guestButton = view.findViewById(R.id.guest);
        signUpButton = view.findViewById(R.id.sign_up);
        TextView loginText = view.findViewById(R.id.login);
//        progressBar = view.findViewById(R.id.progress_bar);
        ImageView googleBtn = view.findViewById(R.id.ic_google);
        ImageView facebookBtn = view.findViewById(R.id.ic_facebook);


        loginText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        loginText.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment);
        });

        signUpButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
//            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            presenter.signup(name, email, password);
        });

        if (googleBtn != null) {
            googleBtn.setOnClickListener(v -> presenter.onGoogleSignInClicked(requireActivity()));
        }

        if (facebookBtn != null) {
            facebookBtn.setOnClickListener(v -> {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            });
        }

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                presenter.onFacebookTokenReceived(loginResult.getAccessToken().getToken());
            }
            @Override
            public void onCancel() {
                if (getView() != null) {
                    Snackbar.make(getView(), R.string.facebook_login_cancelled, Snackbar.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(), R.string.facebook_login_cancelled, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                onSignUpError(error.getMessage());
            }
        });

        guestButton.setOnClickListener(v -> presenter.loginGuest());

    }

    @Override
    public void showProgress() {
//        progressBar.setVisibility(View.VISIBLE);
        signUpButton.setEnabled(false);
    }

    @Override
    public void hideProgress() {
//        progressBar.setVisibility(View.GONE);
        signUpButton.setEnabled(true);
    }

    @Override
    public void onSignUpSuccess() {
        if (getView() != null) {
            Snackbar.make(getView(), R.string.sign_up_successful_please_login, Snackbar.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), R.string.sign_up_successful_please_login, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_loginFragment);
    }

    @Override
    public void onGuestLoginSuccess() {
        if (getView() != null) {
            Snackbar.make(getView(), R.string.login_successful, Snackbar.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), R.string.login_successful, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }


    @Override
    public void onSignUpError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), R.string.sign_up_successful_please_login, Snackbar.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), getString(R.string.sign_up_failed) + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNameError(String error) {
        nameInput.setError(error);
        nameInput.requestFocus();
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
    public void showConfirmPasswordError(String error) {
        confirmPasswordInput.setError(error);
        confirmPasswordInput.requestFocus();
    }

    @Override
    public void launchGoogleSignIn(Intent intent) {
        googleSignInLauncher.launch(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}

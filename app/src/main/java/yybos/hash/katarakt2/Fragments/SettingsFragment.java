package yybos.hash.katarakt2.Fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;

public class SettingsFragment extends Fragment {
    private MainActivity mainActivityInstance;

    private boolean inLogin = true;

    private FrameLayout frameLayout;
    private EditText username;
    private EditText email;
    private EditText password;
    private Button loginButton;

    private View root;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.root = getView();
        if (this.root == null)
            return;

        this.frameLayout = this.root.findViewById(R.id.loginFrameLayout);
        TextView switchButton = this.root.findViewById(R.id.loginSwitchButton);

        // inflate the login layout box and put it into the frame layout
        frameLayout.addView(LayoutInflater.from(this.getContext()).inflate(R.layout.layout_login_box, frameLayout, false));

        // its here because of the login layout
        this.email = this.root.findViewById(R.id.loginEmailEdittext);
        this.password = this.root.findViewById(R.id.loginPasswordEdittext);
        this.loginButton = this.root.findViewById(R.id.loginButton);

        this.mainActivityInstance = ((MainActivity) requireActivity());

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        loginButton.setOnClickListener(v -> {
            this.buttonClickAnimation(v);

            this.mainActivityInstance.setLoginEmail(this.email.getText().toString());
            this.mainActivityInstance.setLoginPassword(this.password.getText().toString());

            this.email.setText("");
            this.password.setText("");

            if (!this.inLogin) {
                this.mainActivityInstance.setLoginUsername(this.username.getText().toString());
                this.username.setText("");
            }
        });
        switchButton.setOnClickListener(this::switchBox);
    }

    // button click animation
    private void buttonClickAnimation (View v) {
        Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.button_clicked);
        animation.setRepeatMode(ValueAnimator.REVERSE);

        v.startAnimation(animation);
    }

    private void switchBox (View v) {
        if (inLogin) {
            inLogin = false;

            frameLayout.removeAllViews();
            frameLayout.addView(LayoutInflater.from(this.getContext()).inflate(R.layout.layout_register_box, frameLayout, false));

            this.email = this.root.findViewById(R.id.loginEmailEdittext);
            this.password = this.root.findViewById(R.id.loginPasswordEdittext);
            this.loginButton = this.root.findViewById(R.id.loginButton);

            ((TextView) v).setText("Already have an account? You shouldn't be here");
        }
        else {
            inLogin = true;

            frameLayout.removeAllViews();
            frameLayout.addView(LayoutInflater.from(this.getContext()).inflate(R.layout.layout_login_box, frameLayout, false));

            this.email = this.root.findViewById(R.id.registerEmailEdittext);
            this.password = this.root.findViewById(R.id.registerPasswordEdittext);
            this.loginButton = this.root.findViewById(R.id.registerButton);

            ((TextView) v).setText("Don't have an account yet? What a crime");
        }
    }
}
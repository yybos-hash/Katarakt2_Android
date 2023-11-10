package yybos.hash.katarakt2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.ChatFragment;
import yybos.hash.katarakt2.Fragments.LoginFragment;
import yybos.hash.katarakt2.Fragments.SettingsFragment;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class MainActivity extends AppCompatActivity {
    private View selectedTab;
    private View selectionTab;

    private ImageView buttonChat;
    private ImageView buttonSettings;
    private ImageView buttonLogin;

    private Client client;
    private List<Message> history;

    private String loginUsername;
    private String loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        selectionTab = findViewById(R.id.selectionTab);

        buttonChat = findViewById(R.id.buttonChat);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonLogin = findViewById(R.id.buttonLogin);

        selectedTab = null;

        buttonChat.setOnClickListener(this::tabPressed);
        buttonSettings.setOnClickListener(this::tabPressed);
        buttonLogin.setOnClickListener(this::tabPressed);

        this.history = new ArrayList<>();
        this.client = new Client(this.history);

        Thread t1 = new Thread(client::connect);
        t1.start();
    }

    private void tabPressed (View view) {
        if (view == selectedTab)
            return;

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);

        ValueAnimator anim;
        anim = ObjectAnimator.ofFloat(selectedTab != null ? (selectedTab.getX() - 55) : view.getX(), view.getX() - 55);
        //                                              :Sex_Penis:

        selectedTab = view;

        if (view == buttonChat)
            fragmentManager.replace(R.id.fragmentContainerView, new ChatFragment());

        else if (view == buttonSettings)
            fragmentManager.replace(R.id.fragmentContainerView, new SettingsFragment());

        else if (view == buttonLogin)
            fragmentManager.replace(R.id.fragmentContainerView, new LoginFragment());


        fragmentManager.addToBackStack(null).commit();

        anim.addUpdateListener(valueAnimator -> selectionTab.setX((float) valueAnimator.getAnimatedValue()));
        anim.start();
    }

    // login

    public void setLoginUsername (String username) {
        this.loginUsername = username;
    }
    public void setLoginPassword (String password) {
        this.loginPassword = password;
    }

    // message history

    public List<Message> getHistory () {
        return this.history;
    }

    // client listener

    public void addListener (ClientInterface clientInterface) {
        this.client.addMessageListener(clientInterface);
    }
    public void removeListener (ClientInterface clientInterface) {
        this.client.removeMessageListener(clientInterface);
    }
}
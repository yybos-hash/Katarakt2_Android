package yybos.hash.katarakt2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.ChatFragment;
import yybos.hash.katarakt2.Fragments.LoginFragment;
import yybos.hash.katarakt2.Fragments.SettingsFragment;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class MainActivity extends AppCompatActivity {
    private View selectedTab;
    private View selectionTab;

    private ImageView buttonChat;
    private ImageView buttonSettings;
    private ImageView buttonLogin;

    private Client client;
    private List<Message> history;
    private List<Chat> chats;

    private String loginEmail;
    private String loginPassword;

    public int currentChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        selectionTab = findViewById(R.id.activitySelectionTab);

        buttonChat = findViewById(R.id.activityButtonChat);
        buttonSettings = findViewById(R.id.activityButtonSettings);
        buttonLogin = findViewById(R.id.activityButtonLogin);

        selectedTab = null;

        buttonChat.setOnClickListener(this::tabPressed);
        buttonSettings.setOnClickListener(this::tabPressed);
        buttonLogin.setOnClickListener(this::tabPressed);

        this.loginEmail = "plusmuriel@gmail.com";
        this.loginPassword = "123";

        this.history = new ArrayList<>();
        this.chats = new ArrayList<>();

        this.client = new Client(this.chats, this.history, this.loginEmail, this.loginPassword);

        this.client.tryConnection();
    }

    private void tabPressed (View view) {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);

        if (view == this.buttonChat)                                                  // see more in PopupErrorFragment.java about this tag (onDestroy())
            fragmentManager.replace(R.id.activityFragmentContainerView, new ChatFragment(), "chatFragmentInstance");

        else if (view == this.buttonSettings)
            fragmentManager.replace(R.id.activityFragmentContainerView, new SettingsFragment());

        else if (view == this.buttonLogin)
            fragmentManager.replace(R.id.activityFragmentContainerView, new LoginFragment());

        fragmentManager.addToBackStack(null).commit();
    }
    public void moveSelectionTab (Fragment fragment) {
        View view;

        if (fragment instanceof ChatFragment)
            view = this.buttonChat;

        else if (fragment instanceof SettingsFragment)
            view = this.buttonSettings;

        else if (fragment instanceof LoginFragment)
            view = this.buttonLogin;

        else
            return;

        ValueAnimator anim;
        anim = ObjectAnimator.ofFloat(selectedTab != null ? (selectedTab.getX() - 55) : view.getX(), view.getX() - 55);
        //                                              :Sex_Penis:

        this.selectedTab = view;

        anim.addUpdateListener(valueAnimator -> selectionTab.setX((float) valueAnimator.getAnimatedValue()));
        anim.start();
    }

    // login
    public void setLoginEmail(String username) {
        this.loginEmail = username;
    }
    public void setLoginPassword (String password) {
        this.loginPassword = password;
    }

    // histories
    public List<Message> getHistory () {
        return this.history;
    }
    public List<Chat> getChats () {
        return this.chats;
    }

    // client
    public Client getClient () {
        return this.client;
    }

    public void addListener (ClientInterface clientInterface) {
        this.client.addMessageListener(clientInterface);
    }
    public void removeListener (ClientInterface clientInterface) {
        this.client.removeMessageListener(clientInterface);
    }
}
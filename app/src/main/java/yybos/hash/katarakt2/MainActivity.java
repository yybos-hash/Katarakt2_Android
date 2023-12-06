package yybos.hash.katarakt2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.ChatFragment;
import yybos.hash.katarakt2.Fragments.CustomToastFragment;
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
    private List<Message> messageHistory;
    private List<Chat> chatsHistory;
    // chats history will hold chats while the chatsFragment is null (So basically while the chatsFragment is not displayed, it holds the chats and then adds them to the adapter)

    private String ipAddress;
    private int port;

    private String loginEmail;
    private String loginPassword;
    private String loginUsername;

    private ChatFragment chatFragmentInstance;
    private SettingsFragment settingsFragmentInstance;
    private CustomToastFragment customToastInstance;

    public int currentChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        this.selectionTab = findViewById(R.id.activitySelectionTab);

        this.buttonChat = findViewById(R.id.activityButtonChat);
        this.buttonSettings = findViewById(R.id.activityButtonSettings);
        this.buttonLogin = findViewById(R.id.activityButtonLogin);

        this.selectedTab = null;

        this.buttonChat.setOnClickListener(this::tabPressed);
        this.buttonSettings.setOnClickListener(this::tabPressed);
        this.buttonLogin.setOnClickListener(this::tabPressed);

        this.loginEmail = " ";
        this.loginPassword = " ";
        this.loginUsername = " ";
        // just to initialize it

        this.messageHistory = new ArrayList<>();
        this.chatsHistory = new ArrayList<>();

        this.client = new Client(this);
        this.client.tryConnection();

        this.tabPressed(this.buttonSettings);
    }

    private void tabPressed (View view) {
        if (view == this.selectedTab)
            return;

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);

        if (view == this.buttonChat)
            fragmentManager.replace(R.id.activityFragmentContainerView, new ChatFragment());

        else if (view == this.buttonSettings)
            fragmentManager.replace(R.id.activityFragmentContainerView, new SettingsFragment());

        else if (view == this.buttonLogin)
            fragmentManager.replace(R.id.activityFragmentContainerView, new LoginFragment());

        fragmentManager.addToBackStack(null).commit();
    }
    public void moveSelectionTab (Fragment fragment) {
        View view;

        if (fragment instanceof ChatFragment) {
            view = this.buttonChat;
            this.chatFragmentInstance = (ChatFragment) fragment;
        }
        else if (fragment instanceof SettingsFragment) {
            view = this.buttonSettings;
            this.settingsFragmentInstance = (SettingsFragment) fragment;
        }
        else if (fragment instanceof LoginFragment) {
            view = this.buttonLogin;
        }
        else
            return;

        ValueAnimator anim;
        anim = ObjectAnimator.ofFloat(selectedTab != null ? (selectedTab.getX() - 52) : selectionTab.getX(), view.getX() - 52);
        //                                              :Sex_Penis:

        this.selectedTab = view;

        anim.addUpdateListener(valueAnimator -> selectionTab.setX((float) valueAnimator.getAnimatedValue()));
        anim.start();
    }

    // login and client
    public void setLoginEmail(String email) {
        this.loginEmail = email.replace("\0", "");
    }
    public void setLoginPassword (String password) {
        this.loginPassword = password.replace("\0", "");
    }
    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername.replace("\0", "");
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String getLoginEmail () {
        return this.loginEmail;
    }
    public String getLoginPassword () {
        return this.loginPassword;
    }
    public String getLoginUsername() {
        return this.loginUsername;
    }
    public String getIpAddress() {
        return this.ipAddress;
    }
    public int getPort() {
        return this.port;
    }


    // histories
    public List<Message> getMessageHistory() {
        return this.messageHistory;
    }
    public List<Chat> getChatsHistory () {
        return this.chatsHistory;
    }

    // client
    public Client getClient () {
        return this.client;
    }

    public void addListener (ClientInterface clientInterface) {
        this.client.addEventListener(clientInterface);
    }
    public void removeListener (ClientInterface clientInterface) {
        this.client.removeEventListener(clientInterface);
    }

    // fragments

    public ChatFragment getChatFragmentInstance () {
        return this.chatFragmentInstance;
    }
    public SettingsFragment getSettingsFragmentInstance () {
        return this.settingsFragmentInstance;
    }

    public void showCustomToast (String message, int backgroundColor) {
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putInt("background", backgroundColor);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (this.customToastInstance != null && this.customToastInstance.isAdded()) {
            fragmentTransaction.remove(this.customToastInstance);
        }

        this.customToastInstance = new CustomToastFragment();
        this.customToastInstance.setArguments(args);

        // Set custom animations for entering and exiting the fragment
        fragmentTransaction.setCustomAnimations(R.anim.custom_toast_expand, R.anim.custom_toast_contract);

        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Replace the existing fragment with the new one
            fragmentTransaction.replace(R.id.customToastFragmentView, this.customToastInstance);

            // Commit the transaction
            fragmentTransaction.commitNow();
        }
        else {
            // Call a function on the UI thread
            this.runOnUiThread(() -> {
                // Replace the existing fragment with the new one
                fragmentTransaction.replace(R.id.customToastFragmentView, this.customToastInstance);

                // Commit the transaction
                fragmentTransaction.commitNow();
            });
        }
    }
}
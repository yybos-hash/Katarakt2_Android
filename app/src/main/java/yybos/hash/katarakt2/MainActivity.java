package yybos.hash.katarakt2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.ChatFragment;
import yybos.hash.katarakt2.Fragments.Custom.CustomToastFragment;
import yybos.hash.katarakt2.Fragments.LoginFragment;
import yybos.hash.katarakt2.Fragments.SettingsFragment;
import yybos.hash.katarakt2.Fragments.TerminalFragment;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class MainActivity extends AppCompatActivity {
    private View selectedTab;
    private View selectionTab;

    private FrameLayout buttonChat;
    private FrameLayout buttonSettings;
    private FrameLayout buttonLogin;

    private Client client;
    private List<Message> messageHistory;
    private String loginUsername;

    private ChatFragment chatFragmentInstance;
    private CustomToastFragment customToastInstance;

    public int currentChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        this.loginUsername = "";

        this.selectionTab = findViewById(R.id.activitySelectionTab);

        this.buttonChat = findViewById(R.id.activityButtonChat);
        this.buttonSettings = findViewById(R.id.activityButtonSettings);
        this.buttonLogin = findViewById(R.id.activityButtonLogin);

        this.selectedTab = null;

        this.buttonChat.setOnClickListener(this::tabPressed);
        this.buttonSettings.setOnClickListener(this::tabPressed);
        this.buttonLogin.setOnClickListener(this::tabPressed);

        this.messageHistory = new ArrayList<>();

        this.client = new Client(this);
    }

    private void tabPressed (View view) {
        if (view == this.selectedTab)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.isStateSaved())
            return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);

        if (view == this.buttonChat)
            transaction.replace(R.id.activityFragmentContainerView, new ChatFragment());

        else if (view == this.buttonLogin)
            transaction.replace(R.id.activityFragmentContainerView, new LoginFragment());

        else if (view == this.buttonSettings)
            transaction.replace(R.id.activityFragmentContainerView, new SettingsFragment());

        transaction.addToBackStack(null).commit();
    }
    public void moveSelectionTab (Fragment fragment) {
        View view;

        if (fragment instanceof ChatFragment) {
            view = this.buttonChat;
            this.chatFragmentInstance = (ChatFragment) fragment;
        }
        else if (fragment instanceof LoginFragment) {
            view = this.buttonLogin;
        }
        else if (fragment instanceof SettingsFragment) {
            view = this.buttonSettings;
        }
        else
            return;

        this.selectedTab = view;

        float targetX = view.getX() + ((float) view.getWidth() - (float) this.selectionTab.getWidth()) / 2;

        ValueAnimator anim = ObjectAnimator.ofFloat(this.selectionTab.getX(), targetX);
        anim.addUpdateListener(valueAnimator -> this.selectionTab.setX((float) valueAnimator.getAnimatedValue()));
        anim.start();
    }

    public void openTerminal () {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.isStateSaved())
            return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activityFragmentContainerView, new TerminalFragment());
        transaction.commit();
    }

    // login and client

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername.replace("\0", "");
    }
    public String getLoginUsername() {
        return this.loginUsername;
    }

    // histories

    public List<Message> getMessageHistory() {
        return this.messageHistory;
    }

    // client

    public Client getClient () {
        return this.client;
    }

    public void addClientListener(ClientInterface clientInterface) {
        this.client.addEventListener(clientInterface);
    }
    public void removeClientListener(ClientInterface clientInterface) {
        this.client.removeEventListener(clientInterface);
    }

    // fragments

    public ChatFragment getChatFragmentInstance () {
        return this.chatFragmentInstance;
    }

    public synchronized void showCustomToast (String message, int backgroundColor) {
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putInt("background", backgroundColor);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.isStateSaved())
            fragmentManager.executePendingTransactions();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (this.customToastInstance != null && this.customToastInstance.isAdded()) {
            transaction.remove(this.customToastInstance);
        }

        this.customToastInstance = new CustomToastFragment();
        this.customToastInstance.setArguments(args);

        // Set custom animations for entering and exiting the fragment
        transaction.setCustomAnimations(R.anim.custom_toast_expand, R.anim.custom_toast_contract);

        // Replace the existing fragment with the new one
        transaction.replace(R.id.customToastFragmentView, this.customToastInstance);

        // Use a Handler to post the transaction on the main thread
        // Commit the transaction
        new Handler(Looper.getMainLooper()).post(transaction::commitNow);
    }
}
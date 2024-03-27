package yybos.hash.katarakt2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.AnimeFragment;
import yybos.hash.katarakt2.Fragments.Custom.CustomToastFragment;
import yybos.hash.katarakt2.Fragments.FolderFragment;
import yybos.hash.katarakt2.Fragments.LoginFragment;
import yybos.hash.katarakt2.Fragments.MessagesFragment;
import yybos.hash.katarakt2.Fragments.SettingsFragment;
import yybos.hash.katarakt2.Fragments.TerminalFragment;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Constants;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class MainActivity extends AppCompatActivity {
    private View selectedTab;
    private View selectionTab;

    private FrameLayout buttonChat;
    private FrameLayout buttonSettings;
    private FrameLayout buttonLogin;
    private FrameLayout buttonAnime;
    private FrameLayout buttonFolder;

    private Client client;
    private List<PacketObject> messageHistory;
    private String loginUsername;

    private MessagesFragment messagesFragmentInstance;
    private CustomToastFragment customToastInstance;

    public int currentChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        this.setFragment(new WelcomeFragment(), false);

        this.loginUsername = "";

        this.selectionTab = findViewById(R.id.activitySelectionTab);
        this.selectionTab.setAlpha(0f);

        this.buttonChat = findViewById(R.id.activityButtonChat);
        this.buttonSettings = findViewById(R.id.activityButtonSettings);
        this.buttonLogin = findViewById(R.id.activityButtonLogin);
        this.buttonAnime = findViewById(R.id.activityButtonAnime);
        this.buttonFolder = findViewById(R.id.activityButtonFolder);

        this.selectedTab = null;

        this.buttonChat.setOnClickListener(this::tabPressed);
        this.buttonSettings.setOnClickListener(this::tabPressed);
        this.buttonLogin.setOnClickListener(this::tabPressed);
        this.buttonAnime.setOnClickListener(this::tabPressed);
        this.buttonFolder.setOnClickListener(this::tabPressed);

        this.messageHistory = new ArrayList<>();

        this.client = new Client(this);

        // read default server and connect to it
        new Thread(() -> {
            String content = MainActivity.this.readFileFromInternalStorage(this, Constants.defaultServerFilename);
            if (content.isEmpty())
                return;

            Server defaultServer = MainActivity.this.parseJsonString(content);
            MainActivity.this.client.tryConnection(defaultServer);
        }).start();
    }

    private void tabPressed (View view) {
        if (view == this.selectedTab)
            return;

        if (view == this.buttonChat)
            this.setFragment(new MessagesFragment(), true);

        else if (view == this.buttonLogin)
            this.setFragment(new LoginFragment(), true);

        else if (view == this.buttonSettings)
            this.setFragment(new SettingsFragment(), true);

        else if (view == this.buttonAnime)
            this.setFragment(new AnimeFragment(), true);

        else if (view == this.buttonFolder)
            this.setFragment(new FolderFragment(), true);
    }
    public void moveSelectionTab (Fragment fragment) {
        View view;

        if (fragment instanceof MessagesFragment) {
            view = this.buttonChat;
            this.messagesFragmentInstance = (MessagesFragment) fragment;
        }
        else if (fragment instanceof LoginFragment) {
            view = this.buttonLogin;
        }
        else if (fragment instanceof SettingsFragment) {
            view = this.buttonSettings;
        }
        else if (fragment instanceof AnimeFragment) {
            view = this.buttonAnime;
        }
        else if (fragment instanceof FolderFragment) {
            view = this.buttonFolder;
        }
        else
            return;

        this.selectedTab = view;

        float targetX = view.getX() + ((float) view.getWidth() - (float) this.selectionTab.getWidth()) / 2;

        ValueAnimator anim = ObjectAnimator.ofFloat(this.selectionTab.getX(), targetX);
        anim.addUpdateListener(valueAnimator -> this.selectionTab.setX((float) valueAnimator.getAnimatedValue()));
        anim.setDuration(300);
        anim.start();

        if (this.selectionTab.getAlpha() == 0f) {
            anim = ObjectAnimator.ofFloat(0f, 1f);
            anim.addUpdateListener(valueAnimator -> this.selectionTab.setAlpha((float) valueAnimator.getAnimatedValue()));
            anim.setDuration(300);
            anim.start();
        }
    }
    private void setFragment (Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.isStateSaved())
            fragmentManager.executePendingTransactions();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        transaction.replace(R.id.activityFragmentContainerView, fragment);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openTerminal () {
        this.setFragment(new TerminalFragment(), false);
    }

    // login and client
    public void setLoginUsername (String loginUsername) {
        this.loginUsername = loginUsername.replace("\0", "");
    }
    public String getLoginUsername () {
        return this.loginUsername;
    }

    // histories
    public List<PacketObject> getMessageHistory () {
        return this.messageHistory;
    }

    // client
    public Client getClient () {
        return this.client;
    }
    public void addClientListener (ClientInterface clientInterface) {
        this.client.addEventListener(clientInterface);
    }
    public void removeClientListener (ClientInterface clientInterface) {
        this.client.removeEventListener(clientInterface);
    }

    // fragments
    public MessagesFragment getChatFragmentInstance () {
        return this.messagesFragmentInstance;
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

    // read default server
    public String readFileFromInternalStorage (Context context, String fileName) {
        StringBuilder content = new StringBuilder();

        try {
            File serversFile = new File(context.getFilesDir(), fileName);
            if (!serversFile.exists())
                serversFile.createNewFile();

            // Open the file for reading
            FileInputStream fileInputStream = context.openFileInput(fileName);

            // Wrap the FileInputStream in an InputStreamReader
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            // Wrap the InputStreamReader in a BufferedReader
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Read each line from the file
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }

            // Close the streams
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }
    public Server parseJsonString (String jsonString) {
        if (jsonString.isEmpty())
            return null;

        Gson gson = new Gson();

        // Deserialize the JSON string into a list of Server objects
        return gson.fromJson(jsonString, Server.class);
    }
}
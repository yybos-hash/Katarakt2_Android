package yybos.hash.katarakt2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import java.util.List;

import yybos.hash.katarakt2.Fragments.ChatFragment;
import yybos.hash.katarakt2.Fragments.SettingsFragment;
import yybos.hash.katarakt2.Fragments.ViewModels.ChatViewModel;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class MainActivity extends AppCompatActivity {
    private View selectedTab;
    private View selectionTab;
    private ImageView buttonChat;
    private ImageView buttonSettings;

    private Client client;
    private ChatViewModel history;

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

        selectedTab = null;

        buttonChat.setOnClickListener(this::tabPressed);
        buttonSettings.setOnClickListener(this::tabPressed);

        this.history = new ChatViewModel();
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
        if (view == buttonChat) {
            anim = ObjectAnimator.ofFloat(selectionTab.getX(), view.getX() - 60);
            selectedTab = buttonChat;

            ChatFragment newFragment = new ChatFragment();
            fragmentManager.replace(R.id.fragmentContainerView, newFragment);
        }
        else {
            anim = ObjectAnimator.ofFloat(selectionTab.getX(), view.getX() - 60);
            selectedTab = buttonSettings;

            SettingsFragment newFragment = new SettingsFragment();
            fragmentManager.replace(R.id.fragmentContainerView, newFragment);
        }

        fragmentManager.addToBackStack(null).commit();

        anim.addUpdateListener(valueAnimator -> {
            selectionTab.setX((float) valueAnimator.getAnimatedValue());
        });
        anim.start();
    }

    public Client getClientConnection () {
        return this.client;
    }
    public ChatViewModel getViewModel () {
        return this.history;
    }
}
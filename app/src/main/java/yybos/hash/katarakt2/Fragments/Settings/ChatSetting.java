package yybos.hash.katarakt2.Fragments.Settings;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.Custom.CustomSpinnerFragment;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Command;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatSetting extends Fragment implements ClientInterface {
    private final List<Chat> chats = new ArrayList<>();
    private MainActivity mainActivityInstance;

    private FragmentContainerView spinnerContainer;

    public ChatSetting () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mainActivityInstance = ((MainActivity) requireActivity());
        this.mainActivityInstance.addListener(this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.setting_chat, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.spinnerContainer = root.findViewById(R.id.settingChatContainer);
        this.spinnerContainer.setId(View.generateViewId());

        CustomSpinnerFragment customSpinner = new CustomSpinnerFragment(this.chats);

        ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                .add(this.spinnerContainer.getId(), customSpinner)
                .commit();

        if (mainActivityInstance.getClient().isConnected()) {
            mainActivityInstance.getClient().sendCommand(Command.getChats());
        }
    }

    @Override
    public void onDestroyView () {
        this.mainActivityInstance.removeListener(this);

        super.onDestroyView();
    }

    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onChatReceived(Chat chat) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chats.add(chat);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chats.add(chat);
            });
        }
    }
}

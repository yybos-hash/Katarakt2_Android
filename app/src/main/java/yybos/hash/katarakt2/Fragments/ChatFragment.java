package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import yybos.hash.katarakt2.Fragments.Adapters.ChatViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatFragment extends Fragment implements ClientInterface {
    private ConstraintLayout constraintLayout;

    private FrameLayout generalFrameLayout; // used for displaying things on the screen

    private EditText editText;
    private ImageView sendButton;
    private ImageView chatsButton;
    private RecyclerView recyclerView;

    private ChatViewAdapter chatAdapter;
    private List<Message> history;

    private MainActivity mainActivityInstance;
    private Client client;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainActivityInstance = ((MainActivity) requireActivity());

        // get client for sending messages, connecting, etc
        this.client = this.mainActivityInstance.getClient();

        // get message history if there are any messages previously sent
        this.history = this.mainActivityInstance.getHistory();

        // listen to incoming messages
        this.mainActivityInstance.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getView() == null)
            return;

        View root = getView();

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        this.constraintLayout = root.findViewById(R.id.chatConstraintLayout);

        this.editText = root.findViewById(R.id.chatEditText);
        this.chatsButton = root.findViewById(R.id.chatChatsButton);
        this.sendButton = root.findViewById(R.id.chatSendButton);

        this.chatAdapter = new ChatViewAdapter(null);

        this.chatsButton.setOnClickListener(this::displayChatsList);
        this.sendButton.setOnClickListener(this::sendMessage);

        this.recyclerView = root.findViewById(R.id.chatRecycler);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(this.chatAdapter);
        this.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // get rid of that 'wave' effect when trying to scroll beyond the limits of the linearLayout

        if (!this.client.isConnected())
            displayErrorMessage("Oh Noes!", "It looks like you are not connected. CONNECT, BITCH", "Ok :(", "Shut the fuck up");
    }

    public void sendMessage (View v) {
        String content = this.editText.getText().toString().trim();
        this.editText.setText("");

        Message message = Message.toMessage(Message.Type.Message, content, this.mainActivityInstance.currentChatId, this.client.user.getName(), this.client.user.getId());

        this.client.sendMessage(message);
        this.chatAdapter.addMessage(message);
        this.recyclerView.scrollToPosition(this.chatAdapter.getItemCount() - 1);
    }

    // display list of chats
    public void displayChatsList (View v) {
        if (this.getContext() == null)
            return;

        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.getContext());
        this.generalFrameLayout.setId(View.generateViewId());

        FrameLayout.LayoutParams fragmentFrameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayout.gravity = Gravity.END;

        // add fragment to frame layout
        ChatsFragment fragment = new ChatsFragment();

        // initiate fragment manager and
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(this.generalFrameLayout.getId(), fragment);
        fragmentManager.commit();

        // add frame layout to constraintLayout
        this.constraintLayout.addView(this.generalFrameLayout, fragmentFrameLayout);
    }

    // display the error popup fragment
    public void displayErrorMessage (String title, String description, String firstButtonText, String secondButtonText) {
        if (this.getContext() == null)
            return;

        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.getContext());
        this.generalFrameLayout.setId(View.generateViewId());

        FrameLayout.LayoutParams fragmentFrameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayout.gravity = Gravity.CENTER;

        // add fragment to frame layout
        // set args for title, description, etc
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putString("firstButtonText", firstButtonText);
        args.putString("secondButtonText", secondButtonText);

        PopupErrorFragment fragment = new PopupErrorFragment();
        fragment.setArguments(args);

        // initiate fragment manager and
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(this.generalFrameLayout.getId(), fragment);
        fragmentManager.commit();

        // add frame layout to constraintLayout
        this.constraintLayout.addView(this.generalFrameLayout, fragmentFrameLayout);
    }
    public void removeGeneralFrameLayout() {
        this.generalFrameLayout.removeAllViews();
        this.constraintLayout.removeView(this.generalFrameLayout);
    }

    // remove listener once the fragment is destroyed
    @Override
    public void onDestroy () {
        this.mainActivityInstance.removeListener(this);
        super.onDestroy();
    }

    // messages
    @Override
    public void onMessageReceived(Message message) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatAdapter.addMessage(message);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chatAdapter.addMessage(message);
            });
        }
    }

    public void updateMessageHistory (int chatId) {
        this.chatAdapter.clear();
        this.chatAdapter.notifyDataSetChanged();
        // no other way, must use notifyDataSetChanged()

        this.client.getChatHistory(chatId);
        this.mainActivityInstance.currentChatId = chatId;
    }
}

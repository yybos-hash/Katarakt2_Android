package yybos.hash.katarakt2.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatFragment extends Fragment implements ClientInterface {
    private ConstraintLayout constraintLayout;

    private FrameLayout generalFrameLayout; // used for displaying things on the screen

    private EditText editText;
    private RecyclerView recyclerView;

    private ChatsFragment chatsFragment;
    private PopupErrorFragment popupErrorFragment;
    private InputPopupFragment inputPopupFragment;

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



        // changed all of this to the onViewCreated because of the back stack (the fragment is not destroyed neither created when its added to the back strack)
        // but the view is destroyed and created again, so ill be using that
        this.mainActivityInstance = ((MainActivity) requireActivity());

        // get client for sending messages, connecting, etc
        this.client = this.mainActivityInstance.getClient();

        // get message history if there are any messages previously sent
        this.history = this.mainActivityInstance.getMessageHistory();

        // listen to incoming messages
        this.mainActivityInstance.addListener(this);



        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        this.constraintLayout = root.findViewById(R.id.chatConstraintLayout);
        this.editText = root.findViewById(R.id.chatEditText);
        ImageView chatsButton = root.findViewById(R.id.chatChatsButton);
        ImageView sendButton = root.findViewById(R.id.chatSendButton);

        this.chatAdapter = new ChatViewAdapter(this.history);

        // this.buttonClickAnimation(v);
        chatsButton.setOnClickListener(this::displayChatsList);
        // this.buttonClickAnimation(v);
        sendButton.setOnClickListener(this::sendMessage);
        this.editText.setOnFocusChangeListener((v, focused) -> {
            if (focused) {
                if (ChatFragment.this.client == null || !ChatFragment.this.client.isConnected())
                    this.displayErrorMessage("Hold Up!", "You not connected = you not send message", "Alright, smart boy", "Bet");
            }
        });

        this.recyclerView = root.findViewById(R.id.chatRecycler);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(this.chatAdapter);
        this.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // get rid of that 'wave' effect when trying to scroll beyond the limits of the linearLayout

        if (this.chatAdapter.getItemCount() > 0)
            this.scrollToLastMessage();

        if (!this.client.isConnected()) {
             this.displayErrorMessage("Oh Noes!", "It looks like you are not connected. CONNECT, BITCH", "Ok :(", "Shut the fuck up");
        }
        if (this.client.isConnected() && this.mainActivityInstance.getLoginUsername().trim().isEmpty()) {
            this.displayInputPopup();
        }
    }

    // send message through client
    private void sendMessage (View v) {
        String content = this.editText.getText().toString().trim().replace("\0", ""); // just to make sure, remove any possible null characters
        if (content.trim().isEmpty())
            return;

        this.editText.setText("");

        Message message = Message.toMessage(content, this.mainActivityInstance.currentChatId, this.client.user.getUsername(), this.client.user.getId());

        this.client.sendMessage(message);
        this.chatAdapter.addMessage(message);

        this.scrollToLastMessage();
    }

    // display things
    private void displayChatsList (View v) {
        this.client.getChats();
        // get chats

        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.requireContext());
        this.generalFrameLayout.setId(View.generateViewId());
        this.generalFrameLayout.setOnClickListener(view -> this.closeChatsList());
        this.generalFrameLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));

        FrameLayout.LayoutParams fragmentFrameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayout.gravity = Gravity.END;

        // add fragment to frame layout
        this.chatsFragment = new ChatsFragment();

        // initiate fragment manager
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.chats_list_expand, R.anim.chats_list_contract);
        fragmentManager.add(this.generalFrameLayout.getId(), this.chatsFragment);
        fragmentManager.commit();

        // add frame layout to constraintLayout
        this.constraintLayout.addView(this.generalFrameLayout, fragmentFrameLayout);
    }
    public void displayErrorMessage (String title, String description, String firstButtonText, String secondButtonText) {
        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.requireContext());
        this.generalFrameLayout.setId(View.generateViewId());
        this.generalFrameLayout.setOnClickListener(view -> this.closeErrorMessage());
        this.generalFrameLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));

        FrameLayout.LayoutParams fragmentFrameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayout.gravity = Gravity.CENTER;

        // add fragment to frame layout
        // set args for title, description, etc
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putString("firstButtonText", firstButtonText);
        args.putString("secondButtonText", secondButtonText);

        this.popupErrorFragment = new PopupErrorFragment();
        this.popupErrorFragment.setArguments(args);

        // initiate fragment manager and
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(this.generalFrameLayout.getId(), this.popupErrorFragment);
        fragmentManager.commit();

        // add frame layout to constraintLayout
        this.constraintLayout.addView(this.generalFrameLayout, fragmentFrameLayout);
    }
    public void displayInputPopup () {
        if (this.getContext() == null)
            return;

        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.getContext());
        this.generalFrameLayout.setId(View.generateViewId());
        this.generalFrameLayout.setOnClickListener(view -> this.closeErrorMessage());

        FrameLayout.LayoutParams fragmentFrameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayout.gravity = Gravity.CENTER;

        this.inputPopupFragment = new InputPopupFragment();

        // initiate fragment manager and
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(this.generalFrameLayout.getId(), this.inputPopupFragment);
        fragmentManager.commit();

        // add frame layout to constraintLayout
        this.constraintLayout.addView(this.generalFrameLayout, fragmentFrameLayout);
    }

    public void closeInputPopup () {

    }
    public void closeErrorMessage () {
        if (this.popupErrorFragment == null)
            return;

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        transaction.remove(this.popupErrorFragment);
        transaction.commit();

        // remove after the animation finishes (125ms) // there is not a method to execute a function when the animation ends, so this is a substitute
        new Handler(Looper.getMainLooper()).postDelayed(this::removeGeneralFrameLayout, 10);
    }
    public void closeChatsList () {
        if (this.chatsFragment == null)
            return;

        this.chatsFragment.destroy();
    }
    public void removeGeneralFrameLayout() {
        this.generalFrameLayout.removeAllViews();
        this.constraintLayout.removeView(this.generalFrameLayout);
    }

    // remove listener once the fragment is destroyed
    @Override
    public void onDestroyView () {
        // remove listener
        this.mainActivityInstance.removeListener(this);

        super.onDestroyView();
    }

    // events
    @Override
    public void onMessageReceived (Message message) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatAdapter.addMessage(message);
            this.scrollToLastMessage();
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chatAdapter.addMessage(message);
                this.scrollToLastMessage();
            });
        }
    }
    @Override
    public void onChatReceived(Chat chat) {
        if (this.chatsFragment == null || !this.chatsFragment.isVisible()) {
            this.mainActivityInstance.getChatsHistory().add(chat);
            return;
        }

        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatsFragment.addChat(chat);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chatsFragment.addChat(chat);
            });
        }
    }

    public void scrollToLastMessage () {
        this.recyclerView.scrollToPosition(this.chatAdapter.getItemCount() - 1);
    }

    public void updateMessageHistory (int chatId) {
        if (chatId == this.mainActivityInstance.currentChatId && this.chatAdapter.getItemCount() > 0)
            return;

        this.chatAdapter.clear();
        this.chatAdapter.notifyDataSetChanged();
        // no other way, must use notifyDataSetChanged()

        this.history.clear();
        this.client.getChatHistory(chatId);
        this.mainActivityInstance.currentChatId = chatId;
    }
}

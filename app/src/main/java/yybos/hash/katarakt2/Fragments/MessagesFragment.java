package yybos.hash.katarakt2.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
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
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import yybos.hash.katarakt2.Fragments.Popup.InfoPopupFragment;
import yybos.hash.katarakt2.Fragments.Popup.InputPopupFragment;
import yybos.hash.katarakt2.Fragments.ViewAdapters.MessagesViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Constants;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Anime;
import yybos.hash.katarakt2.Socket.Objects.Media.Directory;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message.Command;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;
import yybos.hash.katarakt2.Socket.Objects.Message.User;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;

public class MessagesFragment extends Fragment implements ClientInterface {
    private ConstraintLayout constraintLayout;

    private EditText editText;
    private RecyclerView recyclerView;

    private MessagesViewAdapter chatAdapter;
    private List<PacketObject> history;

    private MainActivity mainActivityInstance;
    private Client client;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getView() == null)
            return;

        View root = getView();

        // moved all of this to the onViewCreated because of the back stack (the fragment is not destroyed neither created when its added to the back stack)
        // but the view is destroyed and created again, so ill be using that
        this.mainActivityInstance = ((MainActivity) requireActivity());

        // get client for sending messages, connecting, etc
        this.client = this.mainActivityInstance.getClient();

        // get message history if there are any messages previously sent
        this.history = this.mainActivityInstance.getMessageHistory();

        // client will send message with a timestamp to the server, server generates id in the database and returns message, if message has the same timestamp as any other message in this
        // list, then the message was sent succesfully. This has a tiny, very little, overhead

        // listen to incoming messages
        this.mainActivityInstance.addClientListener(this);

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        this.constraintLayout = root.findViewById(R.id.chatConstraintLayout);
        this.editText = root.findViewById(R.id.chatEditText);
        ImageView chatsButton = root.findViewById(R.id.chatChatsButton);
        ImageView sendButton = root.findViewById(R.id.chatSendButton);

        this.chatAdapter = new MessagesViewAdapter(this, this.history);
        this.chatAdapter.setHasStableIds(true);

        chatsButton.setOnClickListener(this::displayChatsList);
        sendButton.setOnClickListener(this::sendMessage);
        this.editText.setOnFocusChangeListener((v, focused) -> {
            if (focused) {
                if (MessagesFragment.this.client == null || !MessagesFragment.this.client.isConnected()) {
                    v.clearFocus();
                    this.displayInfo(
                        "Hold Up!",
                        "You not connected = you not send message",
                        "Alright, smart boy",
                        "Bet",
                        (resultkey, result) -> MessagesFragment.this.closeInfoPopup(resultkey)
                    );
                    return;
                }

                if (MessagesFragment.this.mainActivityInstance.currentChatId <= 0) {
                    MessagesFragment.this.mainActivityInstance.showCustomToast("No chat selected", Color.argb(90, 235, 64, 52));
                }
            }
        });

        this.recyclerView = root.findViewById(R.id.chatRecycler);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(this.chatAdapter);
        this.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER); // get rid of that 'wave' effect when trying to scroll beyond the limits of the linearLayout

        if (this.chatAdapter.getItemCount() > 0)
            this.scrollToLastMessage();

        if (!this.client.isConnected()) {
            this.displayInfo(
                "The App",
                "It shows here that you are not connected to a server",
                "I will connect",
                "Red pilled message box",
                (resultKey, result) -> {
                    int button = result.getInt("button");

                    if (button == 0) {
                        MessagesFragment.this.mainActivityInstance.showCustomToast("Fuck you", Color.argb(90, 235, 64, 52));
                    }

                    MessagesFragment.this.closeInfoPopup(resultKey);
                }
            );
        }

        if (this.mainActivityInstance.getLoginUsername().trim().isEmpty() && this.client.isConnected()) {
            this.displayUsernameInputPopup();
        }

        // reads from the defaultChat file; if there is something inside, parse; updateMessageHistory using the parsed chat id
        new Thread(() -> {
            String chatJson = this.readFileFromInternalStorage(requireContext(), Constants.defaultChatFilename);
            if (chatJson.isEmpty())
                return;

            Chat defaultChat = Chat.fromString(chatJson);

            if (this.client.isConnected() && this.mainActivityInstance.currentChatId == 0)
                this.updateMessageHistory(defaultChat.getId());
        }).start();
    }

    // send message through client
    private void sendMessage (View v) {
        if (!this.client.isConnected())
            return;

        String content = this.editText.getText().toString().trim().replace("\0", ""); // just to make sure, remove any possible null characters
        if (content.trim().isEmpty())
            return;

        this.editText.setText("");

        if (content.equals("/terminal")) {
            this.mainActivityInstance.openTerminal();
            return;
        }

        Message message = Message.toMessage(content, this.mainActivityInstance.currentChatId, this.client.user.getUsername());

        // if the chat id is valid
        if (MessagesFragment.this.mainActivityInstance.currentChatId > 0) {
            this.client.sendMessage(message);
            this.chatAdapter.addMessage(message);
            // this.history.add(message); for some reason this thing is duplicating messages
        }

        this.scrollToLastMessage();
    }

    public void downloadFile (MediaFile mediaFile) {
        this.client.downloadFile(mediaFile);
    }

    // display things
    public void showCustomToast (String message, int backgroundColor) {
        if (this.client.isConnected())
            this.mainActivityInstance.showCustomToast(message, backgroundColor);
        else
            this.mainActivityInstance.showCustomToast("My brother, you ain't even connected", Color.argb(80, 245, 66, 66));
    }

    public FrameLayout createFrameLayout () {
        FrameLayout generalFrameLayout = new FrameLayout(this.requireContext());
        generalFrameLayout.setId(View.generateViewId());

        FrameLayout.LayoutParams fragmentFrameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayoutParams.gravity = Gravity.END;

        this.constraintLayout.addView(generalFrameLayout, fragmentFrameLayoutParams);

        return generalFrameLayout;
    }
    private void displayChatsList (View v) {
        // create frame layout for popup fragment
        FrameLayout generalFrameLayout = this.createFrameLayout();
        generalFrameLayout.setOnClickListener(view -> {
            // this will trigger the resultListener by calling the destroy method in the chatsFragment
            MessagesFragment.this.closeChats();
        });

        ValueAnimator frameFadeIn = ValueAnimator.ofInt(0, 100);
        frameFadeIn.setDuration(200);
        frameFadeIn.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeIn.start();

        // add fragment to frame layout
        ChatsFragment chatsFragment = new ChatsFragment(this);

        // initiate fragment manager
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();
        fragmentManager.add(generalFrameLayout.getId(), chatsFragment, "chatsFragmentInstance");
        fragmentManager.commit();

        getParentFragmentManager().setFragmentResultListener("chatsFragment", this, (resultKey, result) -> {
            ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
            frameFadeOut.setDuration(200);
            frameFadeOut.addUpdateListener((animator) -> {
                int value = (int) animator.getAnimatedValue();
                generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
            });
            frameFadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    generalFrameLayout.removeAllViews();
                    MessagesFragment.this.constraintLayout.removeView(generalFrameLayout);
                }
            });
            frameFadeOut.start();
        });

        // frame layout is already added to the screen
    }
    public void displayInfo (String title, String description, String firstButtonText, String secondButtonText, FragmentResultListener listener) {
        String resultKey = "displayInfo" + System.currentTimeMillis();

        // create frame layout for popup fragment
        FrameLayout generalFrameLayout = this.createFrameLayout();
        generalFrameLayout.setOnClickListener(view -> {
            ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
            frameFadeOut.setDuration(200);
            frameFadeOut.addUpdateListener((animator) -> {
                int value = (int) animator.getAnimatedValue();
                generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
            });
            frameFadeOut.start();

            InfoPopupFragment infoPopupFragment = (InfoPopupFragment) getParentFragmentManager().findFragmentByTag(resultKey);

            if (infoPopupFragment == null)
                return;

            infoPopupFragment.destroy();
        });
        generalFrameLayout.setZ(this.constraintLayout.getChildCount() + 15);

        ValueAnimator frameFadeIn = ValueAnimator.ofInt(0, 100);
        frameFadeIn.setDuration(200);
        frameFadeIn.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeIn.start();

        // set args for title, description, etc
        InfoPopupFragment infoPopupFragment = new InfoPopupFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putString("firstButtonText", firstButtonText);
        args.putString("secondButtonText", secondButtonText);
        args.putString("resultKey", resultKey);

        infoPopupFragment.setArguments(args);

        // initiate fragment manager and
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentTransaction.add(generalFrameLayout.getId(), infoPopupFragment, resultKey);
        fragmentTransaction.commit();

        getParentFragmentManager().setFragmentResultListener(resultKey, this, listener);

        // frame layout is already added to the screen
    }

    public void displayUsernameInputPopup () {
        if (this.getContext() == null)
            return;

        String resultKey = "displayInput" + System.currentTimeMillis();

        // create frame layout for popup fragment
        FrameLayout generalFrameLayout = this.createFrameLayout();
        generalFrameLayout.setOnClickListener(view -> {
            ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
            frameFadeOut.setDuration(150);
            frameFadeOut.addUpdateListener((animator) -> {
                int value = (int) animator.getAnimatedValue();
                generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
            });
            frameFadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    InputPopupFragment inputPopupFragment = (InputPopupFragment) getParentFragmentManager().findFragmentByTag(resultKey);

                    if (inputPopupFragment == null)
                        return;

                    inputPopupFragment.destroy();
                }
            });
            frameFadeOut.start();

            String data = MessagesFragment.this.client.getClientIp();

            User newUser = User.toUser(MessagesFragment.this.mainActivityInstance.getClient().user.getId(), data, null, null);

            MessagesFragment.this.mainActivityInstance.setLoginUsername(data);
            MessagesFragment.this.chatAdapter.updateUsername(newUser);
        });

        ValueAnimator frameFadeIn = ValueAnimator.ofInt(0, 100);
        frameFadeIn.setDuration(200);
        frameFadeIn.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeIn.start();

        InputPopupFragment inputPopupFragment = new InputPopupFragment();

        Bundle args = new Bundle();
        args.putString("title", "Greetings!");
        args.putString("text", "The server is gently asking for an username");
        args.putString("hint", "Username");
        args.putString("resultKey", resultKey);

        inputPopupFragment.setArguments(args);

        // initiate fragment manager
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(generalFrameLayout.getId(), inputPopupFragment, resultKey);
        fragmentManager.commit();

        getParentFragmentManager().setFragmentResultListener(resultKey, this, (requestKey, result) -> {
            String data = result.getString("inputResult");

            MessagesFragment.this.closeInputPopup(resultKey);

            if (data == null)
                return;

            if (data.trim().isEmpty())
                data = MessagesFragment.this.client.getClientIp();
            else
                MessagesFragment.this.mainActivityInstance.getClient().updateUsername(data);

            User newUser = User.toUser(MessagesFragment.this.mainActivityInstance.getClient().user.getId(), data, null, null);

            MessagesFragment.this.mainActivityInstance.setLoginUsername(data);
            MessagesFragment.this.chatAdapter.updateUsername(newUser);
        });

        // frame layout is already added to the screen
    }

    public void closeChats () {
        ChatsFragment chatsFragment = (ChatsFragment) getParentFragmentManager().findFragmentByTag("chatsFragmentInstance");

        if (chatsFragment == null)
            return;

        chatsFragment.destroy();
    }
    public void closeInfoPopup (String tag) {
        InfoPopupFragment infoPopupFragment = (InfoPopupFragment) getParentFragmentManager().findFragmentByTag(tag);

        if (infoPopupFragment == null)
            return;

        View infoPopupView = infoPopupFragment.getView();
        if (infoPopupView == null)
            return;

        // remove the frameLayout (parent) from the constraintLayout (xuxu beleza)
        FrameLayout generalFrameLayout = (FrameLayout) infoPopupView.getParent();

        ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
        frameFadeOut.setDuration(150);
        frameFadeOut.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                generalFrameLayout.removeAllViews();
                MessagesFragment.this.constraintLayout.removeView(generalFrameLayout);

                super.onAnimationEnd(animation);
            }
        });
        frameFadeOut.start();
    }
    public void closeInputPopup (String tag) {
        InputPopupFragment inputPopupFragment = (InputPopupFragment) getParentFragmentManager().findFragmentByTag(tag);

        if (inputPopupFragment == null)
            return;

        View inputPopupView = inputPopupFragment.getView();
        if (inputPopupView == null)
            return;

        // remove the frameLayout (parent) from the constraintLayout (xuxu beleza)
        FrameLayout generalFrameLayout = (FrameLayout) inputPopupView.getParent();

        ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
        frameFadeOut.setDuration(150);
        frameFadeOut.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                generalFrameLayout.removeAllViews();
                MessagesFragment.this.constraintLayout.removeView(generalFrameLayout);

                super.onAnimationEnd(animation);
            }
        });
        frameFadeOut.start();
    }

    @Override
    public void onDestroyView () {
        // remove listener once the fragment is destroyed
        this.mainActivityInstance.removeClientListener(this);

        super.onDestroyView();
    }

    // events
    @Override
    public void onCommandReceived (Command command) {

    }
    @Override
    public void onMessageReceived (Message message) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatAdapter.addMessage(message); // check to see if this message is inside unsentMessages
            this.scrollToLastMessage();
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chatAdapter.addMessage(message); // this one too
                this.scrollToLastMessage();
            });
        }
    }
    @Override
    public void onChatReceived (Chat chat) {
        // chatFragment doesnt have anything to do with chats
    }
    @Override
    public void onAnimeReceived (Anime anime) {

    }
    @Override
    public void onFileReceived (MediaFile mediaFile) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatAdapter.addFile(mediaFile);
            this.scrollToLastMessage();
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chatAdapter.addFile(mediaFile);
                this.scrollToLastMessage();
            });
        }
    }

    @Override
    public void onDirectoryReceived(Directory directory) {

    }

    // shits

    public void clearChat () {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatAdapter.clear();
            this.history.clear();
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chatAdapter.clear();
                this.history.clear();
            });
        }
    }

    public void scrollToLastMessage () {
        this.recyclerView.scrollToPosition(this.chatAdapter.getItemCount() - 1);
    }

    public void updateMessageHistory (int chatId) {
        if (chatId == this.mainActivityInstance.currentChatId && this.chatAdapter.getItemCount() > 0)
            return;

        // out of nowhere I got an exception that the chatAdapter.clear() method was not an ui-thread
        this.mainActivityInstance.runOnUiThread(() -> {
            this.chatAdapter.clear();

            this.history.clear();
            this.client.getChatHistory(chatId);
            this.mainActivityInstance.currentChatId = chatId;
        });
    }

    public void getChats () {
        this.client.getChats();
    }
    public void createChat (String name) {
        this.client.sendCommand(Command.createChat(name));
    }
    public void deleteChat (int id) {
        this.client.sendCommand(Command.deleteChat(id));
    }

    public int getCurrentChatId () {
        return this.mainActivityInstance.currentChatId;
    }

    // default chat
    public String readFileFromInternalStorage (Context context, String fileName) {
        StringBuilder content = new StringBuilder();

        try {
            File serversFile = new File(context.getFilesDir(), fileName);

            if (!serversFile.exists()) {
                if (!serversFile.createNewFile()) {
                    return "";
                }
            }

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
}

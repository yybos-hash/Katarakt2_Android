package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.Fragments.Adapters.ChatsViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Chat;


public class ChatsFragment extends Fragment {
    private MainActivity mainActivityInstance;
    private ChatsViewAdapter chatsAdapter;

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainActivityInstance = ((MainActivity) requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_chats_list, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = getView();
        if (root == null)
            return;

        this.chatsAdapter = new ChatsViewAdapter(((MainActivity) getActivity()).getChatFragmentInstance());

        RecyclerView recyclerView = root.findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.chatsAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ImageView closeButton = root.findViewById(R.id.chatsClose);
        closeButton.setOnClickListener(this::closeButton);

        this.progressBar = root.findViewById(R.id.chatsProgressBar);
    }

    // button click animation

    @Override
    public void onDestroy () {
        // little trick to get the chatFragment instance. Basically I create a tag when creating the chatFragment, then i can identify it here using the tag
        ChatFragment chatFragmentInstance = ((ChatFragment) getParentFragmentManager().findFragmentByTag("chatFragmentInstance"));

        if (chatFragmentInstance != null)
            chatFragmentInstance.closeChatsList();

        super.onDestroy();
    }

    private void closeButton (View view) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        transaction.remove(this);
        transaction.commit();
    }

    public void addChat (Chat chat) {
        if (this.progressBar.getVisibility() == View.VISIBLE)
            this.progressBar.setVisibility(View.INVISIBLE);

        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatsAdapter.addChat(chat);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.chatsAdapter.addChat(chat);
            });
        }
    }
}
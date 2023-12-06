package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import yybos.hash.katarakt2.Fragments.Adapters.ChatsViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Chat;

public class ChatsFragment extends Fragment {
    private MainActivity mainActivityInstance;
    private ChatsViewAdapter chatsAdapter;
    private LinearLayout linearLayout;

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        this.mainActivityInstance = ((MainActivity) requireActivity());

        this.progressBar = root.findViewById(R.id.chatsProgressBar);
        this.linearLayout = root.findViewById(R.id.chatsLinearLayout);

        List<Chat> chatsHistory = this.mainActivityInstance.getChatsHistory();

        this.chatsAdapter = new ChatsViewAdapter(chatsHistory, ((MainActivity) requireActivity()).getChatFragmentInstance());

        RecyclerView recyclerView = root.findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(this.chatsAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ImageView closeButton = root.findViewById(R.id.chatsClose);
        closeButton.setOnClickListener(this::closeButton);

        // set everything to invisible so the chats_list_expand animation doesnt glitch the layout
        closeButton.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        // make things visible again after the animation ends
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            closeButton.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }, 200);

        if (!this.mainActivityInstance.getClient().isConnected() || !chatsHistory.isEmpty())
            this.progressBar.setVisibility(View.INVISIBLE);
    }

    // button click animation

    private void closeButton (View view) {
        this.destroy();
    }

    public void addChat (Chat chat) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.chatsAdapter.addChat(chat);
            this.progressBar.setVisibility(View.INVISIBLE);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                ChatsFragment.this.chatsAdapter.addChat(chat);
                ChatsFragment.this.progressBar.setVisibility(View.INVISIBLE);
            });
        }
    }
    public void destroy () {
        this.linearLayout.removeAllViews();
        // remove after the animation finishes (125ms)
        new Handler(Looper.getMainLooper()).postDelayed(() -> this.mainActivityInstance.getChatFragmentInstance().removeGeneralFrameLayout(), 125);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.chats_list_expand, R.anim.chats_list_contract);
        transaction.remove(this);
        transaction.commit();
    }
}
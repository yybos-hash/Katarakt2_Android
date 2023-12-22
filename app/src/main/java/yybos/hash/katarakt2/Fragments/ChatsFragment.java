package yybos.hash.katarakt2.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import yybos.hash.katarakt2.Fragments.Popup.InputPopupFragment;
import yybos.hash.katarakt2.Fragments.ViewAdapters.ChatsViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Chat;

public class ChatsFragment extends Fragment {
    private MainActivity mainActivityInstance;
    private ChatsViewAdapter chatsAdapter;
    private LinearLayout linearLayout;
    private ImageView addButton;
    private FrameLayout generalFrameLayout;

    private InputPopupFragment inputPopupFragment;

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

        this.addButton = root.findViewById(R.id.chatsAdd);
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

        this.addButton.setOnClickListener((v) -> ChatsFragment.this.addNewChat());

        // set everything to invisible so the chats_list_expand animation doesnt glitch the layout
        recyclerView.setVisibility(View.INVISIBLE);

        // make things visible again after the animation ends
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            recyclerView.setVisibility(View.VISIBLE);
        }, 300);

        if (!this.mainActivityInstance.getClient().isConnected() || !chatsHistory.isEmpty())
            this.progressBar.setVisibility(View.INVISIBLE);
    }

    private void closeButton (View view) {
        this.destroy();
    }

    private void addNewChat () {
        if (this.getContext() == null)
            return;

        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.getContext());
        this.generalFrameLayout.setId(View.generateViewId());
        this.generalFrameLayout.setOnClickListener(view -> this.closeInputPopup());
        this.generalFrameLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));

        FrameLayout.LayoutParams fragmentFrameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayout.gravity = Gravity.CENTER;

        this.inputPopupFragment = new InputPopupFragment();

        Bundle args = new Bundle();
        args.putString("resultKey", "chatsNewChat");

        this.inputPopupFragment.setArguments(args);

        // initiate fragment manager and
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(this.generalFrameLayout.getId(), this.inputPopupFragment);
        fragmentManager.commit();

        getParentFragmentManager().setFragmentResultListener("chatsNewChat", this.inputPopupFragment, (requestKey, result) -> {
            String data = result.getString("inputResult");

            Chat newChat = Chat.toChat(0, data);

            ChatsFragment.this.addChat(newChat);
        });

        // add frame layout to constraintLayout
        this.linearLayout.addView(this.generalFrameLayout, fragmentFrameLayout);
    }
    private void closeInputPopup () {
        this.removeGeneralFrameLayout();
    }

    private void removeGeneralFrameLayout() {
        this.generalFrameLayout.removeAllViews();
        this.linearLayout.removeView(this.generalFrameLayout);
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
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.chats_list_expand, R.anim.chats_list_contract);
        transaction.remove(this);
        transaction.commit();
    }

    @Override
    public void onDestroyView () {
        this.linearLayout.removeAllViews();
        this.mainActivityInstance.getChatFragmentInstance().removeGeneralFrameLayout();

        super.onDestroyView();
    }
}
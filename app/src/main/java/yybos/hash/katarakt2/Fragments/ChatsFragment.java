package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    MainActivity mainActivityInstance;
    ChatsViewAdapter chatsAdapter;

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

        List<Chat> chats = this.mainActivityInstance.getChats();

        this.chatsAdapter = new ChatsViewAdapter(chats);

        RecyclerView recyclerView = root.findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.chatsAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ImageView closeButton = root.findViewById(R.id.chatsClose);
        closeButton.setOnClickListener(this::closeButton);
    }

    @Override
    public void onDestroy () {
        // little trick to get the chatFragment instance. Basically I create a tag when creating the chatFragment, then i can identify it here using the tag
        ChatFragment chatFragmentInstance = ((ChatFragment) getParentFragmentManager().findFragmentByTag("chatFragmentInstance"));

        if (chatFragmentInstance != null)
            chatFragmentInstance.removePopupFrameLayout();

        super.onDestroy();
    }

    private void closeButton (View view) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
    }
}
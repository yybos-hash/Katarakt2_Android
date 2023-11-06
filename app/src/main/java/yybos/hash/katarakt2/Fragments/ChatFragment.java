package yybos.hash.katarakt2.Fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import yybos.hash.katarakt2.Fragments.Adapters.ChatViewAdapter;
import yybos.hash.katarakt2.Fragments.ViewModels.ChatViewModel;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatFragment extends Fragment {
    private EditText editText;
    private ChatViewModel chatViewModel;

    private LinearLayout chatLinearLayout;
    private ChatViewAdapter chatAdapter;
    private List<Message> history;
    private Client client;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.chatViewModel = ((MainActivity) requireActivity()).getViewModel();
        this.history = this.chatViewModel.getChatHistory().getValue();
        this.client = ((MainActivity) requireActivity()).getClientConnection();
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

        View rootView = getView();

        this.chatLinearLayout = rootView.findViewById(R.id.chatLinearLayout);
        this.editText = rootView.findViewById(R.id.chatEditText);

        this.chatAdapter = new ChatViewAdapter(null);

        RecyclerView recyclerView = rootView.findViewById(R.id.chatRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.chatAdapter);

        this.chatViewModel.getChatHistory().observe(getViewLifecycleOwner(), messages -> {
            // add message to recyclerView
            this.chatAdapter.addMessage(messages.get(messages.size() - 1));
        });
    }
}

package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import yybos.hash.katarakt2.Fragments.Adapters.ChatViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatFragment extends Fragment implements ClientInterface {
    private EditText editText;

    private LinearLayout chatLinearLayout;
    private ChatViewAdapter chatAdapter;
    private List<Message> history;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get message history if there are any messages previously sent
        this.history = ((MainActivity) requireActivity()).getHistory();

        // listen to incoming messages
        ((MainActivity) requireActivity()).addListener(this);
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

        this.chatAdapter = new ChatViewAdapter(this.history);

        RecyclerView recyclerView = rootView.findViewById(R.id.chatRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.chatAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // get rid of that 'wave' effect when trying to scroll beyond the limits of the linearLayout
    }

    // remove listener once the fragment is destroyed
    @Override
    public void onDestroy () {
        ((MainActivity) requireActivity()).removeListener(this);

        super.onDestroy();
    }

    // for receiving messages
    @Override
    public void onMessageReceived(Message message) {
        this.chatAdapter.addMessage(message);
    }
}

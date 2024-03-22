package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.Fragments.ViewAdapters.AnimeViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Anime;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message.Command;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;

public class AnimeFragment extends Fragment implements ClientInterface {
    private MainActivity mainActivityInstance;
    private AnimeViewAdapter adapter;

    public AnimeFragment () {
        // empty constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anime, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = getView();
        if (root == null)
            return;

        this.mainActivityInstance = ((MainActivity) requireActivity());
        this.mainActivityInstance.addClientListener(this);

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        // fetch all animes
        Client client = this.mainActivityInstance.getClient();
        client.sendCommand(Command.getAnimeList());

        this.adapter = new AnimeViewAdapter(this);
        this.adapter.setHasStableIds(true);

        RecyclerView recyclerView = root.findViewById(R.id.animeRecyclerView);
        recyclerView.setAdapter(this.adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void addEntry () {

    }

    @Override
    public void onDestroyView () {
        this.mainActivityInstance.removeClientListener(this);

        super.onDestroyView();
    }

    @Override
    public void onCommandReceived (Command command) {
    }

    @Override
    public void onMessageReceived (Message message) {
    }

    @Override
    public void onChatReceived (Chat chat) {
    }

    @Override
    public void onAnimeReceived (Anime anime) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.adapter.addAnime(anime);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.adapter.addAnime(anime);
            });
        }
    }

    @Override
    public void onFileReceived(MediaFile mediaFile) {

    }
}

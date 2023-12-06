package yybos.hash.katarakt2.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;

import yybos.hash.katarakt2.Fragments.Adapters.SettingsViewAdapter;
import yybos.hash.katarakt2.Fragments.ViewHolders.SettingsViewHolder;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Constants;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class SettingsFragment extends Fragment {
    private MainActivity mainActivityInstance;

    private SettingsViewAdapter settingsAdapter;
    private SettingsViewHolder serverViewHolder;

    private FrameLayout generalFrameLayout;
    private ConstraintLayout constraintLayout;

    private ServerInfoFragment serverInfoFragment;

    private FloatingActionButton floatingButton;

    private List<Server> serverList;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.mainActivityInstance = ((MainActivity) requireActivity());

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        this.settingsAdapter = new SettingsViewAdapter(this);

        RecyclerView recyclerView = root.findViewById(R.id.configRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(settingsAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        this.constraintLayout = root.findViewById(R.id.configConstraintLayout);

        this.floatingButton = root.findViewById(R.id.configFloatingButton);
        this.floatingButton.setOnClickListener((v) -> {
            this.createInfo();
        });

        // read and place all the serverssss
        String serversJson = this.readFileFromInternalStorage(requireContext(), Constants.serversListFilename);
        this.serverList = this.parseJsonString(serversJson);
        if (this.serverList != null) {
            for (Server server : this.serverList)
                this.addServer(server);
        }
    }

    public void addServer (Server server) {
        this.settingsAdapter.addServer(server);

        // apply changes to serversList
        this.writeServersToFile(getContext(), this.settingsAdapter.getServers(), Constants.serversListFilename);
    }

    // server infos
    public void createInfo () {
        this.floatingButton.setEnabled(false);

        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.requireContext());
        this.generalFrameLayout.setId(View.generateViewId());
        this.generalFrameLayout.setOnClickListener(view -> this.closeInfo());
        this.generalFrameLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));

        ServerInfoFragment serverInfoFragment = new ServerInfoFragment();
        this.serverInfoFragment = serverInfoFragment;

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // initiate fragment manager
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(this.generalFrameLayout.getId(), serverInfoFragment);
        fragmentManager.commit();

        // add frame layout to constraintLayout
        this.constraintLayout.addView(this.generalFrameLayout, frameLayoutParams);
    }
    public void openInfo (SettingsViewHolder viewHolder) {
        if (viewHolder == null)
            return;

        this.serverViewHolder = viewHolder;

        this.floatingButton.setEnabled(false);

        // create frame layout for popup fragment
        this.generalFrameLayout = new FrameLayout(this.requireContext());
        this.generalFrameLayout.setId(View.generateViewId());
        this.generalFrameLayout.setOnClickListener(view -> this.closeInfo());
        this.generalFrameLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));

        ServerInfoFragment serverInfoFragment = new ServerInfoFragment();
        this.serverInfoFragment = serverInfoFragment;

        Bundle args = new Bundle();
        args.putString("serverip", viewHolder.serverInfo.serverIp);
        args.putInt("serverport", viewHolder.serverInfo.serverPort);
        args.putString("email", viewHolder.serverInfo.email);
        args.putString("password", viewHolder.serverInfo.password);

        serverInfoFragment.setArguments(args);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // initiate fragment manager
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(this.generalFrameLayout.getId(), serverInfoFragment);
        fragmentManager.commit();

        // add frame layout to constraintLayout
        this.constraintLayout.addView(this.generalFrameLayout, frameLayoutParams);
    }
    public void saveInfo (Server server) {
        if (this.serverViewHolder == null || server == null)
            return;

        this.serverViewHolder.setInfo(server);
        this.settingsAdapter.updateServer(this.serverViewHolder);

        // apply changes to serversList
        this.writeServersToFile(getContext(), this.settingsAdapter.getServers(), Constants.serversListFilename);
    }
    public void closeInfo () {
        this.floatingButton.setEnabled(true);

        if (this.serverInfoFragment == null)
            return;

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        transaction.remove(this.serverInfoFragment);
        transaction.commit();

        // remove after the animation finishes (125ms)
        new Handler(Looper.getMainLooper()).postDelayed(this::removeGeneralFrameLayout, 125);
    }

    private void removeGeneralFrameLayout() {
        this.generalFrameLayout.removeAllViews();
        this.constraintLayout.removeView(this.generalFrameLayout);
    }

    // abubeblu
    public void writeServersToFile(Context context, List<Server> serverList, String fileName) {
        if (serverList == null)
            return;

        Gson gson = new Gson();

        try {
            // Open the file for writing
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            // Wrap the FileOutputStream in an OutputStreamWriter
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

            // Convert the list of servers to JSON and write it to the file
            String jsonString = gson.toJson(serverList);
            outputStreamWriter.write(jsonString);

            // Close the streams
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFileFromInternalStorage(Context context, String fileName) {
        StringBuilder content = new StringBuilder();

        try {
            File serversFile = new File(context.getFilesDir(), Constants.serversListFilename);
            serversFile.createNewFile();

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
    public List<Server> parseJsonString(String jsonString) {
        Gson gson = new Gson();

        // Define the type of the collection you want to deserialize
        Type serverListType = (new TypeToken<List<Server>>() {}).getType();

        // Deserialize the JSON string into a list of Server objects
        return gson.fromJson(jsonString, serverListType);
    }

    @Override
    public void onDestroyView () {
        // apply changes to serversList
        this.writeServersToFile(getContext(), this.settingsAdapter.getServers(), Constants.serversListFilename);

        super.onDestroyView();
    }

    public void tryConnection(Server server) {
        this.mainActivityInstance.setIpAddress(server.serverIp);
        this.mainActivityInstance.setPort(server.serverPort);

        this.mainActivityInstance.setLoginEmail(server.email);
        this.mainActivityInstance.setLoginPassword(server.password);

        this.mainActivityInstance.getClient().tryConnection();
    }
}
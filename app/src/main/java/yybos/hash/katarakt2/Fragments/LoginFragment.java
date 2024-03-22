package yybos.hash.katarakt2.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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

import yybos.hash.katarakt2.Fragments.Popup.ServerInfoPopupFragment;
import yybos.hash.katarakt2.Fragments.ViewAdapters.LoginViewAdapter;
import yybos.hash.katarakt2.Fragments.ViewHolders.LoginViewHolder;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Constants;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class LoginFragment extends Fragment {
    private MainActivity mainActivityInstance;

    private LoginViewAdapter serverAdapter;
    private LoginViewHolder serverViewHolder;

    private ConstraintLayout constraintLayout;
    private FloatingActionButton floatingButton;

    private List<Server> serverList;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.mainActivityInstance = ((MainActivity) requireActivity());

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        this.serverAdapter = new LoginViewAdapter(this);

        RecyclerView recyclerView = root.findViewById(R.id.loginRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(serverAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        this.constraintLayout = root.findViewById(R.id.loginConstraintLayout);

        this.floatingButton = root.findViewById(R.id.loginFloatingButton);
        this.floatingButton.setOnClickListener((v) -> {
            this.createInfo();
        });

        // thread to read servers
        new Thread(() -> {
            // read and place all the serverssss
            String serversJson = this.readFileFromInternalStorage(requireContext(), Constants.serversListFilename);
            this.serverList = this.parseJsonString(serversJson);
            if (this.serverList != null) {
                for (Server server : this.serverList)
                    this.addServer(server);
            }
        }).start();
    }

    public void addServer (Server server) {
        this.mainActivityInstance.runOnUiThread(() -> {
            this.serverAdapter.addServer(server);
        });
    }

    // server infos
    public void createInfo () {
        String resultKey = "createInfo" + System.currentTimeMillis();

        this.floatingButton.setEnabled(false);

        // create frame layout for popup fragment
        FrameLayout generalFrameLayout = new FrameLayout(this.requireContext());
        generalFrameLayout.setId(View.generateViewId());
        generalFrameLayout.setOnClickListener(view -> {
            ServerInfoPopupFragment infoPopupFragment = (ServerInfoPopupFragment) getParentFragmentManager().findFragmentByTag(resultKey);

            if (infoPopupFragment == null)
                return;

            infoPopupFragment.destroy();
        });

        ValueAnimator frameFadeIn = ValueAnimator.ofInt(0, 100);
        frameFadeIn.setDuration(200);
        frameFadeIn.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeIn.start();

        ServerInfoPopupFragment serverInfoPopupFragment = new ServerInfoPopupFragment();

        Bundle args = new Bundle();
        args.putBoolean("isCreating", true);
        args.putString("resultKey", resultKey);

        serverInfoPopupFragment.setArguments(args);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // initiate fragment manager
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(generalFrameLayout.getId(), serverInfoPopupFragment, resultKey);
        fragmentManager.commit();

        getParentFragmentManager().setFragmentResultListener(resultKey, this, (resKey, result) -> {
            this.closeInfo(resultKey);

            Server server = new Server();
            server.serverIp = result.getString("serverIp");
            server.serverPort = result.getInt("serverPort");
            server.email = result.getString("email");
            server.password = result.getString("password");

            if (server.serverIp == null || server.email == null || server.serverPort == -1) {
                return;
            }

            this.addServer(server);

            // apply changes to serversList
            this.writeServersToFile(getContext(), this.serverAdapter.getServers(), Constants.serversListFilename);
        });

        // add frame layout to constraintLayout
        this.constraintLayout.addView(generalFrameLayout, frameLayoutParams);
    }
    public void openInfo (LoginViewHolder viewHolder) {
        if (viewHolder == null)
            return;

        String resultKey = "openInfo" + System.currentTimeMillis();

        this.serverViewHolder = viewHolder;
        this.floatingButton.setEnabled(false);

        // create frame layout for popup fragment
        FrameLayout generalFrameLayout = new FrameLayout(this.requireContext());
        generalFrameLayout.setId(View.generateViewId());
        generalFrameLayout.setOnClickListener(view -> {
            ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
            frameFadeOut.setDuration(200);
            frameFadeOut.addUpdateListener((animator) -> {
                int value = (int) animator.getAnimatedValue();
                generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
            });
            frameFadeOut.start();

            this.closeInfo(resultKey);
        });
        ValueAnimator frameFadeIn = ValueAnimator.ofInt(0, 100);
        frameFadeIn.setDuration(200);
        frameFadeIn.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeIn.start();

        ServerInfoPopupFragment serverInfoPopupFragment = new ServerInfoPopupFragment();

        Bundle args = new Bundle();
        args.putString("serverIp", viewHolder.serverInfo.serverIp);
        args.putInt("serverPort", viewHolder.serverInfo.serverPort);
        args.putString("email", viewHolder.serverInfo.email);
        args.putString("password", viewHolder.serverInfo.password);
        args.putString("resultKey", resultKey);

        serverInfoPopupFragment.setArguments(args);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(generalFrameLayout.getId(), serverInfoPopupFragment, resultKey);
        fragmentManager.commit();

        getParentFragmentManager().setFragmentResultListener(resultKey, this, (resKey, result) -> {
            Server server = new Server();
            server.serverIp = result.getString("serverIp");
            server.serverPort = result.getInt("serverPort");
            server.email = result.getString("email");
            server.password = result.getString("password");
            server.isDefault = viewHolder.serverInfo.isDefault;

            viewHolder.setInfo(server);

            this.saveInfo(viewHolder);
            this.closeInfo(resKey);
        });

        // add frame layout to constraintLayout
        this.constraintLayout.addView(generalFrameLayout, frameLayoutParams);
    }
    public void saveInfo (LoginViewHolder viewHolder) {
        if (viewHolder == null)
            return;

        this.serverAdapter.updateServer(viewHolder);
        this.saveDefaultServer(viewHolder.serverInfo);

        // apply changes to serversList
        this.writeServersToFile(getContext(), this.serverAdapter.getServers(), Constants.serversListFilename);
    }
    public void saveDefaultServer (Server server) {
        this.writeServerToFile(requireContext(), server, Constants.defaultServerFilename);
    }
    public void closeInfo (String tag) {
        this.floatingButton.setEnabled(true);

        ServerInfoPopupFragment infoPopupFragment = (ServerInfoPopupFragment) getParentFragmentManager().findFragmentByTag(tag);

        if (infoPopupFragment == null)
            return;

        View infoPopupView = infoPopupFragment.getView();
        if (infoPopupView == null)
            return;

        // remove the frameLayout (parent) from the constraintLayout (xuxu beleza)
        FrameLayout generalFrameLayout = (FrameLayout) infoPopupView.getParent();

        ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
        frameFadeOut.setDuration(200);
        frameFadeOut.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                generalFrameLayout.removeAllViews();
                LoginFragment.this.constraintLayout.removeView(generalFrameLayout);

                super.onAnimationEnd(animation);
            }
        });
        frameFadeOut.start();
    }

    // abubeblu
    public void writeServerToFile (Context context, Server server, String fileName) {
        if (server == null)
            return;

        Gson gson = new Gson();

        try {
            // Open the file for writing
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            // Wrap the FileOutputStream in an OutputStreamWriter
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

            if (server.isDefault) {
                String jsonString = gson.toJson(server);
                outputStreamWriter.write(jsonString);
            }
            else
                outputStreamWriter.write("");

            // Close the streams
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeServersToFile (Context context, List<Server> serverList, String fileName) {
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
    public String readFileFromInternalStorage (Context context, String fileName) {
        StringBuilder content = new StringBuilder();

        try {
            File serversFile = new File(context.getFilesDir(), Constants.serversListFilename);
            if (!serversFile.exists())
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
    public List<Server> parseJsonString (String jsonString) {
        if (jsonString.isEmpty())
            return null;

        Gson gson = new Gson();

        // Define the type of the collection you want to deserialize
        Type serverListType = (new TypeToken<List<Server>>() {}).getType();

        // Deserialize the JSON string into a list of Server objects
        return gson.fromJson(jsonString, serverListType);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
    }

    public void tryConnection (Server server) {
        this.mainActivityInstance.getClient().tryConnection(server);
    }
}
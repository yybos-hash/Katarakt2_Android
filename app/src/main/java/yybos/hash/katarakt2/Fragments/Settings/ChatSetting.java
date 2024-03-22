package yybos.hash.katarakt2.Fragments.Settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import yybos.hash.katarakt2.Fragments.Custom.CustomSpinnerFragment;
import yybos.hash.katarakt2.Fragments.Custom.Listeners.SpinnerListener;
import yybos.hash.katarakt2.Fragments.Custom.Objects.NullObject;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Constants;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Anime;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message.Command;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;

public class ChatSetting extends Fragment implements ClientInterface, SpinnerListener {
    private MainActivity mainActivityInstance;

    private CustomSpinnerFragment spinner;

    public ChatSetting () { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mainActivityInstance = ((MainActivity) requireActivity());
        this.mainActivityInstance.addClientListener(this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.setting_chat, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        FrameLayout spinnerContainer = root.findViewById(R.id.settingChatContainer);
        spinnerContainer.setId(View.generateViewId());

        this.spinner = new CustomSpinnerFragment(this);

        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.isStateSaved())
            fragmentManager.executePendingTransactions();

        fragmentManager.beginTransaction()
                .add(spinnerContainer.getId(), this.spinner)
                .commit();

        new Thread(() -> {
            String defaultChatJson = ChatSetting.this.readFileFromInternalStorage(requireContext(), Constants.defaultChatFilename);

            if (defaultChatJson.isEmpty()) {
                ChatSetting.this.mainActivityInstance.runOnUiThread(() -> {
                    ChatSetting.this.spinner.setOption(new NullObject());
                });

                return;
            }

            Chat defaultChat = Chat.fromString(defaultChatJson);
            if (defaultChat == null)
                return;

            ChatSetting.this.mainActivityInstance.runOnUiThread(() -> {
                ChatSetting.this.spinner.setOption(defaultChat);
            });
        }).start();
    }

    @Override
    public void onDestroyView () {
        this.mainActivityInstance.removeClientListener(this);

        super.onDestroyView();
    }

    @Override
    public void onCommandReceived (Command command) {}

    // client listener
    @Override
    public void onMessageReceived (Message message) {}

    @Override
    public void onChatReceived (Chat chat) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.spinner.addObject(chat);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.spinner.addObject(chat);
            });
        }
    }

    @Override
    public void onAnimeReceived(Anime anime) {}

    @Override
    public void onFileReceived(MediaFile mediaFile) {}

    // spinner listener
    @Override
    public void onSpinnerExpand () {
        if (mainActivityInstance.getClient().isConnected()) {
            mainActivityInstance.getClient().sendCommand(Command.getChats());
        }
    }

    @Override
    public void onSpinnerContract () {}

    @Override
    public void onObjectSelected(Object object) {
        try {
            Chat chat = (Chat) object;
            this.writeToFile(requireContext(), chat, Constants.defaultChatFilename);
        }
        catch (ClassCastException e) {
            this.writeToFile(requireContext(), null, Constants.defaultChatFilename);
        }
    }

    // abubeblu
    public void writeToFile (Context context, Chat chat, String fileName) {
        try {
            // Open the file for writing
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            // Wrap the FileOutputStream in an OutputStreamWriter
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

            if (chat == null) {
                outputStreamWriter.write("");
                return;
            }

            //
            SimpleDateFormat customDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, c) -> c.serialize(customDateFormat.format(src)));
            //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

            Gson objParser = gsonBuilder.create();

            // Convert the list of servers to JSON and write it to the file
            String jsonString = objParser.toJson(chat);
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
            File serversFile = new File(context.getFilesDir(), fileName);
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
}

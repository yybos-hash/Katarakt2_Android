package yybos.hash.katarakt2.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Anime;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message.Command;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;

public class TerminalFragment extends Fragment implements ClientInterface {
    private MainActivity mainActivityInstance;

    private TextView textViewOutput;
    private EditText input;

    public TerminalFragment () { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terminal, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.textViewOutput = root.findViewById(R.id.terminalText);
        this.input = root.findViewById(R.id.terminalEdittext);

        this.mainActivityInstance = (MainActivity) requireActivity();
        this.mainActivityInstance.addClientListener(this);

        Client client = this.mainActivityInstance.getClient();

        this.input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String input = this.input.getText().toString();
                String commandStr = input.split(" ")[0];
                String argsStr = input.substring(commandStr.length());

                this.input.setText("");

                if (commandStr.equals("exit")) {
                    // initiate fragment manager
                    FragmentManager fragmentManager = getParentFragmentManager();
                    if (!fragmentManager.isStateSaved()) {
                        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                        if (inputMethodManager != null)
                            inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(), 0);

                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.remove(this);
                        transaction.commit();
                    }

                    return true;
                }

                Command command = Command.prompt(argsStr);
                client.sendCommand(command);

                return true;
            }
            return false;
        });
    }

    @Override
    public void onCommandReceived(Command command) {
        this.mainActivityInstance.runOnUiThread(() -> {
            String output = this.textViewOutput.getText() + "\n" + command.getString("output");
            this.textViewOutput.setText(output);
        });
    }

    @Override
    public void onMessageReceived(Message message) {
        //
    }

    @Override
    public void onChatReceived(Chat chat) {
        //
    }

    @Override
    public void onAnimeReceived(Anime anime) {

    }

    @Override
    public void onFileReceived(MediaFile mediaFile) {

    }

    @Override
    public void onDestroyView () {
        this.mainActivityInstance.removeClientListener(this);

        super.onDestroyView();
    }
}

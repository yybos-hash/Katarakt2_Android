package yybos.hash.katarakt2.Fragments.Popup;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;

public class ServerInfoPopupFragment extends Fragment {
    private MainActivity mainActivityInstance;

    private String serverIp;
    private int serverPort;

    private String email;
    private String password;

    private boolean isCreating;

    private String resultkey;
    private Bundle args;

    public ServerInfoPopupFragment() {
        this.args = new Bundle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainActivityInstance = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        if (args != null) {
            this.isCreating = args.getBoolean("isCreating");

            String serverIp = args.getString("serverIp");
            int serverPort = args.getInt("serverPort");

            String email = args.getString("email");
            String password = args.getString("password");

            this.serverIp = (serverIp == null) ? "" : serverIp;
            this.serverPort = (serverPort == 0) ? -1 : serverPort;
            this.email = (email == null) ? "" : email;
            this.password = (password == null) ? "" : password;

            this.resultkey = args.getString("resultKey");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_server_info, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        TextView titleTextView = root.findViewById(R.id.serverInfoTitle);
        if (this.isCreating)
            titleTextView.setText("Create Server");
        else
            titleTextView.setText("Edit Server");

        EditText serverIpEdittext = root.findViewById(R.id.serverInfoIpEdittext);
        EditText serverPortEdittext = root.findViewById(R.id.serverInfoPortEdittext);

        EditText emailEdittext = root.findViewById(R.id.serverInfoEmailEdittext);
        EditText passwordEdittext = root.findViewById(R.id.serverInfoPasswordEdittext);

        Button saveButton = root.findViewById(R.id.serverInfoSaveButton);

        serverIpEdittext.setText(this.serverIp);
        if (this.serverPort != -1)
            serverPortEdittext.setText(String.valueOf(this.serverPort));

        emailEdittext.setText(this.email);
        passwordEdittext.setText(this.password);

        saveButton.setOnClickListener((v) -> {
            // if the number was not a number
            try {
                String serverIp = serverIpEdittext.getText().toString();
                if (serverPortEdittext.getText().toString().trim().isEmpty())
                    return;

                int serverPort = Integer.parseInt(serverPortEdittext.getText().toString());
                String email = emailEdittext.getText().toString();
                String password = passwordEdittext.getText().toString();

                this.args.putString("serverIp", serverIp);
                this.args.putInt("serverPort", serverPort);
                this.args.putString("email", email);
                this.args.putString("password", password);

                if (!this.serverIp.equals(serverIp) || this.serverPort != serverPort || !this.email.equals(email) || !this.password.equals(password)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(), 0);
                    }

                    this.destroy();
                }
            }
            catch (NumberFormatException e) {
                this.mainActivityInstance.showCustomToast("The port was not a number XD", Color.argb(90, 235, 64, 52));
            }
        });
    }

    public void destroy () {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        transaction.remove(this);
        transaction.commit();

        getParentFragmentManager().setFragmentResult(this.resultkey, this.args);
    }
}

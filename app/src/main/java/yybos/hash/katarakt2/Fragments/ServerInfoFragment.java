package yybos.hash.katarakt2.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class ServerInfoFragment extends Fragment {
    private MainActivity mainActivityInstance;

    private String serverIp;
    private int serverPort;

    private String email;
    private String password;

    private boolean isCreating;

    public ServerInfoFragment () {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainActivityInstance = ((MainActivity) requireActivity());

        Bundle args = getArguments();
        if (args != null) {
            this.isCreating = false;

            this.serverIp = args.getString("serverip");
            this.serverPort = args.getInt("serverport");

            this.email = args.getString("email");
            this.password = args.getString("password");
        }
        else {
            this.isCreating = true;

            this.serverIp = "";
            this.serverPort = -1;

            this.email = "";
            this.password = "";
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
        if (isCreating)
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
            LoginFragment loginFragmentInstance = ((MainActivity) requireActivity()).getLoginFragmentInstance();

            // if the number was not a number
            try {
                Server server = new Server();
                server.serverIp = serverIpEdittext.getText().toString();
                server.serverPort = Integer.parseInt(serverPortEdittext.getText().toString());
                server.email = emailEdittext.getText().toString();
                server.password = passwordEdittext.getText().toString();

                if (isCreating) {
                    // only if there is info
                    if (!this.serverIp.equals(server.serverIp) || this.serverPort != server.serverPort || !this.email.equals(server.email) || !this.password.equals(server.password))
                        loginFragmentInstance.addServer(server);
                }
                else {
                    // only if info was changed
                    if (!this.serverIp.equals(server.serverIp) || this.serverPort != server.serverPort || !this.email.equals(server.email) || !this.password.equals(server.password))
                        loginFragmentInstance.saveInfo(server);
                }

                loginFragmentInstance.closeInfo();
            }
            catch (NumberFormatException e) {
                this.mainActivityInstance.showCustomToast("The port was not a number XD", Color.argb(90, 235, 64, 52));
            }
        });
    }
}

package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;

public class InputPopupFragment extends Fragment {
    private MainActivity mainActivityInstance;

    public InputPopupFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_input_popup, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.mainActivityInstance = (MainActivity) requireActivity();

        EditText usernameEdittext = root.findViewById(R.id.inputPopupUsername);

        Button acceptButton = root.findViewById(R.id.inputPopupAcceptButton);
        Button cancelButton = root.findViewById(R.id.inputPopupCancelButton);

        acceptButton.setOnClickListener((v) -> {
            String username = usernameEdittext.getText().toString().trim();

            if (username.isEmpty()) {

            }
            else {
                this.mainActivityInstance.setLoginUsername(username);
                this.mainActivityInstance.getClient().setUsername(username);
            }
        });
    }
}

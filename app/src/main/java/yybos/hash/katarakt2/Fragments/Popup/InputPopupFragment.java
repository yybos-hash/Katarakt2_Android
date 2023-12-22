package yybos.hash.katarakt2.Fragments.Popup;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

        String requestKey;
        if (getArguments() != null)
            requestKey = getArguments().getString("resultKey");
        else {
            requestKey = "";
        }

        this.mainActivityInstance = (MainActivity) requireActivity();

        EditText inputEdittext = root.findViewById(R.id.inputPopupEdittext);

        Button acceptButton = root.findViewById(R.id.inputPopupAcceptButton);
        Button cancelButton = root.findViewById(R.id.inputPopupCancelButton);

        acceptButton.setOnClickListener((v) -> {
            String content = inputEdittext.getText().toString().trim();

            if (content.isEmpty()) {
                InputPopupFragment.this.mainActivityInstance.showCustomToast("Nuh uh. You either press cancel or write something", Color.argb(90, 235, 64, 52));
            }
            else {
                Bundle result = new Bundle();
                result.putString("inputResult", content);
                getParentFragmentManager().setFragmentResult(requestKey, result);

                InputPopupFragment.this.destroy();
            }
        });
        cancelButton.setOnClickListener((v) -> {
            InputPopupFragment.this.destroy();
        });
    }

    @Override
    public void onDestroyView () {

        super.onDestroyView();
    }

    private void destroy () {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.remove(InputPopupFragment.this);
        transaction.commit();
    }
}

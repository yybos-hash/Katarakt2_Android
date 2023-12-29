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

public class InputPopupFragment extends Fragment {
    private MainActivity mainActivityInstance;
    private String resultKey;
    private String result;

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

        if (getArguments() != null)
            this.resultKey = getArguments().getString("resultKey");
        else {
            this.resultKey = "";
        }

        this.mainActivityInstance = (MainActivity) requireActivity();

        TextView title = root.findViewById(R.id.inputPopupTitle);
        TextView text = root.findViewById(R.id.inputPopupText);

        title.setText(getArguments().getString("title"));
        text.setText(getArguments().getString("text"));

        EditText inputEdittext = root.findViewById(R.id.inputPopupEdittext);
        inputEdittext.setHint(getArguments().getString("hint"));

        Button acceptButton = root.findViewById(R.id.inputPopupAcceptButton);
        Button cancelButton = root.findViewById(R.id.inputPopupCancelButton);

        acceptButton.setOnClickListener((v) -> {
            String content = inputEdittext.getText().toString().trim();

            if (content.trim().isEmpty()) {
                InputPopupFragment.this.mainActivityInstance.showCustomToast("Nuh uh. You either press cancel or write something", Color.argb(90, 235, 64, 52));
            }
            else {
                this.result = content;

                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(), 0);
                }

                InputPopupFragment.this.destroy();
            }
        });
        cancelButton.setOnClickListener((v) -> {
            this.result = "";

            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(), 0);
            }

            InputPopupFragment.this.destroy();
        });
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
    }

    public void destroy () {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        transaction.remove(this);
        transaction.commit();

        Bundle result = new Bundle();
        result.putString("inputResult", this.result);
        getParentFragmentManager().setFragmentResult(this.resultKey, result);
    }
}

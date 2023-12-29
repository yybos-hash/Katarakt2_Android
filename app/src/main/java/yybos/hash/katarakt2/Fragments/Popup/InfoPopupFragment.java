package yybos.hash.katarakt2.Fragments.Popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.Fragments.ChatFragment;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;

public class InfoPopupFragment extends Fragment {
    private String resultKey = "";
    private int result = -1;

    public InfoPopupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_popup_error, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        // views
        TextView titleTextView = root.findViewById(R.id.popupErrorTitle);
        TextView descriptionTextView = root.findViewById(R.id.popupErrorDescription);

        Button firstButton = root.findViewById(R.id.popupErrorFirstButton);
        Button secondButton = root.findViewById(R.id.popupErrorSecondButton);

        if (getArguments() != null) {
            Bundle args = this.getArguments();

            // get view's text
            String title = args.getString("title");
            String description = args.getString("description");

            String firstButtonText = args.getString("firstButtonText");
            String secondButtonText = args.getString("secondButtonText");

            this.resultKey = args.getString("resultKey");

            // change view's text
            titleTextView.setText(title);
            descriptionTextView.setText(description);

            firstButton.setText(firstButtonText);
            secondButton.setText(secondButtonText);

            // set buttons listener
            firstButton.setOnClickListener((v) -> {
                this.result = 1;
                InfoPopupFragment.this.destroy();
            });
            secondButton.setOnClickListener((v) -> {
                this.result = 0;
                InfoPopupFragment.this.destroy();
            });
        }
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

        Bundle args = new Bundle();
        args.putInt("button", this.result);
        getParentFragmentManager().setFragmentResult(this.resultKey, args);
    }
}
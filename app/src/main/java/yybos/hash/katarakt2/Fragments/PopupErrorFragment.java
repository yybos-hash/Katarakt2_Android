package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.R;

public class PopupErrorFragment extends Fragment {
    public PopupErrorFragment() {
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
        View root = getView();
        if (root == null)
            return;

        // views
        TextView titleTextView = root.findViewById(R.id.popupErrorTitle);
        TextView descriptionTextView = root.findViewById(R.id.popupErrorDescription);

        Button firstButton = root.findViewById(R.id.popupErrorFirstButton);
        Button secondButton = root.findViewById(R.id.popupErrorSecondButton);

        // set buttons listener
        firstButton.setOnClickListener(this::buttonAction);
        secondButton.setOnClickListener(this::buttonAction);

        if (getArguments() != null) {
            Bundle args = this.getArguments();

            // get view's text
            String title = args.getString("title");
            String description = args.getString("description");

            String firstButtonText = args.getString("firstButtonText");
            String secondButtonText = args.getString("secondButtonText");

            // change view's text
            titleTextView.setText(title);
            descriptionTextView.setText(description);

            firstButton.setText(firstButtonText);
            secondButton.setText(secondButtonText);
        }
    }

    @Override
    public void onDestroy () {
        // little trick to get the chatFragment instance. Basically I create a tag when creating the chatFragment, then i can identify it here using the tag
        ChatFragment chatFragmentInstance = ((ChatFragment) getParentFragmentManager().findFragmentByTag("chatFragmentInstance"));

        if (chatFragmentInstance != null)
            chatFragmentInstance.removeGeneralFrameLayout();

        super.onDestroy();
    }

    // button action

    private void buttonAction (View view) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
    }
}
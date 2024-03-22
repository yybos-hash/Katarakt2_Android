package yybos.hash.katarakt2.Fragments.Popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.R;

public class OptionFragment extends Fragment {
    Drawable icon;
    String text;

    public OptionFragment () {

    }
    public OptionFragment (String text, Drawable icon) {
        this.text = text;
        this.icon = icon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_options_option, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        TextView text = root.findViewById(R.id.optionText);
        ImageView icon = root.findViewById(R.id.optionIcon);

        text.setText(this.text);
        icon.setBackground(this.icon);
    }
}

package yybos.hash.katarakt2.Fragments.Custom.ViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.Custom.CustomSpinnerFragment;
import yybos.hash.katarakt2.Fragments.Custom.ViewHolders.SpinnerViewHolder;
import yybos.hash.katarakt2.R;

public class SpinnerViewAdapter extends RecyclerView.Adapter<SpinnerViewHolder> {
    private List<Object> objectsList = new ArrayList<>();
    private CustomSpinnerFragment spinnerFragment;

    public SpinnerViewAdapter (CustomSpinnerFragment spinnerFragment) {
        this.spinnerFragment = spinnerFragment;
    }

    public void addObject (Object object) {
        this.objectsList.add(object);
        notifyItemInserted(this.getItemCount() - 1);
    }

    @NonNull
    @Override
    public SpinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_spinner_object, parent, false);
        return new SpinnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SpinnerViewHolder holder, int position) {
        Object object = this.objectsList.get(position);

        holder.objectName.setText(object.toString());
        holder.constraint.setOnClickListener((v) -> {
            SpinnerViewAdapter.this.spinnerFragment.objectSelected(object);
        });
    }

    @Override
    public int getItemCount() {
        return this.objectsList.size();
    }
}

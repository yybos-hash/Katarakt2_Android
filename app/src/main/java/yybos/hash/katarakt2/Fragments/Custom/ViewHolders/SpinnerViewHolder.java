package yybos.hash.katarakt2.Fragments.Custom.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;

public class SpinnerViewHolder extends RecyclerView.ViewHolder {
    public TextView objectName;
    public ConstraintLayout constraint;

    public SpinnerViewHolder(@NonNull View itemView) {
        super(itemView);

        this.objectName = itemView.findViewById(R.id.spinnerObjectName);
        this.constraint = itemView.findViewById(R.id.spinnerObjectConstraint);
    }
}

package yybos.hash.katarakt2.Fragments.ViewAdapters;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.Custom.CustomToggleFragment;
import yybos.hash.katarakt2.Fragments.LoginFragment;
import yybos.hash.katarakt2.Fragments.ViewHolders.LoginViewHolder;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class LoginViewAdapter extends RecyclerView.Adapter<LoginViewHolder> {
    private final List<Server> servers = new ArrayList<>();
    private final List<LoginViewHolder> holders = new ArrayList<>();

    private final LoginFragment loginFragmentInstance;

    public LoginViewAdapter(LoginFragment fragment) {
        this.loginFragmentInstance = fragment;
    }

    public void addServer (Server server) {
        this.servers.add(server);
        notifyItemChanged(this.servers.size() - 1);
    }
    public void updateServer(LoginViewHolder serverViewHolder) {
        int position = serverViewHolder.getAdapterPosition();
        if (position == NO_POSITION) {
            this.addServer(serverViewHolder.serverInfo);
            return;
        }

        Server server = this.servers.get(position);
        server.set(serverViewHolder.serverInfo);

        notifyItemChanged(position);
    }

    public List<Server> getServers() {
        return new ArrayList<>(this.servers);
    }

    @NonNull
    @Override
    public LoginViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_server, parent, false);
        return new LoginViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LoginViewHolder holder, int position) {
        Server server = this.servers.get(position);

        this.holders.add(holder);
        holder.setInfo(server);

        // Dynamically add CustomToggleFragment to the FragmentContainerView
        FragmentManager fragmentManager = ((FragmentActivity) this.loginFragmentInstance.requireContext()).getSupportFragmentManager();
        if (!fragmentManager.isStateSaved()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(holder.fragmentContainer.getId(), holder.toggleInstance);
            transaction.commit();

            if (server.isDefault) {
                holder.toggleInstance.makeTrue();
            }
        }

        holder.mainLinearLayout.setOnLongClickListener((v) -> {
            this.loginFragmentInstance.openInfo(holder);
            return true;
        });
        holder.mainLinearLayout.setOnClickListener((v) -> {
            this.loginFragmentInstance.tryConnection(server);
        });
        holder.fragmentContainer.setOnClickListener((v) -> {

            // disabled any other active toggles
            if (!holder.toggleInstance.getState()) {
                for (LoginViewHolder viewHolder : this.holders) {
                    CustomToggleFragment toggleInstance = viewHolder.toggleInstance;

                    if (toggleInstance.getState()) {
                        toggleInstance.makeFalse();
                        viewHolder.serverInfo.isDefault = false;
                    }
                }
            }

            holder.toggleInstance.toggle();

            server.isDefault = true;
            this.loginFragmentInstance.saveInfo(holder, server);
        });
    }

    @Override
    public int getItemCount() {
        return this.servers.size();
    }
}

package net.mready.workshop.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.mready.workshop.R;
import net.mready.workshop.models.Repository;

import java.util.ArrayList;
import java.util.List;

public class RepositoriesAdapter extends RecyclerView.Adapter<RepositoriesAdapter.RepositoryViewHolder> {

    private final List<Repository> repositories;

    public RepositoriesAdapter() {
        repositories = new ArrayList<>();
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories.clear();
        this.repositories.addAll(repositories);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_repository;
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RepositoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {
        holder.bind(repositories.get(position));
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    class RepositoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFullName;
        private TextView tvDescription;
        private TextView tvUrl;

        private RepositoryViewHolder(View itemView) {
            super(itemView);

            tvFullName = (TextView) itemView.findViewById(R.id.tv_repository_full_name);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_repository_description);
            tvUrl = (TextView) itemView.findViewById(R.id.tv_repository_url);
        }

        private void bind(Repository repository) {
            tvFullName.setText(repository.getFullName());
            tvDescription.setText(repository.getDescription());
            tvUrl.setText(repository.getUrl());
        }

    }

}
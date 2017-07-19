package com.linked_sys.hns.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linked_sys.hns.Model.Tasks;
import com.linked_sys.hns.R;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<Tasks> tasks;
    public ArrayList<Tasks> filteredList;
    private TasksFilter tasksFilter;
    private TaskAdapterListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subject, homework, status;
        RelativeLayout  taskRow;

        MyViewHolder(View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.txt_subject);
            homework = (TextView) view.findViewById(R.id.txt_homework);
            status = (TextView) view.findViewById(R.id.txt_status);
            taskRow = (RelativeLayout) view.findViewById(R.id.task_container);
        }
    }

    public TasksAdapter(Context mContext, ArrayList<Tasks> tasks, TaskAdapterListener listener) {
        this.mContext = mContext;
        this.tasks = tasks;
        this.filteredList = tasks;
        this.listener = listener;
        getFilter();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tasks_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Tasks tasks = filteredList.get(position);
        holder.subject.setText(tasks.getSubject());
        holder.homework.setText(tasks.getHomework());

        holder.status.setText(tasks.getStatus());

        if (tasks.getStatus().equals("null")) {
            holder.status.setText("");
        } else {
            holder.status.setText(mContext.getResources().getString(R.string.txt_not_solved));
        }

//        holder.iconText.setText(tasks.getSubject().substring(0, 1).toUpperCase());
//        holder.imgProfile.setColorFilter(tasks.getColor());
        applyClickEvents(holder, position);
    }

    @Override
    public long getItemId(int position) {
        return filteredList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.taskRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTaskRowClicked(position);
            }
        });
        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTaskRowClicked(position);
            }
        });
    }

    public interface TaskAdapterListener {
        void onTaskRowClicked(int position);
    }

    private class TasksFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Tasks> tempList = new ArrayList<>();
                // search content in tasks list
                for (Tasks task : tasks) {
                    if (task.getSubject().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(task);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = tasks.size();
                filterResults.values = tasks;
            }
            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         *
         * @param constraint text
         * @param results    filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Tasks>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        if (tasksFilter == null) {
            tasksFilter = new TasksFilter();
        }
        return tasksFilter;
    }
}

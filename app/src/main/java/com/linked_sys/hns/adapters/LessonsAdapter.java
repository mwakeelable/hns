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

import com.linked_sys.hns.Model.Lessons;
import com.linked_sys.hns.R;

import java.util.ArrayList;


public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<Lessons> lessons;
    public ArrayList<Lessons> filteredList;
    private LessonsFilter lessonsFilter;
    private LessonsAdapterListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView course_title, lesson_title;
        RelativeLayout row, container;

        MyViewHolder(View view) {
            super(view);
            course_title = (TextView) view.findViewById(R.id.txt_course_name);
            lesson_title = (TextView) view.findViewById(R.id.txt_lesson_name);
            row = (RelativeLayout) view.findViewById(R.id.lesson_row);
            container = (RelativeLayout) view.findViewById(R.id.lesson_container);
        }
    }

    public LessonsAdapter(Context mContext, ArrayList<Lessons> lessons, LessonsAdapterListener mListener) {
        this.mContext = mContext;
        this.lessons = lessons;
        this.listener = mListener;
        this.filteredList = lessons;
        getFilter();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Lessons lessons = filteredList.get(position);
        holder.course_title.setText(lessons.getCourseName());
        holder.lesson_title.setText(lessons.getLessonName());
        applyClickEvents(holder, position);
    }

    @Override
    public long getItemId(int position) {
        return filteredList.get(position).getLessonID();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    private class LessonsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Lessons> tempList = new ArrayList<>();
                // search content in lessons list
                for (Lessons lesson : lessons) {
                    if (lesson.getCourseName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(lesson);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = lessons.size();
                filterResults.values = lessons;
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
            filteredList = (ArrayList<Lessons>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        if (lessonsFilter == null) {
            lessonsFilter = new LessonsFilter();
        }
        return lessonsFilter;
    }

    public interface LessonsAdapterListener {
        void onLessonRowClicked(int position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLessonRowClicked(position);
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLessonRowClicked(position);
            }
        });
    }
}

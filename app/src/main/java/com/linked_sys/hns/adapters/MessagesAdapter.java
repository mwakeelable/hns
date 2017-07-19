package com.linked_sys.hns.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jrummyapps.android.util.HtmlBuilder;
import com.linked_sys.hns.Model.Message;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.CircleTransform;
import com.linked_sys.hns.components.FlipAnimator;
import com.linked_sys.hns.network.ApiEndPoints;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<Message> messages;
    public ArrayList<Message> filteredList;
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;
    private Bitmap avatar;
    private MessageFilter messageFilter;
    private boolean outbox = false;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView from, subject, message, iconText, txt_date;
        //        ZamanTextView timestamp;
        ImageView imgProfile, imgSeen;
        RelativeLayout iconContainer, iconBack, iconFront, messageContainer, messageRow;

        MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
//            timestamp = (ZamanTextView) view.findViewById(R.id.txt_timestamp);
            imgSeen = (ImageView) view.findViewById(R.id.img_seen);
            txt_date = (TextView) view.findViewById(R.id.timestamp);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            messageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            messageRow = (RelativeLayout) view.findViewById(R.id.message_row);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public MessagesAdapter(Context mContext, ArrayList<Message> messages, MessageAdapterListener listener, boolean outbox) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
        this.filteredList = messages;
        this.outbox = outbox;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        getFilter();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Message message = filteredList.get(position);
        holder.from.setText(message.getSenderName1() + " " + message.getSenderName4());
        holder.subject.setText(message.getSubject());
        holder.message.setMovementMethod(LinkMovementMethod.getInstance());
        holder.message.setText(getFromHTML(message.getMessage()));
        String date = getDateFormat(message.getTimestamp());
        long timeStamp = convertToTimestamp(date);
//        holder.timestamp.setTimeStamp(timeStamp);
//        holder.timestamp.setVisibility(View.GONE);
        if (message.isRead() && outbox)
            holder.imgSeen.setVisibility(View.VISIBLE);
        else
            holder.imgSeen.setVisibility(View.GONE);
        holder.txt_date.setText(date);
        String senderName = message.getSenderName1().substring(0, 1).toUpperCase() + message.getSenderName4().substring(0, 1).toUpperCase();
        holder.iconText.setText(senderName);
        holder.itemView.setActivated(selectedItems.get(position, false));
        applyReadStatus(holder, message);
        applyIconAnimation(holder, position);
        applyProfilePicture(holder, message);
        applyClickEvents(holder, position);
    }

    private Spanned getFromHTML(String message) {
        HtmlBuilder html = new HtmlBuilder();
        html.p(message);
        return html.build();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.iconFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.messageRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

//        holder.timestamp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onMessageRowClicked(position);
//            }
//        });
    }

    private void applyProfilePicture(final MyViewHolder holder, final Message message) {
        if (!message.getPicture().equals("null")) {
            Glide
                    .with(mContext)
                    .load(ApiEndPoints.BASE_URL + message.getPicture() + "?width=320")
                    .asBitmap()
                    .thumbnail(0.5f)
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            holder.imgProfile.setImageResource(R.drawable.avatar_placeholder);
                            holder.imgProfile.setColorFilter(message.getColor());
                            holder.iconText.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            avatar = resource;
                            holder.imgProfile.setColorFilter(null);
                            holder.iconText.setVisibility(View.GONE);
                            holder.imgProfile.setImageBitmap(avatar);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            holder.imgProfile.setImageResource(R.drawable.avatar_placeholder);
                            holder.imgProfile.setColorFilter(message.getColor());
                            holder.iconText.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
            holder.imgProfile.setImageResource(R.drawable.avatar_placeholder);
            holder.imgProfile.setColorFilter(message.getColor());
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return filteredList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    private void applyReadStatus(MyViewHolder holder, Message message) {
        if (message.isRead()) {
            holder.from.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.message));
        } else {
            if (outbox) {
                holder.from.setTypeface(null, Typeface.NORMAL);
                holder.subject.setTypeface(null, Typeface.NORMAL);
                holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
                holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.message));
            } else {
                holder.from.setTypeface(null, Typeface.BOLD);
                holder.subject.setTypeface(null, Typeface.BOLD);
                holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.from));
                holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            }
        }
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {
        void onIconClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }

    private String getDateFormat(String date) {
        String dateFormat = "";
        if (date.charAt(5) != '0')
            dateFormat += date.charAt(5);
        dateFormat += date.charAt(6);
        dateFormat += '/';
        if (date.charAt(8) != '0')
            dateFormat += date.charAt(8);
        dateFormat += date.charAt(9);
        dateFormat += '/';
        dateFormat += date.charAt(2);
        dateFormat += date.charAt(3);
        return dateFormat;
    }

    private long convertToTimestamp(String msg_date) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = formatter.parse(msg_date);
            long output = date.getTime() / 1000L;
            String str = Long.toString(output);
            long timestamp = Long.parseLong(str) * 1000;
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private class MessageFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Message> tempList = new ArrayList<>();
                // search content in messages list
                for (Message message : messages) {
                    if (message.getSenderName1().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(message);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = messages.size();
                filterResults.values = messages;
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
            filteredList = (ArrayList<Message>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        if (messageFilter == null) {
            messageFilter = new MessageFilter();
        }
        return messageFilter;
    }
}

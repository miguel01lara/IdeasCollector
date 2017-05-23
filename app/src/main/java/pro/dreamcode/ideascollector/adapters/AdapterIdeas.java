package pro.dreamcode.ideascollector.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import pro.dreamcode.ideascollector.AppIdeasCollector;
import pro.dreamcode.ideascollector.R;
import pro.dreamcode.ideascollector.beans.Ideas;
import pro.dreamcode.ideascollector.extras.Util;

/**
 * Created by migue on 25/04/2017.
 */

public class AdapterIdeas extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener{

    private static final String TAG = "MIKE";
    private LayoutInflater inflater;
    private Realm realmMgmt;
    private RealmResults<Ideas> realmResults;
    private RealmResults<Ideas> realmTotalResults;
    private MarkListener markListener;
    private final static int ITEM = 0;
    private final static int NO_ITEM = 1;
    private Context context;
    private int filterOption;

    public AdapterIdeas(Context context, Realm realm, RealmResults<Ideas> results, RealmResults<Ideas> realmTotalResults, MarkListener markListener) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        realmMgmt = realm;
        this.realmTotalResults = realmTotalResults;
        this.markListener  =markListener;
        update(results);
    }

    @Override
    public long getItemId(int position) {
        if (!realmResults.isEmpty()) {
            return realmResults.get(position).getInitialTime();
        } else {
            return RecyclerView.NO_ID;
        }
    }

    public void update(RealmResults results){
        realmResults = results;
        this.filterOption = AppIdeasCollector.load(context);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View v;
        if (viewType != ITEM) {
            v = inflater.inflate(R.layout.no_items, parent, false);
            return new NoItemsHolder(v);
        } else {
            v = inflater.inflate(R.layout.idea_item_layout, parent, false);
            return new ViewHolderIdeas(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        if (holder instanceof  ViewHolderIdeas) {
            Ideas idea = realmResults.get(position);
            ViewHolderIdeas holderIdeas = (ViewHolderIdeas) holder;
            holderIdeas.setBackground(idea.isCompleted());
            holderIdeas.setItemIcon(idea.isCompleted());
            holderIdeas.tvDescription.setText(idea.getDescription());
            holderIdeas.setDate(idea.getPlannedTime());

        }
    }

//    @Override
//    public int getItemCount() {
//        Log.d(TAG, "getItemCount: " + realmResults.size());
//        if (!realmResults.isEmpty()) {
//                return realmResults.size();
//
//        } else {
//            if (filterOption == Filter.LEAST_TIME_LEFT || filterOption == Filter.MOST_TIME_LEFT){
//                return ITEM;
//
//            } else {
//                return NO_ITEM;
//            }
//        }
//    }

    @Override
    public int getItemCount() {
        //realmTotalResults = realmMgmt.where(Ideas.class).findAllAsync();

        Log.d(TAG, "getItemCount: Total = " + realmTotalResults.size() + " | Partial = " + realmResults.size());
        if (!realmTotalResults.isEmpty()) {
            if (!realmResults.isEmpty()){
                return realmResults.size();
            } else {
                return NO_ITEM;
            }

        } else {
            return ITEM;
        }
    }

    @Override
    public int getItemViewType (int position){
        if (!realmTotalResults.isEmpty()){
            if (!realmResults.isEmpty()){
                return ITEM;
            } else {
                return NO_ITEM;
            }

        } else {
            return ITEM;
        }
    }

//    @Override
//    public int getItemViewType (int position){
//        Log.d(TAG, "getItemViewType: ");
//        if (!realmResults.isEmpty()){
//            return ITEM;
//        } else {
//            if (filterOption == Filter.COMPLETE || filterOption == Filter.INCOMPLETE){
//                return NO_ITEM;
//            } else {
//                return ITEM;
//            }
//        }
//    }

    @Override
    public void onSwipe(final int position) {
        realmMgmt.executeTransaction(new Realm.Transaction() {
                @Override
            public void execute(Realm realm) {
                realmResults.get(position).deleteFromRealm();
            }
        });
        notifyDataSetChanged();
    }

    class ViewHolderIdeas extends RecyclerView.ViewHolder{

        TextView tvDescription;
        TextView tvDate;
        Context context;
        View itemView;

        public ViewHolderIdeas(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.itemView = itemView;
            tvDescription = (TextView) itemView.findViewById(R.id.tv_idea);
            tvDate = (TextView) itemView.findViewById(R.id.tv_idea_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markListener.markCompleted(getAdapterPosition());
                }
            });
        }

        public void setBackground(boolean completed) {

            Drawable drawable;
            if (completed){
                drawable = ContextCompat.getDrawable(context, R.color.colorItemCompleted);
            } else {
                drawable = ContextCompat.getDrawable(context, R.drawable.bg_idea_item);
            }
            Util.setBackground(itemView, drawable);
        }

        public void setItemIcon(boolean completed){
            Util.setBullet(itemView, completed);
        }

        public void setDate(long time) {
            String formattedDate = DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_MONTH).toString();
            tvDate.setText(formattedDate);
        }
    }

    class NoItemsHolder extends RecyclerView.ViewHolder {

        public NoItemsHolder(View itemView) {
            super(itemView);
        }
    }
}

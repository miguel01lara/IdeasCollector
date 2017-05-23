package pro.dreamcode.ideascollector.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pro.dreamcode.ideascollector.extras.Util;

/**
 * Created by migue on 28/04/2017.
 */

public class CollectorRecyclerView extends RecyclerView {

    private List<View> emptyViews = Collections.emptyList();
    private List<View> noEmptyViews = Collections.emptyList();

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleViews();
        }
    };

    private void toggleViews() {
        if (getAdapter() != null && !emptyViews.isEmpty() && !noEmptyViews.isEmpty()){

            if (getAdapter().getItemCount() == 0){
                setVisibility(GONE);
                Util.hideViews(noEmptyViews);
                Util.showViews(emptyViews);

            }  else{
                setVisibility(VISIBLE);
                Util.hideViews(emptyViews);
                Util.showViews(noEmptyViews);
            }
        }
    }

    public CollectorRecyclerView(Context context) {
        super(context);
    }

    public CollectorRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CollectorRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null){
            adapter.registerAdapterDataObserver(dataObserver);
        }
        dataObserver.onChanged();
    }

    public void hideIfEmpty(View ...views) {
        noEmptyViews = Arrays.asList(views);
    }

    public void showIfEmpty(View ...views) {
        emptyViews = Arrays.asList(views);
    }
}

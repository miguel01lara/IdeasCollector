package pro.dreamcode.ideascollector;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import pro.dreamcode.ideascollector.adapters.AdapterIdeas;
import pro.dreamcode.ideascollector.adapters.Filter;
import pro.dreamcode.ideascollector.adapters.MarkListener;
import pro.dreamcode.ideascollector.adapters.SimpleTouchCallback;
import pro.dreamcode.ideascollector.beans.Ideas;
import pro.dreamcode.ideascollector.services.NotificationService;
import pro.dreamcode.ideascollector.widgets.CollectorRecyclerView;

public class ActivityMain extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout orderLayout;
    private CollectorRecyclerView listIdeas;
    private Button addNew;
    private Button orderBtn;
    private TextView orderHolder;
    private PopupMenu popupMenu;
    private Realm realmMgmt;
    private AdapterIdeas adapter;
    private View initialLayout;
    private RealmResults<Ideas> realmRes;
    private RealmResults<Ideas> realmTotalResults;
    private MarkListener markListener = new MarkListener() {
        @Override
        public void markCompleted(int position) {
            showDialogMark(position);
        }

        @Override
        public void markCompletedInRealm(final int position) {
            realmMgmt.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmRes.get(position).setCompleted(true);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public boolean isItemCompleted(int position) {
            return realmRes.get(position).isCompleted();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    PopupMenu.OnMenuItemClickListener menuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            boolean handled = true;
            int filterOption = Filter.LEAST_TIME_LEFT;
            switch (id){
                case R.id.menu_item_most_time:
                    AppIdeasCollector.save(ActivityMain.this, Filter.MOST_TIME_LEFT);
                    filterOption = Filter.MOST_TIME_LEFT;
                    break;
                case R.id.menu_item_least_time:
                    AppIdeasCollector.save(ActivityMain.this, Filter.LEAST_TIME_LEFT);
                    filterOption = Filter.LEAST_TIME_LEFT;
                    break;
                case R.id.menu_item_complete:
                    AppIdeasCollector.save(ActivityMain.this, Filter.COMPLETE);
                    filterOption = Filter.COMPLETE;
                    break;
                case R.id.menu_item_incomplete:
                    AppIdeasCollector.save(ActivityMain.this, Filter.INCOMPLETE);
                    filterOption = Filter.INCOMPLETE;
                    break;
                default:
                    handled = false;
                    break;
            }
            queryAccordingToFilter(filterOption);
            return handled;
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_create_idea) {
                showDialogAdd();
            } else if (v.getId() == R.id.btn_set_order){
                Context wrapper = new ContextThemeWrapper(ActivityMain.this, R.style.popupMenuStyle);
                popupMenu = new PopupMenu(wrapper, orderBtn);
                popupMenu.getMenuInflater().inflate(R.menu.menu_order_selector, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuListener);
                popupMenu.show();
            }
        }
    };

    private RealmChangeListener realmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            adapter.update(realmRes);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orderLayout = (LinearLayout) findViewById(R.id.order_viewer_layout);
        initialLayout = findViewById(R.id.initial_layout);
        listIdeas = (CollectorRecyclerView) findViewById(R.id.rv_ideas);
        addNew = (Button) findViewById(R.id.btn_create_idea);
        orderBtn = (Button) findViewById(R.id.btn_set_order);
        orderHolder = (TextView) findViewById(R.id.order_holder);
        addNew.setOnClickListener(clickListener);
        orderBtn.setOnClickListener(clickListener);

        realmMgmt = Realm.getDefaultInstance();
        int filterOption = AppIdeasCollector.load(this);
        queryAccordingToFilter(filterOption);

        realmTotalResults = realmMgmt.where(Ideas.class).findAllAsync();

        adapter = new AdapterIdeas(this, realmMgmt, realmRes, realmTotalResults, markListener);
        adapter.setHasStableIds(true);

        listIdeas.setAdapter(adapter);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        listIdeas.setLayoutManager(linearManager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listIdeas.hideIfEmpty(toolbar, orderLayout);
        listIdeas.showIfEmpty(initialLayout);

        SimpleTouchCallback callback = new SimpleTouchCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(listIdeas);
        AppIdeasCollector.setRalewayThin(this, addNew, orderBtn, orderHolder);

        if (toolbar.getChildAt(0) instanceof TextView) {
            TextView title = (TextView) toolbar.getChildAt(0);
            AppIdeasCollector.setRalewayThin(this, title);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 5000, pendingIntent);
    }

    private void queryAccordingToFilter(int filterOption) {

        switch (filterOption){
            case Filter.MOST_TIME_LEFT:
                realmRes = realmMgmt.where(Ideas.class).findAllSortedAsync("plannedTime", Sort.DESCENDING);
                orderBtn.setText(R.string.furthest_first);
                break;
            case Filter.LEAST_TIME_LEFT:
                realmRes = realmMgmt.where(Ideas.class).findAllSortedAsync("plannedTime", Sort.ASCENDING);
                orderBtn.setText(R.string.nearest_first);
                break;
            case Filter.COMPLETE:
                realmRes = realmMgmt.where(Ideas.class).equalTo("completed", true).findAllSortedAsync("plannedTime");
                orderBtn.setText(R.string.completed_only);
                break;
            case Filter.INCOMPLETE:
                realmRes = realmMgmt.where(Ideas.class).equalTo("completed", false).findAllSortedAsync("plannedTime");
                orderBtn.setText(R.string.pending_only);
                break;
        }
        realmRes.addChangeListener(realmListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        realmRes.addChangeListener(realmListener);
        realmTotalResults.addChangeListener(realmListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realmRes.removeAllChangeListeners();
        realmTotalResults.removeAllChangeListeners();
    }

    private void showDialogAdd() {
        DialogAddIdea dialofFrag = new DialogAddIdea();
        dialofFrag.show(getSupportFragmentManager(), "Add");
    }
    private void showDialogMark(int position) {
        DialogMarkedCompleted dialofFrag = new DialogMarkedCompleted();
        dialofFrag.setFragmentListener(markListener);
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialofFrag.setArguments(bundle);
        dialofFrag.show(getSupportFragmentManager(), "Mark");
    }
}

package pro.dreamcode.ideascollector;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Calendar;

import io.realm.Realm;
import pro.dreamcode.ideascollector.beans.Ideas;

/**
 * Created by migue on 24/04/2017.
 */

public class DialogAddIdea extends DialogFragment {

    private ImageButton closeBtn;
    private EditText ideaInput;
    private Button addIdeaBtn;
    private Realm realmMgmt;
    private DatePicker datePicker;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_add_idea:
                    addIdeaInfo();
                    break;
            }
            dismiss();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogTheme);
    }

    // TODO make a far better definition of the adding to Realm process
    private void addIdeaInfo() {
        //
        String date = datePicker.getDayOfMonth() + "/" + datePicker.getMonth() + "/" + datePicker.getYear();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //
        long initialTime = System.currentTimeMillis();
        final String ideaDescription = ideaInput.getText().toString();

        realmMgmt = Realm.getDefaultInstance();

        realmMgmt.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Ideas idea = realmMgmt.createObject(Ideas.class, System.currentTimeMillis());

                idea.setPlannedTime(calendar.getTimeInMillis());
                idea.setDescription(ideaDescription);
                idea.setCompleted(false);
            }
        });
        realmMgmt.close();
    }

    public DialogAddIdea() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_idea, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        datePicker = (DatePicker) view.findViewById(R.id.dpk_date_picker);
        ideaInput = (EditText) view.findViewById(R.id.edt_idea_description);
        closeBtn = (ImageButton) view.findViewById(R.id.btn_close_add_idea);
        addIdeaBtn = (Button) view.findViewById(R.id.btn_add_idea);

        addIdeaBtn.setOnClickListener(clickListener);
        closeBtn.setOnClickListener(clickListener);
    }

    //TODO  temporary full-screen mechanism
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}

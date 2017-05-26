package pro.dreamcode.ideascollector;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import io.realm.Realm;
import pro.dreamcode.ideascollector.beans.Ideas;
import pro.dreamcode.ideascollector.widgets.CollectorDatePicker;

/**
 * Created by migue on 24/04/2017.
 */

public class DialogAddIdea extends DialogFragment {

    private ImageButton closeBtn;
    private TextView dialogAddTitle;
    private EditText ideaInput;
    private Button addIdeaBtn;
    private Realm realmMgmt;
    private CollectorDatePicker datePicker;

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

    private void addIdeaInfo() {
        final String ideaDescription = ideaInput.getText().toString();

        realmMgmt = Realm.getDefaultInstance();

        realmMgmt.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Ideas idea = realmMgmt.createObject(Ideas.class, System.currentTimeMillis());

                idea.setPlannedTime(datePicker.getTime());
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

        dialogAddTitle = (TextView)view.findViewById(R.id.txv_title_add_idea);
        datePicker = (CollectorDatePicker) view.findViewById(R.id.dpk_date_picker);
        ideaInput = (EditText) view.findViewById(R.id.edt_idea_description);
        closeBtn = (ImageButton) view.findViewById(R.id.btn_close_add_idea);
        addIdeaBtn = (Button) view.findViewById(R.id.btn_add_idea);

        addIdeaBtn.setOnClickListener(clickListener);
        closeBtn.setOnClickListener(clickListener);

        AppIdeasCollector.setRalewayThin(getActivity(), ideaInput, addIdeaBtn, dialogAddTitle);
    }

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

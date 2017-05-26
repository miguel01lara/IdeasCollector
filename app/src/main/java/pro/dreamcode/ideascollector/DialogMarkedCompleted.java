package pro.dreamcode.ideascollector;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import pro.dreamcode.ideascollector.adapters.MarkListener;

/**
 * Created by migue on 09/05/2017.
 */

public class DialogMarkedCompleted extends DialogFragment {

    private ImageButton closeBtn;
    private Button markBtn;
    private Bundle arguments;
    private MarkListener markListener;
    private int position;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_mark_completed:
                    markCompleted();
                    break;
            }
            dismiss();
        }
    };

    private void markCompleted() {
        if (markListener != null) {
            markListener.markCompletedInRealm(position);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mark_completed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeBtn = (ImageButton) view.findViewById(R.id.btn_close);
        markBtn = (Button) view.findViewById(R.id.btn_mark_completed);

        AppIdeasCollector.setRalewayThin(getActivity(), markBtn);

        arguments = getArguments();

        if (arguments != null){
            position = arguments.getInt("POSITION");
        }

        Drawable d;
        String text;

        if (!markListener.isItemCompleted(position)) {
            d = ContextCompat.getDrawable(getActivity(), R.drawable.ic_incomplete);
            text = "Mark as Completed";
        }
        else {
            d = ContextCompat.getDrawable(getActivity(), R.drawable.ic_completed);
            text = "Already Completed!";
        }
        markBtn.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
        markBtn.setText(text);

        closeBtn.setOnClickListener(listener);
        markBtn.setOnClickListener(listener);
    }

    public void setFragmentListener(MarkListener markListener) {
        this.markListener = markListener;
    }
}

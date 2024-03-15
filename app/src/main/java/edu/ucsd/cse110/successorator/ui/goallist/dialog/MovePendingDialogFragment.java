package edu.ucsd.cse110.successorator.ui.goallist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentMovePendingDialogBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

public class MovePendingDialogFragment extends DialogFragment {

    private FragmentMovePendingDialogBinding view;
    private MainViewModel activityModel;
    private Integer goalID;
    private String goalText;
    private Boolean goalCompleted;
    private Integer goalSortOrder;
    private String goalFrequency;
    private String goalContext;

    MovePendingDialogFragment() {
        // Required empty public constructor
    }

    public static MovePendingDialogFragment newInstance(Integer id, String text, boolean isCompleted, Integer sortOrder, String frequency, String context) {
        MovePendingDialogFragment fragment = new MovePendingDialogFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("text", text);
        args.putBoolean("isCompleted", isCompleted);
        args.putInt("sortOrder", sortOrder);
        args.putString("frequency", frequency);
        args.putString("context", context);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        view = FragmentMovePendingDialogBinding.inflate(getLayoutInflater());

        this.goalID = getArguments().getInt("id");
        this.goalText = getArguments().getString("text");
        this.goalCompleted = getArguments().getBoolean("isCompleted");
        this.goalSortOrder = getArguments().getInt("sortOrder");
        this.goalFrequency = getArguments().getString("frequency");
        this.goalContext = getArguments().getString("context");

        setupListeners();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("")
                .setView(view.getRoot());
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setupListeners() {
        view.moveToToday.setOnClickListener(v -> {

            activityModel.remove(this.goalID);
            Goal newGoal = new Goal(null, goalText, goalCompleted, -1, goalFrequency, SuccessDate.getCurrentDateAsString(), goalContext);
            activityModel.append(newGoal);
            dismiss();
        });
        view.moveToTomorrow.setOnClickListener(v -> {

            activityModel.remove(this.goalID);
            Goal newGoal = new Goal(null, goalText, goalCompleted, -1, goalFrequency, SuccessDate.getTmwsDateAsString(), goalContext);
            activityModel.append(newGoal);
            dismiss();
        });
        view.finishPendingGoal.setOnClickListener(v -> {

            activityModel.remove(this.goalID);
            Goal newGoal = new Goal(null, goalText, true, -1, goalFrequency, SuccessDate.getCurrentDateAsString(), goalContext);
            activityModel.append(newGoal);
            dismiss();
        });
        view.deletePendingGoal.setOnClickListener(v -> {

            activityModel.remove(this.goalID);
            dismiss();
        });
    }
}


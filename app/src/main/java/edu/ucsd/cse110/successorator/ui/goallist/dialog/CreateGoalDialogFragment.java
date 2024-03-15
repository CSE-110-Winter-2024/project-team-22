package edu.ucsd.cse110.successorator.ui.goallist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCreateTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreateGoalDialogFragment extends DialogFragment {
    private FragmentDialogCreateTaskBinding view;
    private MainViewModel activityModel;
    private Date DisplayDate;
    private String context = "Home";

    private CalendarDialogFragment calendarFragment = CalendarDialogFragment.newInstance();
    CreateGoalDialogFragment() {
        // Required empty public constructor
    }

//    public static CreateCardDialogFragment newInstance(){
//        var fragment = new CreateCardDialogFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static CreateGoalDialogFragment newInstance() {
        CreateGoalDialogFragment fragment = new CreateGoalDialogFragment();
        Bundle args = new Bundle();
        //args.putSerializable("displayedDate", displayedDate); // Assuming Date is Serializable
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //var modelOwner = requireActivity();
        //var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        //var modelProvider = new ViewModelProvider(modelOwner,modelFactory);
        //this.activityModel = modelProvider.get(MainViewModel.class);
        //activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        //if (getArguments() != null) {
        //    DisplayDate = (Date) getArguments().getSerializable("displayedDate");
        //}
        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        this.view = FragmentDialogCreateTaskBinding.inflate(getLayoutInflater());
//        return new AlertDialog.Builder(getActivity())
//                .setTitle("New Task...")
//                .setMessage("Please provide the new task title.")
//                .setView(view.getRoot())
//                .setPositiveButton("Save",this::onPositiveButtonClick)
//                .setNegativeButton("Cancel",this::onNegativeButtonClick)
//                .create();
        view = FragmentDialogCreateTaskBinding.inflate(getLayoutInflater());

        view.goalDate.setOnClickListener(v -> {
            FragmentManager fmng = getActivity().getSupportFragmentManager();
            calendarFragment.show(fmng, "ChooseCalendarFragment");
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Task...")
                .setMessage("Please provide the new task title.")
                .setView(view.getRoot())
                .setPositiveButton("Save", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick);
        setupContextButtons();

        int selectedHomeBtn = R.drawable.home_selected_button;
        view.homeButton.setBackgroundResource(selectedHomeBtn);

        RadioButton oneTimeButton = view.oneTimeButton;
        RadioButton dailyBtn = view.dailyButton;
        RadioButton weeklyButton = view.weeklyButton;
        if (activityModel.getLabel().getValue().equals("Recurring")) {
            oneTimeButton.setVisibility(View.GONE);
            dailyBtn.toggle();
        } else {
            oneTimeButton.setVisibility(View.VISIBLE);
        }


        weeklyButton.setText("weekly on " + activityModel.getDateOtherFormat().substring(0,4));
//        weeklyButton.setText("Weekly");
        RadioButton yearlyButton = view.yearlyButton;
        yearlyButton.setText("yearly on "+ activityModel.getDateOtherFormat().substring(4,8));
//        yearlyButton.setText("Yearly");
        RadioButton monthlyButton = view.monthlyButton;
         String day = activityModel.getDateOtherFormat().substring(6,8);
        day = day.trim();
        int int_day = Integer.parseInt(day);
        int x = (int_day+6)/7;
        String temp;
        if (x==1){
            temp = "st ";
        }
        else if (x==2){
            temp = "nd ";
        }
        else if(x==3){
            temp = "rd ";
        }
        else{
            temp = "th ";
        }

        String X = String.valueOf(x);
        monthlyButton.setText("monthly on "+X+temp+activityModel.getDateOtherFormat().substring(0,4));
//        monthlyButton.setText("Monthly");
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        calendarFragment.getChosenDate().observe(this, sDate -> {
            Log.d("MyDialogFragment", "Updating goal date with: " + sDate);
            view.goalDate.setText(sDate);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        calendarFragment.getChosenDate().removeObservers(this);
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which){
        var text = view.cardFrontEditText.getText().toString();
        var frequency = getSelectedFrequency(view.frequencyGroup);

        var goalDateString = view.goalDate.getText().toString();

        var goal = new Goal(null, text, false, -1, frequency, goalDateString, context);
        activityModel.append(goal);
        // activityModel.updateDisplayedGoals();
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which){
        dialog.cancel();
    }
    private String getSelectedFrequency(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = view.getRoot().findViewById(selectedId);
        if (selectedId == view.monthlyButton.getId()){
            return "Monthly";
        }
        if (selectedId == view.weeklyButton.getId()){
            return "Weekly";
        }
        if (selectedId == view.yearlyButton.getId()){
            return "Yearly";
        }
        return selectedRadioButton.getText().toString();
    }
    private void setupContextButtons() {
        int selectedHomeBtn = R.drawable.home_selected_button;
        int selectedWorkBtn = R.drawable.work_selected_button;
        int selectedSchoolBtn = R.drawable.school_selected_button;
        int selectedErrandsBtn = R.drawable.errands_selected_button;

        // Assuming you have access to your buttons in view binding
        view.homeButton.setOnClickListener(v -> {
            context = "Home";
            resetAttributes();
            view.homeButton.setBackgroundResource(selectedHomeBtn);
        });
        view.workButton.setOnClickListener(v -> {
            context = "Work";
            resetAttributes();
            view.workButton.setBackgroundResource(selectedWorkBtn);
        });
        view.schoolButton.setOnClickListener(v -> {
            context = "School";
            resetAttributes();
            view.schoolButton.setBackgroundResource(selectedSchoolBtn);
        });
        view.errandsButton.setOnClickListener(v -> {
            context = "Errands";
            resetAttributes();
            view.errandsButton.setBackgroundResource(selectedErrandsBtn);
        });
    }

    private void resetAttributes() {
        int unselectedHomeBtn = R.drawable.home_button;
        int unselectedWorkBtn = R.drawable.work_button;
        int unselectedSchoolBtn = R.drawable.school_button;
        int unselectedErrandsBtn = R.drawable.errands_button;

        view.homeButton.setBackgroundResource(unselectedHomeBtn);
        view.workButton.setBackgroundResource(unselectedWorkBtn);
        view.schoolButton.setBackgroundResource(unselectedSchoolBtn);
        view.errandsButton.setBackgroundResource(unselectedErrandsBtn);
    }

}

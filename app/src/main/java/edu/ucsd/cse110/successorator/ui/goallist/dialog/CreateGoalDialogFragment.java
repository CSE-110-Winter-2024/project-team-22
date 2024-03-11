package edu.ucsd.cse110.successorator.ui.goallist.dialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.time.LocalDate;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCalendarBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCreateTaskBinding;
import edu.ucsd.cse110.successorator.databinding.ListItemGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import androidx.appcompat.app.AppCompatActivity;

import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class CreateGoalDialogFragment extends DialogFragment {
    private FragmentDialogCreateTaskBinding view;
    private MainViewModel activityModel;
    private Date DisplayDate;
    private String context;

    private CalendarDialogFragment calendarFragment = CalendarDialogFragment.newInstance();
    CreateGoalDialogFragment(){
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
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
//        this.view = FragmentDialogCreateTaskBinding.inflate(getLayoutInflater());
//        return new AlertDialog.Builder(getActivity())
//                .setTitle("New Task...")
//                .setMessage("Please provide the new task title.")
//                .setView(view.getRoot())
//                .setPositiveButton("Save",this::onPositiveButtonClick)
//                .setNegativeButton("Cancel",this::onNegativeButtonClick)
//                .create();
        view = FragmentDialogCreateTaskBinding.inflate(getLayoutInflater());

        view.goalDate.setOnClickListener( v -> {
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


        RadioButton weeklyButton = view.weeklyButton;
        weeklyButton.setText("weekly on " + activityModel.getDate().toString().substring(0,4));
        RadioButton yearlyButton = view.yearlyButton;
        yearlyButton.setText("yearly on "+ activityModel.getDate().toString().substring(4,10));
        RadioButton monthlyButton = view.monthlyButton;
        String day = activityModel.getDate().toString().substring(8,10);
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
        monthlyButton.setText("monthly on "+X+temp+activityModel.getDate().toString().substring(0,4));
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
        // HEAD
        LocalDate goalCreationDate = null;
        if (DisplayDate != null) {
            goalCreationDate = SuccessDate.dateToLocalDate(DisplayDate); // Assuming SuccessDate has this method
        }
        // END HEAD
        // US 4
        LocalDate goalDate;

        if (goalDateString.equals("Click here to choose a date ...")) {
            goalDate = SuccessDate.getCurrentDate();
            // END US4
        } else {
            // Fallback to the current date if DisplayDate is not available
            goalCreationDate = SuccessDate.getCurrentDate();
        }
        var goal = new Goal(null, text, false, -1, frequency, goalCreationDate, context);
        activityModel.append(goal);
//        activityModel.updateDisplayedGoals();
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
        // Assuming you have access to your buttons in view binding
        view.homeButton.setOnClickListener(v -> context = "Home");
        view.workButton.setOnClickListener(v -> context = "Work");
        view.schoolButton.setOnClickListener(v -> context = "School");
        view.errandsButton.setOnClickListener(v -> context = "Errands");
    }



}

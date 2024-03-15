package edu.ucsd.cse110.successorator.ui.goallist.dialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDate;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCalendarBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCreateTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.util.MutableLiveDataSubjectAdapter;

public class CalendarDialogFragment extends DialogFragment {
    private FragmentDialogCalendarBinding view;
    private MainViewModel activityModel;
    MutableLiveData<String> chosenDate = new MutableLiveData<>();
    private String saveDate;

    CalendarDialogFragment(){
        // Required empty public constructor
    }



//    public static CreateCardDialogFragment newInstance(){
//        var fragment = new CreateCardDialogFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static CalendarDialogFragment newInstance() {
        CalendarDialogFragment fragment = new CalendarDialogFragment();
        Bundle args = new Bundle();
        // args.putSerializable();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        var modelOwner = requireActivity();
//        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
//        var modelProvider = new ViewModelProvider(modelOwner,modelFactory);
//        this.activityModel = modelProvider.get(MainViewModel.class);
//        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        view = FragmentDialogCalendarBinding.inflate(getLayoutInflater());

        CalendarView calendarView = view.calendarView;
        calendarView.setOnDateChangeListener((calendarView1, year, month, dayOfMonth) -> {
            LocalDate date = LocalDate.of(year, month+1, dayOfMonth);
            DateTimeFormatter f = DateTimeFormatter.ofPattern(SuccessDate.getFormatString());
            String dateFormatted = date.format(f);
            saveDate = dateFormatted;
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a date for your task:")
                .setView(view.getRoot())
                .setPositiveButton("Save", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick);
        return builder.create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which){
       // var dateInMillis = view.calendarView.getDate();
       // Instant instant = Instant.ofEpochMilli(dateInMillis);
       // LocalDate actualDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        if (saveDate == null) {
            chosenDate.setValue(SuccessDate.getCurrentDateAsString());
        } else {
            chosenDate.setValue(saveDate);
        }

        // sort order is an invalid value here, because append/prepend will replace it
        // activityModel.passSelectedDate(actualDateString);
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which){
        dialog.cancel();
    }
    private String getSelectedFrequency(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = view.getRoot().findViewById(selectedId);

        return selectedRadioButton.getText().toString();
    }

    public LiveData<String> getChosenDate() {
        return chosenDate;
    }

}


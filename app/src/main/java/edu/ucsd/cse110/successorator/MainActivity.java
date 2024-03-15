package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.ui.expandviews.ExpandViewsFragment;
import edu.ucsd.cse110.successorator.ui.expandviews.PendingFragment;
import edu.ucsd.cse110.successorator.ui.expandviews.RecurringFragment;
import edu.ucsd.cse110.successorator.ui.expandviews.TomorrowFragment;
import edu.ucsd.cse110.successorator.ui.focusview.FocusModeFragment;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListFragment;
import edu.ucsd.cse110.successorator.ui.goallist.OtherDateFragment;
import edu.ucsd.cse110.successorator.ui.goallist.dialog.CreateGoalDialogFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;
    private static LocalDate date; // date show in Successorator

    private MainViewModel viewModel;

    private boolean isShowingToday = true;
    private boolean isShowingExpand = true;
    private boolean isShowingFocus = true;
    public String label;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        var binding = ActivityMainBinding.inflate(getLayoutInflater());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(binding.getRoot());

        // Show Current Day
        date = SuccessDate.getCurrentDate();
        displayDate();
        // nextDay Button
        ImageButton next_day_button = (ImageButton)findViewById(R.id.imageButton_next);
        next_day_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDay();
                displayDate();
            }
        });

        viewModel.getGoalsSize().observe(isEmpty -> {
            if(isEmpty == null) return;
            TextView textViewMessage = findViewById(R.id.text_view_no_goals);
            if (isEmpty){
                // No goals present, show the message
                textViewMessage.setVisibility(View.VISIBLE);
            } else {
                // Goals are present, hide the message
                textViewMessage.setVisibility(View.GONE);
            }
        });

        // update label and date for Today, Tomorrow, Pending, Recurring
        viewModel.getFocus().observe(focus -> {
            TextView focusIndicator = findViewById(R.id.focusIndicator);
            focusIndicator.setText("Focus: " + focus);
        });

        viewModel.getLabel().observe(label -> {
                if (label == null) return;
                swapToLabelFragment(label);
                ImageButton advanceDateBtn = findViewById(R.id.imageButton_next);
                TextView textViewDate = findViewById(R.id.text_label);
                if (!label.equals("Today") && !label.equals("Tomorrow") && !label.equals("Pending") && !label.equals("Recurring")) {
                    textViewDate.setText("");
                } else {
                    textViewDate.setText(label);
                }

                if(label.equals("Today")){
                    advanceDateBtn.setVisibility(View.VISIBLE);
                    displayDate(SuccessDate.getCurrentDate());
                    date = SuccessDate.getCurrentDate();
                } else if (label.equals("Tomorrow")){
                    advanceDateBtn.setVisibility(View.VISIBLE);
                    displayDate(SuccessDate.getCurrentDate().plusDays(1));
                    date = SuccessDate.getCurrentDate().plusDays(1);
                } else if (label.equals("Pending") || label.equals("Recurring")){
                    advanceDateBtn.setVisibility(View.GONE);
                    displayNoDate();
                }

                if (!label.equals("Today") && !label.equals("Tomorrow") && !label.equals("Pending") && !label.equals("Recurring")) {
                    // no advance date button on pending and recurring
                    swapToDateFragment();
                    //advanceDateBtn.setActivated(false);

                    //TextView textViewMessage = findViewById(R.id.text_view_no_goals);
                    //textViewDate.setText("");
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var itemId = item.getItemId();

        if (itemId == R.id.action_bar_add_goal_views) {
            var dialogFragment = CreateGoalDialogFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "CreateDialogFragment");
        }

        if (itemId == R.id.action_bar_expand_more_views){
            swapExpandFragment(viewModel.getLabel().getValue());
        }

        if (itemId == R.id.action_bar_focus_mode_views){
            swapFocusFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    public void swapExpandFragment(String fromLabel) {
        if (!isShowingExpand){
            swapToLabelFragment(fromLabel);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ExpandViewsFragment.newInstance())
                    .commit();
        }
        isShowingExpand = !isShowingExpand;
    }

    public void swapToDateFragment() {
        label = viewModel.getLabel().getValue();
        if (label.equals("Today") || label.equals("Tomorrow") || label.equals("Pending") || label.equals("Recurring")) {
            swapToLabelFragment(label);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, OtherDateFragment.newInstance())
                    .commit();
        }
    }

    public void swapToLabelFragment(String label) {
        if (label.equals("Today")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GoalListFragment.newInstance())
                    .commit();
        } else if (label.equals("Tomorrow")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, TomorrowFragment.newInstance())
                    .commit();
        } else if (label.equals("Pending")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, PendingFragment.newInstance())
                    .commit();
        } else if (label.equals("Recurring")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, RecurringFragment.newInstance())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, OtherDateFragment.newInstance())
                    .commit();
        }
    }

    public void swapFocusFragment() {
        label = viewModel.getLabel().getValue();
        if (!isShowingFocus){
            swapToLabelFragment(label);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, FocusModeFragment.newInstance())
                    .commit();
        }
        isShowingFocus = !isShowingFocus;
    }

    // next day
    private void nextDay(){
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(date);
        //calendar.add(Calendar.DAY_OF_MONTH,1);
        //date = calendar.getTime();

        LocalDate theNextDay = date;
        theNextDay = theNextDay.plusDays(1);
        date = theNextDay;
        viewModel.setCurrentDate(SuccessDate.dateToString(date));

        displayDate();
        // you want to refresh the list of goals currently being displayed

    }

    // display Date in textview
    private void displayDate(){
        //SimpleDateFormat dateFormat = new SimpleDateFormat("EE, M/d");
        //SimpleDateFormat dayFormat = new SimpleDateFormat("M/d");
        //String currentDate = dateFormat.format(date);
        //String currentDay = dayFormat.format(date);
        //Log.d("===============", date.toString());
        //TextView textViewDate = findViewById(R.id.tomorrow_date);
        //textViewDate.setText(currentDate);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE M/d");
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("M/d");
        String currentDate = date.format(dateFormat);
        String currentDay = date.format(dayFormat);
        TextView textViewDate = findViewById(R.id.tomorrow_date);
        textViewDate.setText(currentDate);
    }

    private void displayNoDate(){
        //SimpleDateFormat dateFormat = new SimpleDateFormat("EE, M/d");
        //String currentDate = dateFormat.format(date);
        //Log.d("===============", date.toString());
        //TextView textViewDate = findViewById(R.id.text_view_date);
        //viewModel.setDate(date);
        TextView textViewDate = findViewById(R.id.tomorrow_date);
        textViewDate.setText("");
    }

    private void nextDayOneTime(){
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(date);
        //calendar.add(Calendar.DAY_OF_MONTH,1);
        //Date date = calendar.getTime();
        //displayDate(date);

        LocalDate calendar = SuccessDate.getCurrentDate();
        calendar = date;
        calendar.plusDays(1);
        LocalDate newDate = calendar;
        displayDate(newDate);
    }

    //private void displayDate(Date d){
    //    SimpleDateFormat dateFormat = new SimpleDateFormat("EE, M/d");
    //    String currentDate = dateFormat.format(d);
    //    Log.d("===============", date.toString());
    //    //TextView textViewDate = findViewById(R.id.text_view_date);
    //    //viewModel.setDate(date);
    //    TextView textViewDate = findViewById(R.id.tomorrow_date);
    //    textViewDate.setText(currentDate);
    //}

    private void displayDate(LocalDate d){
       // SimpleDateFormat dateFormat = new SimpleDateFormat("EE, M/d");
       // String currentDate = dateFormat.format(d);
       // Log.d("===============", date.toString());
       // //TextView textViewDate = findViewById(R.id.text_view_date);
       // //viewModel.setDate(date);
       // TextView textViewDate = findViewById(R.id.tomorrow_date);
       // textViewDate.setText(currentDate);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE M/d");
        String currentDate = d.format(dateFormat);
        TextView textViewDate = findViewById(R.id.tomorrow_date);
        textViewDate.setText(currentDate);
    }

    private void removeAllCompleted() {
        viewModel.removeAllCompleted();
    }
}

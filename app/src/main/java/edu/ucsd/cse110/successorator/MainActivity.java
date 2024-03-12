package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.ui.expandviews.ExpandViewsFragment;
import edu.ucsd.cse110.successorator.ui.focusview.FocusModeFragment;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListFragment;
import edu.ucsd.cse110.successorator.ui.goallist.dialog.CreateGoalDialogFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;
    private static Date date; // date show in Successorator

    private MainViewModel viewModel;

    private boolean isShowingToday = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        var binding = ActivityMainBinding.inflate(getLayoutInflater());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(binding.getRoot());

        // Show Current Day
        date = new Date();
        displayDate();
        // nextDay Button
        ImageButton next_day_button = (ImageButton)findViewById(R.id.imageButton_next);
        next_day_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDay();
                displayDate();
                // removeAllCompleted();
            }
        });

        viewModel.getGoalsForToday().observe(goalsForToday -> {
            TextView textViewMessage = findViewById(R.id.text_view_no_goals);
            if (goalsForToday.size()==0 ){
                // No goals present, show the message
                textViewMessage.setVisibility(View.VISIBLE);
            } else {
                // Goals are present, hide the message
                textViewMessage.setVisibility(View.GONE);
            }
        });

        // TODO:
        // update label and date for Today, Tomorrow, Pending, Recurring
        viewModel.getLabel().observe(label -> {
                // update label for Today, Tomorrow, Pending, Recurring
                TextView textViewDate = findViewById(R.id.text_label);
                textViewDate.setText(label);
                // update date for Today, Tomorrow, Pending, Recurring
                if(label.equals("Today")){
                    displayDate();
                } else if (label.equals("Tomorrow")){
                    nextDayOneTime();
                } else {
                    displayNoDate();
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
//            swapFragment();
            var dialogFragment = ExpandViewsFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "ExpandDialogFragment");
        }

        if (itemId == R.id.action_bar_focus_mode_views){
            swapFocusFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    private void swapFragment() {
        if (!isShowingToday){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GoalListFragment.newInstance())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ExpandViewsFragment.newInstance())
                    .commit();
        }
        isShowingToday = !isShowingToday;
    }

    private void swapFocusFragment() {
        if (!isShowingToday){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GoalListFragment.newInstance())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, FocusModeFragment.newInstance())
                    .commit();
        }
        isShowingToday = !isShowingToday;
    }


    // next day
    private void nextDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        date = calendar.getTime();
        viewModel.setCurrentDate(date);
        displayDate();

        // you want to refresg the list of goals currently being displayed

    }

    // display Date in textview
    private void displayDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, M/d");
        SimpleDateFormat dayFormat = new SimpleDateFormat("M/d");
        String currentDate = dateFormat.format(date);
        String currentDay = dayFormat.format(date);
        Log.d("===============", date.toString());
        TextView textViewDate = findViewById(R.id.tomorrow_date);
        textViewDate.setText(currentDate);
    }

    private void displayNoDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, M/d");
        String currentDate = dateFormat.format(date);
        Log.d("===============", date.toString());
//        TextView textViewDate = findViewById(R.id.text_view_date);
        //viewModel.setDate(date);
        TextView textViewDate = findViewById(R.id.tomorrow_date);
        textViewDate.setText("");
    }

    private void nextDayOneTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date date = calendar.getTime();
        displayDate(date);
    }

    private void displayDate(Date d){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, M/d");
        String currentDate = dateFormat.format(d);
        Log.d("===============", date.toString());
//        TextView textViewDate = findViewById(R.id.text_view_date);
        //viewModel.setDate(date);
        TextView textViewDate = findViewById(R.id.tomorrow_date);
        textViewDate.setText(currentDate);
    }

    private void removeAllCompleted() {
        viewModel.removeAllCompleted();
    }
}

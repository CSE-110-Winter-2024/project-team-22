package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.fragment.app.*;
import edu.ucsd.cse110.successorator.MainActivity;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
// import java.util.Date; NOTE: Use java.time API instead
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.FilterGoals;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class MainViewModel extends ViewModel {
    private final GoalRepository goalRepositoryDB;
    private final MutableSubject<Goal> goal;
    private final MutableSubject<Boolean> isCompleted;
    private final MutableSubject<Boolean> isEmpty;

    // allGoals, today, tomorrow, pending, recurring
    // Changes on database update only
    MutableSubject<List<Goal>> allGoals;
    MutableSubject<List<Goal>> todayGoals;
    MutableSubject<List<Goal>> tomorrowGoals;
    MutableSubject<List<Goal>> pendingGoals;
    MutableSubject<List<Goal>> recurringGoals;


    // allGoals, today, tomorrow, pending, recurring
    // Changes on state update and database update
    MutableSubject<List<Goal>> showingGoals;
    MutableSubject<List<Goal>> showingTodayGoals;
    MutableSubject<List<Goal>> showingTomorrowGoals;
    MutableSubject<List<Goal>> showingPendingGoals;
    MutableSubject<List<Goal>> showingRecurringGoals;

    private final ArrayList<String> dropdown = new ArrayList<>(Arrays.asList("Today", "Tomorrow", "Pending", "Recurring"));
    private Date currentDate;

    // focus can be: Home, Work, School, Errands, All
    private MutableSubject<String> focus;
    // label can be: Today, Tomorrow, Pending, Recurring
    private MutableSubject<String> label;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepositoryDB());
                    });

    public MainViewModel(GoalRepository goalRepositoryDB) {
        this.goalRepositoryDB = goalRepositoryDB;

        // Create the observable subjects.
        this.goal = new SimpleSubject<>();
        this.currentDate = new Date();

        this.isCompleted = new SimpleSubject<>();
        this.isEmpty = new SimpleSubject<>();
        isEmpty.setValue(true);

        // state
        this.label = new SimpleSubject<>();
        this.focus = new SimpleSubject<>();
        this.label.setValue("Today");
        this.focus.setValue("All");

        // Semi-Asynchronous, changes depending on focus, label state and database update
        this.showingGoals = new SimpleSubject<>();
        this.showingTodayGoals= new SimpleSubject<>();
        this.showingTomorrowGoals= new SimpleSubject<>();
        this.showingPendingGoals= new SimpleSubject<>();
        this.showingRecurringGoals= new SimpleSubject<>();

        this.showingGoals.setValue(new ArrayList<>());
        this.showingTodayGoals.setValue(new ArrayList<>());
        this.showingTomorrowGoals.setValue(new ArrayList<>());
        this.showingPendingGoals.setValue(new ArrayList<>());
        this.showingRecurringGoals.setValue(new ArrayList<>());

        // Asynchronous, changes only on database update
        this.allGoals = new SimpleSubject<>();
        this.todayGoals = new SimpleSubject<>();
        this.tomorrowGoals = new SimpleSubject<>();
        this.pendingGoals = new SimpleSubject<>();
        this.recurringGoals = new SimpleSubject<>();

        setupDatabaseObservers();
        setupAllGoalsObserver();
        setupFocusStateObserver();
        setupLabelStateObserver();

    }

    public void setupDatabaseObservers() {
        // When database updates, reflect changes on UI
        // e.g. if we add a new goal in TomorrowFragment,
        // and this goal has a date for tomorrow,
        // we want this goal to show up there right away.
        todayGoals.observe(goalList -> {
            if (goalList == null) return;
            goalList.sort(Comparator.comparing(Goal::getContext));
            showingTodayGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
        tomorrowGoals.observe(goalList -> {
            if (goalList == null) return;
            goalList.sort(Comparator.comparing(Goal::getContext));
            showingTomorrowGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
        pendingGoals.observe(goalList -> {
            if (goalList == null) return;
            goalList.sort(Comparator.comparing(Goal::getContext));
            showingPendingGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
        recurringGoals.observe(goalList -> {
            if (goalList == null) return;
            goalList.sort(Comparator.comparing(Goal::getDate));
            showingRecurringGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
    }

   public void setupAllGoalsObserver() {
       // get all goals from database, update on database change only
       // NOTE: showingGoals == todayGoals
       goalRepositoryDB.findAll().observe(goalList -> {
           if (goalList == null) return; // not ready yet, ignore
           isEmpty.setValue(goalList.isEmpty());

           goalList.sort(Comparator.comparing(Goal::getContext));
           allGoals.setValue(goalList);
           showingGoals.setValue(FilterGoals.labelFilter(allGoals.getValue(), label.getValue()));

           // filter for todays goals, also filter for label and focus SOON
           todayGoals.setValue(FilterGoals.recurringFilter(goalList, SuccessDate.getCurrentDateAsString(), false));
           tomorrowGoals.setValue(FilterGoals.recurringFilter(goalList, SuccessDate.getTmwsDateAsString(), false));
           pendingGoals.setValue(FilterGoals.pendingFilter(goalList));
           recurringGoals.setValue(FilterGoals.recurringFilter(goalList, null, true));
       });
   }

    public void setupFocusStateObserver() {
        // if the focus state changes
        // NOTE: this is a good place for Depth Inversion
        // specifically we can make todayGoals, tomorrowGoals into classes and implement the filter methods in there.
        // otherwise we're going to have enclosion hell on these functions ...

        // if the label state changes
        focus.observe(focusString -> {
            List<Goal> temp1;
            List<Goal> temp2;
            List<Goal> temp3;
            List<Goal> temp4;
            List<Goal> temp5;
            if (todayGoals.getValue() != null) {
                temp1 = FilterGoals.labelFilter(todayGoals.getValue(), label.getValue());
                temp1 = FilterGoals.focusFilter(temp1, focusString);
                temp1 = FilterGoals.recurringFilter(temp1, SuccessDate.getCurrentDateAsString(), false);
                showingTodayGoals.setValue(temp1);
            }
            if (tomorrowGoals.getValue() != null) {
                temp2 = FilterGoals.labelFilter(tomorrowGoals.getValue(), label.getValue());
                temp2 = FilterGoals.focusFilter(temp2, focusString);
                temp2 = FilterGoals.recurringFilter(temp2, SuccessDate.getTmwsDateAsString(), false);
                showingTomorrowGoals.setValue(temp2);
            }
            if (pendingGoals.getValue() != null) {
                temp3 = ((FilterGoals.labelFilter(pendingGoals.getValue(), label.getValue())));
                temp3 = (FilterGoals.focusFilter(temp3, focusString));
                temp3 = FilterGoals.pendingFilter(temp3);
                showingPendingGoals.setValue(temp3);
            }
            if (recurringGoals.getValue() != null) {
                temp4 = ((FilterGoals.labelFilter(pendingGoals.getValue(), label.getValue())));
                temp4 = (FilterGoals.focusFilter(temp4, focusString));
                temp4 = FilterGoals.recurringFilter(showingRecurringGoals.getValue(), null, true);
                showingRecurringGoals.setValue(temp4);
            }
            if (allGoals.getValue() != null) {
                temp5 = ((FilterGoals.labelFilter(allGoals.getValue(), label.getValue())));
                temp5 = (FilterGoals.focusFilter(temp5, focusString));
                temp5 = FilterGoals.recurringFilter(temp5, SuccessDate.getCurrentDateAsString(), false);
                showingGoals.setValue(temp5);
            }
        });
    }

    public void setupLabelStateObserver() {
        // if the label state changes
        label.observe(labelString -> {
            List<Goal> temp1;
            List<Goal> temp2;
            List<Goal> temp3;
            List<Goal> temp4;
            List<Goal> temp5;
            if (todayGoals.getValue() != null) {
                temp1 = FilterGoals.labelFilter(todayGoals.getValue(), labelString);
                temp1 = FilterGoals.focusFilter(temp1, focus.getValue());
                temp1 = FilterGoals.recurringFilter(temp1, SuccessDate.getCurrentDateAsString(), false);
                showingTodayGoals.setValue(temp1);
            }
            if (tomorrowGoals.getValue() != null) {
                temp2 = FilterGoals.labelFilter(tomorrowGoals.getValue(), labelString);
                temp2 = FilterGoals.focusFilter(temp2, focus.getValue());
                temp2 = FilterGoals.recurringFilter(temp2, SuccessDate.getTmwsDateAsString(), false);
                showingTomorrowGoals.setValue(temp2);
            }
            if (pendingGoals.getValue() != null) {
                temp3 = ((FilterGoals.labelFilter(pendingGoals.getValue(), labelString)));
                temp3 = (FilterGoals.focusFilter(temp3, focus.getValue()));
                temp3 = FilterGoals.pendingFilter(temp3);
                showingPendingGoals.setValue(temp3);
            }
            if (recurringGoals.getValue() != null) {
                temp4 = ((FilterGoals.labelFilter(pendingGoals.getValue(), labelString)));
                temp4 = (FilterGoals.focusFilter(temp4, focus.getValue()));
                temp4 = FilterGoals.recurringFilter(showingRecurringGoals.getValue(), null, true);
                showingRecurringGoals.setValue(temp4);
            }
            if (allGoals.getValue() != null) {
                temp5 = ((FilterGoals.labelFilter(allGoals.getValue(), labelString)));
                temp5 = (FilterGoals.focusFilter(temp5, focus.getValue()));
                temp5 = FilterGoals.recurringFilter(temp5, SuccessDate.getCurrentDateAsString(), false);
                showingGoals.setValue(temp5);
            }
        });
    }

   public Subject<String> getFocus() {
        return focus;
   }

    public Date getDate(){return currentDate;}

    public Subject<String> getLabel(){
        return label;
    }

    public Subject<List<Goal>> getGoals() {
        return showingGoals;
    }

    public Subject<List<Goal>> getGoalsForToday() {
        return showingTodayGoals;
    }

    public Subject<List<Goal>>  getGoalsForTomorrow() {
        return showingTomorrowGoals;
    }

    public Subject<List<Goal>> getGoalsForPending() {
        return showingPendingGoals;
    }

    public Subject<List<Goal>> getGoalsForRecurring() {
        return showingRecurringGoals;
    }

    public void toToday(){label.setValue("Today");}
    public void toTomorrow(){
        label.setValue("Tomorrow");
    }
    public void toPending(){
        label.setValue("Pending");
    }
    public void toRecurring(){
        label.setValue("Recurring");
    }
    public void focusHome() {
        focus.setValue("Home");
    }

    public void focusWork() {
        focus.setValue("Work");
    }

    public void focusSchool() {
        focus.setValue("School");
    }

    public void focusErrands() {
        focus.setValue("Errands");
    }

    /*
     Util
     */

    public void save(Goal goal) { goalRepositoryDB.save(goal); }

    // Mainly gets called when a new goal is added.
    public void append(Goal goal) {
        goalRepositoryDB.append(goal);
        updateIsEmpty();
    }

    // Mainly gets called from CardListFragment when goal is tapped.
    public void prepend(Goal goal) {
        goalRepositoryDB.prepend(goal);
        updateIsEmpty();
    }

    public void remove (int id){
        goalRepositoryDB.remove(id);
    }

    public void removeAllCompleted() {
    }

    public MutableSubject<Boolean> getGoalsSize() {
        //List<Goal> goalsList = goals.getValue();
        return isEmpty;
    }

    private void updateIsEmpty() {
        List<Goal> currentGoals = showingGoals.getValue();
        if (currentGoals != null) {
            isEmpty.setValue(currentGoals.isEmpty());
        }
    }

    public void setCurrentDate(Date date) {
        this.currentDate = date;
    }

    public static LocalDate getNextMonthSameDayOfWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // Calculate the ordinal (nth occurrence) of today's dayOfWeek in the current month
        int ordinal = (today.getDayOfMonth() - 1) / 7 + 1;

        // Get the first day of the next month
        LocalDate firstDayOfNextMonth = today.plusMonths(1).withDayOfMonth(1);

        // Find the nth occurrence of today's dayOfWeek in the next month
        LocalDate nextMonthSameDayOfWeek = firstDayOfNextMonth.with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek));

        return nextMonthSameDayOfWeek;
    }

    // somewhat similar to method in FilterGoals
    // filters goals for todays date
    private void updateGoalsForToday() {
        //LocalDate displayLocalDate = currentDate.toInstant()
        //        .atZone(ZoneId.systemDefault())
        //        .toLocalDate();
        //List<Goal> currentGoals = goals.getValue();

        //if (currentGoals != null) {
        //    List<Goal> filteredGoalsForToday = new ArrayList<>();

        //    filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

        //    filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Weekly", displayLocalDate));

        //    filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Monthly", displayLocalDate));

        //    filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Yearly", displayLocalDate));

        //    filteredGoalsForToday.addAll(currentGoals.stream()
        //            .filter(goal -> "One Time".equals(goal.getFrequency()) &&
        //                    goal.getDate().isEqual(displayLocalDate))
        //            .collect(Collectors.toList()));
        //}

        // filteredGoalsForToday.sort(Comparator.comparing(Goal::getContext));
        // todayGoals.setValue(filteredGoalsForToday);
    }

    // filters goals by Tomorrows date
    private void updateGoalsForTomorrow() {
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(currentDate);
        //calendar.add(Calendar.DAY_OF_MONTH,1);
        //LocalDate displayLocalDate = calendar.getTime().toInstant()
        //        .atZone(ZoneId.systemDefault())
        //        .toLocalDate();

        //LocalDate tmwDate = SuccessDate.getCurrentDate().plusDays(1);

        //List<Goal> currentGoals = goals.getValue();
        //if (currentGoals != null) {
        //    List<Goal> filteredGoalsForTomorrow = new ArrayList<>();

        //    filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

        //    filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Weekly", displayLocalDate));

        //    filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Monthly", displayLocalDate));

        //    filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Yearly", displayLocalDate));


        //    filteredGoalsForTomorrow.sort(Comparator.comparing(Goal::getContext));
        //    tomorrowGoals.setValue(filteredGoalsForTomorrow);

        //}
    }

    // filters goals that are recurring
    private void updateGoalsForRecurring() {
        //List<Goal> currentGoals = goals.getValue();
        //if (currentGoals != null) {
        //    List<Goal> filteredGoalsForRecurring = new ArrayList<>();

        //    filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

        //    filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Weekly"));

        //    filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Monthly"));

        //    filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Yearly"));

        //    filteredGoalsForRecurring.sort(Comparator.comparing(Goal::getContext));
        //    recurringGoals.setValue(filteredGoalsForRecurring);

        //}
    }

    private List<Goal> filterGoalsByFrequency(List<Goal> goals, String frequency) {
        return goals.stream()
                .filter(goal -> goal.getFrequency().equals(frequency))
                .collect(Collectors.toList());
    }

    private List<Goal> filterGoalsByFrequency(List<Goal> goals, String frequency, LocalDate referenceDate) {
        //switch (frequency) {
        //    case "Weekly":
        //        DayOfWeek referenceDayOfWeek = referenceDate.getDayOfWeek();
        //        return goals.stream()
        //                .filter(goal -> goal.getFrequency().equals(frequency) && goal.getDate().getDayOfWeek() == referenceDayOfWeek)
        //                .collect(Collectors.toList());
        //    case "Monthly":
        //        int referenceDayOfMonth = referenceDate.getDayOfMonth();
        //        Month referenceMonth = referenceDate.getMonth();
        //        int referenceWeekInMonth = (referenceDate.getDayOfMonth() + 6) / 7;
        //        DayOfWeek referenceWeekDay = referenceDate.getDayOfWeek();

        //        return goals.stream()
        //                .filter(goal -> {
        //                    if (!goal.getFrequency().equals(frequency)) {
        //                        return false;
        //                    }

        //                    LocalDate goalDate = goal.getDate();
        //                    Month goalMonth = goalDate.getMonth();
        //                    int goalWeekInMonth = (goalDate.getDayOfMonth() + 6) / 7;
        //                    DayOfWeek goalWeekDay = goalDate.getDayOfWeek();
        //                    LocalDate firstDayOfNextMonth = referenceDate.plusMonths(1).withDayOfMonth(1);
        //                    LocalDate firstSameDayOfWeekNextMonth = firstDayOfNextMonth.with(TemporalAdjusters.firstInMonth(goalWeekDay));
        //                    LocalDate lastSameDayOfWeekLasttMonth = referenceDate.minusMonths(1).with(TemporalAdjusters.lastInMonth(referenceWeekDay));
        //                    int lastWeekInMonth = (lastSameDayOfWeekLasttMonth.getDayOfMonth()+6)/7;
        //                    if (goalWeekInMonth==5 && !referenceDate.equals(goalDate)&&referenceWeekInMonth!=5 &&lastWeekInMonth<5 ){
        //                        return goalWeekDay == referenceWeekDay && referenceWeekInMonth==1 && referenceMonth.getValue()>goalMonth.plus(1).getValue();
        //                    }

        //                    return goalWeekInMonth == referenceWeekInMonth && goalWeekDay == referenceWeekDay;
        //                })
        //                .collect(Collectors.toList());

        //    case "Yearly":
        //        referenceDayOfMonth = referenceDate.getDayOfMonth();
        //        return goals.stream()
        //                .filter(goal -> goal.getFrequency().equals(frequency) &&
        //                        goal.getDate().getMonth() == referenceDate.getMonth() &&
        //                        goal.getDate().getDayOfMonth() == referenceDayOfMonth)
        //                .collect(Collectors.toList());
        //    default:
        //        return filterGoalsByFrequency(goals, frequency);
        //}
        return new ArrayList<>();
    }

    public void resetRecursiveGoalstoIncomplete () {
        //    List<Goal> currentGoals = goals.getValue();
        //    if (currentGoals != null) {
        //        List<Goal> updatedGoals = new ArrayList<>();

        //        for (Goal goal : currentGoals) {
        //            if (!goal.getFrequency().equals("One Time") && goal.isCompleted()) {
        //                updatedGoals.add(goal.withCompleted(false));
        //            } else {
        //                updatedGoals.add(goal);
        //            }
        //        }
        //        goalRepositoryDB.save(updatedGoals);

        //        goals.setValue(updatedGoals);

        //        updateGoalsForToday();
        //        updateGoalsForTomorrow();
        //        updateGoalsForRecurring();
        //    }
    }

}

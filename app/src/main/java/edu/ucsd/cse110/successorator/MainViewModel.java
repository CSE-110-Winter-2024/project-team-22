package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
// import java.util.Date; NOTE: Use java.time API instead
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.FilterGoals;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class MainViewModel extends ViewModel {
    private final GoalRepository goalRepositoryDB;
    private final MutableSubject<List<Goal>> goals;
    private final MutableSubject<Goal> goal;
    private final MutableSubject<Boolean> isCompleted;
    private final MutableSubject<Boolean> isEmpty;


    // One-time, daily, weekly, monthly, yearly
    private MutableSubject<List<Goal>> oneTimeGoals;
    private MutableSubject<List<Goal>> dailyGoals;
    private MutableSubject<List<Goal>> weeklyGoals;
    private MutableSubject<List<Goal>> monthlyGoals;
    private MutableSubject<List<Goal>> yearlyGoals;

    // home, work, school or errands
    private MutableSubject<List<Goal>> homeGoals;
    private MutableSubject<List<Goal>> workGoals;
    private MutableSubject<List<Goal>> schoolGoals;
    private MutableSubject<List<Goal>> errandGoals;

    // today, tomorrow, pending, recurring
    MutableSubject<List<Goal>> todayGoals = new SimpleSubject<>();
    MutableSubject<List<Goal>> tomorrowGoals = new SimpleSubject<>();
    MutableSubject<List<Goal>> pendingGoals = new SimpleSubject<>();
    MutableSubject<List<Goal>> recurringGoals = new SimpleSubject<>();

    private final ArrayList<String> frequencies = new ArrayList<>(Arrays.asList("One Time", "Daily", "Weekly", "Monthly", "Yearly"));
    private final ArrayList<String> contexts = new ArrayList<>(Arrays.asList("Home", "Work", "School", "Errands"));
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
        this.goals = new SimpleSubject<>();
        this.goal = new SimpleSubject<>();
        this.currentDate = new Date();

        this.isCompleted = new SimpleSubject<>();
        this.isEmpty = new SimpleSubject<>();
        this.label = new SimpleSubject<>();
        this.focus = new SimpleSubject<>();
        isEmpty.setValue(true);

        // When we open the app, we start with this
        this.label.setValue("Today");
        this.focus.setValue("All");

        this.oneTimeGoals = new SimpleSubject<>();
        this.dailyGoals =  new SimpleSubject<>();
        this.weeklyGoals = new SimpleSubject<>();
        this.monthlyGoals = new SimpleSubject<>();
        this.yearlyGoals = new SimpleSubject<>();

        this.homeGoals = new SimpleSubject<>();
        this.workGoals = new SimpleSubject<>();
        this.schoolGoals = new SimpleSubject<>();
        this.errandGoals = new SimpleSubject<>();

        this.todayGoals = new SimpleSubject<>();

       goalRepositoryDB.findAll().observe(goalList -> {
            if (goalList == null) return; // not ready yet, ignore
            isEmpty.setValue(goalList.isEmpty());

            // filter here using label and focus
            goals.setValue(goalList);
           updateGoalsForToday();
           updateGoalsForTomorrow();
           updateGoalsForRecurring();
       });

        // ("One Time", "Daily", "Weekly", "Monthly", "Yearly"));
        frequencies.forEach(freq -> {
            goalRepositoryDB.findAllFrequencyGoals(freq).observe(goalList -> {
                if (freq.equals("One Time")) {oneTimeGoals.setValue(goalList);}
                else if (freq.equals("Daily")) {dailyGoals.setValue(goalList);}
                else if (freq.equals("Weekly")) {weeklyGoals.setValue(goalList);}
                else if (freq.equals("Monthly")) {monthlyGoals.setValue(goalList);}
                else if (freq.equals("Yearly")) {yearlyGoals.setValue(goalList);}
            });
        });

        // ("Home", "Work", "School", "Errands"));
        contexts.forEach(context -> {
            goalRepositoryDB.findAllContextsGoals(context).observe(goalList -> {
                if (context.equals("Home")) {homeGoals.setValue(goalList);}
                else if (context.equals("Work")) {workGoals.setValue(goalList);}
                else if (context.equals("School")) {schoolGoals.setValue(goalList);}
                else if (context.equals("Errands")) {errandGoals.setValue(goalList);}
            });
        });

        // ("Today", "Tomorrow", "Pending", "Recurring"));
        dropdown.forEach(choice -> {
            goalRepositoryDB.findAllDropdownGoals(choice).observe(goalList -> {
                if (choice.equals("Today")) {todayGoals.setValue(goalList);}
                else if (choice.equals("Tomorrow")) {tomorrowGoals.setValue(goalList);}
                else if (choice.equals("Pending")) {pendingGoals.setValue(goalList);}
                else if (choice.equals("Recurring")) {recurringGoals.setValue(goalList);}
            });
        });

        goals.observe(GoalList -> {
            if (GoalList == null) {
                todayGoals.setValue(new ArrayList<>());
                tomorrowGoals.setValue(new ArrayList<>());
                pendingGoals.setValue(new ArrayList<>());
                recurringGoals.setValue(new ArrayList<>());
            }
        });
    }

    // getGoals() gets called by GoalListFragment
    public Subject<List<Goal>> getGoals() {
        return goals;
    }

    public MutableSubject<Boolean> getGoalsSize() {
        //List<Goal> goalsList = goals.getValue();
        return isEmpty;
    }

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

    private void updateIsEmpty() {
        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            isEmpty.setValue(currentGoals.isEmpty());
        }
    }

    public Subject<List<Goal>> getWeeklyGoals() {
        return weeklyGoals;
    }

    public Subject<List<Goal>> getOneTimeGoals() {
        return oneTimeGoals;
    }
    public Subject<List<Goal>> getDailyGoals() {
        return dailyGoals;
    }
    public Subject<List<Goal>> getMonthlyGoals() {
        return monthlyGoals;
    }
    public Subject<List<Goal>> getYearlyGoals() {
        return yearlyGoals;
    }

    public Subject<List<Goal>> getGoalsForToday() {
        return todayGoals;
    }

    public void setCurrentDate(Date date) {
        this.currentDate = date;
        resetRecursiveGoalstoIncomplete();
        updateGoalsForToday();
        updateGoalsForTomorrow();
        updateGoalsForRecurring();
    }
    /*
    public void updateDisplayedGoals() {
        LocalDate displayLocalDate = Instant.ofEpochMilli(currentDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        DayOfWeek displayDayOfWeek = displayLocalDate.getDayOfWeek();
        List<Goal> currentGoals = goals.getValue();
        List<Goal> updatedWeeklyGoals = new ArrayList<>();
        for (Goal goal : currentGoals) {
            LocalDate goalCreationDate = goal.getDate();
            DayOfWeek goalDayOfWeek = goalCreationDate.getDayOfWeek();
            if (goalDayOfWeek == displayDayOfWeek) {
                updatedWeeklyGoals.add(goal);
            }
        }
        this.todayGoals.setValue(updatedWeeklyGoals);

    }
     */
    private void updateGoalsForToday() {
        LocalDate displayLocalDate = currentDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> filteredGoalsForToday = new ArrayList<>();

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Weekly", displayLocalDate));

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Monthly", displayLocalDate));

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Yearly", displayLocalDate));

            filteredGoalsForToday.addAll(currentGoals.stream()
                    .filter(goal -> "One Time".equals(goal.getFrequency()) &&
                            goal.getDate().isEqual(displayLocalDate))
                    .collect(Collectors.toList()));

            filteredGoalsForToday.sort(Comparator.comparing(Goal::getContext));
            todayGoals.setValue(filteredGoalsForToday);

        }
    }

    private void updateGoalsForTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        LocalDate displayLocalDate = calendar.getTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();


        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> filteredGoalsForTomorrow = new ArrayList<>();

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Weekly", displayLocalDate));

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Monthly", displayLocalDate));

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Yearly", displayLocalDate));


            filteredGoalsForTomorrow.sort(Comparator.comparing(Goal::getContext));
            tomorrowGoals.setValue(filteredGoalsForTomorrow);

        }
    }

    private void updateGoalsForRecurring() {


        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> filteredGoalsForRecurring = new ArrayList<>();

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Weekly"));

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Monthly"));

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Yearly"));


            filteredGoalsForRecurring.sort(Comparator.comparing(Goal::getContext));
            recurringGoals.setValue(filteredGoalsForRecurring);

        }
    }
    private List<Goal> filterGoalsByFrequency(List<Goal> goals, String frequency) {
        return goals.stream()
                .filter(goal -> goal.getFrequency().equals(frequency))
                .collect(Collectors.toList());
    }

    private List<Goal> filterGoalsByFrequency(List<Goal> goals, String frequency, LocalDate referenceDate) {
        switch (frequency) {
            case "Weekly":
                DayOfWeek referenceDayOfWeek = referenceDate.getDayOfWeek();
                return goals.stream()
                        .filter(goal -> goal.getFrequency().equals(frequency) && goal.getDate().getDayOfWeek() == referenceDayOfWeek)
                        .collect(Collectors.toList());
            case "Monthly":
                int referenceDayOfMonth = referenceDate.getDayOfMonth();
                Month referenceMonth = referenceDate.getMonth();
                int referenceWeekInMonth = (referenceDate.getDayOfMonth() + 6) / 7;
                DayOfWeek referenceWeekDay = referenceDate.getDayOfWeek();

                return goals.stream()
                        .filter(goal -> {
                            if (!goal.getFrequency().equals(frequency)) {
                                return false;
                            }

                            LocalDate goalDate = goal.getDate();
                            Month goalMonth = goalDate.getMonth();
                            int goalWeekInMonth = (goalDate.getDayOfMonth() + 6) / 7;
                            DayOfWeek goalWeekDay = goalDate.getDayOfWeek();
                            LocalDate firstDayOfNextMonth = referenceDate.plusMonths(1).withDayOfMonth(1);
                            LocalDate firstSameDayOfWeekNextMonth = firstDayOfNextMonth.with(TemporalAdjusters.firstInMonth(goalWeekDay));
                            LocalDate lastSameDayOfWeekLasttMonth = referenceDate.minusMonths(1).with(TemporalAdjusters.lastInMonth(referenceWeekDay));
                            int lastWeekInMonth = (lastSameDayOfWeekLasttMonth.getDayOfMonth()+6)/7;
                            if (goalWeekInMonth==5 && !referenceDate.equals(goalDate)&&referenceWeekInMonth!=5 &&lastWeekInMonth<5 ){
                                return goalWeekDay == referenceWeekDay && referenceWeekInMonth==1 && referenceMonth.getValue()>goalMonth.plus(1).getValue();
                            }

                            return goalWeekInMonth == referenceWeekInMonth && goalWeekDay == referenceWeekDay;
                        })
                        .collect(Collectors.toList());

            case "Yearly":
                referenceDayOfMonth = referenceDate.getDayOfMonth();
                return goals.stream()
                        .filter(goal -> goal.getFrequency().equals(frequency) &&
                                goal.getDate().getMonth() == referenceDate.getMonth() &&
                                goal.getDate().getDayOfMonth() == referenceDayOfMonth)
                        .collect(Collectors.toList());
            default:
                return filterGoalsByFrequency(goals, frequency);
        }
    }

    public void resetRecursiveGoalstoIncomplete () {
        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> updatedGoals = new ArrayList<>();

            for (Goal goal : currentGoals) {
                if (!goal.getFrequency().equals("One Time") && goal.isCompleted()) {
                    updatedGoals.add(goal.withCompleted(false));
                } else {
                    updatedGoals.add(goal);
                }
            }
            goalRepositoryDB.save(updatedGoals);

            goals.setValue(updatedGoals);

            updateGoalsForToday();
            updateGoalsForTomorrow();
            updateGoalsForRecurring();
        }
    }

    public void toToday(){
        label.setValue("Today");
    }

    public Date getDate(){
        return currentDate;
    }

    public void toTomorrow(){
        label.setValue("Tomorrow");
    }
    public void toPending(){
        label.setValue("Pending");
    }
    public void toRecurring(){
        label.setValue("Recurring");
    }

    public Subject<String> getLabel(){
        return label;
    }

    public Subject<List<Goal>>  getGoalsForTomorrow() {
        return tomorrowGoals;
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

    // Methods below get called from FocusModeFragment
    // e.g.
    // User clicks hamburger menu ->
    // User clicks on a context (e.g. Home) ->
    // focusHome() method gets called
    //
    // NOTE: e.g. the goals marked with "Home" were already pre-loaded
    // when the app started, we simply have to filter them
    // depending on the current label.
    public void focusHome() {
        MutableSubject<List<Goal>> goalsFiltered = (MutableSubject<List<Goal>>) FilterGoals.filterGoalsFocusToModel(homeGoals, label.getValue());
        goals.setValue(goalsFiltered.getValue());
        focus.setValue("Home");
    }

    public Subject<List<Goal>> getGoalsForPending() {
        return pendingGoals;
    }

    public void focusWork() {
        MutableSubject<List<Goal>> goalsFiltered = (MutableSubject<List<Goal>>) FilterGoals.filterGoalsFocusToModel(workGoals, label.getValue());
        goals.setValue(goalsFiltered.getValue());
        focus.setValue("Work");
    }

    public void focusSchool() {
        MutableSubject<List<Goal>> goalsFiltered = (MutableSubject<List<Goal>>) FilterGoals.filterGoalsFocusToModel(schoolGoals, label.getValue());
        goals.setValue(goalsFiltered.getValue());
        focus.setValue("School");
    }

    public void focusErrands() {
        MutableSubject<List<Goal>> goalsFiltered = (MutableSubject<List<Goal>>) FilterGoals.filterGoalsFocusToModel(errandGoals, label.getValue());
        goals.setValue(goalsFiltered.getValue());
        focus.setValue("Errands");
    }

    public Subject<List<Goal>> getGoalsForRecurring() {
        return recurringGoals;
    }
}

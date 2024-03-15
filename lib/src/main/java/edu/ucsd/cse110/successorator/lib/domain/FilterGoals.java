package edu.ucsd.cse110.successorator.lib.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class FilterGoals {

    // Filter goals by Frequency, Date and Context
    // just pass in your goals and specify Frequency, Date or Context
    // e.g. filter strictly by frequency:
    // frequency = "Yearly", date = null, context = null
    // will return back the Goals in the list provided that have frequencies
    // equal to "Yearly"
    public static Subject<List<Goal>> filterGoalsByFDC(List<Goal> unfilteredGoals, String frequency, String dateString, String context) {
        boolean filtered = false;
        List<Goal> filteredGoals = unfilteredGoals;
        List<Goal> parseGoals = unfilteredGoals;
        // first filter by frequency
        if (frequency != null && !filtered) {
            filtered = true;
            filteredGoals = parseGoals.stream()
                    .filter(goal -> frequency.equals(goal.getFrequency()))
                    .collect(Collectors.toList());
        }

        if (dateString != null && !filtered) {
            filtered = true;
            filteredGoals = parseGoals.stream()
                    .filter(goal -> dateString.equals(goal.getDate()))
                    .collect(Collectors.toList());
        }

        if (context != null && context != "All" && !filtered) {
            filtered = true;
            filteredGoals = parseGoals.stream()
                    .filter(goal -> context.equals(goal.getContext()))
                    .collect(Collectors.toList());
        }

        MutableSubject<List<Goal>> fgSubject = new SimpleSubject<List<Goal>>();

        if (filtered) {
            fgSubject.setValue(filteredGoals);
        } else {
            fgSubject.setValue(unfilteredGoals);
        }

        return fgSubject;
    }

    public static Subject<List<Goal>> filterGoalsByLabel(List<Goal> oldGoals, String label) {
        MutableSubject<List<Goal>> filteredFocusGoals = new SimpleSubject<>();
        if (label.equals("Today") && oldGoals != null) {
            //filteredFocusGoals = (MutableSubject<List<Goal>>) filterGoalsByFDC(oldGoals, null, SuccessDate.dateToString(SuccessDate.getCurrentDate()), null);
            // do more things here
            filteredFocusGoals.setValue(recurringFilter(oldGoals, SuccessDate.getCurrentDateAsString(), false));
        } else if (label.equals("Tomorrow") && oldGoals != null) {
            // filteredFocusGoals = (MutableSubject<List<Goal>>) filterGoalsByFDC(oldGoals, null, SuccessDate.dateToString(SuccessDate.getCurrentDate().plusDays(1)), null);
            filteredFocusGoals.setValue(recurringFilter(oldGoals, SuccessDate.getTmwsDateAsString(), false));
        } else if (label.equals("Pending") && oldGoals != null) {
            filteredFocusGoals = (MutableSubject<List<Goal>>) filterGoalsByFDC(oldGoals, null, "Pending", null);
        } else if (label.equals("Recurring") && oldGoals != null) {
            MutableSubject<List<Goal>> filteredDaily = (MutableSubject<List<Goal>>) filterGoalsByFDC(oldGoals, "Daily", null, null);
            MutableSubject<List<Goal>> filteredWeekly = (MutableSubject<List<Goal>>) filterGoalsByFDC(oldGoals, "Weekly", null, null);
            MutableSubject<List<Goal>> filteredMonthly = (MutableSubject<List<Goal>>) filterGoalsByFDC(oldGoals, "Monthly", null, null);
            MutableSubject<List<Goal>> filteredYearly = (MutableSubject<List<Goal>>) filterGoalsByFDC(oldGoals, "Yearly", null, null);

            List<Goal> fGoals = new ArrayList<>();
            List<Goal> fDaily = (ArrayList<Goal>) filteredDaily.getValue();
            List<Goal> fWeekly = (ArrayList<Goal>) filteredWeekly.getValue();
            List<Goal> fMonthly = (ArrayList<Goal>) filteredMonthly.getValue();
            List<Goal> fYearly = (ArrayList<Goal>) filteredYearly.getValue();

            fGoals.addAll(fDaily);
            fGoals.addAll(fWeekly);
            fGoals.addAll(fMonthly);
            fGoals.addAll(fYearly);

            fGoals = filterByDate(fGoals);
            filteredFocusGoals.setValue(fGoals);
        } else {
            filteredFocusGoals.setValue(oldGoals);
        }

        return filteredFocusGoals;
    }

    // we specify a target date, if the goals land on this target date we add it
    // to the list of weekly goals that land on this date
    public static Subject<List<Goal>> filterWeeklyGoals(List<Goal> oldGoals, String target) {
        MutableSubject<List<Goal>> newWeeklyGoals = new SimpleSubject<>();
        LocalDate targetAsDate = SuccessDate.stringToDate(target);

        List<Goal> newGoals = new ArrayList<>();
        oldGoals.forEach(goal -> {
            if (goal.getDate().equals("Pending")) return;
            LocalDate weeksMinus = SuccessDate.stringToDate(goal.getDate());
            LocalDate weeksPlus = SuccessDate.stringToDate(goal.getDate());
            long weeksBetween = ChronoUnit.WEEKS.between(SuccessDate.stringToDate(goal.getDate()), targetAsDate);
            weeksPlus = weeksPlus.plusWeeks(weeksBetween);
            weeksMinus = weeksMinus.minusWeeks(weeksBetween);
            if (weeksPlus.isEqual(targetAsDate) || weeksMinus.isEqual(targetAsDate)) {
                newGoals.add(goal);
            }
        });
        newWeeklyGoals.setValue(newGoals);
        return newWeeklyGoals;
    }

    public static Subject<List<Goal>> filterMonthlyGoals(List<Goal> oldGoals, String target) {
        MutableSubject<List<Goal>> newMonthlyGoals = new SimpleSubject<>();
        LocalDate targetAsDate = SuccessDate.stringToDate(target);

        List<Goal> newGoals = new ArrayList<>();
        oldGoals.forEach(goal -> {
            if (goal.getDate().equals("Pending")) return;
//            LocalDate monthsMinus = SuccessDate.stringToDate(goal.getDate());
//            LocalDate monthsPlus = SuccessDate.stringToDate(goal.getDate());
//            long monthsBetween = ChronoUnit.MONTHS.between(SuccessDate.stringToDate(goal.getDate()), targetAsDate);
//            monthsPlus = monthsPlus.plusMonths(monthsBetween);
//            monthsMinus = monthsMinus.minusMonths(monthsBetween);
//            if (monthsPlus.isEqual(targetAsDate) || monthsMinus.isEqual(targetAsDate)) {
//                newGoals.add(goal);
//            }
            int targetDayOfMonth = targetAsDate.getDayOfMonth();
            Month targetMonth = targetAsDate.getMonth();
            int targetWeekInMonth = (targetAsDate.getDayOfMonth() + 6) / 7;
            DayOfWeek targeteWeekDay = targetAsDate.getDayOfWeek();
            LocalDate goalDate = SuccessDate.stringToDate(goal.getDate());
            Month goalMonth = goalDate.getMonth();
            int goalWeekInMonth = (goalDate.getDayOfMonth() + 6) / 7;
            DayOfWeek goalWeekDay = goalDate.getDayOfWeek();
            LocalDate firstDayOfNextMonth = targetAsDate.plusMonths(1).withDayOfMonth(1);
            LocalDate firstSameDayOfWeekNextMonth = firstDayOfNextMonth.with(TemporalAdjusters.firstInMonth(goalWeekDay));
            LocalDate lastSameDayOfWeekLasttMonth = targetAsDate.minusMonths(1).with(TemporalAdjusters.lastInMonth(targeteWeekDay));
            int lastWeekInMonth = (lastSameDayOfWeekLasttMonth.getDayOfMonth()+6)/7;
            if (goalWeekInMonth==5 && !targetAsDate.equals(goalDate)&&targetWeekInMonth!=5 &&lastWeekInMonth<5 ){
                if (goalWeekDay == targeteWeekDay && targetWeekInMonth==1 && targetMonth.getValue()>goalMonth.plus(1).getValue()){
                    newGoals.add(goal);
                }
            }
            if (goalWeekInMonth == targetWeekInMonth && goalWeekDay == targeteWeekDay){
                newGoals.add(goal);
            }

        });
        newMonthlyGoals.setValue(newGoals);
        return newMonthlyGoals;
    }

    public static Subject<List<Goal>> filterYearlyGoals(List<Goal> oldGoals, String target) {
        MutableSubject<List<Goal>> newYearlyGoals = new SimpleSubject<>();
        LocalDate targetAsDate = SuccessDate.stringToDate(target);

        List<Goal> newGoals = new ArrayList<>();
        oldGoals.forEach(goal -> {
            if (goal.getDate().equals("Pending")) return;
            LocalDate yearsMinus = SuccessDate.stringToDate(goal.getDate());
            LocalDate yearsPlus = SuccessDate.stringToDate(goal.getDate());
            long yearsBetween = ChronoUnit.YEARS.between(SuccessDate.stringToDate(goal.getDate()), targetAsDate);
            yearsPlus = yearsPlus.plusYears(yearsBetween);
            yearsMinus = yearsMinus.minusYears(yearsBetween);
            if (yearsPlus.isEqual(targetAsDate) || yearsMinus.isEqual(targetAsDate)) {
                newGoals.add(goal);
            }
        });
        newYearlyGoals.setValue(newGoals);
        return newYearlyGoals;
    }

    public static List<Goal> labelFilter(List<Goal> allGoals, String value) {
        MutableSubject<List<Goal>> goalsFiltered = new SimpleSubject<>();
        if (value.equals("Today") || value.equals("Tomorrow") || value.equals("Pending") || value.equals("Recurring")) {
            goalsFiltered = (MutableSubject<List<Goal>>) FilterGoals.filterGoalsByLabel(allGoals, value);
        } else {
            // if label is a date then do this
            goalsFiltered.setValue(allGoals);
        }

        // sort by context
        // then sort by uncompleted vs completed
        if (goalsFiltered.getValue() != null) {
            goalsFiltered.setValue(filterByCompletedAndContext(goalsFiltered.getValue()));
        }

        return goalsFiltered.getValue();
    }

    public static List<Goal> focusFilter(List<Goal> goalsList, String value) {
        MutableSubject<List<Goal>> goalsFiltered = null;
        goalsFiltered = (MutableSubject<List<Goal>>) FilterGoals.filterGoalsByFDC(goalsList, null, null, value);

        // sort by context
        // then sort by uncompleted vs completed
        if (goalsFiltered.getValue() != null) {
            goalsFiltered.setValue(filterByCompletedAndContext(goalsFiltered.getValue()));
        }

        return goalsFiltered.getValue();
    }

    public static List<Goal> pendingFilter(List<Goal> goalList) {
        List<Goal> pendingGoals = FilterGoals.filterGoalsByFDC(goalList, null, "Pending", null).getValue();
        return pendingGoals;
    }

    public static List<Goal> recurringFilter(List<Goal> goalList, String theDate, boolean recurringOnly) {
        List<Goal> gatheredGoals = new ArrayList<>();

        List<Goal> tgls2 = FilterGoals.filterGoalsByFDC(goalList, "Daily", null, null).getValue(); // get daily, date doesn't matter
        List<Goal> tgls3 = FilterGoals.filterGoalsByFDC(goalList, "Weekly", null, null).getValue(); // get all weekly
        List<Goal> tgls5 = FilterGoals.filterGoalsByFDC(goalList, "Monthly", null, null).getValue(); // get all weekly
        List<Goal> tgls7 = FilterGoals.filterGoalsByFDC(goalList, "Yearly", null, null).getValue(); // get all weekly
        List<Goal> tgls1 = new ArrayList<>();
        List<Goal> tgls4 = new ArrayList<>();
        List<Goal> tgls6 = new ArrayList<>();
        List<Goal> tgls8 = new ArrayList<>();

        if (theDate != null) {
            // call filterGoalsByFDC in this format, 1/3 parameter only.
            tgls1 = FilterGoals.filterGoalsByFDC(goalList, "One Time", theDate, null).getValue(); // one-time on same date
            tgls1 = FilterGoals.filterGoalsByFDC(tgls1, null, theDate, null).getValue(); // one-time on same date

            tgls4 = FilterGoals.filterWeeklyGoals(tgls3, theDate).getValue(); // get new date, if it lands on today keep it
            tgls6 = FilterGoals.filterMonthlyGoals(tgls5, theDate).getValue(); // get new date, if it lands on today keep it
            tgls8 = FilterGoals.filterYearlyGoals(tgls7, theDate).getValue(); // get new date, if it lands on today keep it
        }

        gatheredGoals.addAll(tgls2);
        if (recurringOnly) {
            gatheredGoals.addAll(tgls3);
            gatheredGoals.addAll(tgls5);
            gatheredGoals.addAll(tgls7);
            // sort by date
            gatheredGoals =  filterByDate(gatheredGoals);
        } else {
            gatheredGoals.addAll(tgls1);
            gatheredGoals.addAll(tgls4);
            gatheredGoals.addAll(tgls6);
            gatheredGoals.addAll(tgls8);
            // sort by context
            // then sort by uncompleted vs completed
            gatheredGoals =  filterByCompletedAndContext(gatheredGoals);
        }

       List<Goal> withoutPendingGoals = new ArrayList<>();

       gatheredGoals.forEach(goal -> {
           if (!goal.getDate().equals("Pending")) {
               withoutPendingGoals.add(goal);
           }
       });

        return withoutPendingGoals;
    }

    public static List<Goal> filterByContext(List<Goal> goalList) {
        List<Goal> homeGoals = new ArrayList<>();
        List<Goal> workGoals = new ArrayList<>();
        List<Goal> schoolGoals = new ArrayList<>();
        List<Goal> errandGoals = new ArrayList<>();

        List<Goal> sortedGoals = new ArrayList<>();

        goalList.forEach(goal -> {
            if (goal.getContext().equals("Home")) { homeGoals.add(goal);}
            else if (goal.getContext().equals("Work")) {workGoals.add(goal);}
            else if (goal.getContext().equals("School")) {schoolGoals.add(goal);}
            else if (goal.getContext().equals("Errands")) {errandGoals.add(goal);}
        });

        sortedGoals.addAll(homeGoals);
        sortedGoals.addAll(workGoals);
        sortedGoals.addAll(schoolGoals);
        sortedGoals.addAll(errandGoals);

        return sortedGoals;
    }

    public static List<Goal> filterByCompletedAndContext(List<Goal> goalList) {
        List<Goal> completedGoals = new ArrayList<>();
        List<Goal> uncompletedGoals = new ArrayList<>();

        List<Goal> sortedGoals = new ArrayList<>();
        goalList.forEach(goal -> {
            if (goal.isCompleted()) { completedGoals.add(goal);}
            else {uncompletedGoals.add(goal);}
        });

        List<Goal> uncompletedGoalsContext = filterByContext(uncompletedGoals);

        sortedGoals.addAll(uncompletedGoalsContext);
        sortedGoals.addAll(completedGoals);
        return sortedGoals;
    }

    public static List<Goal> filterByDate(List<Goal> goalList) {
        goalList.sort(Comparator.comparing(Goal::getDateAsLocalDate));
        return goalList;
    }
}

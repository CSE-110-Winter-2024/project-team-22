package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.Observer;
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
        List<Goal> filteredGoals = unfilteredGoals;
        // first filter by frequency
        if (frequency != null) {
            filteredGoals = filteredGoals.stream()
                    .filter(goal -> frequency.equals(goal.getFrequency()))
                    .collect(Collectors.toList());
        }

        if (dateString != null) {
            filteredGoals = filteredGoals.stream()
                    .filter(goal -> dateString.equals(SuccessDate.dateToString(goal.getDate())))
                    .collect(Collectors.toList());
        }

        if (context != null) {
            filteredGoals = filteredGoals.stream()
                    .filter(goal -> context.equals(goal.getFrequency()))
                    .collect(Collectors.toList());
        }

        MutableSubject<List<Goal>> fgSubject = new SimpleSubject<List<Goal>>();
        fgSubject.setValue(filteredGoals);

        return fgSubject;
    }

    public static Subject<List<Goal>> filterGoalsFocusToModel(MutableSubject<List<Goal>> focusGoals, String label) {
        MutableSubject<List<Goal>> filteredFocusGoals = new SimpleSubject<>();
        if (label.equals("Today")) {
            filteredFocusGoals = (MutableSubject<List<Goal>>) filterGoalsByFDC(focusGoals.getValue(), null, SuccessDate.dateToString(SuccessDate.getCurrentDate()), null);
        } else if (label.equals("Tomorrow")) {
            filteredFocusGoals = (MutableSubject<List<Goal>>) filterGoalsByFDC(focusGoals.getValue(), null, SuccessDate.dateToString(SuccessDate.getCurrentDate().plusDays(1)), null);
        } else if (label.equals("Pending")) {
            filteredFocusGoals = (MutableSubject<List<Goal>>) filterGoalsByFDC(focusGoals.getValue(), null, "Pending", null);
        } else if (label.equals("Recurring")) {
            MutableSubject<List<Goal>> filteredDaily = (MutableSubject<List<Goal>>) filterGoalsByFDC(focusGoals.getValue(), "Daily", null, null);
            MutableSubject<List<Goal>> filteredWeekly = (MutableSubject<List<Goal>>) filterGoalsByFDC(focusGoals.getValue(), "Weekly", null, null);
            MutableSubject<List<Goal>> filteredMonthly = (MutableSubject<List<Goal>>) filterGoalsByFDC(focusGoals.getValue(), "Monthly", null, null);
            MutableSubject<List<Goal>> filteredYearly = (MutableSubject<List<Goal>>) filterGoalsByFDC(focusGoals.getValue(), "Yearly", null, null);

            ArrayList<Goal> fGoals = new ArrayList<>();
            ArrayList<Goal> fDaily = (ArrayList<Goal>) filteredDaily.getValue();
            ArrayList<Goal> fWeekly = (ArrayList<Goal>) filteredWeekly.getValue();
            ArrayList<Goal> fMonthly = (ArrayList<Goal>) filteredMonthly.getValue();
            ArrayList<Goal> fYearly = (ArrayList<Goal>) filteredYearly.getValue();

            fGoals.addAll(fDaily);
            fGoals.addAll(fWeekly);
            fGoals.addAll(fMonthly);
            fGoals.addAll(fYearly);
            filteredFocusGoals.setValue(fGoals);

            // proceed to filter from oldest to newest date
            // will implement this later
        }

        return filteredFocusGoals;
    }


    public static Subject<List<Goal>> filterGoalsOnEntry(List<Goal> unfilteredGoals, String label, String focus, String displayDate) {
        return null;
    }
}

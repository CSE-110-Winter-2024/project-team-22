package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.util.Subject;

public class FilterGoalsTest {

    List<Goal> goals = List.of(
            new Goal(0, "Do Homework", false, 0,"OneTime",SuccessDate.getCurrentDateAsString(), "School"),
            new Goal(1, "Go to Gym", false, 1,"Weekly",SuccessDate.getCurrentDateAsString(), "Home"),
            new Goal(2, "Eat Dinner", false, 2,"Monthly",SuccessDate.getCurrentDateAsString(), "Home")
        );

    @Test
    public void filterGoalsByFDC() {
        Subject<List<Goal>> expected = FilterGoals.filterGoalsByFDC(goals, "Weekly",SuccessDate.getCurrentDateAsString(), "Home" );
        assertEquals(expected.getValue().size(),1);
    }

    @Test
    public void filterGoalsByLabel() {
        Subject<List<Goal>> expected = FilterGoals.filterGoalsByLabel(goals, "Recurring");
        assertEquals(expected.getValue().size(),2);
    }

    @Test
    public void filterWeeklyGoals() {
        Subject<List<Goal>> expected = FilterGoals.filterWeeklyGoals(goals, SuccessDate.getCurrentDateAsString());
        assertEquals(expected.getValue().size(),3);
    }

    @Test
    public void filterMonthlyGoals() {
        Subject<List<Goal>> expected = FilterGoals.filterMonthlyGoals(goals, SuccessDate.getCurrentDateAsString());
        assertEquals(expected.getValue().size(),3);
    }

    @Test
    public void filterYearlyGoals() {
        Subject<List<Goal>> expected = FilterGoals.filterYearlyGoals(goals, SuccessDate.getCurrentDateAsString());
        assertEquals(expected.getValue().size(),3);
    }

    @Test
    public void labelFilter() {
        List<Goal> expected = FilterGoals.labelFilter(goals, "Recurring");
        assertEquals(expected.size(),2);
    }

    @Test
    public void focusFilter() {
        List<Goal> expected = FilterGoals.focusFilter(goals, "Home");
        assertEquals(expected.size(),2);
    }

    @Test
    public void pendingFilter() {
        List<Goal> expected = FilterGoals.pendingFilter(goals);
        assertEquals(expected.size(),0);
    }

    @Test
    public void recurringFilter() {
        List<Goal> expected = FilterGoals.recurringFilter(goals,SuccessDate.getCurrentDateAsString(),true);
        assertEquals(expected.size(),2);
    }

    @Test
    public void filterByContext() {
        List<Goal> expected = FilterGoals.filterByContext(goals);
        assertEquals(expected.size(),3);
    }

    @Test
    public void filterByCompletedAndContext() {
        List<Goal> expected = FilterGoals.filterByCompletedAndContext(goals);
        assertEquals(expected.size(),3);
    }

    @Test
    public void filterByDate() {
        List<Goal> goals = new ArrayList<Goal>();
        goals.add(new Goal(0, "Do Homework", false, 0,"OneTime",SuccessDate.getCurrentDateAsString(), "School"));
        goals.add(new Goal(1, "Go to Gym", false, 1,"Weekly",SuccessDate.getCurrentDateAsString(), "Home"));
        goals.add(new Goal(2, "Eat Dinner", false, 2,"Monthly",SuccessDate.getCurrentDateAsString(), "Home"));
        List<Goal> res = FilterGoals.filterByDate(goals);
        assertEquals(res.size(),3);
    }
}
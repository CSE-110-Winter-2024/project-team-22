package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class GoalTest {
    @Test
    public void withCompleted() {
        Goal goal = new Goal(0, "Do Homework", false, 0,"OneTime",SuccessDate.getCurrentDateAsString(), "School");
        goal = goal.withCompleted(true);
        assertTrue(goal.isCompleted());
    }

    @Test
    public void withId() {
        Goal goal = new Goal(0, "Do Homework", false, 0,"OneTime",SuccessDate.getCurrentDateAsString(), "School");
        goal = goal.withId(1);
        assertEquals((Integer)1,goal.id());
    }


    @Test
    public void withSortOrder() {
        Goal goal = new Goal(0, "Do Homework", false, 0,"OneTime",SuccessDate.getCurrentDateAsString(), "School");
        goal = goal.withSortOrder(1);
        assertEquals(1,goal.sortOrder());
    }
}
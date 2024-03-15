package edu.ucsd.cse110.successorator;

import junit.framework.TestCase;

import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

public class BDDTests extends TestCase {
    private SimpleGoalRepository mockRepository;
    private InMemoryDataSource dataSource;
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        dataSource = new InMemoryDataSource();
        //dataSource.putGoal(new Goal(0, "Go home", false, 1, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        //dataSource.putGoal(new Goal(1, "Do homework", true, 2, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        //dataSource.putGoal(new Goal(2, "Go to gym", false, 3, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        //dataSource.putGoal(new Goal(3, "Go shopping", true, 4, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        mockRepository = new SimpleGoalRepository(dataSource);
        viewModel = new MainViewModel(mockRepository);
    }

    /*
    US1: As a user, I want to set the frequency of the goal.
        So that the goal can be added as one-time, daily, weekly, monthly, or yearly.

        Given a daily goal “Cook Dinner”
        When I press “+“
        Then I Type in “Cook Dinner” and press Daily
        When I press Save
        Then the goal “Cook Dinner” shows in Today, Tomorrow, and Recurring views
     */
    public void testUS1() {
        boolean[] isTestPassed = new boolean[3];
        Goal newGoal = new Goal(null, "Cook Dinner", false, -1, "Daily", SuccessDate.getCurrentDateAsString(), "Home");
        viewModel.append(newGoal);
        // for today

        viewModel.getGoals().observe(goals -> {
                assert goals != null;
                assertEquals(1, goals.size());
                assertTrue(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
                isTestPassed[0] = true;
        });
        // for tomorrow
        viewModel.getGoalsForTomorrow().observe(goals -> {
            assert goals != null;
            assertEquals(1, goals.size());
            assertTrue(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[1] = true;
        });
        // for recurring
        viewModel.getGoalsForRecurring().observe(goals -> {
            assert goals != null;
            assertEquals(1, goals.size());
            assertTrue(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[2] = true;
        });

        assertTrue(isTestPassed[0]);
        assertTrue(isTestPassed[1]);
        assertTrue(isTestPassed[2]);
    }

    /*

    US2: As a user, I want to have separate categories for the goals
        So that I can switch to Today, Tomorrow, Pending, or Recurring views.

        Given the goal “Cook Dinner” is in Tomorrow view
        When I press v
        Then I press Tomorrow
        And the view changes from Today to Tomorrow
        And “Cook Dinner” shows in Tomorrow view
     */
    public void testUS2() {
        boolean[] isTestPassed = new boolean[4];
        Goal newGoal1 = new Goal(null, "Cook Dinner", false, -1, "One Time", SuccessDate.getTmwsDateAsString(), "Home");
        viewModel.append(newGoal1);

        // for today
        viewModel.getGoals().observe(goals -> {
            assert goals != null;
            assertEquals(0, goals.size());
            assertFalse(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[0] = true;
        });
        // for tomorrow
        viewModel.getGoalsForTomorrow().observe(goals -> {
            assert goals != null;
            assertEquals(1, goals.size());
            assertTrue(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[1] = true;
        });
        // for pending
        viewModel.getGoalsForRecurring().observe(goals -> {
            assert goals != null;
            assertEquals(0, goals.size());
            assertFalse(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[2] = true;
        });
        // for recurring
        viewModel.getGoalsForRecurring().observe(goals -> {
            assert goals != null;
            assertEquals(0, goals.size());
            assertFalse(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[3] = true;
        });

        assertTrue(isTestPassed[0]);
        assertTrue(isTestPassed[1]);
        assertTrue(isTestPassed[2]);
        assertTrue(isTestPassed[3]);
    }

    /*
    US3: As a user, I want to be able to move goals from one category/view to another
        So that I can adjust their category to feed my needs.

        Given the goal “Research plane tickets” in Pending view
        And I want to move it to Today view
        When I press and hold “Research plane tickets”
        And I press Move to Today
        Then the goal “Research plane tickets” is moved to Today view
     */
    public void testUS3() {
        boolean[] isTestPassed = new boolean[4];
        Goal newGoal1 = new Goal(null, "Research plane tickets", false, -1, "One Time", "Pending", "Home");
        viewModel.append(newGoal1);

        // today prior to moving
        viewModel.getGoals().observe(goals -> {
            if (isTestPassed[0] == false) {
                assert goals != null;
                assertEquals(0, goals.size());
                assertFalse(goals.stream().anyMatch(goal -> "Research plane tickets".equals(goal.text())));
                isTestPassed[0] = true;
            }

        });

        // pending prior to moving
        viewModel.getGoalsForPending().observe(goals -> {
            if (isTestPassed[1] == false) {
                assert goals != null;
                assertEquals(1, goals.size());
                assertTrue(goals.stream().anyMatch(goal -> "Research plane tickets".equals(goal.text())));

                isTestPassed[1] = true;
                Goal pendingGoal = goals.get(0);
                viewModel.remove(pendingGoal.id());
                Goal goalToday = new Goal(null, pendingGoal.text(), pendingGoal.isCompleted(), -1, pendingGoal.getFrequency(), SuccessDate.getCurrentDateAsString(), pendingGoal.getContext());
                viewModel.append(goalToday);
            }
        });

        // for today after moving
        viewModel.getGoals().observe(goals -> {
            if (isTestPassed[2] == false) {
                assert goals != null;
                assertEquals(1, goals.size());
                assertTrue(goals.stream().anyMatch(goal -> "Research plane tickets".equals(goal.text())));
                isTestPassed[2] = true;
            }
        });

        // for pending after moving
        viewModel.getGoalsForPending().observe(goals -> {
            if (isTestPassed[3] == false) {
                assert goals != null;
                assertEquals(0, goals.size());
                assertFalse(goals.stream().anyMatch(goal -> "Research plane tickets".equals(goal.text())));
                isTestPassed[3] = true;
            }
        });

        assertTrue(isTestPassed[0]);
        assertTrue(isTestPassed[1]);
        assertTrue(isTestPassed[2]);
        assertTrue(isTestPassed[3]);
    }

    /*

    US4: As a user, I want to add a goal with start time and recurring frequency
        So that I can set the start time for recurring of the goal and recurring frequency(daily, weekly, monthly, yearly).

        Given a goal “Takeout Trash”
        And I want to recurring this goal weekly on Sunday
        When I press +
        Then I press Weekly on …
        When I press February 20,2024 and choose February 25,2024
        And I press Save
        Then the goal “Takeout Trash, weekly on Sunday” shows in Recurring
     */

    public void testUS4() {
        boolean[] isTestPassed = new boolean[1];
        Goal newGoal1 = new Goal(null, "Takeout Trash", false, -1, "Weekly", "Feb-25-2024", "Home");
        viewModel.append(newGoal1);

        // today prior to moving
        viewModel.getGoalsForRecurring().observe(goals -> {
            if (isTestPassed[0] == false) {
                assert goals != null;
                assertEquals(1, goals.size());
                assertTrue(goals.stream().anyMatch(goal -> "Takeout Trash".equals(goal.text())));
                isTestPassed[0] = true;
            }
        });

        assertTrue(isTestPassed[0]);
    }

    /*
    US5: As a user, I want to be able to tag my goal with a work context
        So that I know what my to-do is related to (e.g., Home, Work, School, or Errands).

        Given “Cook Dinner” is a goal for “Home”
        When I press +
        Then I type in “Cook Dinner” and press H and press Daily
        When I press Save
        Then the goal “H Cook Dinner” shows in Today, Tomorrow, and Recurring views
     */
    public void testUS5() {
        boolean[] isTestPassed = new boolean[3];
        Goal newGoal = new Goal(null, "Cook Dinner", false, -1, "Daily", SuccessDate.getCurrentDateAsString(), "Home");
        viewModel.append(newGoal);
        // for today

        viewModel.getGoals().observe(goals -> {
            assert goals != null;
            assertEquals(1, goals.size());
            assertTrue(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[0] = true;
        });
        // for tomorrow
        viewModel.getGoalsForTomorrow().observe(goals -> {
            assert goals != null;
            assertEquals(1, goals.size());
            assertTrue(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[1] = true;
        });
        // for recurring
        viewModel.getGoalsForRecurring().observe(goals -> {
            assert goals != null;
            assertEquals(1, goals.size());
            assertTrue(goals.stream().anyMatch(goal -> "Cook Dinner".equals(goal.text())));
            isTestPassed[2] = true;
        });

        assertTrue(isTestPassed[0]);
        assertTrue(isTestPassed[1]);
        assertTrue(isTestPassed[2]);
    }

    /*

    US6: As a user, I want my goals to be grouped by work context
        So that my to-do’s are more organized.

        Given a list of goals in the Today view
        When I open the Today view
        Then all goals are grouped by work context
     */
    public void testUS6() {
        boolean[] isTestPassed = new boolean[1];
        // Goals with different contexts
        Goal newGoal1 = new Goal(null, "Goal1", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Errands");
        Goal newGoal2 = new Goal(null, "Goal2", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Work");
        Goal newGoal3 = new Goal(null, "Goal3", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Home");
        Goal newGoal4 = new Goal(null, "Goal4", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "School");
        viewModel.append(newGoal1);
        viewModel.append(newGoal2);
        viewModel.append(newGoal3);
        viewModel.append(newGoal4);

        Goal exGoal1 = new Goal(3, "Goal1", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Errands");
        Goal exGoal2 = new Goal(1, "Goal2", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Work");
        Goal exGoal3 = new Goal(0, "Goal3", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Home");
        Goal exGoal4 = new Goal(2, "Goal4", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "School");
        List<Goal> expected = new ArrayList<>();
        expected.add(exGoal3);
        expected.add(exGoal2);
        expected.add(exGoal4);
        expected.add(exGoal1);

        // for today
        viewModel.getGoals().observe(goals -> {
            if (isTestPassed[0] == false) {
                assert goals != null;
                assertEquals(4, goals.size());

                for(int i = 0; i < goals.size(); i++) {
                    assertEquals(expected.get(i).text(), goals.get(i).text());
                }

                isTestPassed[0] = true;
            }
        });

        assertTrue(isTestPassed[0]);
    }

    /*
    US7: As a user, I want to have a Focus mode
        So that the app shows just the goals I want to finish right now.

        Given Today view have two “Home” goals, one “School” goal and one “Errands” goal
        When I press 三
        And I press H
        Then Today view shows two “Home” goals and archives other goals.
        And 三 changes to 三
     */
    public void testUS7() {
        boolean[] isTestPassed = new boolean[2];
        // Goals with different contexts
        Goal newGoal1 = new Goal(null, "Goal1", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Home");
        Goal newGoal2 = new Goal(null, "Goal2", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Home");
        Goal newGoal3 = new Goal(null, "Goal3", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "School");
        Goal newGoal4 = new Goal(null, "Goal4", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Errands");
        viewModel.append(newGoal1);
        viewModel.append(newGoal2);
        viewModel.append(newGoal3);
        viewModel.append(newGoal4);

        // for today before focus
        viewModel.getGoals().observe(goals -> {
            if (isTestPassed[0] == false) {
                assert goals != null;
                assertEquals(4, goals.size());

                assertTrue(goals.stream().anyMatch(goal -> "Goal1".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal2".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal3".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal4".equals(goal.text())));

                isTestPassed[0] = true;
            }
        });

        // set focus to Home
        viewModel.focusHome();

        // for today after focus
        viewModel.getGoals().observe(goals -> {
            if (isTestPassed[1] == false) {
                assert goals != null;
                assertEquals(2, goals.size());

                assertTrue(goals.stream().anyMatch(goal -> "Goal1".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal2".equals(goal.text())));
                assertFalse(goals.stream().anyMatch(goal -> "Goal3".equals(goal.text())));
                assertFalse(goals.stream().anyMatch(goal -> "Goal4".equals(goal.text())));

                isTestPassed[1] = true;
            }
        });

        assertTrue(isTestPassed[0]);
        assertTrue(isTestPassed[1]);
    }

    /*
    US8: As a user, I want to be able to exit the Focus mode
        So that I can see my entire goal list when I need to.

        Given Today view shows two “Home” goals and archives other goals.
        When I press 三
        And I press Cancel
        Then Today view shows two “Home” goals, one “School” goal and one “Errands” goal
        And 三 changes to 三
     */
    public void testUS8() {
        boolean[] isTestPassed = new boolean[2];
        // Goals with different contexts
        Goal newGoal1 = new Goal(null, "Goal1", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Home");
        Goal newGoal2 = new Goal(null, "Goal2", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Home");
        Goal newGoal3 = new Goal(null, "Goal3", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "School");
        Goal newGoal4 = new Goal(null, "Goal4", false, -1, "One Time", SuccessDate.getCurrentDateAsString(), "Errands");
        viewModel.append(newGoal1);
        viewModel.append(newGoal2);
        viewModel.append(newGoal3);
        viewModel.append(newGoal4);

       // set focus to Home
        viewModel.focusHome();

        // for today when focusing on just Home
        viewModel.getGoals().observe(goals -> {
            if (isTestPassed[0] == false) {
                assert goals != null;
                assertEquals(2, goals.size());

                assertTrue(goals.stream().anyMatch(goal -> "Goal1".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal2".equals(goal.text())));
                assertFalse(goals.stream().anyMatch(goal -> "Goal3".equals(goal.text())));
                assertFalse(goals.stream().anyMatch(goal -> "Goal4".equals(goal.text())));

                isTestPassed[0] = true;
            }
        });

        // set focus to All
        viewModel.focusAll();

        // for today after focus to all
        viewModel.getGoals().observe(goals -> {
            if (isTestPassed[1] == false) {
                assert goals != null;
                assertEquals(4, goals.size());

                assertTrue(goals.stream().anyMatch(goal -> "Goal1".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal2".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal3".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal4".equals(goal.text())));

                isTestPassed[1] = true;
            }
        });

        assertTrue(isTestPassed[0]);
        assertTrue(isTestPassed[1]);

    }
}


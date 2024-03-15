package edu.ucsd.cse110.successorator;

import androidx.annotation.Nullable;

import junit.framework.TestCase;

import org.junit.Before;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.lib.util.Observer;

public class MainViewModelTest extends TestCase {
    private SimpleGoalRepository mockRepository;
    private  InMemoryDataSource dataSource;
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoal(new Goal(0, "Go home", false, 1, "Weekly",SuccessDate.getCurrentDateAsString(), "Home"));
        dataSource.putGoal(new Goal(1, "Do homework", true, 2, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        dataSource.putGoal(new Goal(2, "Go to gym", false, 3, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        dataSource.putGoal(new Goal(3, "Go shopping", true, 4, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        mockRepository = new SimpleGoalRepository(dataSource);
        viewModel = new MainViewModel(mockRepository);
    }

    public void testSetupDateObserver() {
        // Set the current date to "Today"
        //String today = SuccessDate.turnToDisplayDateString(SuccessDate.getCurrentDate());
        String today = SuccessDate.getCurrentDateAsString();

        viewModel.setCurrentDate(today);

        // Assert that the label is set to "Today"
        assertEquals("Today", viewModel.getLabel().getValue());

        // Change the date to tomorrow's date
        String tmw = SuccessDate.getTmwsDateAsString();
        viewModel.setCurrentDate(tmw);
        // Assert the label is updated to "Tomorrow"
        assertEquals("Tomorrow", viewModel.getLabel().getValue());
    }


    public void testGetFocus() {
        // Set the focus to a specific value
        viewModel.focusHome(); // Assuming this sets the focus to "Home"
        // Now verify getFocus() returns the expected value
        assertEquals("Home", viewModel.getFocus().getValue());
    }

    public void testGetDate() {
        // Set the current date in the ViewModel
        String expectedDate = "Mar-13-2024";
        viewModel.setCurrentDate(expectedDate);
        // Verify that getDate() returns the correct date
        assertEquals(expectedDate, viewModel.getDate());
    }

    public void testGetLabel() {
        // Set the label to a specific value, e.g., to "Today"
        viewModel.toToday(); // Assuming this sets the label to "Today"
        // Verify that getLabel() returns "Today"
        assertEquals("Today", viewModel.getLabel().getValue());
    }

    public void testGetGoals() {
        Goal goal1 = new Goal(1, "Goal 1", false, 1, "Daily",
                SuccessDate.getCurrentDateAsString(), "Home");
        Goal goal2 = new Goal(2, "Goal 2", false, 1, "Daily",
                SuccessDate.getCurrentDateAsString(), "Work");
        // Add goals to the dataSource
        dataSource.putGoal(goal1);
        dataSource.putGoal(goal2);
        viewModel.setupDatabaseObservers();  // This needs to be called to setup the observers.
        viewModel.toToday();  // Set the label to today to match the goals added


        // Act
        final boolean[] isTestPassed = {false}; // Using an array to allow modification inside the observer
        viewModel.getGoals().observe(new Observer<List<Goal>>() {
            @Override
            public void onChanged(@Nullable List<Goal> goals) {
                // Assert that the goals are as expected
                assertEquals(4, goals.size());
                assertTrue(goals.stream().anyMatch(goal -> "Goal 1".equals(goal.text())));
                assertTrue(goals.stream().anyMatch(goal -> "Goal 2".equals(goal.text())));
                isTestPassed[0] = true;
            }
        });

        // Assert
        assertTrue(isTestPassed[0]);

    }

    public void testGetGoalsForTomorrow() {
        Goal goalForTomorrow = new Goal(5, "Tomorrow's Goal", false, 1, "Daily",
                SuccessDate.getTmwsDateAsString(), "Work");
        dataSource.putGoal(goalForTomorrow);
        viewModel.setupDatabaseObservers();  // This needs to be called to setup the observers.

        // Act & Assert
        viewModel.getGoalsForTomorrow().observe(new Observer<List<Goal>>() {
            @Override
            public void onChanged(@Nullable List<Goal> goals) {
                // Assert that the goals for tomorrow contain the goal we added
                assertTrue(goals.stream().anyMatch(goal -> "Tomorrow's Goal".equals(goal.text())));
            }
        });
    }

    public void testGetGoalsForPending() {
        Goal pendingGoal = new Goal(6, "Pending Goal", false, 1, "Pending",
                "Pending", "Work");
        dataSource.putGoal(pendingGoal);
        viewModel.setupDatabaseObservers();  // This needs to be called to setup the observers.

        // Act & Assert
        viewModel.getGoalsForPending().observe(goals -> {
            // Assert that the goals for pending contain the goal we added
            assertTrue(goals.stream().anyMatch(goal -> "Pending Goal".equals(goal.text())));
        });
    }

    public void testGetGoalsForRecurring() {
        Goal recurringGoal = new Goal(7, "Recurring Goal", false, 1, "Weekly",
                "Mar-15-2024", "Work");
        dataSource.putGoal(recurringGoal);
        viewModel.setupDatabaseObservers();  // This needs to be called to setup the observers.

        // Act & Assert
        viewModel.getGoalsForRecurring().observe(new Observer<List<Goal>>() {
            @Override
            public void onChanged(@Nullable List<Goal> goals) {
                // Assert that the goals for recurring contain the goal we added
                assertTrue(goals.stream().anyMatch(goal -> "Recurring Goal".equals(goal.text())));
            }
        });


    }

    public void testGetGoalsForDate() {
        LocalDate testDate = LocalDate.of(2024, 3, 19);
        String formattedDate = testDate.format(DateTimeFormatter.ofPattern("MMM-d-yyyy")); // Adjust the date format as needed
        dataSource.putGoal(new Goal(1, "Goal for Mar-19-2024", false, 1, "Daily", formattedDate, "Work"));


        viewModel.setCurrentDate(formattedDate);

        // Trigger the method that updates showingForDateGoals based on the set date
        viewModel.getGoalsForDate().observe(goalsForDate -> {
            // Verify the goals for the specific date are as expected
            assertFalse(goalsForDate.isEmpty());
            assertEquals(1, goalsForDate.size());
            assertEquals("Goal for Mar-19-2024", goalsForDate.get(0).text());
            assertEquals(formattedDate, goalsForDate.get(0).getDate());
        });
    }

    public void testToToday() {
        viewModel.toToday();
        assertEquals("Today", viewModel.getLabel().getValue());
    }

    public void testToTomorrow() {
        viewModel.toTomorrow();
        assertEquals("Tomorrow", viewModel.getLabel().getValue());
    }

    public void testToPending() {
        viewModel.toPending();
        assertEquals("Pending", viewModel.getLabel().getValue());
    }

    public void testToRecurring() {
        viewModel.toRecurring();
        assertEquals("Recurring", viewModel.getLabel().getValue());
    }


    public void testFocusHome() {
        viewModel.focusHome();
        assertEquals("Home", viewModel.getFocus().getValue());
    }

    public void testFocusWork() {
        viewModel.focusWork();
        assertEquals("Work", viewModel.getFocus().getValue());
    }

    public void testFocusSchool() {
        viewModel.focusSchool();
        assertEquals("School", viewModel.getFocus().getValue());
    }

    public void testFocusErrands() {
        viewModel.focusErrands();
        assertEquals("Errands", viewModel.getFocus().getValue());
    }


    public void testAppend() {
        Goal testGoal = new Goal(null, "Test Goal", false, 99, "One Time", SuccessDate.getCurrentDateAsString(), "Home");
        int initialSize = mockRepository.dataSource.getGoals().size();


        viewModel.append(testGoal);

        int newSize = mockRepository.dataSource.getGoals().size();
        assertEquals(initialSize + 1, newSize); // Verify the goal was added

        List<Goal> goals = mockRepository.getCompletedOrUncompleted(false);
        Goal addedGoal = goals.get(goals.size() - 1);
        assertEquals("Home", addedGoal.getContext());
        assertEquals("One Time", addedGoal.getFrequency());
    }

    public void testPrepend() {
        Goal testGoal = new Goal(null, "Test Goal", false, 99, "One Time", "Mar-15-2024", "Home");
        int initialSize = mockRepository.dataSource.getGoals().size();


        viewModel.prepend(testGoal);

        int newSize = mockRepository.dataSource.getGoals().size();
        assertEquals(initialSize + 1, newSize); // Verify the goal was added

        List<Goal> goals = mockRepository.getCompletedOrUncompleted(false);
        Goal addedGoal = goals.get(0);

        assertEquals("Home", addedGoal.getContext());
        assertEquals("One Time", addedGoal.getFrequency());

    }

    public void testRemove() {
        int initialSize = mockRepository.dataSource.getGoals().size();
        Goal goalToRemove = mockRepository.dataSource.getGoals().get(0);
        viewModel.remove(goalToRemove.id());

        int newSize = mockRepository.dataSource.getGoals().size();
        assertFalse(mockRepository.dataSource.getGoals().contains(goalToRemove));
        assertEquals(initialSize - 1, newSize);
    }

    public void testRemoveAllCompleted() {
        viewModel.removeAllCompleted();

        // Verify that only uncompleted goals remain
        for (Goal goal : mockRepository.dataSource.getGoals()) {
            assertFalse(goal.isCompleted());
        }
    }

    public void testGetGoalsSize() {
        int expectedSize = mockRepository.dataSource.getGoals().size();
        int actualSize = 4;
        assertEquals(expectedSize, actualSize);
    }

    public void testSetCurrentDate() {
        String newDate = "Mar-13-2024";
        viewModel.setCurrentDate(newDate);
        assertEquals(newDate, viewModel.getDate());
    }
}
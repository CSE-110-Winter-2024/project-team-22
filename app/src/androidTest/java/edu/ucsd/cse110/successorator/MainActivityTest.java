package edu.ucsd.cse110.successorator;

import static androidx.test.core.app.ActivityScenario.launch;

import static junit.framework.TestCase.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalList;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private GoalRepository goalRepository;

    private GoalDao goalDao;
    GoalList goalsTomorrow;

    @Before
    public void setUpTomorrow() {
        goalsTomorrow = new GoalList();
        LocalDate theNextDay = SuccessDate.getCurrentDate();
        theNextDay = theNextDay.plusDays(1);
        String theNextDayStr = SuccessDate.dateToString(theNextDay);
        goalsTomorrow.setGoals(List.of(
                new Goal(0, "Do Homework", false, 0,"OneTime",theNextDayStr, "School"),
                new Goal(1, "Go to Gym", false, 1,"OneTime",theNextDayStr, "Home"),
                new Goal(2, "Eat Dinner", false, 2,"OneTime",theNextDayStr, "Home"),
                new Goal(3, "Buy Groceries", false, 3,"OneTime",theNextDayStr, "Home"),
                new Goal(4, "Meeting with CSE110", false, 4,"OneTime",theNextDayStr, "Work"),
                new Goal(5, "Club Activities", true, 5,"OneTime",theNextDayStr, "Errands"),
                new Goal(6, "Watch Lecture", true, 6,"OneTime",theNextDayStr, "School"),
                new Goal(7, "Visit family", true, 7,"OneTime",theNextDayStr, "Errands"),
                new Goal(8, "Study for CSE110", true, 8,"OneTime",theNextDayStr, "Work")
        ));
    }

    @Test
    public void TomorrowView(){
        // GIVEN
        int expected = 9;
        goalRepository = new SimpleGoalRepository(new InMemoryDataSource()); // Should use DB
        for (Goal g : goalsTomorrow.getGoals()){
            goalRepository.append(g);
        }
        // WHEN
        MainViewModel viewModel = new MainViewModel(goalRepository);
        viewModel.setupDatabaseObservers();
        // THEN
        assertEquals(expected,viewModel.getGoalsForTomorrow().getValue().size());
//        assertEquals(goalRepository.findAll().getValue().size(),9);

    }
}
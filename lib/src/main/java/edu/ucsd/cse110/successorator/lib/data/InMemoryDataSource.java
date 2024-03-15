package edu.ucsd.cse110.successorator.lib.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

/**
 * Class used as a sort of "database" of decks and flashcards that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private int id = 0;
    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    private final Map<Integer, Goal> goals
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final MutableSubject<List<Goal>> allGoalSubject
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public final static List<Goal> DEFAULT_CARDS = List.of(
            new Goal(0, "Do Homework", false, 0, "One Time", "Mar-14-2024", "School"),
            new Goal(1, "Go to Gym", false, 1, "Weekly", "Mar-15-024","Home"),
            new Goal(2, "Eat Dinner", false, 2, "Monthly", "Mar-19-024","Errands"),
            new Goal(3, "Buy Groceries", false, 3, "One Time", "Mar-15-024","Home"),
            new Goal(4, "Meeting with CSE110", false, 4, "Weekly", "Mar-18-024","Work"),
            new Goal(5, "Club Activities", false, 5, "One Time", "Mar-15-024","Home"),
            new Goal(6, "Watch Lecture", false, 6, "Yearly", "Mar-17-024","Errands"),
            new Goal(7, "Visit family", false, 7, "Weekly", "Mar-15-024","School"),
            new Goal(8, "Study for CSE110", false, 8, "One Time", "Pending","Home")
    );

    public static List<Goal> TEST_GOALS = List.of(
            new Goal(0, "Do Homework", false, 0, "One Time", "Mar-14-2024", "School"),
            new Goal(1, "Go to Gym", false, 1, "Weekly", "Mar-15-024","Home"),
            new Goal(2, "Eat Dinner", false, 2, "Monthly", "Mar-19-024","Errands"),
            new Goal(3, "Buy Groceries", false, 3, "One Time", "Mar-15-024","Home"),
            new Goal(4, "Meeting with CSE110", false, 4, "Weekly", "Mar-18-024","Work"),
            new Goal(5, "Club Activities", false, 5, "One Time", "Mar-15-024","Home"),
            new Goal(6, "Watch Lecture", false, 6, "Yearly", "Mar-17-024","Errands"),
            new Goal(7, "Visit family", false, 7, "Weekly", "Mar-15-024","School"),
            new Goal(8, "Study for CSE110", false, 8, "One Time", "Pending","Home")
    );


    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putGoals(DEFAULT_CARDS);
        return data;
    }

    public List<Goal> getGoals() {
        return List.copyOf(goals.values());
    }

    public Goal getGoal(int id) {
        return goals.get(id);
    }

    public Subject<Goal> getGoalSubject(int id) {
        if (!goalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getGoal(id));
            goalSubjects.put(id, subject);
        }
        return goalSubjects.get(id);
    }

    public Subject<List<Goal>> getAllGoalSubject() {
        return allGoalSubject;
    }

    public int getMinSortOrder() {
        return minSortOrder;
    }

    public int getMaxSortOrder() {
        return maxSortOrder;
    }

    public void putGoal(Goal card) {
        var fixedCard = preInsert(card);

        goals.put(fixedCard.id(), fixedCard);

        postInsert();
        assertSortOrderConstraints();

        if (goalSubjects.containsKey(fixedCard.id())) {
            goalSubjects.get(fixedCard.id()).setValue(fixedCard);
        }
        allGoalSubject.setValue(getGoals());
    }

    public void putGoals(List<Goal> goals) {
        var fixedGoals = goals.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedGoals.forEach(goal -> this.goals.put(goal.id(), goal));
        postInsert();
        assertSortOrderConstraints();

        fixedGoals.forEach(goal -> {
            if (goalSubjects.containsKey(goal.id())) {
                goalSubjects.get(goal.id()).setValue(goal);
            }
        });
        allGoalSubject.setValue(getGoals());
    }

    public void removeGoal(int id) {
        var goal = goals.get(id);
        if (goal == null) return;
        var sortOrder = goal.sortOrder();

        goals.remove(id);
        shiftSortOrders(sortOrder, maxSortOrder, -1);

        if (goalSubjects.containsKey(id)) {
            goalSubjects.get(id).setValue(null);
        }
        allGoalSubject.setValue(getGoals());
    }

    public void shiftSortOrders(int from, int to, int by) {
        var goals = this.goals.values().stream()
                .filter(goal -> goal.sortOrder() >= from && goal.sortOrder() <= to)
                .map(goal -> goal.withSortOrder(goal.sortOrder() + by))
                .collect(Collectors.toList());

        putGoals(goals);
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that new
     * cards inserted have an id, and updates the nextId if necessary.
     */
    public Goal preInsert(Goal goal) {
        var id = goal.id();
        if (id == null) {
            // If the goal has no id, give it one.
            goal = goal.withId(this.id++);
        }
        else if (id > this.id) {
            // If the goal has an id, update nextId if necessary to avoid giving out the same
            // one. This is important for when we pre-load cards like in fromDefault().
            this.id = id + 1;
        }

        return goal;
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that the
     * min and max sort orders are up to date after an insert.
     */
    private void postInsert() {
        // Keep the min and max sort orders up to date.
        minSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        maxSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE);
    }

    /**
     * Safety checks to ensure the sort order constraints are maintained.
     * <p></p>
     * Will throw an AssertionError if any of the constraints are violated,
     * which should never happen. Mostly here to make sure I (Dylan) don't
     * write incorrect code by accident!
     */
    private void assertSortOrderConstraints() {
        // Get all the sort orders...
        var sortOrders = goals.values().stream()
                .map(Goal::sortOrder)
                .collect(Collectors.toList());

        // Non-negative...
        /*assert sortOrders.stream().allMatch(i -> i >= 0);

        // Unique...
        assert sortOrders.size() == sortOrders.stream().distinct().count();

        // Between min and max...
        assert sortOrders.stream().allMatch(i -> i >= minSortOrder);
        assert sortOrders.stream().allMatch(i -> i <= maxSortOrder);*/
    }

    public List<Goal> getWeeklyGoals() {
        return goals.values().stream()
                .filter(goal -> "Weekly".equals(goal.getFrequency()))
                .collect(Collectors.toList());
    }

    public Subject<List<Goal>> findAllWeeklyGoals() {
        List<Goal> weeklyGoals = getWeeklyGoals();
        MutableSubject<List<Goal>> weeklyGoalsSubject = new SimpleSubject<>();
        weeklyGoalsSubject.setValue(weeklyGoals);
        return weeklyGoalsSubject;
    }
}

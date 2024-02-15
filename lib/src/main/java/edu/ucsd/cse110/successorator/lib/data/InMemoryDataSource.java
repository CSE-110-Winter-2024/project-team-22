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
 * Class used as a sort of "database" of days and goals that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private int nextId = 0;

    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    // NOTE: We don't care about sort order in goals
    private final Map<Integer, Goal> goals
            = new HashMap<>();

    Map<Integer, Goal> completedGoals
            = new HashMap<>();
    Map<Integer, Goal> uncompletedGoals
            = new HashMap<>();

    private final Map<Integer, MutableSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final MutableSubject<List<Goal>> allGoalsSubject
            = new SimpleSubject<>();

    private final MutableSubject<List<Goal>> completedGoalsSubject
            = new SimpleSubject<>();
    private final MutableSubject<List<Goal>> uncompletedGoalsSubject
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public final static List<Goal> DEFAULT_GOALS = List.of(
            new Goal(0, "do homework", 0),
            new Goal(1, "go to gym", 1),
            new Goal(2, "eat lunch", 2)
    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putGoals(DEFAULT_GOALS);
        return data;
    }

    public List<Goal> getGoals() {
        return List.copyOf(goals.values());
    }

    public List<Goal> getCompletedGoals() {
        return List.copyOf(completedGoals.values());
    }

    public List<Goal> getUncompletedGoals() {
        return List.copyOf(uncompletedGoals.values());
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

    public Subject<List<Goal>> getAllGoalsSubject() {
        return allGoalsSubject;
    }

    public Subject<List<Goal>> getCompletedGoalsSubject() { return completedGoalsSubject;}
    public Subject<List<Goal>> getUncompletedGoalsSubject() { return uncompletedGoalsSubject;}

    public int getMinSortOrder() {
        return minSortOrder;
    }

    public int getMaxSortOrder() {
        return maxSortOrder;
    }

    public void putGoal(Goal task) {
        /* var fixedTask = preInsert(task);

        goals.put(fixedTask.id(), fixedTask);
        postInsert();
        assertSortOrderConstraints();

        if (goalSubjects.containsKey(fixedTask.id())) {
            goalSubjects.get(fixedTask.id()).setValue(fixedTask);
        }

        allGoalsSubject.setValue(getGoals());
         */

        var fixedTask = preInsert(task);

        goals.put(fixedTask.id(), fixedTask);

        if (fixedTask.isCompleted()) {
            completedGoals.put(fixedTask.id(), fixedTask);
        } else {
            uncompletedGoals.put(fixedTask.id(), fixedTask);
        }

        postInsert();
        assertSortOrderConstraints();

        if (goalSubjects.containsKey(fixedTask.id())) {
            goalSubjects.get(fixedTask.id()).setValue(fixedTask);
        }

        allGoalsSubject.setValue(getGoals());
        completedGoalsSubject.setValue(getCompletedGoals());
        uncompletedGoalsSubject.setValue(getUncompletedGoals());

    }

    public void putGoals(List<Goal> tasks) {
        /*
        var fixedTasks = tasks.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedTasks.forEach(task -> goals.put(task.id(), task));
        postInsert();
        assertSortOrderConstraints();

        fixedTasks.forEach(task -> {
            if (goalSubjects.containsKey(task.id())) {
                goalSubjects.get(task.id()).setValue(task);
            }
        });

        allGoalsSubject.setValue(getGoals());

         */

        var fixedTasks = tasks.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedTasks.forEach(task -> {
            goals.put(task.id(), task);

            if (task.isCompleted()) {
                completedGoals.put(task.id(), task);
            } else {
                uncompletedGoals.put(task.id(), task);
            }
        });

        postInsert();
        assertSortOrderConstraints();

        fixedTasks.forEach(task -> {
            if (goalSubjects.containsKey(task.id())) {
                goalSubjects.get(task.id()).setValue(task);
            }
        });

        allGoalsSubject.setValue(getGoals());
        completedGoalsSubject.setValue(getCompletedGoals());
        uncompletedGoalsSubject.setValue(getUncompletedGoals());
    }

    public void removeGoal(int id) {
        var task = goals.get(id);
        var sortOrder = task.sortOrder();

        goals.remove(id);

        if (task.isCompleted()) {
            completedGoals.remove(id);
        } else {
            uncompletedGoals.remove(id);
        }

        shiftSortOrders(sortOrder, maxSortOrder, -1);

        if (goalSubjects.containsKey(id)) {
            goalSubjects.get(id).setValue(null);
        }

        allGoalsSubject.setValue(getGoals());
        completedGoalsSubject.setValue(getCompletedGoals());
        uncompletedGoalsSubject.setValue(getUncompletedGoals());
    }

    public void shiftSortOrders(int from, int to, int by) {
        var tasks = goals.values().stream()
                .filter(task -> task.sortOrder() >= from && task.sortOrder() <= to)
                .map(task -> task.withSortOrder(task.sortOrder() + by))
                .collect(Collectors.toList());

        putGoals(tasks);
    }

    // reset sort order amongst lists
    public void shiftSortOrdersCompleted(int from, int to, int by) {
        var tasks = completedGoals.values().stream()
                .filter(task -> task.sortOrder() >= from && task.sortOrder() <= to)
                .map(task -> task.withSortOrder(task.sortOrder() + by))
                .collect(Collectors.toList());

        putGoals(tasks);
    }

    // reset sort order between lists
    public void shiftSortOrdersUncompleted(int from, int to, int by) {
        var tasks = uncompletedGoals.values().stream()
                .filter(task -> task.sortOrder() >= from && task.sortOrder() <= to)
                .map(task -> task.withSortOrder(task.sortOrder() + by))
                .collect(Collectors.toList());

        putGoals(tasks);
    }


    /**
     * Private utility method to maintain state of the fake DB: ensures that new
     * cards inserted have an id, and updates the nextId if necessary.
     */
    private Goal preInsert(Goal task) {
        var id = task.id();
        if (id == null) {
            // If the card has no id, give it one.
            task = task.withId(nextId++);
        }
        else if (id > nextId) {
            // If the card has an id, update nextId if necessary to avoid giving out the same
            // one. This is important for when we pre-load cards like in fromDefault().
            nextId = id + 1;
        }

        return task;
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
        assert sortOrders.stream().allMatch(i -> i >= 0);

        // Unique...
        assert sortOrders.size() == sortOrders.stream().distinct().count();

        // Between min and max...
        assert sortOrders.stream().allMatch(i -> i >= minSortOrder);
        assert sortOrders.stream().allMatch(i -> i <= maxSortOrder);
    }

}

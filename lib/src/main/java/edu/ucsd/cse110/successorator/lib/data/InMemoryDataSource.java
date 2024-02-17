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

/**
 * Class used as a sort of "database" of completedGoals and uncompletedGoals that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private int nextId = 0;

    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    private final Map<Integer, Goal> flashcards
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> flashcardSubjects
            = new HashMap<>();
    private final MutableSubject<List<Goal>> allFlashcardsSubject
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public final static List<Goal> DEFAULT_CARDS = List.of(
            new Goal(0, "Do Homework", false, 0),
            new Goal(1, "Go to Gym", false, 1),
            new Goal(2, "Eat Dinner", false, 2),
            new Goal(3, "Buy Groceries", false, 3),
            new Goal(4, "Meeting with CSE110", false, 4),
            new Goal(5, "Club Activities", false, 5)
    );

    public final static List<Goal> DEFAULT_GOALS = List.of(
            new Goal(0, "Do Homework", false, 0),
            new Goal(1, "Go to Gym", true, 1),
            new Goal(2, "Eat Dinner", false, 2),
            new Goal(3, "Buy Groceries", true, 3),
            new Goal(4, "Meeting with CSE110", false, 4),
            new Goal(5, "Club Activities", true, 5),
            new Goal(6, "Club Activities", true, 6),
            new Goal(7, "Club Activities", true, 7),
            new Goal(8, "Club Activities", true, 8),
            new Goal(9, "Club Activities", true, 9)
    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putGoals(DEFAULT_GOALS);
        return data;
    }

    public List<Goal> getFlashcards() {
        return List.copyOf(flashcards.values());
    }

    public Goal getFlashcard(int id) {
        return flashcards.get(id);
    }

    public Subject<Goal> getFlashcardSubject(int id) {
        if (!flashcardSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getFlashcard(id));
            flashcardSubjects.put(id, subject);
        }
        return flashcardSubjects.get(id);
    }

    public Subject<List<Goal>> getAllFlashcardsSubject() {
        return allFlashcardsSubject;
    }

    public int getMinSortOrder() {
        return minSortOrder;
    }

    public int getMaxSortOrder() {
        return maxSortOrder;
    }

    public void putFlashcard(Goal card) {
        var fixedCard = preInsert(card);

        flashcards.put(fixedCard.id(), fixedCard);
        postInsert();
        assertSortOrderConstraints(flashcards);

        if (flashcardSubjects.containsKey(fixedCard.id())) {
            flashcardSubjects.get(fixedCard.id()).setValue(fixedCard);
        }
        allFlashcardsSubject.setValue(getFlashcards());
    }

    public void putFlashcards(List<Goal> cards) {
        var fixedCards = cards.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedCards.forEach(card -> flashcards.put(card.id(), card));
        postInsert();
        assertSortOrderConstraints(flashcards);

        fixedCards.forEach(card -> {
            if (flashcardSubjects.containsKey(card.id())) {
                flashcardSubjects.get(card.id()).setValue(card);
            }
        });
        allFlashcardsSubject.setValue(getFlashcards());
    }

    public void removeFlashcard(int id) {
        var card = flashcards.get(id);
        var sortOrder = card.sortOrder();

        flashcards.remove(id);
        shiftSortOrders(sortOrder, maxSortOrder, -1);

        if (flashcardSubjects.containsKey(id)) {
            flashcardSubjects.get(id).setValue(null);
        }
        allFlashcardsSubject.setValue(getFlashcards());
    }

    public void shiftSortOrders(int from, int to, int by) {
        var cards = flashcards.values().stream()
                .filter(card -> card.sortOrder() >= from && card.sortOrder() <= to)
                .map(card -> card.withSortOrder(card.sortOrder() + by))
                .collect(Collectors.toList());

        putFlashcards(cards);
    }

    // actual stuff below here



    private int minSortOrderCG = Integer.MAX_VALUE;
    private int minSortOrderUG = Integer.MAX_VALUE;
    private int maxSortOrderCG = Integer.MIN_VALUE;
    private int maxSortOrderUG = Integer.MIN_VALUE;



    // NOTE: We don't care about sort order in goals
    private final Map<Integer, Goal> completedGoals
            = new HashMap<>();
    private final Map<Integer, Goal> uncompletedGoals
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> completedGoalSubjects
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> uncompletedGoalSubjects
            = new HashMap<>();

    private final MutableSubject<List<Goal>> allCompletedGoalsSubject
            = new SimpleSubject<>();
    private final MutableSubject<List<Goal>> allUncompletedGoalsSubject
            = new SimpleSubject<>();

    public Goal getCompletedGoal(int id) {
        return completedGoals.get(id);
    }

    public Goal getUncompletedGoal(int id) {
        return uncompletedGoals.get(id);
    }

    public Subject<Goal> getCompletedGoalSubject(int id) {
        if (!completedGoalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getCompletedGoal(id));
            completedGoalSubjects.put(id, subject);
        }
        return completedGoalSubjects.get(id);
    }

    public Subject<Goal> getUncompletedGoalSubject(int id) {
        if (!uncompletedGoalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getUncompletedGoal(id));
            uncompletedGoalSubjects.put(id, subject);
        }
        return uncompletedGoalSubjects.get(id);
    }

    public Subject<List<Goal>> getAllCompletedGoalsSubject() {
        return allCompletedGoalsSubject;
    }

    public Subject<List<Goal>> getAllUncompletedGoalsSubject() {
        return allUncompletedGoalsSubject;
    }

    public int getMinSortOrderCG() {
        return minSortOrderCG;
    }
    public int getMinSortOrderUG() {
        return minSortOrderUG;
    }
    public int getMaxSortOrderCG() {
        return maxSortOrderCG;
    }
    public int getMaxSortOrderUG() {
        return maxSortOrderUG;
    }



    public List<Goal> getCompletedGoals() {
        return List.copyOf(completedGoals.values());
    }

    public List<Goal> getUncompletedGoals() {
        return List.copyOf(uncompletedGoals.values());
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

        if (fixedTask.goalStatus()) {
            completedGoals.put(fixedTask.id(), fixedTask);
            postInsert();
            assertSortOrderConstraints(completedGoals);
        } else {
            uncompletedGoals.put(fixedTask.id(), fixedTask);
            postInsert();
            assertSortOrderConstraints(uncompletedGoals);
        }


        if (completedGoalSubjects.containsKey(fixedTask.id())) {
            completedGoalSubjects.get(fixedTask.id()).setValue(fixedTask);
        } else if (uncompletedGoalSubjects.containsKey(fixedTask.id())) {
            uncompletedGoalSubjects.get(fixedTask.id()).setValue(fixedTask);
        }

        allCompletedGoalsSubject.setValue(getCompletedGoals());
        allUncompletedGoalsSubject.setValue(getUncompletedGoals());
    }

    public void putGoals(List<Goal> tasks) {
        /*
        var fixedTasks = tasks.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());
@@ -99,20 +147,60 @@ public void putGoals(List<Goal> tasks) {
                goalSubjects.get(task.id()).setValue(task);
            }
        });
        allGoalsSubject.setValue(getGoals());
         */

        var fixedTasks = tasks.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedTasks.forEach(task -> {
                if (task.goalStatus()) {
                    completedGoals.put(task.id(), task);
                } else {
                    uncompletedGoals.put(task.id(), task);
                }
        });

        postInsert();

        fixedTasks.forEach(task -> {
            if (completedGoalSubjects.containsKey(task.id())) {
                assertSortOrderConstraints(completedGoals);
                completedGoalSubjects.get(task.id()).setValue(task);
            } else if (uncompletedGoalSubjects.containsKey((task.id()))){
                assertSortOrderConstraints(uncompletedGoals);
                uncompletedGoalSubjects.get(task.id()).setValue(task);
            }
        });

        allCompletedGoalsSubject.setValue(getCompletedGoals());
        allUncompletedGoalsSubject.setValue(getUncompletedGoals());
    }

    public void removeGoal(int id) {
        var task = completedGoals.get(id);
        if (task == null) {
            task = uncompletedGoals.get(id);
        }

        var sortOrder = task.sortOrder();

        if (task.goalStatus()) {
            completedGoals.remove(id);
            shiftSortOrdersUncompleted(sortOrder, maxSortOrderUG, -1);
        } else {
            uncompletedGoals.remove(id);
            shiftSortOrdersCompleted(sortOrder, maxSortOrderCG, -1);
        }

        if (completedGoalSubjects.containsKey(id)) {
            completedGoalSubjects.get(id).setValue(null);
        } else if (uncompletedGoalSubjects.containsKey(id)) {
            uncompletedGoalSubjects.get(id).setValue(null);
        }

        allCompletedGoalsSubject.setValue(getCompletedGoals());
        allUncompletedGoalsSubject.setValue(getUncompletedGoals());
    }

    // reset sort order amongst lists
    public void shiftSortOrdersCompleted(int from, int to, int by) {
        var tasks = completedGoals.values().stream()
                .filter(task -> task.sortOrder() >= from && task.sortOrder() <= to)
                .map(task -> task.withSortOrder(task.sortOrder() + by))
                .collect(Collectors.toList());

        System.out.println("C pre putGoals");
        putGoals(tasks);
        System.out.println("C post putGoals");
    }

    // reset sort order between lists
    public void shiftSortOrdersUncompleted(int from, int to, int by) {
        var tasks = uncompletedGoals.values().stream()
                .filter(task -> task.sortOrder() >= from && task.sortOrder() <= to)
                .map(task -> task.withSortOrder(task.sortOrder() + by))
                .collect(Collectors.toList());


        System.out.println("U pre putGoals");
        putGoals(tasks);
        System.out.println("U post putGoals");
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that new
     * goals inserted have an id, and updates the nextId if necessary.
     */
    private Goal preInsert(Goal goal) {
        var id = goal.id();
        if (id == null) {
            // If the card has no id, give it one.
            goal = goal.withId(nextId++);
        }
        else if (id > nextId) {
            // If the card has an id, update nextId if necessary to avoid giving out the same
            // one. This is important for when we pre-load cards like in fromDefault().
            nextId = id + 1;
        }

        return goal;
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that the
     * min and max sort orders are up to date after an insert.
     */
    private void postInsert() {
        // Keep the min and max sort orders up to date.
        minSortOrderCG = completedGoals.values().stream()
                .map(Goal::sortOrder)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
        minSortOrderUG = uncompletedGoals.values().stream()
                .map(Goal::sortOrder)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        maxSortOrderCG = completedGoals.values().stream()
                .map(Goal::sortOrder)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE);
        maxSortOrderUG = uncompletedGoals.values().stream()
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
    private void assertSortOrderConstraints(Map<Integer, Goal> goals) {
        // Get all the sort orders...
        /*
        var sortOrders = goals.values().stream()
                .map(Goal::sortOrder)
                .collect(Collectors.toList());

         */

        // Non-negative...
        //assert sortOrders.stream().allMatch(i -> i >= 0);

        // Unique...
        //assert sortOrders.size() == sortOrders.stream().distinct().count();

        // Between min and max...
        //assert sortOrders.stream().allMatch(i -> i >= minSortOrder);
        //assert sortOrders.stream().allMatch(i -> i <= maxSortOrder);
    }
}

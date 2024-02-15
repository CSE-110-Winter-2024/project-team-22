package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleGoalRepository implements GoalRepository {
    private final InMemoryDataSource dataSource;

    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Subject<Goal> find(int id) {
        return dataSource.getGoalSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        return dataSource.getAllGoalsSubject();
    }

    @Override
    public Subject<List<Goal>> findAllCompleted() {
        return dataSource.getCompletedGoalsSubject();
    }

    @Override
    public Subject<List<Goal>> findAllUncompleted() {
        return dataSource.getUncompletedGoalsSubject();
    }

    @Override
    public void save(Goal goal) {
        dataSource.putGoal(goal);
    }

    @Override
    public void save(List<Goal> goals) {
        dataSource.putGoals(goals);
    }

    @Override
    public void remove(int id) {
        dataSource.removeGoal(id);
    }

    // Only calling this when adding a new Goal (has never
    // been inside the db)
    @Override
    public void append(Goal goal) {
        dataSource.putGoal(
                goal.withSortOrder(dataSource.getMaxSortOrder() + 1)
        );
    }

    @Override
    public void prepend(Goal goal) {
        // Shift all the existing cards up by one.
        // We don't care about sort order in congregated "goals" list.
        // dataSource.shiftSortOrders(0, dataSource.getMaxSortOrder(), 1);

        // we only care about sort order in completed or uncompleted list.
        if (goal.isCompleted()) {
            dataSource.shiftSortOrdersCompleted(0, dataSource.getMaxSortOrder(), 1);
        } else {
            dataSource.shiftSortOrdersUncompleted(0, dataSource.getMaxSortOrder(), 1);
        }

        // Then insert the new card before the first one.
        dataSource.putGoal(
                goal.withSortOrder(dataSource.getMinSortOrder() -1)
        );
    }
}

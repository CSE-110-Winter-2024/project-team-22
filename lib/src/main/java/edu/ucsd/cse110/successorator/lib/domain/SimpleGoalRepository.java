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
        return dataSource.getFlashcardSubject(id);
    }

    public Subject<Goal> findCompleted(int id) {
        return dataSource.getCompletedGoalSubject(id);
    }

    public Subject<Goal> findUncompleted(int id) {
        return dataSource.getUncompletedGoalSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        return dataSource.getAllFlashcardsSubject();
    }

    @Override
    public Subject<List<Goal>> findAllCompleted() {
        return dataSource.getAllCompletedGoalsSubject();
    }
    @Override
    public Subject<List<Goal>> findAllUncompleted() {
        return dataSource.getAllUncompletedGoalsSubject();
    }


    @Override
    public void save(Goal goal) {
        // dataSource.putFlashcard(goal);
        dataSource.putGoal(goal);
    }

    @Override
    public void save(List<Goal> goals) {
        // dataSource.putFlashcards(goals);
        dataSource.putGoals(goals);
    }

    @Override
    public void remove(int id) {
        // dataSource.removeFlashcard(id);
        dataSource.removeGoal(id);
    }

    @Override
    public void append(Goal goal) {
       // dataSource.putFlashcard(
       //         goal.withSortOrder(dataSource.getMaxSortOrder() + 1)
       // );
        if (goal.goalStatus()) {
            dataSource.putGoal(
                    goal.withSortOrder(dataSource.getMaxSortOrderUG() + 1)
            );
        } else {
            dataSource.putGoal(
                    goal.withSortOrder(dataSource.getMaxSortOrderCG() + 1)
            );
        }
    }

    @Override
    public void prepend(Goal goal) {
        if (goal.goalStatus()) {
            // Shift all the existing cards up by one.
            dataSource.shiftSortOrdersCompleted(0, dataSource.getMaxSortOrderCG(), 1);
            // Then insert the new card before the first one.
            dataSource.putGoal(
                    goal.withSortOrder(dataSource.getMinSortOrderCG() -1)
            );
        } else {
            // Shift all the existing goals up by one.
            dataSource.shiftSortOrdersUncompleted(0, dataSource.getMaxSortOrderUG(), 1);
            // Then insert the new goal before the first one.
            dataSource.putGoal(
                    goal.withSortOrder(dataSource.getMinSortOrderUG() -1)
            );
        }
    }
}

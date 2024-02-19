package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleGoalRepository implements GoalRepository {
    private final InMemoryDataSource dataSource;
    private GoalList goals;

    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
        this.goals = new GoalList();
    }

    public void syncLists() {
        List<Goal> goalsData = this.dataSource.getFlashcards();
        GoalList goalsLogic = new GoalList();
        List<Goal> newGoalData;
        newGoalData = goalsLogic.fillGoals(goalsData);

        // remove all goals from data
        goalsData.forEach(goal -> {
            this.dataSource.removeFlashcard(goal.id());
        });

        // reinsert them
        this.dataSource.putFlashcards(newGoalData);
        this.goals = goalsLogic;

    }

    @Override
    public Subject<Goal> find(int id) {
        return dataSource.getFlashcardSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        return dataSource.getAllFlashcardsSubject();
    }

    @Override
    public void save(Goal goal) {
        // process in GoalList

        dataSource.putFlashcard(goal);
    }

    @Override
    public void save(List<Goal> goals) {

        // process in GoalList, adjust sortorder


        dataSource.putFlashcards(goals);
    }

    @Override
    public void remove(int id) {
        // remove from GoalList
        dataSource.removeFlashcard(id);
        syncLists();

        // prepend in GoalList, update sort order in process
        //dataSource.putFlashcard

    }

    @Override
    public void append(Goal goal) {
        // process in GoalList, simply call syncList
        syncLists();
        int sortOrder = goals.getGoalSortOrder(goal, true);

        // get index of where insertion/start of moving goals
        goal = goal.withSortOrder(sortOrder);

        if (goal.id() == null) {
            // get next available Id if it is originally null
            goal = dataSource.preInsert(goal);
            goal = goal.withId(goal.id()+1);
        }

        // Shift all the existing cards up by one.
        dataSource.shiftSortOrders(goal.sortOrder(), dataSource.getMaxSortOrder(), 1);
        // Then insert the new card before the first one.

        dataSource.putFlashcard(goal);

        syncLists();


    }

    @Override
    public void prepend(Goal goal) {
        // process in GoalList, simply call syncList
        syncLists();
        int sortOrder = goals.getGoalSortOrder(goal, false);

        // get index of where insertion/start of moving goals
        goal = goal.withSortOrder(sortOrder);

        if (goal.id() == null) {
            // get next available Id if it is originally null
            goal = dataSource.preInsert(goal);
            goal = goal.withId(goal.id()+1);
        }

        // Shift all the existing cards up by one.
        dataSource.shiftSortOrders(goal.sortOrder(), dataSource.getMaxSortOrder(), 1);
        // Then insert the new card before the first one.

        dataSource.putFlashcard(goal);

        syncLists();

    }

    @Override
    public void removeCompleted() {
        List<Goal> goalsData = this.dataSource.getFlashcards();

        goalsData.forEach(goal -> {
            if (goal.isCompleted()) {
                dataSource.removeFlashcard(goal.id());
            }
        });
        syncLists();
    }
}

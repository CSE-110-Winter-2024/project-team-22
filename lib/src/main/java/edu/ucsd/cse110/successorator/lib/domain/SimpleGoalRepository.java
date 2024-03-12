package edu.ucsd.cse110.successorator.lib.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import kotlin.collections.ArrayDeque;

public class SimpleGoalRepository implements GoalRepository {
    public InMemoryDataSource dataSource;
    private GoalList goals;
    private boolean loaded;


    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
        this.goals = new GoalList();
        this.loaded = false;
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
        if (!this.loaded) {
            dataSource.putFlashcards(goals);
            this.loaded = true;
        }
    }

    @Override
    public void remove(int id) {
        // remove from GoalList
        dataSource.removeFlashcard(id);

        // prepend in GoalList, update sort order in process
        //dataSource.putFlashcard

    }

    @Override
    public void append(Goal goal) {
        // process in GoalList, simply call syncList
        // List<Goal> listOfGoals = syncLists();
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
    }

    @Override
    public void prepend(Goal goal) {
        // process in GoalList, simply call syncList
        // List<Goal> listOfGoals = syncLists();
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
    }

    @Override
    public void removeAllCompleted() {
        List<Goal> goalsData = this.dataSource.getFlashcards();
        List<Goal> deletedData = new ArrayDeque<>();

        goalsData.forEach(goal -> {
            if (goal.isCompleted()) {
                dataSource.removeFlashcard(goal.id());
                deletedData.add(goal);
            }
        });
    }
    public Subject<List<Goal>> findAllWeeklyGoals(){
        return dataSource.findAllWeeklyGoals();
    };

    public LocalDate getDisplayDate (Goal goal){
        return goal.getDate();
    }

    @Override
    public Subject<List<Goal>> findAllFrequencyGoals(String freq) {
        return null;
    }

    @Override
    public Subject<List<Goal>> findAllContextsGoals(String context) {
        return null;
    }

    @Override
    public Subject<List<Goal>> findAllDropdownGoals(String choice) {
        return null;
    }

}

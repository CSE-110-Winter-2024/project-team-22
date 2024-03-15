package edu.ucsd.cse110.successorator.lib.domain;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleGoalRepository implements GoalRepository {
    public InMemoryDataSource dataSource;
//    private GoalList goals;
    private boolean loaded;

    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
        this.loaded = false;
    }

    public List<Goal> getCompletedOrUncompleted(boolean isComplete) {
        List<Goal> goals = dataSource.getGoals();
        List<Goal> newGoals = new ArrayList<Goal>();
        goals.forEach(goal -> {
            if (goal.isCompleted() == isComplete) {
                newGoals.add(goal);
            }
        });
        newGoals.forEach(goal -> {
            dataSource.removeGoal(goal.id());
        });
        return newGoals;
    }

    @Override
    public Subject<Goal> find(int id) {
        return dataSource.getGoalSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        return dataSource.getAllGoalSubject();
    }

/*    @Override
    public void save(Goal goal) {
        // process in GoalList

        dataSource.putFlashcard(goal);
    }*/


    @Override
    public void save(List<Goal> goals) {
        dataSource.putGoals(goals);
        //if (!this.loaded) {
        //    this.loaded = true;
        //}
    }

    @Override
    public void append(Goal goal) {
        List<Goal> goalsC = getCompletedOrUncompleted(true);
        List<Goal> goalsU = getCompletedOrUncompleted(false);

        if (goal.isCompleted()) {
            goalsC.add(goal);
        } else {
            goalsU.add(goal);
        }
        List<Goal> newAllGoals = new ArrayList<Goal>();
        List<Goal> allGoals = new ArrayList<Goal>(goalsU);
        allGoals.addAll(goalsC);

        for (int i = 0; i < allGoals.size(); i++) {
            Goal g = allGoals.get(i).withId(i).withSortOrder(i + 1);
            g.setFrequency(allGoals.get(i).getFrequency());
            g.setDate(allGoals.get(i).getDate());
            newAllGoals.add(g);
        }

        save(newAllGoals);
    }

    @Override
    public void prepend(Goal goal) {
        List<Goal> goalsC = getCompletedOrUncompleted(true);
        List<Goal> goalsU = getCompletedOrUncompleted(false);

        if (goal.isCompleted()) {
            goalsC.add(0, goal);
        } else {
            goalsU.add(0, goal);
        }
        List<Goal> newAllGoals = new ArrayList<Goal>();
        List<Goal> allGoals = new ArrayList<Goal>(goalsU);
        allGoals.addAll(goalsC);

        // reset id and sortOrder?
        for (int i = 0; i < allGoals.size(); i++) {
            Goal g = allGoals.get(i).withId(i).withSortOrder(i + 1);
            g.setFrequency(allGoals.get(i).getFrequency());
            g.setDate(allGoals.get(i).getDate());

            newAllGoals.add(g);
        }

        save(newAllGoals);
    }

    @Override
    public void removeAllCompleted() {
        // rather than goalDao we use dataSource

        List<Goal> goalsList = getCompletedOrUncompleted(true);
        List<Goal> filteredList = new ArrayList<>();
        for (Goal goal : goalsList) {
            if (goal.getDate().equals(SuccessDate.dateToString(SuccessDate.getCurrentDate().minusDays(1)))) {
                filteredList.add(goal);
            }
        }

        filteredList.forEach(goal -> {
            if (goal != null) {
                dataSource.removeGoal(goal.id());
            }
        });
    }

    @Override
    public void remove(int id) {
        // remove from GoalList
        dataSource.removeGoal(id);

        // prepend in GoalList, update sort order in process
        //dataSource.putFlashcard

    }

}

package edu.ucsd.cse110.successorator.data.db;

import static androidx.lifecycle.Transformations.map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;

public class RoomGoalRepository implements GoalRepository {
    private final GoalDao goalDao;

    public RoomGoalRepository(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    List<Goal> getCompletedOrUncompleted(boolean isComplete) {
        List<GoalEntity> goals = goalDao.findAll();
        List<Goal> newGoals = new ArrayList<Goal>();
        goals.forEach(goalE -> {
            if (goalE.isCompleted == isComplete) {
                newGoals.add(goalE.toGoal());
            }
        });
        newGoals.forEach(goal -> {
            goalDao.delete(goal.id());
        });
        return newGoals;
    }

    @Override
    public Subject<Goal> find(int id) {
        var entityLiveData = goalDao.findAsLiveData(id);

        var flashcardLiveData = map(entityLiveData, GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        var entityLiveData = goalDao.findAsLiveData();
        var flashcardLiveData = map(entityLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    /*
    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }
    */

    @Override
    public void save(List<Goal> goals) {
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
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

        // withId withSOrt order causes us to lose Goal creationDate and frequency by calling the constructor.
        // all the information should basically be the same except for maybe the sort order and the id
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
        List<Goal> goalsList = getCompletedOrUncompleted(true);
        List<Goal> filteredList = new ArrayList<>();
        for (Goal goal : goalsList) {
            if (goal.getDate().equals(SuccessDate.dateToString(SuccessDate.getCurrentDate().minusDays(1)))) {
                filteredList.add(goal);
            }
        }

        filteredList.forEach(goal -> {
            if (goal.id() != null) {
                goalDao.delete(goal.id());
            }
        });
    }

    @Override
    public void remove(int id){
        goalDao.delete(id);
    }
}

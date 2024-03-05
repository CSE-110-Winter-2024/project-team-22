package edu.ucsd.cse110.successorator.data.db;

import static androidx.lifecycle.Transformations.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalList;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class RoomGoalRepository implements GoalRepository {
    private final GoalDao goalDao;

    public RoomGoalRepository(GoalDao goalDao){
        this.goalDao = goalDao;
    }

    @Override
    public List<Goal> syncLists() {
        return null;
    }

    @Override
    public Subject<Goal> find (int id) {
        var entityLiveData = goalDao.findAsLiveData(id);

        var flashcardLiveData = map(entityLiveData,GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll(){
        var entityLiveData = goalDao.findAsLiveData();
        var flashcardLiveData = map(entityLiveData,entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }

    @Override
    public void save(List<Goal> goals){
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
    }

    @Override
    public void append(Goal goal){
        goalDao.append(GoalEntity.fromGoal(goal));

    }

    @Override
    public void prepend(Goal goal){
        goalDao.prepend(GoalEntity.fromGoal(goal));

    }

    @Override
    public void removeCompleted() {
        goalDao.deleteCompleted();
    }

    @Override
    public void remove(int id){
        goalDao.delete(id);

    }
}

package edu.ucsd.cse110.successorator.data.db;

import static androidx.lifecycle.Transformations.map;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class RoomGoalRepository implements GoalRepository {
    private final FlashcardDao flashcardDao;

    public RoomGoalRepository(FlashcardDao flashcardDao){
        this.flashcardDao = flashcardDao;
    }

    @Override
    public Subject<Goal> find (int id){
        var entityLiveData= flashcardDao.findAsLiveData(id);
        var flashcardLiveData = map(entityLiveData,FlashcardEntity::toFlashcard);
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll(){
        var entityLiveData= flashcardDao.findAsLiveData();
        var flashcardLiveData = map(entityLiveData,entities -> {
            return entities.stream()
                    .map(FlashcardEntity::toFlashcard)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public void save(Goal goal) {
        flashcardDao.insert(FlashcardEntity.fromFlashcard(goal));
    }

    @Override
    public void save(List<Goal> goals){
        var entities = goals.stream()
                .map(FlashcardEntity::fromFlashcard)
                .collect(Collectors.toList());
        flashcardDao.insert(entities);
    }

    @Override
    public void append(Goal goal){
        flashcardDao.append(FlashcardEntity.fromFlashcard(goal));
    }

    @Override
    public void prepend(Goal goal){
        flashcardDao.prepend(FlashcardEntity.fromFlashcard(goal));
    }

    @Override
    public Subject<Goal> findCompleted(int id) {
        return null;
    }

    @Override
    public Subject<Goal> findUncompleted(int id) {
        return null;
    }

    @Override
    public Subject<List<Goal>> findAllCompleted() {
        return null;
    }

    @Override
    public Subject<List<Goal>> findAllUncompleted() {
        return null;
    }

    @Override
    public void remove(int id){
        flashcardDao.delete(id);
    }
}

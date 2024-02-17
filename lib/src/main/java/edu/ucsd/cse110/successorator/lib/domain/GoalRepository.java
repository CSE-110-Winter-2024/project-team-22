package edu.ucsd.cse110.successorator.lib.domain;

import edu.ucsd.cse110.successorator.lib.util.Subject;

import java.util.List;


public interface GoalRepository {
    Subject<Goal> find(int id);
    Subject<List<Goal>> findAll();
    void save(Goal goal);
    void save(List<Goal> goals);
    void remove(int id);
    void append(Goal goal);
    void prepend(Goal goal);

    Subject<Goal> findCompleted(int id);
    Subject<Goal> findUncompleted(int id);
    Subject<List<Goal>> findAllCompleted();
    Subject<List<Goal>> findAllUncompleted();
}

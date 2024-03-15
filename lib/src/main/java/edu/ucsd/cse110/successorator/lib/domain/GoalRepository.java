package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.util.Subject;


public interface GoalRepository {

    //void syncLists();

    Subject<Goal> find(int id);

    Subject<List<Goal>> findAll();

    //void save(Goal goal);

    void save(List<Goal> goals);

    void remove(int id);

    void append(Goal goal);

    void prepend(Goal goal);

    void removeAllCompleted();

    //Subject<List<Goal>> findAllWeeklyGoals();

    /*String getDisplayDate (Goal goal);

    Subject<List<Goal>> findAllFrequencyGoals(String freq);

    Subject<List<Goal>> findAllContextsGoals(String context);

    Subject<List<Goal>> findAllDropdownGoalsLiveData(String choice);*/
}

package edu.ucsd.cse110.successorator.lib.domain;

import edu.ucsd.cse110.successorator.lib.util.Subject;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


public interface GoalRepository {

    //void syncLists();

    Subject<Goal> find(int id);

    Subject<List<Goal>> findAll();

    void save(Goal goal);

    void save(List<Goal> goals);

    void remove(int id);

    void append(Goal goal);

    void prepend(Goal goal);

    void removeAllCompleted();

    Subject<List<Goal>> findAllWeeklyGoals();

    LocalDate getDisplayDate (Goal goal);

    Subject<List<Goal>> findAllFrequencyGoals(String freq);

    Subject<List<Goal>> findAllContextsGoals(String context);

    Subject<List<Goal>> findAllDropdownGoals(String choice);
}

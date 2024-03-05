package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.GoalList;

import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;


public class MainViewModel extends ViewModel {
    // Domain state (true "Model" state)
    ///private final GoalRepository goalRepository;
    private final GoalRepository goalRepositoryDB;

    // UI state
    private final MutableSubject<List<Goal>> goals;
    private final MutableSubject<Goal> goal;
    private final MutableSubject<Boolean> isCompleted;


    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepositoryDB());
                    });

    public MainViewModel(GoalRepository goalRepositoryDB) {
        //this.goalRepository = goalRepository;
        this.goalRepositoryDB = goalRepositoryDB;

        // Create the observable subjects.
        this.goals = new SimpleSubject<>();
        this.goal = new SimpleSubject<>();
        this.isCompleted = new SimpleSubject<>();

        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepositoryDB.findAll().observe(goalList -> {
                if (goalList == null) return; // not ready yet, ignore

                var goalListSorted = goalList.stream()
                        .sorted(Comparator.comparingInt(Goal::sortOrder))
                        .collect(Collectors.toList());

                goalRepositoryDB.save(goalListSorted);
        });

       goalRepositoryDB.findAll().observe(goalList -> {
            if (goalList == null) return; // not ready yet, ignore

            var goalListSorted = goalList.stream()
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .collect(Collectors.toList());
            goals.setValue(goalListSorted);

            goalRepositoryDB.save(goals.getValue());
       });
    }

    public Subject<List<Goal>> getGoals() {
        return goals;
    }

    public void save(Goal goal) { goalRepositoryDB.save(goal); }

    public void append(Goal goal) {
        //List<Goal> saveGoals = goalRepositoryDB.append(goal);
        goalRepositoryDB.save(goal);
    }

    public void prepend(Goal goal) {
        goalRepositoryDB.prepend(goal);

    }

   /* public void syncLists() {
        List<Goal> saveGoals = goalRepositoryDB.syncLists();
        goalRepositoryDB.save(saveGoals);
    }*/

    public void remove (int id){
        goalRepositoryDB.remove(id);

    }

    public void toggleCompleted(Goal goal){
        // swapping and saving complete status
        Goal g = goal.withCompleted(!goal.isCompleted());
        goalRepositoryDB.remove(goal.id());
        goalRepositoryDB.save(g);
    }

    public void removeCompleted() {
        goalRepositoryDB.removeCompleted();

    }
}

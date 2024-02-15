package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class MainViewModel extends ViewModel {
    // Domain state (true "Model" state)
    private final GoalRepository goalRepository;

    // UI state
    /*
    private final MutableSubject<List<Goal>> completedGoals;
    private final MutableSubject<Boolean> someVar2;
    private final MutableSubject<String> someVar3;

     */

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepository());
                    });

    public MainViewModel(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;

        // Create the observable subjects.
        // Initialize...

        goalRepository.findAll().observe(goals -> {
            if (goals == null) return; // not ready yet, ignore

            // do something
        });
    }

    public void doSomethingWithCompletedGoals() {
        // var completedGoals = this.completedGoals.getValue();
        // if (completedGoals == null) return;

        // goalRepository.save(completedGoals);
    }

    public void append(Goal goal) {
        goalRepository.append(goal);
    }

    public void prepend(Goal goal) {
        goalRepository.prepend(goal);
    }

    public void remove(int id) {
        goalRepository.remove(id);
    }
}


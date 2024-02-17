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
    private final GoalRepository goalRepository;

    // UI state
    private final MutableSubject<List<Goal>> orderedCards;
    private final MutableSubject<Goal> topCard;
    private final MutableSubject<Boolean> isShowingFront;
    private final MutableSubject<String> displayedText;


    private final MutableSubject<List<Goal>> completedGoals;
    private final MutableSubject<List<Goal>> uncompletedGoals;


    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getFlashcardRepository());
                    });

    public MainViewModel(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;

        // Create the observable subjects.
        this.orderedCards = new SimpleSubject<>();
        this.topCard = new SimpleSubject<>();
        this.isShowingFront = new SimpleSubject<>();
        this.displayedText = new SimpleSubject<>();

        this.completedGoals = new SimpleSubject<>();
        this.uncompletedGoals = new SimpleSubject<>();

        // Initialize...
        isShowingFront.setValue(true);

        // When the list of cards changes (or is first loaded), reset the ordering.
        /*
        goalRepository.findAll().observe(cards -> {
            if (cards == null) return; // not ready yet, ignore

            var newOrderedCards = cards.stream()
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .collect(Collectors.toList());

            orderedCards.setValue(newOrderedCards);
        });
         */

        goalRepository.findAllCompleted().observe(goals -> {
            if (goals == null) return; // not ready yet, ignore

            var newCompletedGoals = goals.stream()
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .collect(Collectors.toList());

            completedGoals.setValue(newCompletedGoals);
        });

        goalRepository.findAllUncompleted().observe(goals -> {
            if (goals == null) return; // not ready yet, ignore

            var newUncompletedGoals = goals.stream()
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .collect(Collectors.toList());

            uncompletedGoals.setValue(newUncompletedGoals);
        });

        // When the ordering changes, update the top card.
        orderedCards.observe(cards -> {
            if (cards == null || cards.size() == 0) return;
            var card = cards.get(0);
            this.topCard.setValue(card);
        });

        // When the top card changes, update the displayed text and display the front side.
        topCard.observe(card -> {
            if (card == null) return;

            displayedText.setValue(card.text());
            isShowingFront.setValue(true);
        });

        // When isShowingFront changes, update the displayed text.
        isShowingFront.observe(isShowingFront -> {
            if (isShowingFront == null) return;

            var card = topCard.getValue();
            if (card == null) return;

            var text =card.text();
            displayedText.setValue(text);
        });

    }

    public Subject<String> getDisplayedText() {
        return displayedText;
    }

    public Subject<List<Goal>> getOrderedCards() {
        return orderedCards;
    }

    public Subject<List<Goal>> getCompletedGoals() { return completedGoals;}
    public Subject<List<Goal>> getUncompletedGoals() { return uncompletedGoals;}

    public void flipTopCard() {
        var isShowingFront = this.isShowingFront.getValue();
        if (isShowingFront == null) return;
        this.isShowingFront.setValue(!isShowingFront);
    }

    public void append(Goal card) {
        goalRepository.append(card);
    }

    public void prepend(Goal card) {
        goalRepository.prepend(card);
    }

    public void remove (int id){
        goalRepository.remove(id);
    }

    public Subject<Goal> getGoal(int id) {
        Subject<Goal> goalSubject = goalRepository.findUncompleted(id);
        if (goalSubject == null) {
            goalSubject = goalRepository.findCompleted(id);
        }

        if (goalSubject == null) {
            return null;
        } else {
            return goalSubject;
        }
    }
}

package edu.ucsd.cse110.successorator;


import android.app.Application;

import androidx.room.Room;

import edu.ucsd.cse110.successorator.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.data.db.SECardsDatabase;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;

public class SuccessoratorApplication extends Application {
    private InMemoryDataSource dataSource;
    private GoalRepository goalRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // For testing only
        this.dataSource = InMemoryDataSource.fromDefault();
        this.goalRepository = new SimpleGoalRepository(dataSource);

        // database implementation
        /*
        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        SECardsDatabase.class,
                        "successorator"
                )
                .allowMainThreadQueries()
                .build();
        this.goalRepository = new RoomGoalRepository(database.flashcardDao());

        // Populate the database with some initial data on the first run.
        var sharedPreferances = getSharedPreferences("successorator", MODE_PRIVATE);
        var isFirstRun = sharedPreferances.getBoolean("isFirstRun", true);

        if (isFirstRun && database.flashcardDao().count() == 0){
            goalRepository.save(InMemoryDataSource.DEFAULT_CARDS);

            sharedPreferances.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
         */
    }

    public GoalRepository getFlashcardRepository() {
        return goalRepository;
    }
}

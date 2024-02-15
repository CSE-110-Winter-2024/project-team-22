package edu.ucsd.cse110.successorator;

import android.app.Application;
import android.content.SharedPreferences;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;

public class SuccessoratorApplication extends Application {

    private InMemoryDataSource inMemoryDataSource;
    private GoalRepository goalRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        this.inMemoryDataSource = InMemoryDataSource.fromDefault();
        this.goalRepository = new SimpleGoalRepository(inMemoryDataSource);

        /*
        SECardsDatabase database = Room.databaseBuilder(
                        getApplicationContext(),
                        SECardsDatabase.class,
                        "secards-database"
                )
                .allowMainThreadQueries()
                .build();

        this.flashcardRepository = new RoomFlashcardRepository(database.flashcardDao());

        // Populate the database with some initial data on the first run.
        SharedPreferences sharedPreferences = getSharedPreferences("secards", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun && database.flashcardDao().count() == 0) {
            flashcardRepository.save(InMemoryDataSource.DEFAULT_CARDS);
        }

        sharedPreferences.edit()
                .putBoolean("isFirstRun", false)
                .apply();

         */
    }

    public GoalRepository getGoalRepository() {
        return goalRepository;
    }

}

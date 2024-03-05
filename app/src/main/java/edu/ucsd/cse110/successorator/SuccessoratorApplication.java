package edu.ucsd.cse110.successorator;


import android.app.Application;

import androidx.room.Room;

import java.util.List;

import edu.ucsd.cse110.successorator.data.db.GoalsDatabase;
import edu.ucsd.cse110.successorator.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
//import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;


public class SuccessoratorApplication extends Application {
    //private InMemoryDataSource dataSource;
    //private GoalRepository goalRepository;
    private GoalRepository goalRepositoryDB;

    @Override
    public void onCreate() {
        super.onCreate();
        // for testing purposes

        /*var database = Room.databaseBuilder(
                        getApplicationContext(),
                        GoalsDatabase.class,
                        "successorator"
                )
                .allowMainThreadQueries()
                .build();
        this.goalRepositoryDB = new RoomGoalRepository(database.goalDao());*/

        // Populate the database with some initial data on the first run.
        //var sharedPreferances = getSharedPreferences("successorator", MODE_PRIVATE);
        //var isFirstRun = sharedPreferances.getBoolean("isFirstRun", true);

       /* if (isFirstRun && database.goalDao().count() == 0){
            goalRepositoryDB.save(InMemoryDataSource.DEFAULT_CARDS);
        }*/

        /*sharedPreferances.edit()
                .putBoolean("isFirstRun", false)
                .apply();*/

//        InMemoryDataSource newIM = new InMemoryDataSource();
        var db = Room.databaseBuilder
                        (
                                getApplicationContext(),
                                GoalsDatabase.class,
                                "goals_database1"
                        )
                .allowMainThreadQueries()
                .build();

        this.goalRepositoryDB = new RoomGoalRepository(db.goalDao());
//        this.goalRepositoryDB = new SimpleGoalRepository(newIM);
        /*if (this.goalRepositoryDB.findAll().getValue() != null) {
            db.putFlashcards(this.goalRepositoryDB.findAll().getValue());
        } else {
            db.putFlashcards(List.of());
        }*/

        //this.goalRepositoryDB = db;
        //this.goalRepository = new SimpleGoalRepository(dataSource);
    }

    //public GoalRepository getGoalRepository() {
    //    return goalRepository;
    //}

    public GoalRepository getGoalRepositoryDB() {
        return goalRepositoryDB;
    }
}

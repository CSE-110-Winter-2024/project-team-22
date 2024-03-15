package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Date;
import java.util.List;

@Dao
public interface GoalDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GoalEntity goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<GoalEntity> goals);

    @Query("SELECT * FROM goals where id =:id")
    GoalEntity find(int id);

    @Query("SELECT * FROM goals ORDER by id")
    List<GoalEntity> findAll();

    @Query("SELECT * FROM goals where id =:id")
    LiveData<GoalEntity> findAsLiveData(int id);

    @Query("SELECT * FROM goals ORDER by sort_order")
    LiveData<List<GoalEntity>> findAsLiveData();

    @Query("SELECT COUNT(*) FROM goals")
    int count();

    @Query("SELECT MIN(sort_order) FROM goals")
    int getMinSortOrder();

    @Query("SELECT MAX(sort_order) FROM goals")
    int getMaxSortOrder();

    @Query("UPDATE goals SET sort_order = sort_order + :by " +
            "WHERE sort_order >= :from AND sort_order <= :to")
    void shiftSortOrders(int from, int to, int by);

    @Transaction
    default int append(GoalEntity goal){
        var maxSortOrder = getMaxSortOrder();
        var newFlashcard = new GoalEntity(
                goal.text, goal.isCompleted,
                maxSortOrder +1,"Weekly",
        new Date().toString(),goal.context);
        return Math.toIntExact(insert(newFlashcard));
    }

    @Transaction
    default int prepend(GoalEntity goal){
        shiftSortOrders(getMinSortOrder(),getMaxSortOrder(),1);
        var newFlashcard = new GoalEntity(
                goal.text, goal.isCompleted,
                getMinSortOrder() -1,"Weekly",
        new Date().toString(),goal.context);
        return Math.toIntExact(insert(newFlashcard));
    }

    @Query("DELETE FROM goals WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM goals WHERE frequency = 'Weekly'")
    LiveData<List<GoalEntity>> findAllWeeklyGoals();

    /*
        Get goals at a certain date
        Takes care of Today, Tomorrow and Pending for Dropdown
     */
    @Query("SELECT * FROM goals WHERE date=:creationdate")
    LiveData<List<GoalEntity>> findAllGoalsAtDate(String creationdate);
}






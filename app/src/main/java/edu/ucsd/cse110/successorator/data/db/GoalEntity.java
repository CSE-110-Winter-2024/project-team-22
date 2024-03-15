package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="id")
    public Integer id = null;
    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    @ColumnInfo(name = "frequency")
    public String frequency;

    @ColumnInfo(name = "date")
    public String creationDate;

    @ColumnInfo(name = "context")
    public String context;


//    private Date dateObj;
    GoalEntity(@NonNull String text, boolean isCompleted, int sortOrder,String frequency, String creationDate,String context){
        this.text = text;
        this.isCompleted = isCompleted;
        this.sortOrder = sortOrder;
        this.frequency = frequency;
        this.creationDate = creationDate;
        this.context = context;
    }

    public static GoalEntity fromGoal(@NonNull Goal goal){
        var goalFE = new GoalEntity(goal.text(), goal.isCompleted(), goal.sortOrder(), goal.getFrequency(), goal.getDate(),goal.getContext());
        goalFE.id = goal.id();
        return goalFE;
    }

    public @NonNull Goal toGoal(){
        return new Goal(id, text, isCompleted, sortOrder, frequency, creationDate,context);
    }

//    public static Date stringToDate(String dateString) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE M/d");
//        try {
//            return dateFormat.parse(dateString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return new Date(); // or a default date as per your requirement
//        }
//    }

}


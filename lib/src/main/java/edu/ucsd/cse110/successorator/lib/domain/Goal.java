package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
// import java.util.Date;
import java.util.Objects;

public class Goal implements Serializable {
    private  @Nullable Integer id;
    private final @NonNull String text;
    private final boolean isCompleted;
    private final int sortOrder;

    private String frequency;

    private LocalDate creationDate;


    public Goal(@Nullable Integer id, @NonNull String text, boolean isCompleted, int sortOrder, String frequency, LocalDate creationDate) {
        this.id = id;
        this.text = text;
        this.isCompleted = isCompleted;
        this.sortOrder = sortOrder;
        this.frequency = frequency;
        this.creationDate = creationDate;
    }

    public @Nullable Integer id() {
        return id;
    }
    public Goal withId(int id) {
        return new Goal(id, this.text, this.isCompleted, this.sortOrder, this.frequency, this.creationDate);
    }

    public @NonNull String text() {
        return text;
    }

    public boolean isCompleted(){ return isCompleted;}

    public Goal withCompleted(boolean isCompleted) {
        return new Goal(this.id, this.text, isCompleted, this.sortOrder, this.frequency, this.creationDate);
    }

    public int sortOrder() {
        return sortOrder;
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id, this.text, this.isCompleted, sortOrder, this.frequency, this.creationDate);
    }

    public void setFrequency(@NonNull String frequency) {
        this.frequency = frequency;
    }

    public void setDate (LocalDate current){
        this.creationDate = current;
    }

    public @NonNull String getFrequency() {
        return frequency;
    }
    public @NonNull LocalDate getDate() {
        return this.creationDate;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return sortOrder == goal.sortOrder &&
                Objects.equals(id, goal.id) &&
                Objects.equals(isCompleted, goal.isCompleted) &&
                Objects.equals(text, goal.text) &&
                Objects.equals(frequency,goal.frequency)&&
                Objects.equals(creationDate,goal.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, isCompleted, sortOrder,frequency,creationDate);
    }

    @Override
    public String toString() {
        return "\nGoal{" +
                "id=" + id +
                "\n, text='" + text + '\'' +
                "\n, frequency='" + frequency + '\'' +
                "\n, creationDate=" + creationDate +
                "\n, isCompleted=" + isCompleted +
                "\n, sortOrder=" + sortOrder +
                '}';
    }
}

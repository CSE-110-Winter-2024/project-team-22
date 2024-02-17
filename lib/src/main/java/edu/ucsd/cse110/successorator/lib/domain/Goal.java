package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Goal implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String text;
    private boolean goalStatus;
    private final int sortOrder;

    public Goal(@Nullable Integer id, @NonNull String text, boolean goalStatus, int sortOrder) {
        this.id = id;
        this.text = text;
        this.goalStatus = goalStatus;
        this.sortOrder = sortOrder;
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String text() {
        return text;
    }

    public boolean goalStatus(){ return goalStatus;}
    public void setGoalStatus(boolean status){ this.goalStatus = status;}


    public int sortOrder() {
        return sortOrder;
    }

    public Goal withId(int id) {
        return new Goal(id, this.text, this.goalStatus, this.sortOrder);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id, this.text, this.goalStatus, sortOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return sortOrder == goal.sortOrder &&
                Objects.equals(id, goal.id) &&
                Objects.equals(goalStatus, goal.goalStatus) &&
                Objects.equals(text, goal.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, goalStatus, sortOrder);
    }
}

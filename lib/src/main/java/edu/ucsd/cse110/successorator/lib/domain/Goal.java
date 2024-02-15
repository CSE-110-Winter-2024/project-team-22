package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.Nullable;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Just a dummy domain model that does nothing in particular. Delete me.
 */
public class Goal {
    private final @Nullable Integer id;
    private final @Nullable String text;
    private final @NotNull int sortOrder;

   private final @NotNull boolean isCompleted;

    public Goal (@Nullable Integer id, @Nullable String text, @NotNull int sortOrder) {
        this.id = id;
        this.text = text;
        this.sortOrder = sortOrder;
        this.isCompleted = false;
    }

    @Nullable
    public Integer id() {
        return id;
    }

    @Nullable
    public String text() {
        return text;
    }

    @NotNull
    public int sortOrder() { return sortOrder; }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public Goal withId(int id) {
        return new Goal(id, this.text, this.sortOrder);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id, this.text, sortOrder);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return sortOrder == goal.sortOrder && isCompleted == goal.isCompleted && Objects.equals(id, goal.id) && Objects.equals(text, goal.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, sortOrder, isCompleted);
    }
}
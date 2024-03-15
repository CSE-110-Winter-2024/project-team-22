//package edu.ucsd.cse110.successorator.lib.domain;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class GoalList {
//    private List<Goal> uncompletedGoals;
//    private List<Goal> completedGoals;
//    private List<Goal> goals;
//    private int nextId;
//
//    public GoalList() {
//        uncompletedGoals = new ArrayList<>();
//        completedGoals = new ArrayList<>();
//        goals = new ArrayList<>();
//        nextId = 5;
//    }
//
//    public void setGoals(List<Goal> goals) {
//        this.goals = goals;
//        this.nextId = goals.size() + 1;
//    }
//
//    public List<Goal> getGoals() {
//        return this.goals;
//    }
//
//    public void addGoal(Goal goal) {
//        nextId += 1;
//        if (goal.isCompleted()) {
//            completedGoals.add(goal);
//        } else {
//            uncompletedGoals.add(goal);
//        }
//
//    }
//
//    public List<Goal> fillGoals(List<Goal> goalsData) {
//        this.goals = goalsData;
//
//        // NOTE: the data coming in may not be in correct order.
//        // rn we are simply ...
//        nextId = 5;
//        for (int i = 0; i < goalsData.size(); i++) {
//            nextId += 1;
//            // separate into two lists of completed and uncompleted.
//            if (goalsData.get(i).isCompleted()) {
//                completedGoals.add(goalsData.get(i));
//            } else {
//                uncompletedGoals.add(goalsData.get(i));
//            }
//        }
//
//        Collections.sort(completedGoals, new Comparator<Goal>() {
//            @Override
//            public int compare(Goal g1, Goal g2) {
//                return Integer.compare(g1.sortOrder(), g2.sortOrder());
//            }
//        });
//
//        Collections.sort(uncompletedGoals, new Comparator<Goal>() {
//            @Override
//            public int compare(Goal g1, Goal g2) {
//                return Integer.compare(g1.sortOrder(), g2.sortOrder());
//            }
//        });
//
//        /*
//        // Test sort orders
//        System.out.println("");
//        // See what's happening
//        System.out.println("Goals uncompleted:");
//        uncompletedGoals.forEach(goal -> {
//            System.out.print(goal.toString() + ", ");
//        });
//
//        System.out.println("");
//
//        System.out.println("Goals completed:");
//        completedGoals.forEach(goal -> {
//            System.out.print(goal.toString() + ", ");
//        });
//
//        System.out.println("");
//         */
//
//        // Resets sortOrder to be correct
//        // and create two new lists in the process
//        int order = 0;
//        Goal newGoal;
//        List<Goal> newUncompletedGoals = new ArrayList<Goal>();
//        for (int i = 0; i < uncompletedGoals.size(); i++) {
//            newGoal = uncompletedGoals.get(i).withSortOrder(order);
//            newUncompletedGoals.add(newGoal);
//            order++;
//        }
//
//
//        List<Goal> newCompletedGoals = new ArrayList<Goal>();
//        // reset sortOrders to be correct for completedGOals
//        for (int i = 0; i < completedGoals.size(); i++) {
//            newGoal = completedGoals.get(i).withSortOrder(order);
//            newCompletedGoals.add(newGoal);
//            order++;
//        }
//
//        // simply append to a singular list
//        // this list can be sent back to update the Data
//        this.uncompletedGoals = newUncompletedGoals;
//        this.completedGoals = newCompletedGoals;
//
//        // simply append the two lists to create the correct goals
//        this.goals = new ArrayList<>(uncompletedGoals);
//        goals.addAll(completedGoals);
//
//        return this.goals;
//    }
//
//    // gets the correct sortOrder
//    // for the goal being added
//    // to a different list
//    // depending on the goals.isCompleted() flag
//    public int getGoalSortOrder(Goal goal, boolean append) {
//        boolean done = false;
//        int sortOrder = -1;
//
//        if (append) {
//            if (!goal.isCompleted() && completedGoals.size() == 0 && goals.size() != 0) {
//                sortOrder = uncompletedGoals.get(uncompletedGoals.size()-1).sortOrder() + 1;
//            } else if (uncompletedGoals.size() != 0){
//                sortOrder = goals.get(0).sortOrder();
//            }
//        } else {
//            for (int i = 0; i < goals.size(); i++) {
//                if (goal.isCompleted() && goals.get(i).isCompleted() && !done) {
//                    sortOrder = goals.get(i).sortOrder();
//                    done = true;
//                } else if (!goal.isCompleted() && !goals.get(i).isCompleted() && !done) {
//                    sortOrder = goals.get(i).sortOrder();
//                    done = true;
//                }
//            }
//        }
//
//        if ((goals.size() == 0
//                || completedGoals.size() == 0
//                || uncompletedGoals.size() == 0)
//                && sortOrder == -1) {
//            sortOrder = 0;
//        }
//
//        return sortOrder;
//    }
//
//    public Goal preInsert(Goal goal) {
//        var id = goal.id();
//        if (id == null) {
//            // If the card has no id, give it one.
//            goal = goal.withId(nextId++);
//        }
//
//        else if (id > nextId) {
//            // If the card has an id, update nextId if necessary to avoid giving out the same
//            // one. This is important for when we pre-load cards like in fromDefault().
//            nextId = id + 1;
//        }
//
//        return goal;
//    }
//
//    @Override
//    public String toString() {
//        var unComplte = "";
//        var complete = "";
//        for (Goal g : uncompletedGoals){
//
//            unComplte += g + " ";
//        }
//        for (Goal g : completedGoals){
//
//            complete += g + " ";
//        }
//
//        return unComplte + "\n" + complete;
//    }
//}

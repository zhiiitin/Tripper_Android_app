package com.example.tripper_android_app.goal;

import java.io.Serializable;

public class Goal implements Serializable {

    private static final long serialVersionUID = 1L;
    private int goalId;
    private String goalName;
    private int goalCond1;
    private int goalCond2;
    private int goalCond3;

    // 成就主頁使用
    public Goal(int goalId, int goalCond1, int goalCond2, int goalCond3) {
        super();
        this.goalId = goalId;
        this.goalCond1 = goalCond1;
        this.goalCond2 = goalCond2;
        this.goalCond3 = goalCond3;
    }

    // 成就建構子
    public Goal(int goalId, String goalName, int goalCond1, int goalCond2, int goalCond3) {
        super();
        this.goalId = goalId;
        this.goalName = goalName;
        this.goalCond1 = goalCond1;
        this.goalCond2 = goalCond2;
        this.goalCond3 = goalCond3;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public int getGoalCond1() {
        return goalCond1;
    }

    public void setGoalCond1(int goalCond1) {
        this.goalCond1 = goalCond1;
    }

    public int getGoalCond2() {
        return goalCond2;
    }

    public void setGoalCond2(int goalCond2) {
        this.goalCond2 = goalCond2;
    }

    public int getGoalCond3() {
        return goalCond3;
    }

    public void setGoalCond3(int goalCond3) {
        this.goalCond3 = goalCond3;
    }

}
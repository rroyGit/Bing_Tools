package com.rroycsdev.bingtools;



public class ListItem {

    private String mealB;
    private String mealL;
    private String mealA;
    private String mealD;

    private int resInt;


    public ListItem(String mealB, String mealL, String mealA, String mealD,int resInt) {
        this.mealB = mealB;
        this.mealL = mealL;
        this.mealA = mealA;
        this.mealD = mealD;
        this.resInt = resInt;
    }

    public String getMealB() {
        return mealB;
    }

    public String getMealL() {
        return mealL;
    }

    public String getMealA() {
        return mealA;
    }

    public String getMealD() {
        return mealD;
    }

    public int getResInt() {return resInt;}


}

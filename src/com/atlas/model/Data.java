package com.atlas.model;

import java.io.Serializable;

/**
 * Real-time data model from online server
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class Data implements Serializable {
   
    private double x;
    private double y;
    private String name;
    
    /**
     * Contructor
     * @param x x-coordinate
     * @param y y-Coordinate
     * @param name Name of the data/point
     */
    public Data(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder dataList = new StringBuilder();

        dataList.append("x: ").append(getX()).append(", y: ");
        dataList.append(getY()).append(", Name: ");
        dataList.append(getName()).append(". ");
        dataList.append("\n");
        return dataList.toString();
    }
}

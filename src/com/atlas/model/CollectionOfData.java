package com.atlas.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Data collection of one read-in from server, Populate in one line chart
 *
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class CollectionOfData {

    private ArrayList<Data> data;

    /**
     * Default constructor
     */
    public CollectionOfData() {
    }

    /**
     * Constructor
     *
     * @param data
     */
    public CollectionOfData(ArrayList<Data> data) {
        this.data = data;
    }

    /**
     * Get the data(s) in the collection
     *
     * @return
     */
    public ArrayList<Data> getData() {
        return (ArrayList<Data>) data.clone();
    }

    /**
     * Set data(s) to the collection
     *
     * @param data
     */
    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    /**
     * Add data/point to the collection
     *
     * @param data
     */
    public void addData(Data data) {
        this.data.add(data);
    }

    /**
     * Get the size of the collection
     *
     * @return The size
     */
    public int getSize() {
        return data.size();
    }

    /**
     * Determine if the collection is empty
     *
     * @return
     */
    public boolean isEmpty() {
        return data == null;
    }

    /**
     * Serialize the collection to file
     *
     * @param filename The file name
     * @throws IOException
     */
    public void serializeToFile(File filename) throws IOException {

        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(
                    new FileOutputStream(filename));
            out.writeObject(data);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * De-serialize data from a fine
     *
     * @param filename The file name
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public void deSerializeFromFile(File filename) throws IOException,
            ClassNotFoundException {

        ObjectInputStream in = null;

        try {
            in = new ObjectInputStream(new FileInputStream(filename));
            System.out.println("Found file: " + filename);
            // readObject returns a reference of type Object, 
            // hence the down-cast
            data = (ArrayList<Data>) in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Get data/points by their same x-coordinate
     *
     * @param inputValue The input x-coordinate
     * @return An array list of data/points of same x-coordinate
     */
    public ArrayList<Data> getDataByX(String inputValue) {
        ArrayList<Data> dataWithX = new ArrayList<>();
        data.stream().filter((temp) -> (temp.getX() == Integer.valueOf(inputValue))).forEachOrdered((temp) -> {
            dataWithX.add(temp);
        });
        return dataWithX;
    }

    /**
     * Get data/points by their same y-coordinate
     *
     * @param inputValue The input y-coordinate
     * @return An array list of data/points of same y-coordinate
     */
    public ArrayList<Data> getDataByY(String inputValue) {
        ArrayList<Data> dataWithY = new ArrayList<>();
        data.stream().filter((temp) -> (temp.getY() == Integer.valueOf(inputValue))).forEachOrdered((temp) -> {
            dataWithY.add(temp);
        });
        return dataWithY;
    }

    /**
     * Get data/points by their same name
     *
     * @param inputValue The input name
     * @return An array list of data/points of same name
     */
    public ArrayList<Data> getDataByName(String inputValue) {
        ArrayList<Data> dataWithName = new ArrayList<>();
        data.stream().filter((temp) -> (temp.getName().contains(inputValue))).forEachOrdered((temp) -> {
            dataWithName.add(temp);
        });
        return dataWithName;
    }

    @Override
    public String toString() {
        StringBuilder dataList = new StringBuilder();

        for (Data point : data) {
            dataList.append(point);
        }

        return dataList.toString();
    }
}

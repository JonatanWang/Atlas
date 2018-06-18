package com.atlas.util;

import com.atlas.model.Data;
import com.atlas.model.CollectionOfData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a URL for the desired page,
 * Read all the text returned by the server,
 * Parse points to data collection
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class DataLoader {

    /**
     * Load data from the online server
     * @return A collection of data after one loading
     */
    public CollectionOfData loadData() {

        CollectionOfData dataCollection = new CollectionOfData();
        List<Data> points = new ArrayList<>();
        try {
            // Create a URL for the desired page
            URL url = new URL("http://daily.digpro.se/bios/servlet/bios.servlets.web.RecruitmentTestServlet");

            try ( // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.ISO_8859_1))) {
                String str;
                while ((str = in.readLine()) != null) {
                    if (str.charAt(0) != '#') {
                        System.out.println(str);
                        String[] array = str.split(", ");
                        int x = Integer.valueOf(array[0]);
                        int y = Integer.valueOf(array[1]);
                        String name = array[2];
                        Data point = new Data(x, y, name);
                        points.add(point);
                    }
                }
                if (!points.isEmpty()) {
                    dataCollection.setData((ArrayList<Data>) points);
                    System.out.println("\nDataCollection: \n" + dataCollection.toString());
                }
                in.close();
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return dataCollection;
    }
}

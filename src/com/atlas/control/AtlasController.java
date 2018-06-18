package com.atlas.control;

import com.atlas.model.Data;
import com.atlas.model.CollectionOfData;
import com.atlas.util.DataLoader;
import com.atlas.util.Clock;
import com.atlas.view.AtlasView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for load, auto-load, stop-auto-load, save&load file, search by x-,
 * y-, coordinates and point name, refresh/restore to line chart after search
 *
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class AtlasController {

    private ArrayList<CollectionOfData> collections;
    private Stage primaryStage;
    private AtlasView atlas;
    private ObservableList<Data> data;
    private final DataLoader dataLoader;
    private Timer[] timers;
    private final int NUM_TIMER = 1000;
    private final int AUTO_LOAD_INTERVAL = 30000;

    /**
     * Constructor
     *
     * @param collections The collections of data/points of loadings
     * @param primaryStage
     * @param atlasView
     */
    public AtlasController(ArrayList<CollectionOfData> collections,
            Stage primaryStage, AtlasView atlasView) {
        this.dataLoader = new DataLoader();
        this.collections = collections;
        this.primaryStage = primaryStage;
        this.atlas = atlasView;
        this.timers = new Timer[NUM_TIMER];
    }

    /**
     * Get the timers
     *
     * @return
     */
    public Timer[] getTimers() {
        return timers;
    }

    /**
     * Reload data/points to populate line chart
     *
     * @param tab The current tab
     * @param pane The pane
     * @param path The path
     * @param image The image
     * @param transition The transition
     */
    public void reloadData(int tab, BorderPane pane, Path path,
            ImageView image, PathTransition transition) {

        CollectionOfData dataCollection = dataLoader.loadData();
        if (!dataCollection.isEmpty()) {
            pane.getChildren().addAll(path, image);
            transition.play();
            LineChart lineChart = atlas.getCharts().get(tab);
            Clock timer = new Clock();
            lineChart.setTitle(timer.currentTime());
            XYChart.Series<Number, Number> series = new XYChart.Series();
            series.setName("Realtime Data");
            ObservableList<XYChart.Series<Number, Number>> list
                    = FXCollections.observableArrayList();
            list = getChartData(dataCollection, series);
            lineChart.getData().setAll(list);
            /**
             * Set tooltip
             */
            for (XYChart.Series<Number, Number> s : list) {
                for (XYChart.Data<Number, Number> d : s.getData()) {
                    Tooltip.install(d.getNode(),
                            new Tooltip("X: " + d.getXValue().toString() + "\n"
                                    + "Y : " + d.getYValue() + "\n"
                                    + "Name: " + d.getExtraValue().toString()));
                    //Adding class on hover
                    d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                    //Removing class on exit
                    d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                }
            }
        } else {
            atlas.showInformation("No data loaded, controll network status");
        }
    }

    /**
     * Get the data collection and add to series of line chart
     *
     * @param dataCollection The loaded data collection
     * @param series The current series
     * @return An observable list of series
     */
    private ObservableList<XYChart.Series<Number, Number>> getChartData(CollectionOfData dataCollection,
            XYChart.Series series) {

        ObservableList<XYChart.Series<Number, Number>> list
                = FXCollections.observableArrayList();

        for (int i = 0; i < dataCollection.getSize(); i++) {
            series.getData().add(new XYChart.Data<>(dataCollection.getData().get(i).getX(),
                    dataCollection.getData().get(i).getY(),
                    dataCollection.getData().get(i).getName()));
        }
        list.add(series);
        return list;
    }

    /**
     * Auto load data and re-draw line chart
     *
     * @param tab The current tab
     * @param pane The pane
     * @param path The path
     * @param image The image
     * @param transition The transition
     */
    public void autoReload(int tab, BorderPane pane, Path path, ImageView image, PathTransition transition) {

        /**
         * Determine if any timer remains
         */
        int numOfFreeTimers = 0;
        for (int i = 0; i < NUM_TIMER; i++) {
            if (timers[i] == null) {
                numOfFreeTimers++;
            }
        }
        if (numOfFreeTimers < 1) {
            atlas.showInformation("Timer used up");
            return;
        }
        Timer timer = new Timer();
        timers[tab] = timer;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    reloadData(tab, pane, path, image, transition);
                });
            }
        }, 0, AUTO_LOAD_INTERVAL);
    }

    /**
     * Stop auto loading
     *
     * @param tab The current tab
     */
    public void stopAutoLoad(int tab) {
        if (timers[tab] != null) {
            timers[tab].cancel();
            timers[tab].purge();
            timers[tab] = null;
        }
    }

    /**
     * Create a new map in the tab view
     *
     * @param tabView
     */
    public void createMap(TabPane tabView) {
        collections.add(new CollectionOfData());
        tabView.getTabs().add(atlas.createTab());
    }

    /**
     * Update line chart after loading a file
     *
     * @param collection The data collection from the file
     */
    public void updateChartView(int collection) {

        if (atlas.getSelectedTab() < 0) {
            throw new ArrayIndexOutOfBoundsException("No data collections created");
        }
        if (collections.get(collection).getSize() == 0) {
            atlas.showInformation("No result.");
        } else {
            data = FXCollections.observableArrayList(collections.get(collection).getData());
            atlas.getCharts().get(collection).getData().setAll(data);
        }
    }

    /**
     * Update the line chart after search Can eventually change the line chart
     * to scatter chart, or some way in the line chart to highlight the searched
     * points
     *
     * @param temp The search result: an array list of data/points
     */
    public void updateSearchView(CollectionOfData temp) {
        if (temp.getSize() == 0) {
            atlas.showInformation("No result");
        } else {
            data = FXCollections.observableArrayList(temp.getData());
            atlas.getCharts().get(atlas.getSelectedTab()).getData().setAll(data);
        }
    }

    /**
     * Try to save a tab of line chart
     *
     * @param exitSave The save dialog
     * @param fileChooser The file chooser
     * @param tabs The tabs
     */
    public void trySave(Dialog exitSave, FileChooser fileChooser, ArrayList<Tab> tabs) {

        Optional result = exitSave.showAndWait();
        int temp = (int) result.get();

        switch (temp) {
            case 0:
                break;
            case 1:
                try {
                    saveAs(fileChooser, tabs);
                    primaryStage.close();
                } catch (NullPointerException ex) {
                    atlas.showInformation(ex.getMessage());
                    break;
                }
                break;
            case 2:
                primaryStage.close();
                break;
            default:
                break;
        }
    }

    /**
     * Save the tab of line chart with a name
     *
     * @param fileChooser The file chooser
     * @param selected The selected tab index
     */
    public void saveTabAs(FileChooser fileChooser, int selected) {
        fileChooser.setTitle("Save file");
        File name = fileChooser.showSaveDialog(primaryStage);
        if (name == null) {
            throw new NullPointerException("Save canceled");
        }
        try {
            collections.get(selected).serializeToFile(name);
        } catch (IOException ex) {
            atlas.showInformation(ex.getMessage());
            primaryStage.close();
        }
    }

    /**
     * Save tabs one by one
     *
     * @param fileChooser The file chooser
     * @param tabs The tabs
     */
    public void saveAs(FileChooser fileChooser, ArrayList<Tab> tabs) {
        for (int i = 0; i < tabs.size(); i++) {
            fileChooser.setTitle("Save file map: " + (i + 1));
            if (collections.get(i).getSize() > 0) {
                File name = fileChooser.showSaveDialog(primaryStage);
                if (name == null) {
                    throw new NullPointerException("Save canceled");
                }
                try {
                    collections.get(i).serializeToFile(name);
                } catch (IOException ex) {
                    atlas.showInformation(ex.getMessage());
                    primaryStage.close();
                }
            }
        }
    }

    /**
     * Load a file
     *
     * @param fileChooser The file chooser
     * @param tabView The tab view
     */
    public void loadFile(FileChooser fileChooser, TabPane tabView) {
        fileChooser.setTitle("Open file");
        try {
            File name = fileChooser.showOpenDialog(primaryStage);
            if (name != null) {
                createMap(tabView);
                collections.get(atlas.getSelectedTab()).deSerializeFromFile(name);
                updateChartView(atlas.getSelectedTab());
            } else {
                atlas.showInformation("Canceled file load");
            }
        } catch (IOException | ClassNotFoundException ex) {
            atlas.showInformation(ex.getMessage());
            primaryStage.close();
        }
    }

    /**
     * Close the window
     *
     * @param exitSave The save dialog
     * @param fileChooser The file chooser
     * @param tabs The tabs
     */
    public void closeWindow(Dialog exitSave, FileChooser fileChooser, ArrayList<Tab> tabs) {
        trySave(exitSave, fileChooser, tabs);
    }

    /**
     * Try to save a tab
     *
     * @param fileChooser The file chooser
     * @param exitSave The save dialog
     */
    public void trySaveTab(FileChooser fileChooser, Dialog exitSave) {

        int selected = atlas.getSelectedTab();
        Optional result = exitSave.showAndWait();
        int temp = (int) result.get();

        switch (temp) {

            // cancel
            case 0:
                break;
            // save    
            case 1:
                try {
                    saveTabAs(fileChooser, selected);
                    try {
                        atlas.removeTab(selected);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        atlas.showInformation("Error, cant close tab " + ex.getMessage());
                    }
                } catch (NullPointerException ex) {
                    atlas.showInformation(ex.getMessage());
                    break;
                }
                break;
            // close without save    
            case 2:
                try {
                    atlas.removeTab(selected);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    atlas.showInformation("Error, cant close tab");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Search data/points
     *
     * @param radioGroup The radio toggle group
     * @param xRadio The x radio button
     * @param yRadio The y radio button
     * @param nameRadio The name radio button
     * @param searchField The search field for input
     */
    public void searchData(ToggleGroup radioGroup, RadioButton xRadio, RadioButton yRadio, RadioButton nameRadio, TextField searchField) {
        String inputValue = searchField.getText();

        if (inputValue == null || inputValue.isEmpty()) {
            atlas.showInformation("Invalid search, try again");
        } else {
            CollectionOfData temp = new CollectionOfData();

            if (radioGroup.getSelectedToggle() == xRadio) {

                ArrayList<Data> dataWithX = collections.get(atlas.getSelectedTab()).
                        getDataByX(inputValue);

                dataWithX.forEach((d) -> {
                    temp.addData(d);
                });
            } else if (radioGroup.getSelectedToggle() == yRadio) {
                ArrayList<Data> dataWithY = collections.get(atlas.getSelectedTab()).
                        getDataByY(inputValue);

                dataWithY.forEach((d) -> {
                    temp.addData(d);
                });
            } else if (radioGroup.getSelectedToggle() == nameRadio) {
                ArrayList<Data> dataWithName = collections.get(atlas.getSelectedTab()).
                        getDataByName(inputValue);

                dataWithName.forEach((d) -> {
                    temp.addData(d);
                });
            }
            updateSearchView(temp);
        }
    }

    /**
     * Method to refresh the tab after search Restore to the previous line chart
     */
    public void refreshChart() {
        try {
            System.out.println("selected Index " + atlas.getSelectedTab());
            updateChartView(atlas.getSelectedTab());
        } catch (ArrayIndexOutOfBoundsException ex) {
            atlas.showInformation(ex.getMessage());
        }
    }
}

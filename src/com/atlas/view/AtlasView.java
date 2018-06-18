package com.atlas.view;

import com.atlas.control.AtlasController;
import javafx.scene.layout.BorderPane;
import com.atlas.model.CollectionOfData;
import java.util.ArrayList;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * View of the atlas program
 *
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class AtlasView extends VBox {

    // Member variables
    private Stage primaryStage;
    private ConnectAnimation connectStatus;
    private ArrayList<CollectionOfData> collections;
    private Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private AtlasController controller;
    private FileChooser fileChooser;
    private BorderPane pane;
    private Dialog exitSave;
    private ToggleGroup radioGroup;
    private TextField searchField;
    private TabPane tabView;
    private ArrayList<LineChart> charts;
    private ArrayList<Tab> tabs;
    private PathTransition transition;
    private ImageView image;
    private Path path;

    /**
     * Constructor
     *
     * @param primaryStage
     */
    public AtlasView(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.collections = new ArrayList<>();
        this.charts = new ArrayList<>();
        this.tabs = new ArrayList<>();
        this.controller = new AtlasController(collections, primaryStage, this);
        initView();
        initAnimation();
    }

    /**
     * ***********************************************************************
     * Getters
     * ***********************************************************************
     */
    /**
     * Get the line charts of data
     *
     * @return
     */
    public ArrayList<LineChart> getCharts() {
        return this.charts;
    }

    /**
     * Get the active tab
     *
     * @return
     */
    public int getSelectedTab() {
        return tabView.getSelectionModel().getSelectedIndex();
    }

    /**
     * Create a tab
     *
     * @return A new tab
     */
    public Tab createTab() {
        Tab tab = new Tab();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tab.setText("Map " + collections.size());
                tab.setContent(initChartView());
                tab.setOnCloseRequest(new CloseTabHandler());
                tabs.add(tab);
                tabView.getSelectionModel().select(tab);
            }
        });
        return tab;
    }

    /**
     * Remove a tab from the window
     *
     * @param tab
     */
    public void removeTab(int tab) {
        if (tab < 0) {
            throw new ArrayIndexOutOfBoundsException("Tab is out of bounds");
        } else {
            collections.remove(tab);
            charts.remove(tab);
            tabs.remove(tab);
            tabView.getTabs().remove(tab);
        }
    }

    /**
     * ***********************************************************************
     * Initializers
     * ***********************************************************************
     */
    /**
     * Initialize the first view with an empty line chart
     */
    private void initView() {
        MenuBar menuBar = new MenuBar();
        menuBar = initMenuView(menuBar);
        pane = new BorderPane();
        tabView = new TabPane();
        pane.setCenter(tabView);
        pane.setBottom(initButtonView());
        pane.setTop(initSearchBar());
        pane.prefHeightProperty().bind(this.heightProperty());
        pane.prefWidthProperty().bind(this.widthProperty());
        initFileChooser();
        initSaveDialog();
        this.getChildren().addAll(menuBar, pane);
        controller.createMap(tabView);
        primaryStage.setOnCloseRequest(new CloseHandler());
    }

    /**
     * Initialize the animation for server connection "Flying server" when data
     * read in.
     */
    private void initAnimation() {
        this.connectStatus = new ConnectAnimation(this, "server.png");
        this.path = connectStatus.createPath();
        this.image = connectStatus.createImage(80, 80);
        this.transition = connectStatus.createTransition(path, image);
        this.transition.setOnFinished(new AtlasView.AnimationFinishedHandler());
    }

    /**
     * Initialize the menu bar
     *
     * @param menuBar
     * @return A menu bar
     */
    private MenuBar initMenuView(MenuBar menuBar) {

        Menu menu = new Menu("Menu");
        MenuItem createItem = new MenuItem("New Map");
        MenuItem reloadItem = new MenuItem("Reload");
        MenuItem autoItem = new MenuItem("Auto");
        MenuItem stopItem = new MenuItem("Stop");
        MenuItem refreshItem = new MenuItem("Refresh");
        MenuItem exitItem = new MenuItem("Exit");

        createItem.setOnAction(new CreateMapHandler());
        reloadItem.setOnAction(new ReloadHandler());
        autoItem.setOnAction(new AutoLoadHandler());
        stopItem.setOnAction(new StopHandler());
        refreshItem.setOnAction(new RefreshHandler());
        exitItem.setOnAction(new ExitHandler());

        menu.getItems().addAll(createItem, reloadItem, autoItem,
                stopItem, refreshItem, exitItem);

        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save as");
        MenuItem loadItem = new MenuItem("Load file");

        saveItem.setOnAction(new SaveFileHandler());
        loadItem.setOnAction(new LoadFileHandler());

        fileMenu.getItems().addAll(saveItem, loadItem);

        Menu helpMenu = new Menu("Help");
        MenuItem versionItem = new MenuItem("Version");
        MenuItem aboutItem = new MenuItem("About");

        versionItem.setOnAction(new VersionHandler());
        aboutItem.setOnAction(new AboutHandler());

        helpMenu.getItems().addAll(versionItem, aboutItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, fileMenu, helpMenu);

        return menuBar;
    }

    /**
     * Initialize an empty line chart for the first view
     *
     * @return A line chart without data/points
     */
    public LineChart initChartView() {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X - Axis");
        yAxis.setLabel("Y - Axis");
        LineChart<Number, Number> lineChart
                = new LineChart<>(xAxis, yAxis);
        lineChart.getStylesheets().add(getClass()
                .getResource("/resources/chart.css").toExternalForm());
        charts.add(lineChart);
        return lineChart;
    }

    /**
     * Initialize the buttons at the bottom of the window
     *
     * @return
     */
    private HBox initButtonView() {

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        Button reloadButton = new Button("Reload");
        Button autoButton = new Button("Auto");
        Button stopButton = new Button("Stop");
        Button refreshButton = new Button("Refresh");

        buttons.getChildren().addAll(reloadButton, autoButton, stopButton, refreshButton);

        reloadButton.setOnAction(new ReloadHandler());
        autoButton.setOnAction(new AutoLoadHandler());
        stopButton.setOnAction(new StopHandler());
        refreshButton.setOnAction(new RefreshHandler());
        return buttons;
    }

    /**
     * Initialize a search bar
     *
     * @return A search bar in form of HBox
     */
    private HBox initSearchBar() {

        HBox boxSearch = new HBox(20);
        boxSearch.setAlignment(Pos.CENTER);
        radioGroup = new ToggleGroup();

        RadioButton xRadio = new RadioButton("X-coordinate");
        xRadio.setToggleGroup(radioGroup);
        RadioButton yRadio = new RadioButton("Y-coordinate");
        yRadio.setToggleGroup(radioGroup);
        RadioButton nameRadio = new RadioButton("Name");
        nameRadio.setToggleGroup(radioGroup);
        Button searchButton = new Button("Search");

        searchField = new TextField();
        searchField.setPromptText("Enter value here");
        searchField.setMinWidth(200);

        boxSearch.getChildren().addAll(xRadio, yRadio, nameRadio, searchField, searchButton);
        searchButton.setOnAction(new SearchHandler(xRadio, yRadio, nameRadio));

        return boxSearch;
    }

    /**
     * Initialize file chooser
     */
    private void initFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "MAP", "*.map"));
    }

    /**
     * Initialize save dialog
     */
    private void initSaveDialog() {
        exitSave = new Dialog();
        exitSave.setContentText("Save before closing?");
        exitSave.setTitle("Close and save");

        ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        exitSave.getDialogPane().getButtonTypes().addAll(yesButton, noButton, cancelButton);
        exitSave.setResultConverter(new Callback<ButtonType, Integer>() {

            @Override
            public Integer call(ButtonType b) {
                if (b == yesButton) {
                    return 1;
                } else if (b == noButton) {
                    return 2;
                } else {
                    return 0;
                }
            }
        });
    }
    
    /**
     * An alert to show message for user interaction
     *
     * @param information The message to user
     */
    public void showInformation(String information) {

        alert.setHeaderText("Note!");
        alert.setTitle("Information");
        alert.setContentText(information);
        alert.show();
    }

    /**
     * ***********************************************************************
     * Handlers
     * ***********************************************************************
     */
    /**
     * Handler for reloading data manually
     */
    private class ReloadHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            controller.reloadData(getSelectedTab(), pane, path, image, transition);
        }
    }

    /**
     * Handler for automatically reloading data by a certain frequency
     */
    private class AutoLoadHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent t) {
            controller.autoReload(getSelectedTab(), pane, path, image, transition);
        }
    }

    /**
     * Handler for stopping auto-reloading
     */
    private class StopHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent t) {
            controller.stopAutoLoad(getSelectedTab());
        }
    }

    /**
     * Handler for creating a new map
     */
    private class CreateMapHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            controller.createMap(tabView);
        }
    }

    /**
     * Handler for closing the program
     */
    private class CloseHandler implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent event) {
            event.consume();
            controller.closeWindow(exitSave, fileChooser, tabs);
        }
    }

    /**
     * Handler for exiting the program from menu->exit
     */
    private class ExitHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            event.consume();
            controller.closeWindow(exitSave, fileChooser, tabs);
        }
    }

    /**
     * Handler for closing a single tab
     */
    private class CloseTabHandler implements EventHandler<Event> {

        @Override
        public void handle(Event event) {
            event.consume();
            controller.trySaveTab(fileChooser, exitSave);
        }
    }

    /**
     * Handler for search function
     */
    private class SearchHandler implements EventHandler<ActionEvent> {

        private RadioButton xRadio;
        private RadioButton yRadio;
        private RadioButton nameRadio;

        public SearchHandler(RadioButton xRadio, RadioButton yRadio,
                RadioButton nameRadio) {
            this.xRadio = xRadio;
            this.yRadio = yRadio;
            this.nameRadio = nameRadio;
        }

        @Override
        public void handle(ActionEvent event) {
            controller.searchData(radioGroup, xRadio, yRadio, nameRadio,
                    searchField);
        }
    }

    /**
     * Handler for refreshing searched chart(Scatter chart maybe) to previous
     * line chart after search
     */
    private class RefreshHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            controller.refreshChart();
        }
    }

    /**
     * Handler for saving a line chart in local disk
     */
    private class SaveFileHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            try {
                controller.saveAs(fileChooser, tabs);
            } catch (NullPointerException ex) {
                showInformation(ex.getMessage());
            }
        }
    }

    /**
     * Handler for loading a line chart from previous saved file on local disk
     */
    private class LoadFileHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            controller.loadFile(fileChooser, tabView);
        }
    }

    /**
     * Handler for finishing server connection animation
     */
    private class AnimationFinishedHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            pane.getChildren().removeAll(path, image);
        }
    }

    /**
     * Handler for showing version from menu->version
     */
    private class VersionHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            showInformation("A Realtime Atlas!\n"
                    + "June 2018\n"
                    + "Version 1.0");
        }
    }

    /**
     * Handler for showing about author from menu->about
     */
    private class AboutHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            showInformation("©2018 Zhengyu Wang\n"
                    + "Email: zhengyuw@kth.se\n"
                    + "Tel: 076-056-0936");
        }
    }
}

package com.atlas.view;

import com.atlas.util.SplashScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * The entrance of the program
 *
 * @author
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        AtlasView view = new AtlasView(primaryStage);
        view.setStyle("-fx-background-color: #36d1ed");
      
        Scene scene = new Scene(view, 1400, 800);
        primaryStage.setTitle("Atlas!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

    }

    public static void main(String[] args) {
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.init();
        launch(args);
    }
}

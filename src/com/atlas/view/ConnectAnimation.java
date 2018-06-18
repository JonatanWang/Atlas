package com.atlas.view;

import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 * Animation for showing the connection status with server
 * If data loaded successfully, play the animation once.
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class ConnectAnimation {
    
    private final VBox root;
    private final String image;
    private final double ANIME_DURATION = 2.0;
    
    public ConnectAnimation(VBox root, String image) {
        this.root = root;
        this.image = image;
    }

    /**
     * Create path of the image
     * @return A path
     */
    public Path createPath() {
        Path paths = new Path();
        LineTo line = new LineTo(0,50);
        line.xProperty().bind(root.widthProperty());
        paths.getElements().add(new MoveTo(50,50));
        paths.getElements().add(line);
        paths.setOpacity(0);
        return paths;
    }

    /**
     * Create the path transition
     * @param path The path
     * @param img The shown image
     * @return A path transition
     */
    public PathTransition createTransition(Path path, ImageView img) {
        PathTransition thePath = new PathTransition();
        thePath.setDuration(Duration.seconds(ANIME_DURATION));
        thePath.setPath(path);
        thePath.setNode(img);
        return thePath;
    }
    
    /**
     * Create the animation image view
     * @param height The height of the image view
     * @param width The width of the image view
     * @return An image view of the animation
     */
    public ImageView createImage(double height, double width) {
        Image images = new Image(this.getClass().getResource("/resources/" + this.image).
                toString());
        
        ImageView img = new ImageView(images);
        img.setFitHeight(height);
        img.setFitWidth(width);
        return img;
    }   
}

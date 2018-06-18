package com.atlas.util;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 * Show Atlas logo when program starts
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class SplashScreen {

    /**
     * Define how long the logo is to be splashed
     */
    private static final int DURATION = 5000;
    
    /**
     * Initialize the splash screen
     */
    public void init() {
        JWindow window = new JWindow();
        window.getContentPane()
                .add(new JLabel("", new ImageIcon(getClass()
                        .getResource("/resources/logo.jpg")), SwingConstants.CENTER));
        // setBounds(x, y, width, height)
        window.setBounds(450, 60, 600, 700);
        window.setVisible(true);
        try {
            Thread.sleep(DURATION);
        } catch (InterruptedException e) {
        }
        window.setVisible(false);
        window.dispose();
    }
}

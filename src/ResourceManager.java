import javax.swing.*;
import java.awt.*;

public class ResourceManager {
    private static ResourceManager instance;

    public Image wallImage;
    public Image blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    public Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private ResourceManager() {
        wallImage = loadImage("resources/images/wall.png");
        blueGhostImage = loadImage("resources/images/blueGhost.png");
        orangeGhostImage = loadImage("resources/images/orangeGhost.png");
        pinkGhostImage = loadImage("resources/images/pinkGhost.png");
        redGhostImage = loadImage("resources/images/redGhost.png");

        pacmanUpImage = loadImage("resources/images/pacmanUp.png");
        pacmanDownImage = loadImage("resources/images/pacmanDown.png");
        pacmanLeftImage = loadImage("resources/images/pacmanLeft.png");
        pacmanRightImage = loadImage("resources/images/pacmanRight.png");
    }

    private Image loadImage(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
}

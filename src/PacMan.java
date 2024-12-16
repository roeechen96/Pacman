import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    HashSet<Block> cherries;
    Block pacman;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    long scaredStartTime = 0;
    final int SCARED_DURATION = 10000;
    boolean ghostsScared = false;
    boolean gameOver = false;
    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;
    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image pacmanRightImage;
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };


    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        ResourceManager rm = ResourceManager.getInstance();

        Image wallImage = rm.wallImage;
        Image blueGhostImage = rm.blueGhostImage;
        Image orangeGhostImage = rm.orangeGhostImage;
        Image pinkGhostImage = rm.pinkGhostImage;
        Image redGhostImage = rm.redGhostImage;

        pacmanUpImage = rm.pacmanUpImage;
        pacmanDownImage = rm.pacmanDownImage;
        pacmanLeftImage = rm.pacmanLeftImage;
        pacmanRightImage = rm.pacmanRightImage;

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this); // 20 FPS
        gameLoop.start();
    }


    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();
        cherries = new HashSet<>();
        ResourceManager rm = ResourceManager.getInstance();

        java.util.List<Point> emptySpaces = new ArrayList<>(); // To store empty positions

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X': // Wall
                        walls.add(new Block(rm.wallImage, x, y, tileSize, tileSize));
                        break;
                    case 'P': // Pac-Man
                        pacman = new Block(rm.pacmanRightImage, x, y, tileSize, tileSize);
                        break;
                    case 'b': // Blue Ghost
                        ghosts.add(new Block(rm.blueGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'o': // Orange Ghost
                        ghosts.add(new Block(rm.orangeGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'p': // Pink Ghost
                        ghosts.add(new Block(rm.pinkGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'r': // Red Ghost
                        ghosts.add(new Block(rm.redGhostImage, x, y, tileSize, tileSize));
                        break;
                    case ' ': // Food
                        foods.add(new Block(null, x + 14, y + 14, 4, 4));
                        emptySpaces.add(new Point(c, r)); // Collect empty spaces for cherries
                        break;
                    default:
                        break;
                }

            }
        }

        // Randomly place cherries in empty spaces
        if (!emptySpaces.isEmpty()) {
            int cherryCount = 2; // Number of cherries to place
            for (int i = 0; i < cherryCount; i++) {
                Point randomSpace = emptySpaces.remove(random.nextInt(emptySpaces.size())); // Pick a random empty space
                int x = randomSpace.x * tileSize;
                int y = randomSpace.y * tileSize;

                if (i % 2 == 0) {
                    cherries.add(new Block(rm.cherryImage, x, y, tileSize, tileSize));
                } else {
                    cherries.add(new Block(rm.cherryImage2, x, y, tileSize, tileSize));
                }
            }
        }
    }




    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        for (Block cherry : cherries) {
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }


        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        //score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        } else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }
    }

    private char tileAt(int x, int y) {
        int column = x / tileSize;
        int row = y / tileSize;

        if (row >= 0 && row < rowCount && column >= 0 && column < columnCount) {
            return tileMap[row].charAt(column);
        }
        return ' ';
    }


    public void move() {
        if (gameOver) {
            return; // Stop all movement if the game is over
        }

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Check wall collisions for Pac-Man
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        pacman.x = (pacman.x + boardWidth) % boardWidth;
        pacman.y = (pacman.y + boardHeight) % boardHeight;

        // Check food collisions
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
                break; // Pac-Man can only eat one food per move
            }
        }
        if (foodEaten != null) {
            foods.remove(foodEaten);
        }

        // Check cherry collisions
        Block cherryEaten = null;
        for (Block cherry : cherries) {
            if (collision(pacman, cherry)) {
                cherryEaten = cherry;

                // Activate scared mode
                ghostsScared = true;
                scaredStartTime = System.currentTimeMillis();

                // Change all ghosts to scared image
                ResourceManager rm = ResourceManager.getInstance();
                for (Block ghost : ghosts) {
                    ghost.image = rm.scaredGhostImage;
                }
            }
        }
        if (cherryEaten != null) {
            cherries.remove(cherryEaten);
        }

        // If in scared mode, check if the scared timer has elapsed
        if (ghostsScared) {
            long elapsedTime = System.currentTimeMillis() - scaredStartTime;
            if (elapsedTime > SCARED_DURATION) {
                // Reset ghosts to their original images
                ResourceManager rm = ResourceManager.getInstance();
                ghostsScared = false;

                for (Block ghost : ghosts) {
                    ghost.image = rm.blueGhostImage; // Assuming blueGhostImage as the default
                }
            }
        }

        // Move Ghosts
        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Teleport ghosts if they hit an edge
            ghost.x = (ghost.x + boardWidth) % boardWidth;
            ghost.y = (ghost.y + boardHeight) % boardHeight;

            // Check wall collisions for ghosts
            boolean collidedWithWall = false;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    collidedWithWall = true;
                    break;
                }
            }

            // Change direction randomly or on collision
            if (collidedWithWall || random.nextInt(20) == 0) {
                ghost.updateDirection(directions[random.nextInt(4)]);
                ghost.updateVelocity();
            }

            // Check collisions between Pac-Man and ghosts
            if (collision(ghost, pacman)) {
                if (ghostsScared) {
                    ghost.reset();
                    score += 200; // Award bonus points for eating a scared ghost
                } else {
                    lives--;
                    if (lives <= 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                    return;
                }
            }
        }

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }



    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
            pacman.image = pacmanUpImage;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
            pacman.image = pacmanDownImage;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
            pacman.image = pacmanLeftImage;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
            pacman.image = pacmanRightImage;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        } else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }
}
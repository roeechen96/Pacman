import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int rowCount = 21;
        int columCount = 19;
        int titleSize = 32;
        int boardWidth = columCount * titleSize;
        int boardHeight = rowCount * titleSize;

        JFrame frame = new JFrame("Pac Man");
//        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);


    }
}
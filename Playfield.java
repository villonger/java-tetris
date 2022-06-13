package Tetris;

import javax.swing.*;
import java.awt.*;
import java.io.*;


public class Playfield extends JPanel implements Runnable {
    private int[][] field;
    private int width1;
    private int height1;
    private Piece currentPiece;
    private Piece nextPiece;
    private Color[] color =
            //0 - I, 1 - J, 2 - L, 3 - T, 4 - O, 5 - S, 6 - Z
            {Color.CYAN, Color.BLUE, Color.ORANGE, new Color(128,0,128), Color.YELLOW, Color.GREEN, Color.RED};
    private Piece hold;
    boolean holdUsed = false;
    boolean gameOver = false;
    private int score = 0;
    private int level = 1;
    private int linesClearedTotal = 0;
    private int[] highScores = new int[10];
    int[] bag = new int[7];

    public Playfield() {
        this.width1 = 10;
        this.height1 = 20;
        field = new int[height1][width1];
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                field[i][j] = 0;
            }
        }
        newPiece(false);
    }

    // a method for getting the new piece
    public void newPiece(boolean hold){
        int piece = 0;
        if (!gameOver) {
            if (hold) {
                piece = currentPiece.getTetromino();
            }
            if (nextPiece == null) {
                piece = getPiece();
                nextPiece = new Piece(piece);
            }
            currentPiece = nextPiece;
            piece = getPiece();
            nextPiece = new Piece(piece);
            holdUsed = false;
        }
        if (checkCollision(currentPiece.getX(), currentPiece.getY())) {
            System.out.println("Game Over");
            gameOver = true;
            checkHighScore();
        }
    }

    // a method for clearing the field
    public void clearField() {
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                field[i][j] = 0;
            }
        }
    }

    public void paint(Graphics g) {
        char c;
        super.paint(g);
        this.setBackground(Color.BLACK);

        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                g.setColor(Color.WHITE);
                if (field[i][j] == 1) {
                    g.setColor(new Color(239, 158, 255));
                    c = '⬜';
                }
                else
                {
                    c = '_';
                }
                if (isCurrentPiece(j, i)) {
                    g.setColor(color[currentPiece.getTetromino()]);
                    c = '⬛';
                }

                g.drawString("" + c, j * 20+20, i * 20+20);
            }
        }

        // score and level display

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, width1 * 20 + 25, 40);
        g.drawString("Level: " + level, width1 * 20 + 25, 60);

        // next Piece Display

        g.setColor(Color.WHITE);
        g.drawString("Next Piece", width1 * 20 + 25, 120);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (nextPiece.checkCoordinate(j, i)) {
                    g.setColor(color[nextPiece.getTetromino()]);
                    g.drawString("⬛", (width1 + 1) * 20 + j * 20, ((i + 1) * 20) + 140);
                }
            }
        }

        // hold Piece Display

        g.setColor(Color.WHITE);
        g.drawString("Hold Piece", width1 * 20 + 25, 140 + (4 * 20));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (hold != null && hold.checkCoordinate(j, i)) {
                    g.setColor(color[hold.getTetromino()]);
                    g.drawString("⬛", (width1 + 1) * 20 + j * 20, ((i + 1) * 20) + 240);
                }
            }
        }

        // high Score Display

        g.setColor(Color.WHITE);
        g.drawString("High Scores", width1 * 20 + 25, 240 + (4 * 20));
        for (int i = 0; i < 5; i++) {
            g.drawString(i+1+". "+String.valueOf(highScores[i]), width1 * 20 + 25, (i * 20) + 260 + (4 * 20));
        }

    }


    public boolean isCurrentPiece(int x, int y){
        int localX = x - currentPiece.getX();
        int localY = y - currentPiece.getY();
        if (localX >= 0 && localX < 4 && localY >= 0 && localY < 4) {
            return currentPiece.checkCoordinate(localX, localY);
        }
        return false;
    }

    // game tick
    public void tick(){
        if (!gameOver) {
            if (!checkCollision(currentPiece.getX(), currentPiece.getY() + 1)) {
                currentPiece.setY(currentPiece.getY() + 1);
            } else {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (currentPiece.checkCoordinate(j, i)) {
                            field[currentPiece.getY() + i][currentPiece.getX() + j] = 1;
                        }
                    }
                }
                newPiece(false);
            }
            repaint();
            clearLines();
            checkLevel();
            readHighScores();
        }
    }

    // move the piece left
    public void moveLeft(){
        if (!checkCollision(currentPiece.getX()-1, currentPiece.getY())) {
            currentPiece.setX(currentPiece.getX() - 1);
        }
        repaint();
    }
    // move the piece right
    public void moveRight(){
        if (!checkCollision(currentPiece.getX()+1, currentPiece.getY())) {
            currentPiece.setX(currentPiece.getX() + 1);
        }
        repaint();
    }
    // rotate the piece
    public void rotate(int direction){
        currentPiece.rotate(direction);
        if (checkCollision(currentPiece.getX(), currentPiece.getY())) {
            currentPiece.rotate(direction*(-1));
        }
        repaint();
    }
    // piece harddrop
    public void dropDown(){
        while(!checkCollision(currentPiece.getX(), currentPiece.getY()+1)){
            currentPiece.setY(currentPiece.getY()+1);
        }
        if (!gameOver) {
            tick();
            repaint();
        }
    }


    // a method for checking full lines
    public void clearLines(){
        int linesCleared = 0;
        for (int i = 0; i < height1; i++) {
            boolean fullLine = true;
            for (int j = 0; j < width1; j++) {
                if (field[i][j] == 0) {
                    fullLine = false;
                    break;
                }
            }
            if (fullLine) {
                for (int j = 0; j < width1; j++) {
                    field[i][j] = 0;
                }
                for (int k = i; k > 0; k--) {
                    for (int j = 0; j < width1; j++) {
                        field[k][j] = field[k-1][j];
                    }
                }
                linesCleared++;
            }
        }
        if (linesCleared == 1) {
            score += 40 * level;
            repaint();
        }
        else if (linesCleared == 2) {
            score += 100 * level;
            repaint();
        }
        else if (linesCleared == 3) {
            score += 300 * level;
            repaint();
        }
        else if (linesCleared == 4) {
            score += 1200 * level;
            repaint();
        }
        linesClearedTotal += linesCleared;
    }

    // a method for checking if the level can be increased
    public void checkLevel(){
        if (score >= level*2000 && level < 10) {
            level++;
            repaint();
        }
    }

    // piece hold method
    public void holdPiece(){
        if (!holdUsed) {
            if (hold == null) {
                hold = currentPiece;
                newPiece(true);
            } else {
                Piece temp = currentPiece;
                currentPiece = hold;
                hold = temp;
                currentPiece.setX(3);
                currentPiece.setY(0);
            }
        }

        // disable hold if it was already used on a piece
        holdUsed = true;
        repaint();

    }

    // a method for resetting everything
    public void reset(){
        score = 0;
        level = 1;
        holdUsed = false;
        hold = null;
        newPiece(true);
        clearField();
        gameOver = false;
        repaint();
        newPiece(false);
    }

    // read high scores from highscores.txt, if it doesn't exist, create it and write 5 default scores
    public void readHighScores(){
        try {
            FileReader fr = new FileReader("highscores.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                highScores[i] = Integer.parseInt(line);
                i++;
            }
            br.close();
        } catch (IOException e) {
            FileWriter fw;
            try {
                fw = new FileWriter("highscores.txt");
                BufferedWriter bw = new BufferedWriter(fw);
                for (int i = 0; i < 5; i++) {
                    bw.write("0");
                    bw.newLine();
                }
                bw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // 7-Bag piece generator
    public int getPiece(){

        // if the bag is empty, refill it and shuffle

        if (bag[0] == 0){
            bag = new int[]{1, 2, 3, 4, 5, 6, 7};
            for (int i = 0; i < bag.length; i++) {
                int randomIndexToSwap = (int) (Math.random() * bag.length);
                int temp = bag[i];
                bag[i] = bag[randomIndexToSwap];
                bag[randomIndexToSwap] = temp;
            }
        }

        // print bag for debugging

        for (int i = 0; i < bag.length; i++) {
            System.out.print(bag[i] + " ");
        }

        // get the last non-zero element in the bag

        int piece = 0;
        for (int i = bag.length - 1; i >= 0; i--) {
            if (bag[i] != 0) {
                piece = bag[i]-1;
                bag[i] = 0;
                System.out.println(piece);
                break;
            }
        }

        return piece;
    }




    // check if the current score is a high score, if so write it to file in the correct position
    public void checkHighScore() {
        for (int i = 0; i < 5; i++) {
            if (score > highScores[i]) {
                for (int j = 4; j > i; j--) {
                    highScores[j] = highScores[j - 1];
                }
                highScores[i] = score;
                break;
            }
        }
        try {
            FileWriter writer = new FileWriter("highscores.txt");
            BufferedWriter bw = new BufferedWriter(writer);
            for (int i = 0; i < 5; i++) {
                bw.write(Integer.toString(highScores[i]));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    // a method for checking piece collision
    public boolean checkCollision(int x, int y){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int checkX = x + j;
                int checkY = y + i;
                if (currentPiece.checkCoordinate(j, i)) {
                    if (checkX < 0 || checkX >= width1 || checkY < 0 || checkY >= height1) {
                        return true;
                    }
                    if ((field[checkY][checkX] == 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // runnable
    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep((1000-(level*100)));
                tick();
            } catch (InterruptedException e) {}
        }
    }

}


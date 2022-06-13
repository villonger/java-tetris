package Tetris;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*

a simple Tetris clone

author:

igor

help:

maurice!

 */

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Playfield d = new Playfield();
        frame.add(d);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 480);
        frame.setResizable(false);
        frame.setVisible(true);

        /*
        Controls:

        →   - Move right
        ←   - Move left
        ↑   - Rotate clockwise
        ↓   - Rotate counterclockwise
        ⎵   - HardDrop
        C   - Hold
        R   - Restart

         */

        frame.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        d.rotate(1);
                        break;
                    case KeyEvent.VK_DOWN:
                        d.rotate(-1);
                        break;
                    case KeyEvent.VK_LEFT:
                        d.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        d.moveRight();
                        break;
                    case KeyEvent.VK_SPACE:
                        d.dropDown();
                        break;
                    case KeyEvent.VK_C:
                        d.holdPiece();
                        break;
                    case KeyEvent.VK_R:
                        d.reset();
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        d.run();
    }
}

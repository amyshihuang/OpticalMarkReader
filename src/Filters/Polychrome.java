package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import javax.swing.*;

public class Polychrome implements PixelFilter, Interactive {
    private int n = 2; //total number of intervals
    public Polychrome(){
        //String response = JOptionPane.showInputDialog("choose an n value");
        //n = Integer.parseInt( response );
    }
    public DImage processImage(DImage img) {
        if(n<1) n=1;

        short[][] grid = img.getBWPixelGrid();
        int intervalLength = 255/n;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int val = grid[row][col];
                int intervalNum = val/intervalLength;
                grid[row][col] = (short) ((intervalNum*intervalLength) + (intervalLength/2));
            }
        }

        img.setPixels(grid);
        return img;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {
        if(key=='+') n++;
        if(key=='-') n--;
    }
}

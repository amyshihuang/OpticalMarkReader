package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

public class DisplayInfoFilter implements PixelFilter, Interactive {
    private int timingMarkHeight = 20; //timing mark = each black rectangle on left side of scantron sheet
    private int timingMarkWidth = 8;
    private int timingMarkVerticalDistance; //16-30px
    private int timingMarkLeftBound = 40; //actually center of mark to account for misaligned sheets

    private int bubbleRowWidth;
    private int bubbleRowHeight;

    private int blackThreshold = 185;
    private int whiteThreshold = 240;

    public DisplayInfoFilter() {
        System.out.println("Filter running...");
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] grid = img.getBWPixelGrid();

        grid = crop(grid,0,0,500,500);

        System.out.println("Image is " + grid.length + " by "+ grid[0].length);

        getBlackAndWhiteCount(grid);

        //find location of first timing mark, then update timingMarkVerticalDistance accordingly
        for (int r = 0; r < grid.length; r++) {
            if(grid[r][timingMarkLeftBound] < blackThreshold){
                for (int r2 = 0; r2 < timingMarkHeight; r2++) {

                }
            }
        }

        img.setPixels(grid);
        return img;
    }

    public short[][] crop(short[][] grid, int r1, int c1, int r2, int c2) {
        short[][] grid2 = new short[r2-r1][c2-c1];

        for (int r = 0; r < grid2.length; r++) {
            for (int c = 0; c < grid2[r].length; c++) {
                grid2[r][c] = grid[r1+r][c1+c];

            }
        }

        return grid2;
    }

    public void getBlackAndWhiteCount(short[][] grid){
        int blackCount = 0;
        int whiteCount = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] < blackThreshold) blackCount++;
                if (grid[r][c] > whiteThreshold) whiteCount++;
            }
        }
        System.out.println(blackCount + " nearly black pixels and " + whiteCount + " nearly white pixels");
        System.out.println("----------------------------------------");
        System.out.println("If you want, you could output information to a file instead of printing it.");
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {

    }
}


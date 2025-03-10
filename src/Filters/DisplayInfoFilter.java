package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

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

        img = Polychrome.processImage(img);

        grid = crop(grid, 0, 0, 500, 500);

        System.out.println("Image is " + grid.length + " by " + grid[0].length);

        getBlackAndWhiteCount(grid,0,0,grid.length,grid[0].length);

        //find location of first timing mark, then update timingMarkVerticalDistance accordingly
        int r = 0;
        while (grid[r][timingMarkLeftBound] >= blackThreshold) {
            r++;
        }
        int currTimingMarkTopBound = r;
        //loop over row of bubbles within height of timing mark; for each bubble get black counts
        ArrayList<Integer> blackCountsPerRow = new ArrayList<>();
        for (int currBubble = 0; currBubble < 5; currBubble++) {
            //c1=left bound of zeroth bubble in first column of bubbles
            //c2=right bound of 4th(last) bubble in first column of bubbles
            int currCount = getBlackCount(grid,currTimingMarkTopBound,105+(currBubble*25),currTimingMarkTopBound+timingMarkHeight,(105+(currBubble*25))+20);
            blackCountsPerRow.add(currCount);
        }
        for (int i = 0; i < blackCountsPerRow.size(); i++) {
            System.out.println(blackCountsPerRow.get(i));
        }
        //222-105=117
        //each bubble 20px wide
        //gap between bubbles=5px

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

    public void getBlackAndWhiteCount(short[][] grid, int r1, int c1, int r2, int c2){
        int blackCount = 0;
        int whiteCount = 0;
        for (int r = r1; r < r2; r++) {
            for (int c = c1; c < c2; c++) {
                if (grid[r][c] < blackThreshold) blackCount++;
                if (grid[r][c] > whiteThreshold) whiteCount++;
            }
        }
        System.out.println(blackCount + " nearly black pixels and " + whiteCount + " nearly white pixels");
        System.out.println("----------------------------------------");
        System.out.println("If you want, you could output information to a file instead of printing it.");
    }

    public int getBlackCount(short[][] grid, int r1, int c1, int r2, int c2){
        int blackCount = 0;
        for (int r = r1; r < r2; r++) {
            for (int c = c1; c < c2; c++) {
                if (grid[r][c] < blackThreshold) blackCount++;
            }
        }
        return blackCount;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {

    }
}


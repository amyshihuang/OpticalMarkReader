package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class DisplayInfoFilter implements PixelFilter, Interactive {
    // ----------------------------------------------------------------
    // >>> sizes and distances of elements <<<
    // size of each bubble: 20 px
    // spacing between each bubble in same row: 5 px
    // spacing between rows in same column: 28 px
    // spacing between 0th and 1st col: 52 px
    // spacing between rows in different cols: 4 px above and below
    // ----------------------------------------------------------------

    // ----------------------------------------------------------------
    // DEPRECATED
    /*
    private int timingMarkHeight = 20; //timing mark = each black rectangle on left side of scantron sheet
    private int timingMarkWidth = 8;
    private int timingMarkVerticalDistance; //16-30px
    private int timingMarkLeftBound = 40; //actually center of mark to account for misaligned sheets

     */
    // ----------------------------------------------------------------

    private int bubbleRowWidth;
    private int bubbleRowHeight;

    private int blackThreshold = 185;
    private int whiteThreshold = 240;

    FixedThresholdFilter fixedThresholdFilter = new FixedThresholdFilter();

    public DisplayInfoFilter() {
        System.out.println("Filter running...");
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] grid = img.getBWPixelGrid();

        //TODO: filter not being applied
        img = fixedThresholdFilter.processImage(img);

        grid = crop(grid, 0, 0, 500, 500);

        System.out.println("Image is " + grid.length + " by " + grid[0].length);

        getBlackAndWhiteCount(grid,0,0,grid.length,grid[0].length);

        // ----------------------------------------------------------------
        // DEPRECATED

        /*
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

         */

        // ----------------------------------------------------------------

        //TODO: output inaccurate
        System.out.println(getResult(grid, 0, 0));

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
        //r and c refer to pixels
        for (int r = r1; r < r2; r++) {
            for (int c = c1; c < c2; c++) {
                if (grid[r][c] < blackThreshold) blackCount++;
            }
        }
        return blackCount;
    }

    //TODO: output might be inaccurate
    //get answer for one row (row and col start at zero)
    public int getResult(short[][] grid, int row, int col) {
        ArrayList<Integer> blackPixelCounts = new ArrayList<>();
        //TODO: remove local variables and declare and initialize in class
        int topBound = 40; //pixel at top of first row in first col
        int leftBound = 105; //pixel at left of first row in first col
        int rowSpacing = 28;
        int bubbleSize = 20;
        int rowWidth = 115; //up to 118
        int colSpacing = 52;
        int bubbleSpacing = 5;
        //loop over each bubble in row and add black pixel counts to arraylist
        for (int bubble = 0; bubble < 5; bubble++) {
            //TODO: update variables to use bubbleSpacing
            int r1 = topBound + ((bubbleSize + rowSpacing) * row);
            int c1 = leftBound + (rowWidth * col) + (colSpacing * col);
            int r2 = topBound + ((bubbleSize + rowSpacing) * row) + bubbleSize;
            int c2 = leftBound + (rowWidth * col) + (colSpacing * col) + rowWidth;
            int currBubbleBlackCount = getBlackCount(grid, r1, c1, r2, c2);

            blackPixelCounts.add(currBubbleBlackCount);
        }
        //find which bubble has most black pixels
        int i;
        for (i = 0; i < blackPixelCounts.size(); i++) {
            int largestIndex = 0;
            if (blackPixelCounts.get(i) > blackPixelCounts.get(largestIndex)) {
                largestIndex = i;
            }
        }
        return i;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {

    }
}


package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class DisplayInfoFilter implements PixelFilter, Interactive {

    private int blackThreshold = 185; //grayscale pixel value threshold to set pixel to be black
    private int whiteThreshold = 240; //grayscale pixel value threshold to set pixel to be white
    public int numQuestions; //total number of questions on test

    FixedThresholdFilter fixedThresholdFilter = new FixedThresholdFilter();

    public DisplayInfoFilter() {
        System.out.println("Filter running...");
    }

    @Override
    public DImage processImage(DImage img) {

        // apply filters before setting bw image as grid, so that effects of filters are visible
        img = fixedThresholdFilter.processImage(img);

        short[][] grid = img.getBWPixelGrid();

        System.out.println(getResult(img));

        // create new grid that is a cropped portion of original grid
        short[][] grid2 = crop(grid, 0,0, 700, 700);

        // set image to new cropped grid and displays it
        // only affects what is visible, not necessarily what is being looped over
        img.setPixels(grid2);
        return img;
    }

    //crop grid to specified region
    public short[][] crop(short[][] grid, int r1, int c1, int r2, int c2) {
        short[][] grid2 = new short[r2-r1][c2-c1];
        for (int r = 0; r < grid2.length; r++) {
            for (int c = 0; c < grid2[r].length; c++) {
                grid2[r][c] = grid[r1+r][c1+c];
            }
        }
        return grid2;
    }

    //print numbers of black and white pixels in specified region of grid
    public void getBlackAndWhiteCount(short[][] grid, int r1, int c1, int r2, int c2){
        int blackCount = 0;
        int whiteCount = 0;
        for (int r = r1; r < r2; r++) {
            for (int c = c1; c < c2; c++) {
                if (grid[r][c] <= blackThreshold) blackCount++;
                if (grid[r][c] >= whiteThreshold) whiteCount++;
            }
        }
        System.out.println(blackCount + " nearly black pixels and " + whiteCount + " nearly white pixels");
        System.out.println("----------------------------------------");
        System.out.println("If you want, you could output information to a file instead of printing it.");
    }

    //get number of black pixels within specified region of grid
    public int getBlackCount(short[][] grid, int start_row, int start_col, int end_row, int end_col){
        int blackCount = 0;
        //r and c refer to pixels
        for (int r = start_row; r < end_row; r++) {
            for (int c = start_col; c < end_col; c++) {
                if (grid[r][c] <= blackThreshold) blackCount++;
            }
        }
        return blackCount;
    }

    //get number of white pixels within specified region of grid
    public int getWhiteCount(short[][] grid, int start_row, int start_col, int end_row, int end_col){
        int whiteCount = 0;
        //r and c refer to pixels
        for (int r = start_row; r < end_row; r++) {
            for (int c = start_col; c < end_col; c++) {
                if (grid[r][c] >= whiteThreshold) whiteCount++;
            }
        }
        return whiteCount;
    }

    //Return index with the largest value in array
    int getMaxIndex(ArrayList<Integer> array){
        int max = array.get(0);
        int max_index = 0;
        for (int i = 1; i < array.size(); i++) {
            if(array.get(i) > max){
                max = array.get(i);
                max_index= i;
            }
        }
        return max_index;
    }

    public int getNumQuestions(DImage img){
        // apply filters before setting bw image as grid, so that effects of filters are visible
        img = fixedThresholdFilter.processImage(img);

        short[][] grid = img.getBWPixelGrid();

        int bubbleSizeKeyAns = 20;
        int bubbleSpacingKeyAns = 4; //horizontal and vertical spacing between bubbles
        int startRowKeyAns = 327; //starting pixel positions for each place value
        int startColHundreds = 442;
        int startColTens = 466;
        int startColOnes = 490;
        int hundredsNumBubbles = 2;
        int tensNumBubbles = 10;
        int onesNumBubbles = 10;
        int hundreds; //place values
        int tens;
        int ones;
        ArrayList<Integer> keyItemBlackCounts = new ArrayList<>();

        hundreds = getColResult(grid,keyItemBlackCounts,startRowKeyAns,startColHundreds,bubbleSizeKeyAns,bubbleSpacingKeyAns,hundredsNumBubbles);
        keyItemBlackCounts.clear();
        tens = getColResult(grid,keyItemBlackCounts,startRowKeyAns,startColTens,bubbleSizeKeyAns,bubbleSpacingKeyAns,tensNumBubbles);
        keyItemBlackCounts.clear();
        ones = getColResult(grid,keyItemBlackCounts,startRowKeyAns,startColOnes,bubbleSizeKeyAns,bubbleSpacingKeyAns,onesNumBubbles);
        keyItemBlackCounts.clear();

        return (100*hundreds)+(10*tens)+ones;
    }

    public ArrayList<Integer> getResult(DImage img) {
        // apply filters before setting bw image as grid, so that effects of filters are visible
        img = fixedThresholdFilter.processImage(img);

        short[][] grid = img.getBWPixelGrid();

        // Array to keep track of Black count for each question in a row
        ArrayList<Integer> BlackCountArr = new ArrayList<>();

        // questions with the largest Black count per row
        ArrayList<Integer> Answer_Array = new ArrayList<>();

        // Start Pixel location
        int start_row = 110;
        int start_col = 103;

        int rowSpacing = 28;
        int bubbleSize = 20;
        int bubbleSpacing = 5;
        int numBubbles = 5; //number of bubbles per question

        //TODO: need to handle unexpected student entry situations (eg. questions with multiple bubbles filled in, didn't fill in bubble completely, etc)

        /*
        TODO: for each question compare differences between black counts
         create variable for difference
         OR compare each black count to another, if 2+ are similar then unexpected entry exists

         */


        // loop of rows with the questions
        for (int question = 0; question < numQuestions; question++) {
            if(numQuestions<=25){

                // add the largest black value index into the answer array
                Answer_Array.add(getRowResult(grid,BlackCountArr,start_row,start_col,bubbleSize,bubbleSpacing,numBubbles));

                // clear previous black values
                BlackCountArr.clear();

                // Move to next question
                start_row += bubbleSize+rowSpacing;

                // return to initial col
                start_col = 103;



                //getQuestionResults(grid,Answer_Array,BlackCountArr,start_row,start_col,rowSpacing,bubbleSize,bubbleSpacing,numBubbles);

            }
            else{
                for (int q = 0; q < 25; q++) {

                    // add the largest black value index into the answer array
                    Answer_Array.add(getRowResult(grid,BlackCountArr,start_row,start_col,bubbleSize,bubbleSpacing,numBubbles));

                    // clear previous black values
                    BlackCountArr.clear();

                    // Move to next question
                    start_row += bubbleSize+rowSpacing;

                    // return to initial col
                    start_col = 103;



                    //getQuestionResults(grid,Answer_Array,BlackCountArr,start_row,start_col,rowSpacing,bubbleSize,bubbleSpacing,numBubbles);

                }

                //set start row and start col for second column of questions (q26-50)
                start_row = 134;
                start_col = 272;

                for (int q = 25; q < numQuestions; q++) {

                    // add the largest black value index into the answer array
                    Answer_Array.add(getRowResult(grid,BlackCountArr,start_row,start_col,bubbleSize,bubbleSpacing,numBubbles));

                    // clear previous black values
                    BlackCountArr.clear();

                    // Move to next question
                    start_row += bubbleSize+rowSpacing;

                    // return to initial col
                    start_col = 272;



                    //getQuestionResults(grid,Answer_Array,BlackCountArr,start_row,start_col,rowSpacing,bubbleSize,bubbleSpacing,numBubbles);

                }
            }

        }

        return Answer_Array;
    }

    //INCORRECT RESULTS
    //get results for specified range of questions
    public void getQuestionResults(short[][] grid, ArrayList<Integer> Answer_Array, ArrayList<Integer> BlackCountArr, int start_row, int start_col, int rowSpacing, int bubbleSize, int bubbleSpacing, int numBubbles){
        int initialStartCol = start_col;

        // add the largest black value index into the answer array
        Answer_Array.add(getRowResult(grid,BlackCountArr,start_row,start_col,bubbleSize,bubbleSpacing,numBubbles));

        // clear previous black values
        BlackCountArr.clear();

        // Move to next question
        start_row += bubbleSize+rowSpacing;

        // return to initial col
        start_col = initialStartCol;

    }

    //get answer for given row
    public int getRowResult(short[][] grid, ArrayList<Integer> BlackCountArr, int start_row, int start_col, int bubbleSize, int bubbleSpacing, int numBubbles){
        // set the end location for the getBlackCount
        int end_row = start_row + bubbleSize;
        int end_col = start_col + bubbleSize;

        //loop over each bubble in row and add black pixel counts to arraylist
        for (int bubble = 0; bubble < numBubbles; bubble++) {

            // adding the black counts to an array
            BlackCountArr.add(getBlackCount(grid, start_row, start_col, end_row, end_col)); //black count of one bubble

            // change the start column for the next circle
            start_col += bubbleSize+bubbleSpacing;
            end_col += bubbleSize+bubbleSpacing;

        }

        return getMaxIndex(BlackCountArr); //index of darkest bubble in given row
    }

    //get result for given column (for key answer count)
    public int getColResult(short[][] grid, ArrayList<Integer> BlackCountArr, int start_row, int start_col, int bubbleSize, int bubbleSpacing, int numBubbles){
        // set the end location for the getBlackCount
        int end_row = start_row + bubbleSize;
        int end_col = start_col + bubbleSize;

        //loop over each bubble in col and add black pixel counts to arraylist
        for (int bubble = 0; bubble < numBubbles; bubble++) {

            // adding the black counts to an array
            BlackCountArr.add(getBlackCount(grid, start_row, start_col, end_row, end_col)); //black count of one bubble

            // change the start row for the next circle
            start_row += bubbleSize+bubbleSpacing;
            end_row += bubbleSize+bubbleSpacing;

        }

        return getMaxIndex(BlackCountArr); //index of darkest bubble in given col
    }

    //print contents of grid within specified area
    private void printArr(short[][] grid, int startRow, int startCol, int endRow, int endCol) {
        for (int r = startRow; r < endRow; r++) {
            for (int c = startCol; c < endCol; c++) {
                System.out.print(grid[r][c] + " ");
            }
            System.out.println();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        //print black pixel count for square area with mouse click at top left corner
        img = fixedThresholdFilter.processImage(img);
        short[][] grid = img.getBWPixelGrid();
        int bubbleSize = 20;
        System.out.println(getBlackCount(grid,mouseY,mouseX,mouseY+bubbleSize,mouseX+bubbleSize));
    }

    @Override
    public void keyPressed(char key) {
    }
}

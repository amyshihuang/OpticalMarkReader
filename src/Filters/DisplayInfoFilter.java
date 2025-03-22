package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

//TODO: run this filter from FilterTest class, but eventually everything should be ran from OpticalMarkReaderMain class

public class DisplayInfoFilter implements PixelFilter, Interactive {

    // ----------------------------------------------------------------
    // DEPRECATED (?)
  /*
  private int timingMarkHeight = 20; //timing mark = each black rectangle on left side of scantron sheet
  private int timingMarkWidth = 8;
  private int timingMarkVerticalDistance; //16-30px
  private int timingMarkLeftBound = 40; //actually center of mark to account for misaligned sheets

   */
    // ----------------------------------------------------------------

    //
    // top and bottom edges of rows are located at midpoints of white space between timing marks// Dom

    //2025-03-14:
    // no groups that hard-coded bubble distances could read answers but those that used timing marks could (?)
    // timing marks not perfectly aligned with rows
    // for each row:
    //     use convolution to blur rest of image outside the row
    //     color masking to make row black and white so pencil and borders appear dark
    //     in horizontal 1px line emanating from timing mark, get averages of darkness of each bubble
    //     bubble with smallest average has more dark pixels and is answer
    // create question object and answer object
    // create arraylist of question objects
    // method taking answer object as parameter, to check answers

    private int bubbleRowWidth;
    private int bubbleRowHeight;

    private int blackThreshold = 185;
    private int whiteThreshold = 240;

    private int topBound = 40; //pixel at top of first timing mark

    /*
    // inaccurate values
    private int questionHightDistance = 50;
    private int questionWidthDistance = 28;
    private int circleW = 13;
    private int circleH = 13;

     */

    FixedThresholdFilter fixedThresholdFilter = new FixedThresholdFilter();

    public DisplayInfoFilter() {
        System.out.println("Filter running...");
    }

    @Override
    public DImage processImage(DImage img) {

        // apply filters before setting bw image as grid, so that effects of filters are visible
        img = fixedThresholdFilter.processImage(img);

        short[][] grid = img.getBWPixelGrid();

        ArrayList<Integer> BlackCountArr = new ArrayList<>();

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

        //FIXME: only prints results for questions 1-25 on page 1; also need questions 26-50
        //FIXME: incorrect results are given for questions that have no bubbles filled in
        System.out.println(getResult(grid));

        // create new grid that is a cropped portion of original grid
        short[][] grid2 = crop(grid, 0,0, 500, 500);

        // set image to new cropped grid and displays it
        // only affects what is visible, not necessarily what is being looped over
        img.setPixels(grid2);
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
                if (grid[r][c] <= blackThreshold) blackCount++;
                if (grid[r][c] >= whiteThreshold) whiteCount++;
            }
        }
        System.out.println(blackCount + " nearly black pixels and " + whiteCount + " nearly white pixels");
        System.out.println("----------------------------------------");
        System.out.println("If you want, you could output information to a file instead of printing it.");
    }

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
    int max(ArrayList<Integer> array){
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

    //TODO: if the area being looped over gradually gets more and more offset due to accumulating incorrect row/col distances:
    // 1. get total pixel height of all rows, and total pixel width of all cols
    // 2. divide each by (number of bubbles*bubbleSize)
    // 3. results are the average distances between rows/cols (datatype double)

    //TODO: alternative method to calculate results (IF BUBBLES DON'T WORK)
    // instead of using the bubbles, use the timing marks
    // to get locations of starting rows for each timing mark:
    //     1. set col to be within width of timing marks
    //     2. go down rows until there is a white pixel followed by a dark pixel
    //     3. row you stopped at is the row of the top edge of the timing mark
    //     4. add that row to an arraylist of top edges
    //     5. repeat 2-4 so that you looped over all the timing marks and have an arraylist of the row numbers corresponding to all their top edges

    //FIXME: only prints results for questions 1-25; also need questions 26-50
    //FIXME: incorrect results are given for questions that have no bubbles filled in

    public ArrayList<Integer> getResult(short[][] grid) {
        // Array to keep track of Black count for each question in a row
        ArrayList<Integer> BlackCountArr = new ArrayList<>();

        // questions with the largest Black count per row
        ArrayList<Integer> Answer_Array = new ArrayList<>();

        //TODO: consider declaring and initializing variables as constants instead of only locally in method

        // Start Pixel location
        int start_row = 103;
        int start_col = 105;

        // end Pixel Location
        int end_row, end_col;

        int leftBound = 105; //pixel at left of first row in first col; SAME AS START_COL
        int rowSpacing = 28;
        int bubbleSize = 20;
        int rowWidth = 115; //up to 118
        int colSpacing = 52; //spacing between right edge of questions 1-25, and left edge of 26-50
        int bubbleSpacing = 5;
        // spacing between rows in different cols (questions 1-25, vs 26-50) (eg. questions 1, 26, 2): 4 px above and below

        // loop of rows with the questions
        for (int question = 0; question < 25; question++) {

            // set the end location for the getBlackCount
            end_row = start_row + bubbleSize;
            end_col = start_col + bubbleSize;

            //loop over each bubble in row and add black pixel counts to arraylist
            for (int bubble = 0; bubble < 5; bubble++) {

                //TODO: uncomment this if debugging
                //prints values of pixels in given area
                //printArr(grid, start_row, start_col, end_row, end_col);

                // adding the black counts to an array
                BlackCountArr.add(getBlackCount(grid, start_row, start_col, end_row, end_col)); //black count of one bubble

                // change the start column for the next circle
                start_col += bubbleSize+bubbleSpacing;
                end_col += bubbleSize+bubbleSpacing;

            }

            //FIXME: incorrect results are given for questions that have no bubbles filled in
            // 1. consider checking differences between values of black counts
            //    if all black counts are similar, then that question probably does not have any bubbles filled in
            //    otherwise if one black count is much larger than the others, then that bubble is probably filled in
            // 2. OR use key item count on first page
            // debug by clicking on top left corner of any bubble to print number of black pixels in that 20x20 region (mouseClicked method)

            //TODO: need to handle unexpected student entry situations (eg. questions with multiple bubbles filled in)

            // add the largest black value index into the answer array
            Answer_Array.add(max(BlackCountArr)); //index of darkest bubble in given row

            // clear previous black values
            BlackCountArr.clear();

            // Move to next question
            start_row += bubbleSize+rowSpacing;

            // return to initial col
            //FIXME: startcol=25 works only for questions 1-25; questions 26-50 need different startcol
            start_col = 105;
        }

          /*
          // DEPRECATED

          int r1 = topBound + ((bubbleSize + rowSpacing) * row);
          int c1 = leftBound + (rowWidth * col) + (colSpacing * col);
          int r2 = topBound + ((bubbleSize + rowSpacing) * row) + bubbleSize;
          int c2 = leftBound + (rowWidth * col) + (colSpacing * col) + rowWidth;


           int r1 = topBound + ((bubbleSize + rowSpacing) * row);
           int c1 = leftBound + ((bubbleSize + bubbleSpacing) * col);
           int r2 = topBound + ((bubbleSize + rowSpacing) * row) + bubbleSize;
           int c2 = leftBound + ((bubbleSize + bubbleSpacing) * col) + bubbleSize;
           int currBubbleBlackCount = getBlackCount(grid, r1, c1, r2, c2);


           blackPixelCounts.add(currBubbleBlackCount);
       find which bubble has most black pixels
       int i;
       for (i = 0; i < blackPixelCounts.size(); i++) {
           int largestIndex = 0;
           if (blackPixelCounts.get(i) > blackPixelCounts.get(largestIndex)) {
               largestIndex = i;
           }
       }
       */
        return Answer_Array;
    }

    private void printArr(short[][] grid, int startRow, int startCol, int endRow, int endCol) {
        for (int r = startRow; r < endRow; r++) {
            for (int c = startCol; c < endCol; c++) {
                System.out.print(grid[r][c] + " ");
            }
            System.out.println();
        }
    }

    //print black pixel count for square area with mouse click at top left corner
    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        img = fixedThresholdFilter.processImage(img);
        short[][] grid = img.getBWPixelGrid();
        int bubbleSize = 20;
        System.out.println(getBlackCount(grid,mouseY,mouseX,mouseY+bubbleSize,mouseX+bubbleSize));
    }

    @Override
    public void keyPressed(char key) {

    }
}

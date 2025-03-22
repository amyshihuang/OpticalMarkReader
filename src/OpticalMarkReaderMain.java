import javax.swing.*;
import java.io.File;

public class OpticalMarkReaderMain {
    public static void main(String[] args) {
        String pathToPdf = fileChooser();
        System.out.println("Loading pdf at " + pathToPdf);

        /*
        Your code here to...
        (1).  Load the pdf
        (2).  Loop over its pages
        (3).  Create a DImage from each page and process its pixels
        (4).  Output 2 csv files
         */

        //TODO: 2025-03-21
        // create DisplayInfoFilter object by running its constructor
        // run getResult method from said object, after getting DImage
        //     eg. nameOfFilter.getResult(DImage); CHANGE METHOD PARAMETERS TO TAKE DIMAGE, THRESHOLD, ETC
        //     everything in displayinfofilter should be moved to getResult method
        // loop over each page of pdf (first page is answer key)
        // create a string for the results (compare answers on pages 2+ to those on the first page which is the answer key)
        // add each result to the string using StringBuilder (more info in file reading and writing doc)
        // some code can be copy and pasted from FilterTest class?

        //steps 1,2,3
        FilterTest test = new FilterTest();
        //TODO: run filter on multiple pages instead of only page 1
        test.RunTheFilter();

    }

    private static String fileChooser() {
        String userDirLocation = System.getProperty("user.dir");
        File userDir = new File(userDirLocation);
        JFileChooser fc = new JFileChooser(userDir);
        int returnVal = fc.showOpenDialog(null);
        File file = fc.getSelectedFile();
        return file.getAbsolutePath();
    }
}

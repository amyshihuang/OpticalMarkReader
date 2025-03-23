import FileIO.PDFHelper;
import Filters.DisplayInfoFilter;
import core.DImage;
import processing.core.PImage;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OpticalMarkReaderMain {
    public static void main(String[] args) throws IOException {
        String pathToPdf = fileChooser();
        System.out.println("Loading pdf at " + pathToPdf);

        /*
        Your code here to...
        (1).  Load the pdf
        (2).  Loop over its pages
        (3).  Create a DImage from each page and process its pixels
        TODO: (4).  Output 2 csv files
         "First, a scores file that says, for each test, which questions were right and wrong, as well as the overall total correct."
         "For example:
         page, # right, q1, q2, q3, q4, q5, q6, q7, q8, …
         1, 12, right, right, right, right right, right right, ….
         2, 10, right, wrong, right, right, right, wrong, ….
         3, 2, wrong, wrong, wrong, wrong, right, …."
         "you must also output an item analysis file which says, for each test question, the total number of students who got it wrong in the batch of images you processed."
         */

        //TODO: 2025-03-22
        // create a string for the results (compare answers on pages 2+ to those on the first page which is the answer key)
        // add each result to the string using StringBuilder (more info in file reading and writing doc)
        //some code can be copy and pasted from FilterTest class?

        DisplayInfoFilter filter = new DisplayInfoFilter(); // create DisplayInfoFilter object

        ArrayList<PImage> in = PDFHelper.getPImagesFromPdf(pathToPdf); // create arraylist of PImages from each page of pdf
        // loop over each page in arraylist of PImages
        for (int i = 0; i < in.size(); i++) {
            DImage img = new DImage(in.get(i)); // create DImage from current PImage
            System.out.println("Running filter on page "+ (i+1) +" of "+in.size());  // print which page is being run on
            filter.processImage(img); // process current DImage
        }
        /*
        //for-each loop (can use if not printing page numbers)
        for(PImage PImg : in){
            DImage img = new DImage(PImg);       // you can make a DImage from a PImage
            DisplayInfoFilter filter = new DisplayInfoFilter();
            filter.processImage(img);
        }
         */

        //TODO: obtain correct data before writing them to files
        //writeDataToFile("scores.txt", "This is the contents of the file!");
        //writeDataToFile("itemAnalysis.txt","contents");

        //use if want to read files
        //String data = readFile("myFile.txt");
        //System.out.println("File contains: " + data);

    }

    private static String fileChooser() {
        String userDirLocation = System.getProperty("user.dir");
        File userDir = new File(userDirLocation);
        JFileChooser fc = new JFileChooser(userDir);
        int returnVal = fc.showOpenDialog(null);
        File file = fc.getSelectedFile();
        return file.getAbsolutePath();
    }

    public static void writeDataToFile(String filePath, String data) throws IOException {
        try (FileWriter f = new FileWriter(filePath);
             BufferedWriter b = new BufferedWriter(f);
             PrintWriter writer = new PrintWriter(b);) {

            writer.println(data);

        } catch (IOException error) {
            System.err.println("There was a problem writing to the file: " + filePath);
            error.printStackTrace();
        }
    }

    public static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

}

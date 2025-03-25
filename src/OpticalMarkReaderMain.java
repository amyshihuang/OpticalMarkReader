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
    public static int numQuestions;

    //TODO: should these be constants?
    static DisplayInfoFilter filter = new DisplayInfoFilter(); // create DisplayInfoFilter object
    static ArrayList<PImage> in = new ArrayList<>();

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

        StringBuilder scores = new StringBuilder();
        StringBuilder colHeaders = new StringBuilder("page, # right, ");

        in = PDFHelper.getPImagesFromPdf(pathToPdf); // create arraylist of PImages from each page of pdf

        //TODO: investigate
        filter.numQuestions = filter.getNumQuestions(new DImage(in.get(0))); // get number of questions from first page of pdf
        numQuestions = filter.numQuestions;

        DImage img0 = new DImage(in.get(0));

        // loop over each page in arraylist of PImages
        for (int i = 0; i < in.size(); i++) {
            DImage img = new DImage(in.get(i)); // create DImage from current PImage
            System.out.println("Running filter on page "+ (i+1) +" of "+in.size());  // print which page is being run on
            //filter.getResult(img); // process current DImage
            System.out.println(filter.getResult(img)); // process current DImage

            //append question numbers to col header string builder
            colHeaders.append(", q").append(i+1);

            StringBuilder currAnswers = new StringBuilder();
            StringBuilder currLine = new StringBuilder();

            //TODO: make this a method

            //getScores(i,img);


            //scores.append(filter.getResult(img));
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

    //TODO: in progress
    public static void getScores(int pageIndex, DImage img) {
        StringBuilder pageScores = new StringBuilder(); //line for curr page with page num, num right, right/wrong etc.
        pageScores.append(pageIndex+1).append(", "); // append curr page num
        if(pageIndex==0){
            pageScores.append(numQuestions); //all questions are correct
            //loop over each answer in answer array
            for (int qIndex = 0; qIndex < numQuestions; qIndex++) {
                //append curr answer to pageScores string builder
                pageScores.append(", ").append("right");
            }
        }
        else{
            //loop over each answer in answer array
            for (int qIndex = 0; qIndex < numQuestions; qIndex++) {
                //append curr answer to pageScores string builder
                //if current answer in curr image == curr answer in first page
                if(filter.getResult(img).get(qIndex) == filter.getResult(new DImage(in.get(0))).get(qIndex)){
                    pageScores.append(", ").append("right");
                }
                else{
                    pageScores.append(", ").append("wrong");
                }

            }
        }
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

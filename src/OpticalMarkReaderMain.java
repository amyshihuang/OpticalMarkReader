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

        StringBuilder scoresData = new StringBuilder();
        StringBuilder scoreHeaders = new StringBuilder("page, # right");

        in = PDFHelper.getPImagesFromPdf(pathToPdf); // create arraylist of PImages from each page of pdf

        filter.numQuestions = filter.getNumQuestions(new DImage(in.get(0))); // get number of questions from first page of pdf
        numQuestions = filter.numQuestions;

        DImage img0 = new DImage(in.get(0));

        for (int i = 0; i < numQuestions; i++) {
            //append question numbers to col header string builder
            scoreHeaders.append(", q").append(i+1);
        }

        //TODO: replace with csv
        System.out.println(scoreHeaders);
        scoresData.append(scoreHeaders);

        // loop over each page in arraylist of PImages
        for (int i = 0; i < in.size(); i++) {
            DImage img = new DImage(in.get(i)); // create DImage from current PImage
            System.out.println("Running filter on page "+ (i+1) +" of "+in.size());  // print which page is being run on
            //filter.getResult(img); // process current DImage
            System.out.println(filter.getResult(img)); // process current DImage

            StringBuilder currAnswers = new StringBuilder();
            StringBuilder currLine = new StringBuilder();

            //TODO: replace with csv
            //TODO: make this a method
            System.out.println(getPageScores(i,img));
            scoresData.append("\n").append(getPageScores(i,img));

            //scores.append(filter.getResult(img));
        }

        //TODO: obtain correct data before writing them to files
        writeDataToFile("scores.txt", String.valueOf(scoresData));
        //writeDataToFile("itemAnalysis.txt","contents");

        //use if want to read files
        //String data = readFile("myFile.txt");
        //System.out.println("File contains: " + data);

    }

    //get scores for specified page
    public static StringBuilder getPageScores(int pageIndex, DImage img) {
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
            int numCorrect = 0;
            //array for whether answers are right or wrong
            ArrayList<String> answerCorrectness = new ArrayList<>();
            //loop over each answer in answer array
            for (int qIndex = 0; qIndex < numQuestions; qIndex++) {
                //append curr answer to pageScores string builder
                //if current answer in curr image == curr answer in first page
                if(filter.getResult(img).get(qIndex) == filter.getResult(new DImage(in.get(0))).get(qIndex)){
                    //pageScores.append(", ").append("right");
                    numCorrect++;
                    answerCorrectness.add("right");
                }
                else{
                    //pageScores.append(", ").append("wrong");
                    answerCorrectness.add("wrong");
                }
            }
            pageScores.append(numCorrect);
            //loop over arraylist of answer correctness (right/wrong) and append each to string builder
            for (int ansCorrIndex = 0; ansCorrIndex < answerCorrectness.size(); ansCorrIndex++) {
                pageScores.append(", ").append(answerCorrectness.get(ansCorrIndex));
            }
        }
        return pageScores;
    }

    /*
    public static StringBuilder createScoresCSV(){

    }

     */

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

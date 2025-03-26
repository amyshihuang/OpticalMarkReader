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
    static DImage img0;

    public static void main(String[] args) throws IOException {
        String pathToPdf = fileChooser();
        System.out.println("Loading pdf at " + pathToPdf);

        String scoresFilePath = "scores.csv";
        String itemAnalysisFilePath = "itemAnalysis.csv";

        StringBuilder scoresData = new StringBuilder("page, # right");

        StringBuilder itemAnalysisData = new StringBuilder("question, # wrong");

        in = PDFHelper.getPImagesFromPdf(pathToPdf); // create arraylist of PImages from each page of pdf

        filter.numQuestions = filter.numQuestions(new DImage(in.get(0))); // get number of questions from first page of pdf
        numQuestions = filter.numQuestions;

        img0 = new DImage(in.get(0));

        for (int i = 0; i < numQuestions; i++) {
            //append question numbers to col header string builder
            scoresData.append(", q").append(i+1);
        }

        // loop over each page in arraylist of PImages
        for (int i = 0; i < in.size(); i++) {
            DImage img = new DImage(in.get(i)); // create DImage from current PImage
            System.out.println("Running filter on page "+ (i+1) +" of "+in.size());  // print which page is being run on
            //System.out.println(filter.getResult(img)); // process current DImage
            scoresData.append("\n").append(pageScores(i,img));
        }

        itemAnalysisData.append("\n").append(itemAnalysis(in));

        writeDataToFile(scoresFilePath, String.valueOf(scoresData));
        writeDataToFile(itemAnalysisFilePath, String.valueOf(itemAnalysisData));

        //print file data to console
        String scoresDataRead = readFile(scoresFilePath);
        System.out.println(scoresFilePath+"\n"+ scoresDataRead);

        String itemAnalysisDataRead = readFile(itemAnalysisFilePath);
        System.out.println(itemAnalysisFilePath+"\n" + itemAnalysisDataRead);

    }

    //get scores for specified page
    public static StringBuilder pageScores(int pageIndex, DImage img) {
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
                //if(filter.result(img).get(qIndex) == filter.result(new DImage(in.get(0))).get(qIndex)){
                if(filter.result(img).get(qIndex) == filter.result(img0).get(qIndex)){
                    numCorrect++;
                    answerCorrectness.add("right");
                }
                else{
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

    public static StringBuilder itemAnalysis(ArrayList<PImage> in){
        StringBuilder questionHowManyMissed = new StringBuilder();
        ArrayList<ArrayList<Integer>> resultsList = new ArrayList<>();
        //loop over each page
        for (int page = 0; page < in.size(); page++) {
            DImage img = new DImage(in.get(page)); // create DImage from current PImage
            resultsList.add(filter.result(img)); //add results of curr page to arraylist of results
        }
        //loop over each question
        for (int qIndex = 0; qIndex < numQuestions; qIndex++) {
            int currHowManyMissed = 0;
            questionHowManyMissed.append(qIndex+1);
            //loop over each page after first page
            for (int page = 1; page < in.size(); page++) {
                //check if curr result in curr page is wrong
                if(resultsList.get(page).get(qIndex) != resultsList.get(0).get(qIndex)){
                    currHowManyMissed++;
                }
            }
            questionHowManyMissed.append(", ").append(currHowManyMissed).append("\n");
        }
        return questionHowManyMissed;
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

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**
 * --- Magnetometer Data Processing Program ---
 * created by: Jeffrey Hejna '16
 * email: jeffhejna@gmail.com
 * date: 6/25/2015
 * last updated: 4/29/2016
 *
 * This program will Edge Match units together. You should be able to only have to use
 * the middle option in the interactive layout since Research_Program8 removes horizontal edges
 * between units. Just add the horizontal units together in Surfer and then just work from bottom
 * to top.
 */
public class Edge_Matching3 {
    private String Topfilename="";
    private String Topfilename2="";
    private String Bottomfilename="";
    private String Bottomfilename2="";

    private ArrayList<Double> XvaluesTop = new ArrayList<>(60000);
    private ArrayList<Double> YvaluesTop= new ArrayList<>(60000);
    private ArrayList<Double> ReadingsTop = new ArrayList<>(60000);

    private ArrayList<Double> XvaluesBottom = new ArrayList<>(60000);
    private ArrayList<Double> YvaluesBottom= new ArrayList<>(60000);
    private ArrayList<Double> ReadingsBottom = new ArrayList<>(60000);

    private ArrayList<Double> finalXvalues = new ArrayList<>(60000);
    private ArrayList<Double> finalYvalues = new ArrayList<>(60000);
    private ArrayList<Double> finalReadings = new ArrayList<>(60000);

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double leftCornerReading;
    private double rightCornerReading;

    private double mean;

    private Edge_Matching3(int choice) throws FileNotFoundException {
        if(choice==1||choice==2||choice==3) {
            Bottomfilename = getFileName(1);
            Topfilename = getFileName(4);

        }else if(choice==4){
            Bottomfilename = getFileName(2);
            Bottomfilename2 = getFileName(3);
            Topfilename = getFileName(4);
        }else{
            Bottomfilename = getFileName(1);
            Topfilename = getFileName(5);
            Topfilename2 = getFileName(6);
        }



    }

    /**
     * Creates an interactive layout in which the user can pick which orientation should be used.
     * ** The middle option should now be the only one you have to use!**
     * @return
     */
    private static int getLayout(){
        StdDraw.setXscale(-200,200);
        StdDraw.setYscale(-200,200);

        //middle choice
        StdDraw.filledRectangle(0,10,20,10);
        StdDraw.filledRectangle(0,-11,20,10);

        //bottom left
        StdDraw.filledRectangle(-160,-160,20,10);
        StdDraw.filledRectangle(-119,-160,20,10);
        StdDraw.filledRectangle(-140,-139,20,10);

        //bottom right
        StdDraw.filledRectangle(140,-160,20,10);
        StdDraw.filledRectangle(160,-139,20,10);
        StdDraw.filledRectangle(119,-139,20,10);

        //top left choice
        StdDraw.filledRectangle(-140,160,20,10);
        StdDraw.filledRectangle(-160,139,20,10);

        //top right choice
        StdDraw.filledRectangle(140,160,20,10);
        StdDraw.filledRectangle(160,139,20,10);

        StdDraw.text(0,190,"Click which arrangement the grids you are edge matching are in");
        int choice=0;
        double X;
        double Y;
        while (true) {
            if (StdDraw.mousePressed()) {
                X = StdDraw.mouseX();
                Y = StdDraw.mouseY();
                break;
            }
        }
        if(X>=-50 && X<=50 && Y<=50 && Y>=-50){
            choice=1;
        }else if(X>=-200 && X<=-100 && Y<=200 && Y>=100){
            choice=2;
        }else if(X>=100 && X<=200 && Y<=200 && Y>=100){
            choice=3;
        }else if(X>=-200 && X<=-100 && Y<=-100 && Y>=-200){
            choice=4;
        }else if(X>=100 && X<=200 && Y>=-200 && Y<=-100){
            choice=5;
        }

        return choice;

    }

    /**
     * This function creates GUIs in which the user will select the units they want edge matched,
     * depending on the choice they made in getLayout().
     * @param num
     * @return -1 if both files aren't chosen.
     */
    private String getFileName(int num){
        String filename;
        while(true) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION && num==1) {
                fileChooser.setDialogTitle("Bottom Unit Choice");
                filename = fileChooser.getSelectedFile().getPath();
                String message = "Is "+ filename + " correct for the BOTTOM unit?";
                int reply = JOptionPane.showConfirmDialog(null,message,"IMPORTANT!!",JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION){
                    break;
                }

            }else if(result==JFileChooser.APPROVE_OPTION && num==2) {
                fileChooser.setDialogTitle("Left Bottom Unit Choice");
                filename = fileChooser.getSelectedFile().getPath();
                String message = "Is "+ filename + " correct for the LEFT BOTTOM unit? (LEFT one)";
                int reply = JOptionPane.showConfirmDialog(null,message,"IMPORTANT!!",JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION){
                    break;
                }
            }else if(result==JFileChooser.APPROVE_OPTION && num==3) {
                fileChooser.setDialogTitle("Right Unit Choice");
                filename = fileChooser.getSelectedFile().getPath();
                String message = "Is " + filename + " correct for the RIGHT BOTTOM unit";
                int reply = JOptionPane.showConfirmDialog(null, message, "IMPORTANT!!", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    break;
                }
            }else if(result==JFileChooser.APPROVE_OPTION && num==4) {
                filename = fileChooser.getSelectedFile().getPath();
                fileChooser.setDialogTitle("Top Unit Choice");
                String message = "Is "+ filename + " correct for the TOP unit";
                int reply = JOptionPane.showConfirmDialog(null,message,"IMPORTANT!!",JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION){
                    break;
                }
            }else if(result==JFileChooser.APPROVE_OPTION && num==5) {
                fileChooser.setDialogTitle("Left Top Unit Choice");
                filename = fileChooser.getSelectedFile().getPath();
                String message = "Is "+ filename + " correct for the LEFT TOP unit";
                int reply = JOptionPane.showConfirmDialog(null,message,"IMPORTANT!!",JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION){
                    break;
                }
            }else if(result==JFileChooser.APPROVE_OPTION && num==6) {
                fileChooser.setDialogTitle("Right Top Unit Choice");
                filename = fileChooser.getSelectedFile().getPath();
                String message = "Is "+ filename + " correct for the RIGHT TOP unit";
                int reply = JOptionPane.showConfirmDialog(null,message,"IMPORTANT!!",JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION){
                    break;
                }
            }else if (result == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(null, "An error occurred.");
            }else if(result == JFileChooser.CANCEL_OPTION){
                filename="null";
                break;
            }
        }
        return filename;
    }

    /**
     * This function gets the values for the corners of the units
     * @param option
     * @throws FileNotFoundException
     */
    private void getCorners(int option) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(Topfilename));
        ArrayList<Double> X = new ArrayList<>();
        ArrayList<Double> Y = new ArrayList<>();
        ArrayList<Double> Reading = new ArrayList<>();

        while (scanner.hasNextLine()){
            X.add(scanner.nextDouble());
            Y.add(scanner.nextDouble());
            Reading.add(scanner.nextDouble());
            scanner.nextLine();
        }
        scanner.close();
        if(option==5){
            Scanner scanner2 = new Scanner((new FileInputStream(Topfilename2)));
            scanner2.nextLine();
            while(scanner2.hasNextLine()){
                X.add(scanner2.nextDouble());
                Y.add(scanner2.nextDouble());
                Reading.add(scanner2.nextDouble());
                scanner2.nextLine();
            }
            scanner2.close();
        }

        minY=Y.get(0);                                          //obtaining the max and min X and Y coordinates
        maxY=Y.get(Y.size()-1);
        minX = XvaluesTop.get(0);
        maxX = XvaluesTop.get(XvaluesTop.size()-1);

        for (int i = 0; i < X.size(); i++) {
            if(Y.get(i)==maxY && X.get(i)==minX){
                leftCornerReading = Reading.get(i);            //getting the left and right corner readings
            }else if(Y.get(i)==maxY && X.get(i)==maxX){
                rightCornerReading = Reading.get(i);
            }
        }

        for (int i = 0; i < ReadingsTop.size(); i++) {
            mean += ReadingsTop.get(i);
        }
        mean = mean/ReadingsTop.size();
    }

    /**
     * Grabs the information for the units depending on what choice is made from the interactive layout.
     * @param choice Choice from the layout
     * @throws FileNotFoundException
     */
    private void getGridInfo(int choice) throws FileNotFoundException {
        Scanner Topfile = new Scanner(new FileInputStream(Topfilename));
        while (Topfile.hasNextLine()){
            XvaluesTop.add(Topfile.nextDouble());
            YvaluesTop.add(Topfile.nextDouble());
            ReadingsTop.add(Topfile.nextDouble());
            if (YvaluesTop.get(YvaluesTop.size() - 1) > YvaluesTop.get(0)) {
                XvaluesTop.remove(XvaluesTop.size() - 1);
                YvaluesTop.remove(YvaluesTop.size() - 1);
                ReadingsTop.remove(ReadingsTop.size() - 1);
                break;
            }
            Topfile.nextLine();
        }
        Topfile.close();

        if(choice==5){
            Scanner Topfile2 = new Scanner(new FileInputStream(Topfilename2));
            while(Topfile2.hasNextLine()){
                XvaluesTop.add(Topfile2.nextDouble());
                YvaluesTop.add(Topfile2.nextDouble());
                ReadingsTop.add(Topfile2.nextDouble());
                if (YvaluesTop.get(YvaluesTop.size() - 1) > YvaluesTop.get(0)) {
                    XvaluesTop.remove(XvaluesTop.size() - 1);
                    YvaluesTop.remove(YvaluesTop.size() - 1);
                    ReadingsTop.remove(ReadingsTop.size() - 1);
                    break;
                }
                Topfile2.nextLine();
            }
        }

        getCorners(choice);

        ArrayList<Double> tempXvalues = new ArrayList<>(60000);
        ArrayList<Double> tempYvalues = new ArrayList<>(60000);
        ArrayList<Double> tempReadings = new ArrayList<>(60000);
        if(choice!=4){
            Scanner Bottomfile = new Scanner(new FileInputStream(Bottomfilename));
            while (Bottomfile.hasNextLine()){
                tempXvalues.add(Bottomfile.nextDouble());
                tempYvalues.add(Bottomfile.nextDouble());
                tempReadings.add(Bottomfile.nextDouble());
                Bottomfile.nextLine();
            }
            Bottomfile.close();

        }else{
            Scanner Bottomfile1 = new Scanner(new FileInputStream(Bottomfilename));
            while (Bottomfile1.hasNextLine()){
                tempXvalues.add(Bottomfile1.nextDouble());
                tempYvalues.add(Bottomfile1.nextDouble());
                tempReadings.add(Bottomfile1.nextDouble());
                Bottomfile1.nextLine();
            }
            Bottomfile1.close();

            Scanner Bottomfile2 = new Scanner(new FileInputStream(Bottomfilename2));
            while (Bottomfile2.hasNextLine()){
                tempXvalues.add(Bottomfile2.nextDouble());
                tempYvalues.add(Bottomfile2.nextDouble());
                tempReadings.add(Bottomfile2.nextDouble());
                Bottomfile2.nextLine();
            }
            Bottomfile2.close();
        }

        for (int i = 0; i < tempReadings.size(); i++) {
            if(Objects.equals(tempYvalues.get(i),minY)){
                XvaluesBottom.add(tempXvalues.get(i));
                YvaluesBottom.add(tempYvalues.get(i));
                ReadingsBottom.add(tempReadings.get(i));
            }
        }

    }

    /**
     * Creates the data that will be put into the -TIP file
     */
    private void EdgeMatch(){
        finalXvalues.add(minX);
        finalYvalues.add(maxY);
        finalReadings.add(mean - leftCornerReading);

        finalXvalues.add(maxX);
        finalYvalues.add(maxY);
        finalReadings.add(mean - rightCornerReading);

        for (int i = 0; i < XvaluesTop.size(); i++) {
            for (int j = 0; j < XvaluesBottom.size(); j++) {
                if(Objects.equals(XvaluesTop.get(i), XvaluesBottom.get(j)) &&
                        (XvaluesTop.get(i)-XvaluesTop.get(i).intValue()==0 ||
                                XvaluesTop.get(i)-XvaluesTop.get(i).intValue()==0.25 ||
                                XvaluesTop.get(i)-XvaluesTop.get(i).intValue()==0.5 ||
                                XvaluesTop.get(i)-XvaluesTop.get(i).intValue()==0.75)){
                    finalXvalues.add(XvaluesTop.get(i));
                    finalYvalues.add(YvaluesTop.get(i));
                    finalReadings.add(ReadingsBottom.get(j)-ReadingsTop.get(i));
                }
            }
        }

    }

    /**
     * Creates a -TIP file that will be used to finish edge matching in Surfer
     * @throws IOException
     */
    private void writeFile() throws IOException {

        FileWriter fw = new FileWriter(getFileName1() +"-TIP.dat");
        fw.write("X Y T Line \r\n");
        for (int k = 0; k < finalReadings.size(); k++) {
            fw.write(finalXvalues.get(k) +" "+ finalYvalues.get(k) +" "+ finalReadings.get(k)+"\r\n");
        }
        fw.close();
    }

    /**
     * Splits the file name so it can be used in making the new TIP file
     * @return
     */
    private String getFileName1(){
        String parts[] =  Topfilename.split("\\.");
        Topfilename = parts[0];
        return Topfilename;
    }



    public static void main(String[] args) throws IOException {
        int choice = getLayout();
        Edge_Matching3 edgeMatching = new Edge_Matching3(choice);
        edgeMatching.getGridInfo(choice);
        edgeMatching.EdgeMatch();
        edgeMatching.writeFile();
        JOptionPane.showMessageDialog( null, "You can now exit the windows" );

    }
}

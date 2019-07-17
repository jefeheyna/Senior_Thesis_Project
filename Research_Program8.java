import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * --- Magnetometer Data Processing Program ---
 * created by: Jeffrey Hejna '16
 * email: jeffhejna@gmail.com
 * date: 6/25/2015
 * last updated: 4/23/2016
 *
 * This program will De-stagger units; providing multiple files to test.
 * It will also Destripe and remove edges between horizontally adjacent units by using
 * the Zero Mean Line method to normalize the trends of each transect to 0.
 */
public class Research_Program8 {
    private String fileName;                   // Text file of data
    private ArrayList<Double> finalXvalues;    // X value data after processing
    private ArrayList<Double> finalYvalues;    // Y value data after processing
    private ArrayList<Double> finalReadings;   // Magnetic data after processing
    private ArrayList<Integer> finalLines;

    private Research_Program8(){
        fileName=getFile();
        finalXvalues = new ArrayList<>(60000);
        finalYvalues = new ArrayList<>(60000);
        finalReadings = new ArrayList<>(60000);
        finalLines = new ArrayList<>(60000);
    }

    /**
     * This method creates a GUI to select the file that
     * the user wants to process.
     * @return The file name
     */
    private String getFile(){
        String filename;
        while(true) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                filename = fileChooser.getSelectedFile().getPath();
                String message = "Is "+ filename + " correct?";
                int reply = JOptionPane.showConfirmDialog(null,message,"IMPORTANT!!",JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION){
                    break;
                }
            } else if (result == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(null, "An error occurred.");
            } else if(result == JFileChooser.CANCEL_OPTION){
                filename="null";
                break;
            }
        }
        return filename;
    }

    /**
     * This method reads the text file, creates arrays for the raw data, removes dropouts,
     * shifts x values by 0.25 cm, creates arrays of data for each sensor, and then
     * calls the Destripe method for both sensors
     * @return 0 if completed; -1 if no file was chosen.
     * @throws Exception
     */
    private int readFile() throws Exception {
        if(this.fileName.equals("null")){
            return -1;
        }else{
            Scanner lineScan = new Scanner(new FileInputStream(this.fileName));
            Scanner fileScan = new Scanner(new FileInputStream(this.fileName));

            lineScan.nextLine();                        //skips the heading of the file
            int numLines = 0;                           //gets the number of lines of data
            while (lineScan.hasNextLine()) {
                numLines++;
                lineScan.nextLine();
            }

            double[] X_Raw = new double[numLines];      //creates arrays of raw data
            double[] Y_Raw = new double[numLines];
            double[] Reading_Raw = new double[numLines];
            int[] Line_Raw = new int[numLines];

            fileScan.nextLine();
            int num=0;
            while(fileScan.hasNextLine()){
                X_Raw[num] = fileScan.nextDouble();
                Y_Raw[num] = fileScan.nextDouble();
                try{
                    Reading_Raw[num] = fileScan.nextDouble();
                }catch (Exception e){
                    Reading_Raw[num] = 0.0;
                    fileScan.next();
                    fileScan.next();
                }
                Line_Raw[num] = fileScan.nextInt();

                num++;                  //incrementer
                fileScan.nextLine();    //moves to next line
            }


            ArrayList<Double> Xlist = new ArrayList<>(60000);           //creating array lists in order to
            ArrayList<Double> Ylist = new ArrayList<>(60000);           //remove dropouts from raw arrays
            ArrayList<Double> Readinglist = new ArrayList<>(60000);
            ArrayList<Integer> Linelist = new ArrayList<>(60000);

            for (int i = 0; i < X_Raw.length; i++) {
                if (Reading_Raw[i] != 0.0) {                            //removing dropouts
                    Xlist.add(X_Raw[i]+0.25);                           //adds shift of 0.25 cm to the X values.
                    Ylist.add(Y_Raw[i]);
                    Readinglist.add(Reading_Raw[i]);
                    Linelist.add(Line_Raw[i]);
                }
            }



            double[] Xvalues = new double[Xlist.size()];                //The final arrays after removing dropouts
            double[] Yvalues = new double[Ylist.size()];                //and shifting X values by 0.25 cm.
            double[] Readings = new double[Readinglist.size()];
            int[] Line = new int[Linelist.size()];



            for (int i = 0; i < Xlist.size(); i++) {                    //adding the elements from the array lists
                Xvalues[i] = Xlist.get(i);                              //to the final arrays
                Yvalues[i] = Ylist.get(i);
                Readings[i] = Readinglist.get(i);
                Line[i] = Linelist.get(i);

            }

            num = 0;
            for (int i = 0; i < Line_Raw.length; i++) {                 //Finding the point where the line numbers
                if (Line[i] < Line[i + 1]) {                            //shift from 80 to 0. This is when we go
                    num = i;                                            //from one sensor to the next.
                    break;
                }
            }


            double[] leftSensorX = new double[num + 1];                   //left sensor values
            double[] leftSensorY = new double[num + 1];
            double[] leftSensorReading = new double[num + 1];
            int[] leftSensorLine = new int[num + 1];


            double[] rightSensorX = new double[Xvalues.length - num - 1]; //right sensor values
            double[] rightSensorY = new double[Yvalues.length - num - 1];
            double[] rightSensorReading = new double[Readings.length - num - 1];
            int[] rightSensorLine = new int[Line.length - num - 1];


            for (int i = 0; i < num + 1; i++) {                           //adding all the left sensor
                leftSensorX[i] = Xvalues[i];                              //data to the arrays
                leftSensorY[i] = Yvalues[i];
                leftSensorReading[i] = Readings[i];
                leftSensorLine[i] = Line[i];
            }




            int location = 0;
            for (int i = num + 1; i < Yvalues.length; i++) {              //adding all the right sensor
                rightSensorX[location] = Xvalues[i];                      //data to the arrays
                rightSensorY[location] = Yvalues[i];
                rightSensorReading[location] = Readings[i];
                rightSensorLine[location] = Line[i];
                location++;
            }

            //call Destripe for both left and right sensors
            Destripe(leftSensorX,leftSensorY,leftSensorReading,leftSensorLine);
            Destripe(rightSensorX,rightSensorY,rightSensorReading,rightSensorLine);

            return 0;
        }
    }





    /**
     * Takes each line and subtracts the data points from the best fit line in order to normalize it to 0.
     * @param X X values from sensor
     * @param Y Y values from sensor
     * @param Reading Readings from sensor
     * @param Line Line numbers from sensor
     * @throws InterruptedException
     * @throws IOException
     */
    private void Destripe(double[] X, double[] Y, double[] Reading, int[] Line)
            throws InterruptedException, IOException {

        int lineNum = Line[0];                                            //starting at highest Line number
        int elements, min, max, spot, location;
        while (lineNum >= 0) {

            //resets values
            elements = 0;
            min = 500000000;
            max = -500000000;


            for (int i = 0; i < Y.length; i++) {                          //finding the number of lines of data
                if (Line[i] == lineNum) {                                 //for an individual transect
                    elements++;
                    if (i < min) {
                        min = i;
                    }
                    if (i > max) {
                        max = i;
                    }
                }
            }


            double[] tempX = new double[elements];                        //data values for a particular Line number
            double[] tempY = new double[elements];
            double[] tempReading = new double[elements];
            int[] tempLine = new int[elements];


            spot = 0;
            for (int i = min; i < max + 1; i++) {                          //inserting data into the temporary arrays
                tempX[spot] = X[i];
                tempY[spot] = Y[i];
                tempReading[spot] = Reading[i];
                tempLine[spot] = Line[i];
                spot++;
            }



            double[] regLine = calcRegression(tempY,tempReading);         //Finding best fit line of an individual
                                                                          //transect by calling calcRegression


            location = 0;
            for (int i = 0; i < tempReading.length; i++) {
                tempReading[i] = tempReading[i] - regLine[location];      //Zero Mean Line method
                location++;


                finalXvalues.add(tempX[i]);                               //adding to the final array lists
                finalYvalues.add(tempY[i]);
                finalReadings.add(tempReading[i]);
                finalLines.add(tempLine[i]);
            }
            lineNum--;
        }
    }

    /**
     * This function calculates the best fit line for an individual transect
     * @param Y Y values of the transect
     * @param Reading Magnetic data of a transect
     * @return Array of the values for the best fit line
     */
    private double[] calcRegression(double[] Y, double[] Reading){
        int N = Y.length;
        double sumY=0;
        double sumReading=0;
        double sumYandReading=0;
        double sumMinusYsq=0;


        for (int i = 0; i < Y.length; i++) {
            sumY+=Y[i];
            sumReading+=Reading[i];
        }
        sumY=sumY/N;
        sumReading=sumReading/N;

        for (int i = 0; i < Y.length; i++) {
            sumYandReading+=(Y[i]-sumY)*(Reading[i]-sumReading);
            sumMinusYsq+=(Y[i]-sumY)*(Y[i]-sumY);
        }
        double m = sumYandReading/sumMinusYsq;
        double b = sumReading - m*sumY;

        double[] functionValues = new double[Y.length];
        for (int i = 0; i < Y.length; i++) {
            functionValues[i]= m*Y[i]+b;
        }
        return functionValues;
    }

    /**
     * Shifts the transects in the y direction by different values. Values range from 0.1-0.7 cm.
     * Odd lines are shifted upward while Even lines are shifted downward
     * @throws IOException
     */
    private void Destagger() throws IOException{
        double[] Ytemp= new double[finalYvalues.size()];
        for (double i = 1; i < 8; i++) {
            for (int j = 0; j < finalXvalues.size(); j++) {
                Ytemp[j] = finalYvalues.get(j);
            }
            for (int j = 0; j < finalXvalues.size(); j++) {
                if (finalLines.get(j) % 2 != 0) {
                    Ytemp[j] = finalYvalues.get(j) + (i / 10);
                } else {
                    Ytemp[j] = finalYvalues.get(j) - (i / 10);
                }
            }
            //writes files for the different shifts
            FileWriter fw = new FileWriter(fileName +"-DESPIKE-DESTRIPE-"+(int)i+"-DESTAG.dat");
            fw.write("X Y T Line \r\n");
            for (int j = 0; j < finalXvalues.size(); j++) {
                fw.write(finalXvalues.get(j)+" "+ Ytemp[j]+" "+finalReadings.get(j)
                        +" "+ finalLines.get(j)+"\r\n");
            }
            fw.close();
        }
    }

    /**
     * Writes the file of the processed data values
     * @throws IOException
     */
    private void writeFile() throws IOException {
        FileWriter fw = new FileWriter(getFileName() +"-DESPIKE-DESTRIPE.dat");
        fw.write("X Y T Line \r\n");
        for (int k = 0; k < finalReadings.size(); k++) {
            fw.write(finalXvalues.get(k) +" "+ finalYvalues.get(k) +" "
                    + finalReadings.get(k)+" "+ finalLines.get(k)+"\r\n");
        }
        fw.close();
    }

    /**
     * Splits up the filename
     * @return
     */
    private String getFileName(){
        String parts[] =  fileName.split("\\.");
        fileName = parts[0];
        return fileName;
    }

    public static void main(String[] args) throws Exception {
        Research_Program8 processing = new Research_Program8();
        int x = processing.readFile();
        if (x!=-1) {
            processing.writeFile();
            processing.Destagger();
        }
        JOptionPane.showMessageDialog( null, "Done!" );
    }
}

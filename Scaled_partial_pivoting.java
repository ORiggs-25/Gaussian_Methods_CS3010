// Programmer: Olive Riggs
// Course: CS 3010
// Program: Gaussian Elimination with scaled partial pivoting
// Description:
// - The overall program evaluate value in the x matrix (Ax = B)
// - This program asks the user to enter the linear equations through a console,
//      or choosing a file name containing the linear equations.
//===============================================================================================
// Scenarios:
// - If the user choose to enter equations in the console
//      then this program asks the user to enter number of equations n, where n <= 10.
//      The user should input only the coefficient of the matrix including the b values
// - If the user choose to enter the linear equation through file names,
//      then the user should enter a file name in the console.
//      The values in the files should only contain augmented coefficient matrix
//      (including the b values). If the file doesn't exist then shows an error message.
// - If the input are not as the format that error message will display.
// ===============================================================================================
// Steps in solving the value of x's:
// - The program will show the scaled ratios at each step, and mention the pivot row selected
//   based on the maximum ratio.
// - The program will also show the intermediate matrix at each step of Gaussian Elimination process.
// - Finally, The final output should show the solution in the following format:
//      x = 1
//      y = 2
//      z = 3
//
// NOTE: Upload .java file and report in PDF format
// (report containing the snapshot of test case for any set linear equation from the chosen source input).
// ===========================================================================================================

// Needed libraries:
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Stack;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import static java.lang.System.exit;

class Scaled_partial_pivoting
{

    int numEquations;       // holds number equation from the user input
    float[][] matrix_1;     // hold original matrix
    float[][] matrix_2;     // hold copy matrix
    Scanner keyboard;       // Scanner for user input

    /**
     * Constructor
     * Initializes needed variables such as matrix original and copy of the matrix
     * Nothing is being passed in the parameter
     * @throws FileNotFoundException
     */
    public Scaled_partial_pivoting() throws FileNotFoundException {
        keyboard = new Scanner(System.in);
        numEquations = getNumEquations();
        keyboard.nextLine(); // clear input in scanner
        matrix_1 = getMatrix();
        matrix_2 = Arrays.copyOf(matrix_1, matrix_1.length);
    }

    /**
     * Asking user to input matrix from the file or from the console input
     * @return original matrix which is Matrix 1
     * @throws FileNotFoundException
     */
    private float[][] getMatrix() throws FileNotFoundException {
        matrix_1 = new float[numEquations][numEquations + 1];
        int userChoice;

        do {
            System.out.println("1. Input matrix from input console. ");
            System.out.println("2. Input matrix from a file.");
            System.out.print("Select menu 1 or 2: ");
            userChoice = keyboard.nextInt();
            keyboard.nextLine();

            if (userChoice == 1)
                getMatrix_fromInputConsole();
            else if (userChoice == 2)
                getMatrix_fromFile();
            else
                System.out.print("Invalid Choice!!");
        } while (userChoice != 1 && userChoice != 2);
        return matrix_1;
    }

    /**
     * Function that asks the user to enter number of equations n, where n <= 10.
     * The user should input only the coefficient of the matrix including the b values.
     * Nothing is being passed in the parameter.
     */
    public void getMatrix_fromInputConsole() {
        String[] coefficients;

        System.out.println("\nEnter " + (numEquations+1) + " coefficient for each equation separated by a space:");

        // rows
        for (int i = 0; i < numEquations; i++) {
            System.out.print("Equation " + (i + 1) + ":     ");
            coefficients = keyboard.nextLine().split("\\s+"); // splitting the space using regex

            // columns
            for (int j = 0; j < coefficients.length; j++) {
                matrix_1[i][j] = Float.parseFloat(coefficients[j]);
            }
        }
    }// end getUserInput_thoughtConsole


    /**
     * This method prompt the user to enter a file name in the console.
     * the values in the files should only contain augmented coefficient matrix
     * (including the b values).
     * @throws FileNotFoundException
     */
    private void getMatrix_fromFile() throws FileNotFoundException {
        System.out.print("Enter filename:   ");
        String fileName = keyboard.nextLine();

        // define a new scanner to read the file
        //Scanner sc = new Scanner(new BufferedReader(new FileReader(fileName)));
        try (Scanner sc = new Scanner(new BufferedReader(new FileReader(fileName)));)
        {
            for (int i = 0; i < numEquations && sc.hasNextFloat(); i++)
            {
                for (int j = 0; j < numEquations + 1 && sc.hasNextFloat(); j++)
                {
                    matrix_1[i][j] = sc.nextFloat();
                }
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("\nFile Not Found!!!");
            exit(0); // force the program to stop
        }
    } // end getMatrix_fromFile

    /**
     * Get the number of the equations entered by the user
     * Nothing is being passed in the variable
     * @return numbet input
     */
    public int getNumEquations() {
        System.out.print("Enter the number in the a set of Linear Equations: ");
        return keyboard.nextInt();
    } // end getNumEquations

    /**
     * Get the highest of the coefficient in every row
     * @param inputted matrix
     * @return the array of the highest coefficeient from each row
     */
    private float[] getHighest_absolutCoefficient(float[][] matrix) {
        float[] highestCoefficients = new float[numEquations];  // holds the highest coefficients from each row
        float maxCoefficient;

        for (int i = 0; i < numEquations; i++)
        {
            maxCoefficient = -1;
            for (int j = 0; j < numEquations + 1; j++)
            {
                maxCoefficient = Math.max(maxCoefficient, Math.abs(matrix[i][j]));
            }
            highestCoefficients[i] = maxCoefficient;
        }
        return highestCoefficients;
    } // end getHighest_absolutCoefficient


    /**
     * 
     * @param i
     * @param highestCoefficients
     * @param pivotOrder
     * @param matrix
     * @return
     */
    private int get_pivot_row(int i, float[] highestCoefficients, Stack<Integer> pivotOrder, float[][] matrix)
    {
        int pivot_row;      // holds row with the highest ratio
        float[][] highestRatioSet = {{-1, -1}};  // row number = [i][i], ratio = [1][2]
        float ratio;

        System.out.print("The Pivot ratios are = [");

        for (int j = 0; j < numEquations; j++)
        {
            // the next iteration will not include the pivot that is already used
            if (!pivotOrder.contains(j))
            {
                // push the previous pivot to the stack
                if (i + 1 == numEquations)
                {
                    pivotOrder.push(j);
                    break;
                } else
                {
                    ratio = Math.abs(matrix[j][i] / highestCoefficients[j]);

                    System.out.print(" " + ratio + " ");

                    // if current ratio is higher, then replace with the new max
                    if (ratio > highestRatioSet[0][1])
                    {
                        highestRatioSet[0][0] = j;
                        highestRatioSet[0][1] = ratio;
                    }
                }
            }
        }

        System.out.println("]");
        // return the value on top of stack if reaches to last iteration, otherwise add value to stack.
        if (i + 1 != numEquations)
        {
            pivot_row = (int) highestRatioSet[0][0];    // swap first row with the row with the highest ratio
            pivotOrder.push(pivot_row);
        } else
            pivot_row = pivotOrder.peek();

        return pivot_row;
    } // end get_pivot_row


    private void gaussian_Elimination(int i, int pivot_row, float[] highestCoefficients,
                                      Stack<Integer> pivotOrder, float[][] matrix)
    {
        float num_pivot;    // hold of number pivoting
        float pivot_ratio;  // holds ratio (each value in the 1st column divided by highest coefficient value)
        float target_numPivot; // matrix that is being change the order
        float highestCoefficient;   // holds the highest values from every row

        if (i + 1 != numEquations)
        {
            for (int j = 0; j < numEquations; j++)
            {
                if ((!pivotOrder.contains(j)) || matrix[j][i] == 0)
                {
                    pivot_ratio = matrix[pivot_row][i] / matrix[j][i];
                    highestCoefficient = highestCoefficients[pivot_row];

                    for (int k = 0; k < numEquations + 1; k++)
                    {
                        num_pivot = matrix[pivot_row][k];
                        target_numPivot = matrix[j][k];
                        matrix[j][k] = (num_pivot - pivot_ratio * target_numPivot) / highestCoefficient;
                    }
                    // print new matrices
                    displayMatrix(matrix);
                }
            }
        }
    }





    public static void displayMatrix(float[][] matrix) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        // iterate to all values in the matrix

        for (float[] row : matrix) {
            System.out.print("[ ");
            for(int column = 0; column < row.length; column++) {
                System.out.print(decimalFormat.format(row[column]) + " ");
            }
            System.out.println("]");
        }
        System.out.println();
    } // end displayMatrix


    public static void cloneMatrix(float[][] target, float[][] source){
        for(int i = 0; i < source.length; i++)
            target[i] = source[i].clone();
    }

    private void backSubstitution(float[][] matrix, Stack<Integer> pivotOrder, float[] solutions)
    {
        int pivot_row;
        float tempHighest;

        // iterates through pivot order
        for (int i = numEquations - 1; !pivotOrder.empty(); i--)
        {
            // set the latest pivot in the pivotOrder then delete it from stack
            pivot_row = pivotOrder.pop();
            tempHighest = matrix[pivot_row][i];
            // set solution to the b divided by xj (the highest absolute coefficient from each row)
            solutions[i] = matrix[pivot_row][numEquations] / tempHighest;

            // iterate to value in the column
            for (int j = numEquations - 1; j >= 0; j--)
            {
                if (i != j)
                {
                    solutions[i] -= (matrix[pivot_row][j] / tempHighest) * solutions[j];
                }
            }
        }
    } // end backSubtitution




    public void find_scaled_pivoting (float[][] matrix) {
        float[] highestCoefficients = getHighest_absolutCoefficient(matrix);
        int row_pivot;      // holds the row number as a pivot point
        float[] results = new float[numEquations];

        // initial values in matrix to zeros
        Arrays.fill(results, 0);

        // stack the pivotal equations
        Stack<Integer> pivotOrder = new Stack<>();

        // get augmented matrix using Gaussian Elimination
        for(int i = 0; i < numEquations; i++) {
            // get equation of the pivot row
            row_pivot = get_pivot_row(i, highestCoefficients, pivotOrder, matrix);

            if (i != (numEquations-1))
            {
                System.out.println("The pivot row selected = " + (row_pivot + 1));
                System.out.println("Matrix form after performing elimination: ");
            }

            // find a new matrix using Gaussian Elimination
            gaussian_Elimination(i, row_pivot, highestCoefficients, pivotOrder, matrix);
        }
        // back substitution
        backSubstitution(matrix, pivotOrder, results);
        System.out.println("Solution after back substitution:");
        displaySolution(results);
    }

    public void displaySolution(float[] solutions){
        // 4 decimal places
        DecimalFormat floatingNumber = new DecimalFormat("#.###");

        char ch = 'q';

        for(int i = 0; i < solutions.length; i++)
        {
            System.out.println((char)(i+(int)ch) + " = " + floatingNumber.format(solutions[i]));
        }
        System.out.println();
    } // end displaySolution

}

// need to put comment what each method does
// need to create a txt document
// need to test for bigger equation
// polishing the display
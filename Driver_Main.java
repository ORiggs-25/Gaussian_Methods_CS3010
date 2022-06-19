import java.io.FileNotFoundException;
public class Driver_Main
{
    public static void main(String[] args) throws FileNotFoundException
    {
        Scaled_partial_pivoting gaussian = new Scaled_partial_pivoting();
        float [][] matrix1 = gaussian.matrix_1;
        float [][] matrix2 = new float[matrix1.length][matrix1[0].length];
        System.out.println("\nGaussian Elimination using Scaled Partial Pivoting: ");
        gaussian.cloneMatrix(matrix2, matrix1);
        gaussian.displayMatrix(matrix2);

        gaussian.find_scaled_pivoting(matrix2);
        gaussian.cloneMatrix(matrix2, matrix1);
    }
}

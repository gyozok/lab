package main.java.math_and_geometry;

public class RotateImage {
    public void rotate(int[][] matrix) {
        int[][] rotated = new int[matrix[0].length][matrix.length];
        for (int r=0; r<matrix.length; r++) {
            for (int c = 0; c<matrix[0].length; c++) {
                rotated[c][matrix.length - r -1] = matrix[r][c];
            }
        }
        System.out.println(rotated);
    }

    public void rotateInPlace(int[][] matrix) {
        int n = matrix.length - 1;
        int m = matrix.length /2;
        for (int j=0; j<m; j++) {
            for (int i = j; i < n-j; i++) {
                int tmp = matrix[j][i];
                matrix[j][i] = matrix[n - i][j];
                matrix[n - i][j] = matrix[n - j][n - i];
                matrix[n - j][n - i] = matrix[i][n - j];
                matrix[i][n - j] = tmp;
            }
        }

        System.out.println(matrix);
    }

    static void main() {
        RotateImage sut = new RotateImage();

        int[][] matrix = {
                {1,2},
                {3,4}
        };

        sut.rotate(matrix);
        sut.rotateInPlace(matrix);

        int[][] matrix2 = {
                {1,2,3},
                {4,5,6},
                {7,8,9}
        };
        sut.rotate(matrix2);
        sut.rotateInPlace(matrix2);

        int[][] matrix3= {{5,1,9,11}, {2,4,8,10},{13,3,6,7}, {15,14,12,16}};
        sut.rotate(matrix3);
        sut.rotateInPlace(matrix3);
    }
}

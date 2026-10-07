package main.java.math_and_geometry;

public class SetMatrixZeroes {

    public void setZeroes(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[] rowZero = new boolean[rows];
        boolean[] colZero = new boolean[cols];
        for (int r = 0; r<rows; r++) {
            for (int c = 0; c<cols; c++) {
                if (matrix[r][c] == 0) {
                    rowZero[r] = true;
                    colZero[c] = true;
                }
            }
        }

        for (int r=0; r<rows; r++) {
            if (rowZero[r]) {
                for (int i=0; i<cols; i++) {
                    matrix[r][i] = 0;
                }
            }
        }
        for (int c=0; c<cols; c++) {
            if (colZero[c]) {
                for (int i=0; i<rows; i++) {
                    matrix[i][c] = 0;
                }
            }
        }
        System.out.println();
    }

    public void setZeroInPlace(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        boolean rowZero = false;
        for (int r = 0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                if (matrix[r][c] == 0) {
                    if (r == 0) {
                        rowZero = true;
                    } else {
                        matrix[r][0] = 0;
                    }
                    matrix[0][c] = 0;
                }
            }
        }

        for (int r = 1;r<rows; r++) {
            for (int c=1; c<cols; c++) {
                if (matrix[r][0] == 0 || matrix[0][c] == 0) {
                    matrix[r][c] = 0;
                }
            }
        }

        if (matrix[0][0] == 0) {
            for (int r=0; r<rows; r++) {
                matrix[r][0] = 0;
            }
        }

        if (rowZero) {
            for (int c=0; c<cols; c++) {
                matrix[0][c] = 0;
            }
        }
        System.out.println();
    }

    static void main() {
        SetMatrixZeroes sut = new SetMatrixZeroes();

        int[][] matrix = {
                {0,1},
                {1,0}
        };
        sut.setZeroes(matrix);
        matrix = new int[][] {
                {0,1},
                {1,0}
        };
        sut.setZeroInPlace(matrix);

        int[][] matrix2 = {
                {1,2,3},
                {4,0,5},
                {6,7,8}
        };
        sut.setZeroes(matrix2);
        matrix2 = new int[][]{
                {1,2,3},
                {4,0,5},
                {6,7,8}
        };
        sut.setZeroInPlace(matrix2);
    }
}

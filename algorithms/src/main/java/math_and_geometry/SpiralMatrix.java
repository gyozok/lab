package main.java.math_and_geometry;

import java.util.LinkedList;
import java.util.List;

public class SpiralMatrix {

    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new LinkedList<>();
        int left = 0;
        int right = matrix[0].length;
        int top = 0;
        int bottom = matrix.length;

        while (left < right && top < bottom) {
            //top row
            for (int c = left; c<right; c++) {
                result.add(matrix[top][c]);
            }
            top++;
            //right column
            for (int r = top; r<bottom; r++) {
                result.add(matrix[r][right-1]);
            }
            right--;
            if (!(left<right && top<bottom)) {
                break;
            }
            //bottom row
            for (int c=right-1; c>= left; c--) {
                result.add(matrix[bottom-1][c]);
            }
            bottom--;
            //left column
            for (int r=bottom-1; r>=top; r--) {
                result.add(matrix[r][left]);
            }
            left++;
        }
        return result;
    }

    static void main() {
        SpiralMatrix sut = new SpiralMatrix();

        int[][] matrix = {
                {1,2},
                {3,4}
        };
        System.out.println(sut.spiralOrder(matrix));

        int[][] matrix2 = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8,9}
        };
        System.out.println(sut.spiralOrder(matrix2));

        int[][] matrix3 = {
                {1,2,3,4},
                {5,6,7,8},
                {9,10, 11, 12}
        };
        System.out.println(sut.spiralOrder(matrix3));
    }
}

package edu.konditer.async;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Matrix {
    
    private final double[][] matrix;
    private final Shape shape;

    public Matrix(double[][] matrix) {
        this.shape = new Shape(matrix.length, matrix[0].length);
        this.matrix = matrix;
    }

    public int getWidth() {
        return shape.getWidth();
    }

    public int getHeight() {
        return shape.getHeight();
    }

    public double getItem(int i, int j) {
        return matrix[i][j];
    }

    public boolean isSquare() {
        return shape.getHeight() == shape.getWidth();
    }

    public static double determinant(Matrix matrix) {
        if (!matrix.isSquare()) throw new IllegalArgumentException("Matrix must be square");

        if (matrix.getHeight() == 0) {
            return 0.0;
        }
        if (matrix.getHeight() == 1) {
            return matrix.getItem(0, 0);
        }
        if (matrix.getHeight() == 2) {
            return matrix.getItem(0, 0) * matrix.getItem(1, 1) -
                    matrix.getItem(0, 1) * matrix.getItem(1, 0);
        }

        double det = 0.0;
        for (int i = 0; i < matrix.getHeight(); i++) {
            if (matrix.getItem(0, i) == 0) continue;
            int sign = (i % 2 == 0) ? 1 : -1;
            det += determinant(subMatrix(matrix, 0, i)) * matrix.getItem(0, i) * sign;
        }
        return det;
    }

    public static double fastDeterminant(Matrix matrix) {
        if (!matrix.isSquare()) throw new IllegalArgumentException("Matrix must be square");
        DetCounter detCounter = new DetCounter(matrix);

        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            return forkJoinPool.invoke(detCounter);
        }
    }

    public static Matrix subMatrix(Matrix matrix, int crossI, int crossJ) {
        int newI = 0, newJ;
        double[][] subMatrix = new double[matrix.getHeight() - 1][matrix.getWidth() - 1];

        for (int i = 0; i < matrix.getHeight(); i++) {
            if (i == crossI) continue;

            newJ = 0;
            for (int j = 0; j < matrix.getWidth(); j++) {
                if (j == crossJ) continue;

                subMatrix[newI][newJ] = matrix.getItem(i, j);
                newJ++;
            }
            newI++;
        }

        return new Matrix(subMatrix);
    }

    public static Matrix fromText(String matText) throws NumberFormatException {
        String[] rows = matText.split("\n");
        double[][] matrix = new double[rows.length][rows[0].split(" ").length];

        for (int i = 0; i < rows.length; i++) {
            String[] rowItems = rows[i].split(" ");
            for (int j = 0; j < rowItems.length; j++) {
                matrix[i][j] = Double.parseDouble(rowItems[j]);
            }
        }

        return new Matrix(matrix);
    }

    @Override
    public String toString() {
        String out = "";

        for (double[] row : matrix) {
            for (double item : row) {
                out += item + " ";
            }
            out += "\n";
        }

        return out;
    }

    private static class DetCounter extends RecursiveTask<Double> {
        private final Matrix matrix;

        public DetCounter(Matrix matrix) {
            this.matrix = matrix;
        }

        @Override
        protected Double compute() {
            if (matrix.getHeight() < 9) {
                return Matrix.determinant(matrix);
            }

            double det = 0.0;
            DetCounter[] subCounters = new DetCounter[matrix.getHeight()];
            for (int i = 0; i < matrix.getHeight(); i++) {
                if (matrix.getItem(0, i) == 0) continue;
                Matrix subMatrix = Matrix.subMatrix(matrix, 0, i);
                subCounters[i] = new DetCounter(subMatrix);
                subCounters[i].fork();
            }

            for (int i = 0; i < matrix.getHeight(); i++) {
                if (subCounters[i] == null) continue;
                int sign = (i % 2 == 0) ? 1 : -1;
                det += subCounters[i].join() * matrix.getItem(0, i) * sign;
            }

            return det;
        }
    }

    public static class Shape {
        private final int width;
        private final int height;

        public Shape(int w, int h) {
            this.width = w;
            this.height = h;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}

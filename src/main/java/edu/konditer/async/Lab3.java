package edu.konditer.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Lab3 {

    public static void main(String[] args) {
        String matrixText = readFile("./m1.txt");
        Matrix matrix = Matrix.fromText(matrixText);
        System.out.println(matrix);

        long time00 = System.currentTimeMillis();
        System.out.println(Matrix.determinant(matrix));
        long time01 = System.currentTimeMillis();
        System.out.println("Single thread: " + (time01 - time00));

        long time10 = System.currentTimeMillis();
        System.out.println(Matrix.fastDeterminant(matrix));
        long time11 = System.currentTimeMillis();
        System.out.println("Multi thread: " + (time11 - time10));
    }

    private static String readFile(String path) {
        String out = "";
        Path file = Path.of(path);
        try (InputStream in = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out += line + "\n";
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        return out;
    }
}

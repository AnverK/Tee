package main.java.ru.ifmo.rain.khusainov.tee;

public class Main {
    public static void main(String[] args) {
        Tee tee = Parser.newTee(args);
        if (tee == null) {
            System.exit(1);
        }
        if (tee.printInputStream(System.in) && tee.closeStreams()) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
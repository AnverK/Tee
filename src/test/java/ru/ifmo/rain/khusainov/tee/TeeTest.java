package test.java.ru.ifmo.rain.khusainov.tee;

import main.java.ru.ifmo.rain.khusainov.tee.Tee;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class TeeTest {
    private InputStream inputStream;
    private Tee tee;
    private ByteArrayOutputStream defaultOutputStream;
    private byte[] input;

    private byte[] generateInput(int n) {
        byte[] res = new byte[n];
        Random rnd = new Random(System.currentTimeMillis());
        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) (-128 + rnd.nextInt(255));
        }
        return res;
    }

    private byte[] appendInput(byte[] first, byte[] second) {
        byte[] res = new byte[first.length + second.length];
        System.arraycopy(first, 0, res, 0, first.length);
        System.arraycopy(second, 0, res, first.length, second.length);
        return res;
    }

    private void setTee(boolean apppend, boolean ignore, ArrayList<String> paths) {
        tee = new Tee(apppend, ignore, paths);
        Assert.assertNotEquals(null, tee);
    }

    private void setInputStream(int n) {
        input = generateInput(n);
        inputStream = new ByteArrayInputStream(input);
    }

    private void setNewDefaultOutputStream() {
        defaultOutputStream = new ByteArrayOutputStream();
        Assert.assertTrue(tee.setDefaultOutputStream(defaultOutputStream));
    }

    private boolean checkOutput(String path) throws IOException {
        InputStream fileInput = Files.newInputStream(Paths.get(path));
        int c, cur = 0;
        byte[] block = new byte[1024];
        while ((c = fileInput.read(block)) >= 0) {
            if (cur + c > input.length) {
                return false;
            }
            for (int i = cur; i < cur + c; i++) {
                if (input[i] != block[i - cur]) {
                    return false;
                }
            }
        }
        Files.deleteIfExists(Paths.get(path));
        fileInput.close();
        return true;
    }

    @Test
    public void testEmpty() {
        setTee(false, false, new ArrayList<>());
        setInputStream(0);

        defaultOutputStream = new ByteArrayOutputStream();
        Assert.assertTrue(tee.setDefaultOutputStream(defaultOutputStream));

        tee.printInputStream(inputStream);
        Assert.assertTrue(Arrays.equals(defaultOutputStream.toByteArray(), input));
    }

    @Test
    public void testWithoutFiles() {
        setTee(false, false, new ArrayList<>());
        setInputStream(100);
        setNewDefaultOutputStream();
        tee.printInputStream(inputStream);
        Assert.assertTrue(Arrays.equals(defaultOutputStream.toByteArray(), input));
    }

    @Test
    public void testAppendWithoutFiles() {
        setTee(true, false, new ArrayList<>());
        setInputStream(100);
        setNewDefaultOutputStream();
        tee.printInputStream(inputStream);
        Assert.assertTrue(Arrays.equals(defaultOutputStream.toByteArray(), input));

        byte[] secondWrite = generateInput(200);
        inputStream = new ByteArrayInputStream(secondWrite);
        tee.printInputStream(inputStream);
        Assert.assertTrue(Arrays.equals(defaultOutputStream.toByteArray(), appendInput(input, secondWrite)));
    }

    @Test
    public void testWithFiles() throws IOException {
        ArrayList<String> paths = new ArrayList<>();
        paths.add("output1.txt");
        paths.add("output2.txt");

        setTee(false, false, paths);
        setInputStream(100);
        setNewDefaultOutputStream();
        tee.printInputStream(inputStream);

        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(checkOutput(paths.get(i)));
        }
    }
}
package main.java.ru.ifmo.rain.khusainov.tee;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Tee {
    private boolean appendFlag;
    private boolean ignoreInterruptsFlag;
    private ArrayList<OutputStream> outputStreams;
    private ArrayList<String> outputName;   //for exceptions information
    private OutputStream defaultOutputStream = System.out;

    public Tee(boolean append, boolean ignore, List<String> paths) {
        appendFlag = append;
        ignoreInterruptsFlag = ignore;
        outputStreams = new ArrayList<>(paths.size() + 1);
        outputStreams.add(defaultOutputStream);

        outputName = new ArrayList<>(paths.size() + 1);
        outputName.add("console");

        for (String strPath : paths) {
            try {
                if (appendFlag) {
                    outputStreams.add(Files.newOutputStream(Paths.get(strPath), StandardOpenOption.CREATE, StandardOpenOption.APPEND));
                } else {
                    outputStreams.add(Files.newOutputStream(Paths.get(strPath)));
                }
                outputName.add(Paths.get(strPath).toString());  //not just strPath because we want some standardization of paths (e.g. same separators)
            } catch (IOException | SecurityException e) {
                if (!ignoreInterruptsFlag) {
                    System.err.println("Can not write in file with path: " + strPath);
                }
            } catch (InvalidPathException e) {
                if (!ignoreInterruptsFlag) {
                    System.err.println(strPath + " is invalid path");
                }
            }
        }
    }

    public boolean printInputStream(InputStream inputStream) {
        boolean ok = true;
        int c;
        final byte[] block = new byte[1024];
        try {
            while ((c = inputStream.read(block)) >= 0) {
                for (int i = 0; i < outputStreams.size(); i++) {
                    try {
                        outputStreams.get(i).write(block, 0, c);
                    } catch (IOException e) {
                        if (!ignoreInterruptsFlag) {
                            System.err.println("Error was occured during writing another block to " + outputName.get(i));
                        }
                        ok = false;
                    }
                }
            }
        } catch (IOException e) {
            if (!ignoreInterruptsFlag) {
                System.err.println("Error was occured during reading another block");
            }
            ok = false;
        }
        return ok;
    }

    public boolean closeStreams() {  //closing all streams except default
        boolean ok = true;
        for (int i = 1; i < outputStreams.size(); i++) {
            try {
                outputStreams.get(i).close();
            } catch (IOException e) {
                if (!ignoreInterruptsFlag) {
                    System.err.println("Error was occured during writing another block to " + outputName.get(i));
                }
                ok = false;
            }
        }
        return ok;
    }

    public boolean setDefaultOutputStream(OutputStream outputStream) {   //defaultOutputStream is not closing during printStream!
        if (outputStream == null) {
            return false;
        }
        defaultOutputStream = outputStream;
        ListIterator<OutputStream> listIterator = outputStreams.listIterator(0);
        listIterator.next();
        listIterator.set(outputStream);
        return true;
    }
}
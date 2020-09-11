package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Calculator {
    public int calcSum(String filepath) throws IOException {
        return lineReadTemplate(filepath, (line, value)->(Integer.parseInt(line)+value),0);
    }

    public int calcMul(String filepath) throws IOException {
        return lineReadTemplate(filepath, (line, value)->(Integer.parseInt(line)*value),1);
    }


    public <T> T lineReadTemplate(String filepath, LineCallBack<T> callBack, T initVal) throws IOException {
        BufferedReader br = null;
        T res = initVal;
        try {
            br = new BufferedReader(new FileReader(filepath));
            String line;
            while((line=br.readLine())!=null) res = callBack.Accumulate(line, res);
        } catch(IOException e)  {
            System.out.println(e.getMessage());
            throw e;
        }
        finally {
            if (br != null) br.close();
        }
        return res;
    }
}

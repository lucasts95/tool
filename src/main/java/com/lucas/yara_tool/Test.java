package com.lucas.yara_tool;

import java.io.*;
import java.util.Scanner;

public class Test {
    private String inputStr;

    public void setInputStr(String Str) {
        this.inputStr = Str.toUpperCase();
    }

    public String getInputStr() {
        return this.inputStr;
    }

    public void Save(String path) {
        File file = new File(path);
        if (file.exists()) {
            System.out.println("创建单个文件" + path + "失败，目标文件已经存在");
        }
        if (path.endsWith((File.separator))) {
            System.out.println("创建单个文件" + path + "失败，目标文件不能是目录");
        }
        if (!file.getParentFile().exists()) {
            System.out.println("目标文件所在的目录不存在，准备创建它!");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在的目录失败！");
            }
        }
        try {
            Printstream(file);
            Filewriter(file);
            Printwriter(file);
            FileOutstream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Printstream(File file) {
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(this.getInputStr());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void Filewriter(File file) {
        try {
            FileWriter fw = new FileWriter(file.getAbsolutePath(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.getInputStr());
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Printwriter(File file) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file.getAbsolutePath()));
            pw.println(this.getInputStr());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void FileOutstream(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            fos.write(this.getInputStr().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String Str = in.nextLine();
        Test temp = new Test();
        temp.setInputStr(Str);
        temp.Save("test.txt");

    }
    
    
}

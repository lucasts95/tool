package com.lucas.yara_tool;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ProcessResult {

    private static final Map<String, Set<String>> MATCHED_RESULT = new HashMap<>();
    private static final Map<String, ArrayList<String>> MATCHED_RESULT_SOL = new HashMap<>();

    private static final Set<String> MOCK = new HashSet<>();

    public static void main(String[] args) throws IOException {
//        if (args == null || args.length == 0) {
//            throw new RuntimeException("Missing file path to be processed.");
//        }
        // /Users/yaowang/Documents/CS/fork/yara-runner/incidents-rules/output/opt.txt
        String fileName = "/Users/yaowang/Documents/CS/efs/workspace/results/syntactic-analyzer/2023-01-24/missing_delegate_transfer/findings_aggregated.json";
        readUsingScanner(fileName);
        //System.out.println(MATCHED_RESULT_SOL);
        PrintStream out = new PrintStream("testOut.txt");
        System.setOut(out);
        MATCHED_RESULT_SOL.forEach((k, v) -> System.out.printf("%s %s\n", k, v.toString()));
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~Mocked~~~~~~~~~~~~~~~~~~~~~~");
//        System.out.printf("Mocked size = %d\n", MOCK.size());
//        MOCK.forEach(
//                id -> System.out.printf("https://accelerator.audit.certikpowered.info/project/%s/findings\n", id)
//        );
//
//        Scanner in = new Scanner(System.in);
//        String Str = in.nextLine();
//        Test temp = new Test();
//        temp.setInputStr(Str);
//        temp.Save("./test.txt");
//        saveMapToTxt(MATCHED_RESULT_SOL, "./test.txt");

    }

    private static void readUsingScanner(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        Scanner scanner = new Scanner(path);
        System.out.println("Read text file using Scanner");
        //逐行读取
        while (scanner.hasNextLine()) {
            //逐行处理
            String line = scanner.nextLine();
//            System.out.println("line:==="+line);
            String pId = queryPidAndSC(line);
            if (!Objects.equals(pId, "")) {
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                    String line2 = scanner.nextLine();
                    getLoc(pId, line2);
                }

            }

        }
        scanner.close();
    }

    private static void getLoc(String Pid, String line) {
        if (line.contains("line")) {
            String loc = line.substring(line.indexOf(":") + 2, line.indexOf(","));
            ArrayList<String> scAndLoc = MATCHED_RESULT_SOL.get(Pid);
            int size = scAndLoc.size();
            String sc = scAndLoc.get(size - 1);
            String concat = sc + " L" + loc;
            scAndLoc.remove(size - 1);
            scAndLoc.add(concat);
        }

    }

    private static String queryPidAndSC(String line) {
        if (line.endsWith(".sol\",")) {
//            boolean isMock = line.toLowerCase().contains("mock");
            int index = line.indexOf(' ');
            String rule = line.substring(0, index);
            String[] strArr = line.substring(index + 9).split("/");
            // sol文件
            String[] solNameList = strArr[strArr.length - 1].split("\"");
            String scName = solNameList[0];
//        System.out.println("scName= /s" + scName);
            //System.out.println(Arrays.toString(strArr));
            String pId = strArr[5];
//            if (isMock) {
//                MOCK.add(pId);
//            } else {
            if (MATCHED_RESULT_SOL.containsKey(pId)) {
                // PID已存在就是项目里有多个sc,或者sc下有多个fn匹配
                MATCHED_RESULT_SOL.get(pId).add(scName);
            } else {
                // 新的PID直接加入新的键值对
                ArrayList<String> scAndLoc = new ArrayList<>();
                scAndLoc.add(scName);
                MATCHED_RESULT_SOL.put(pId, scAndLoc);
            }

//            }
            return pId;
        }
        return "";
    }

    public static void saveMapToTxt(Map<String, Set<String>> map, String filePath) {
        OutputStreamWriter outFile = null;
        FileOutputStream fileName;
        try {
            fileName = new FileOutputStream(filePath);
            outFile = new OutputStreamWriter(fileName);
            StringBuilder str = new StringBuilder();
            for (String key : map.keySet()) {
                str.append(key).append(";").append(map.get(key)).append("\n");
            }
            outFile.write(String.valueOf(str));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                outFile.flush();
                outFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


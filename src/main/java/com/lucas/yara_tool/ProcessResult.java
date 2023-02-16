package com.lucas.yara_tool;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ProcessResult {

    private static final Map<String, Set<String>> MATCHED_RESULT = new HashMap<>();
    private static final Map<String, Set<String>> MATCHED_RESULT_SOL = new HashMap<>();

    private static final Set<String> MOCK = new HashSet<>();

    public static void main(String[] args) throws IOException {
//        if (args == null || args.length == 0) {
//            throw new RuntimeException("Missing file path to be processed.");
//        }
        // /Users/yaowang/Documents/CS/fork/yara-runner/incidents-rules/output/opt.txt
        String fileName = "/Users/yaowang/Documents/CS/efs 2/workspace/results/syntactic-analyzer/2023-01-24/missing_delegate_transfer/findings_aggregated.json";
        readUsingScanner(fileName);
        //System.out.println(MATCHED_RESULT_SOL);
        MATCHED_RESULT_SOL.forEach((k, v) -> System.out.printf("%s https://accelerator.audit.certikpowered.info/project/%s  %s\n", k, k, v));
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
        saveMapToTxt(MATCHED_RESULT_SOL, "./test.txt");

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
            processSingleLine(line);
        }
        scanner.close();
    }

    private static void processSingleLine(String line) {
        if (line == null || !line.endsWith(".sol\",")) {
            return;
        }
        boolean isMock = line.toLowerCase().contains("mock");
        int index = line.indexOf(' ');
        String rule = line.substring(0, index);
        String[] strArr = line.substring(index + 9).split("/");
        // sol文件
        String solName = strArr[strArr.length - 1];
//        System.out.println("solName= /s" + solName);
        //System.out.println(Arrays.toString(strArr));
        String value = strArr[5];
        if (isMock) {
            MOCK.add(value);
        } else {
            // 存在一个项目下多个sc都匹配到rule的情况
            if (MATCHED_RESULT_SOL.containsKey(value)) {
                MATCHED_RESULT_SOL.get(value).add(solName);
            } else {
                // value是commit, 第一次增加给set,并给solName
                Set<String> set = new HashSet<>();
                set.add(solName);
                MATCHED_RESULT_SOL.put(value, set);
            }
        }

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


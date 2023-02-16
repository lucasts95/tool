package com.lucas.yara_tool;

import java.io.IOException;
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
        String fileName = "/Users/yaowang/Documents/CS/fork/yara-runner/incidents-rules/output/131.txt";
        readUsingScanner(fileName);
        //System.out.println(MATCHED_RESULT_SOL);
        MATCHED_RESULT.forEach((k, v) -> {
            System.out.printf("Rule = %s, count of project = %d\n", k, v.size());
            v.stream().sorted().forEach(
                    id -> {
                        // id是commit
                        if (MATCHED_RESULT_SOL.containsKey(id)) {
                            MATCHED_RESULT_SOL.get(id).forEach(sol -> System.out.printf("%s https://accelerator.audit.certikpowered.info/project/%s  %s\n", id, id, sol));
                        }
                    }
            );
        });
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~Mocked~~~~~~~~~~~~~~~~~~~~~~");
        System.out.printf("Mocked size = %d\n", MOCK.size());
        MOCK.forEach(
                id -> System.out.printf("https://accelerator.audit.certikpowered.info/project/%s/findings\n", id)
        );

    }

    private static void readUsingScanner(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        Scanner scanner = new Scanner(path);
        System.out.println("Read text file using Scanner");
        //逐行读取
        while (scanner.hasNextLine()) {
            //逐行处理
            String line = scanner.nextLine();
            //System.out.println("line:==="+line);
            processSingleLine(line);
        }
        scanner.close();
    }

    private static void processSingleLine(String line) {
        if (line == null || !line.endsWith(".sol")) {
            return;
        }
        boolean isMock = line.toLowerCase().contains("mock");
        int index = line.indexOf(' ');
        String rule = line.substring(0, index);
        String[] strArr = line.substring(index + 9).split("/");
        // sol文件
        String solName = strArr[strArr.length - 1];
        //System.out.println(Arrays.toString(strArr));
        String value = strArr[2];
        if (isMock) {
            MOCK.add(value);
        } else {
            if (MATCHED_RESULT.containsKey(rule)) {
                MATCHED_RESULT.get(rule).add(value);
                // 存在一个项目下多个sc都匹配到rule的情况
                if (MATCHED_RESULT_SOL.containsKey(value)) {
                    MATCHED_RESULT_SOL.get(value).add(solName);
                } else {
                    // value是commit, 第一次增加给set,并给solName
                    Set<String> set = new HashSet<>();
                    set.add(solName);
                    MATCHED_RESULT_SOL.put(value, set);
                }
            } else {
                Set<String> set = new HashSet<>();
                set.add(value);
                MATCHED_RESULT.put(rule, set);

            }
        }

    }


}
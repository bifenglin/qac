package com.maple.qac;

import cn.hutool.core.util.RandomUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.maple.qac.model.Question;

import java.io.IOException;
import java.util.*;

/**
 * @author maple
 */
public class Bootstrap {
    // 文件名称

    public static void main(String[] args) throws IOException {
        String PDFPATH = Bootstrap.class.getClassLoader().getResource("test.pdf").toString();
        System.out.println("开始加载题目");
        String content = readPdf(PDFPATH);
        ArrayList<String> list = splitContent(content);
        ArrayList<Question> questions = getQuestions(list);
        System.out.println("加载完成");
        System.out.println("打乱顺序出题,输入quit字符退出");
        Collections.shuffle(questions);
        int i = 0;
        for (Question question :
                questions) {
            System.out.println("题目" + i + ":" + question.getTitle());
            question.setOptions(sort(question.getOptions()));
            int optionIndex = 0;
            for (String option :
                    question.getOptions()) {
                if (optionIndex == 0){
                    System.out.print("A:");
                } else if (optionIndex == 1){
                    System.out.print("B:");
                } else if (optionIndex == 2){
                    System.out.print("C:");
                } else if (optionIndex == 3){
                    System.out.print("D:");
                }
                System.out.println(option);
                optionIndex++;
            }
            System.out.println("输入答案");
            Scanner scan = new Scanner(System.in);
            String read = scan.nextLine();
            i++;
            if (read.equals("quit"))
                break;
            System.out.println(confirm(read, question.getOptions(), question.getAnswer()));
        }
        System.out.println("答题结束");
    }

    // 读取pdf
    private static String readPdf(String fileName) {
        String pageContent = "";
        try {
            PdfReader reader = new PdfReader(fileName);
            int pageNum = reader.getNumberOfPages();
            for (int i = 1; i <= pageNum; i++) {
                pageContent += PdfTextExtractor.getTextFromPage(reader, i);//读取第i页的文档内容
            }
            return pageContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageContent;
    }

    // 将content内容变成题目集合
    private static ArrayList<String> splitContent(String content) {
        String[] temp = content.split("单选题");
        ArrayList<String> list = new ArrayList<>();
        int i = 2;
        while (temp.length > 1) {
            // 发现第229题格式不正确 特殊处理
            if (i == 229) {
                temp = temp[1].split("\n" + String.valueOf(i) + ".");
            } else {
                temp = temp[1].split("\n" + String.valueOf(i) + ". ");
            }
            list.add(temp[0]);
            i++;
        }
        return list;
    }

    private static ArrayList<Question> getQuestions(ArrayList<String> list) {
        ArrayList<Question> questions = new ArrayList<Question>();
        list.stream().forEach(index -> {
            String[] options = new String[4];
            Question question = new Question();
            String[] temp = new String[4];
            if (index.contains("\nA.")) {
                temp = index.split("\nA.");
                question.setTitle(temp[0]);
            }
            if (index.contains("\nB.")) {
                temp = temp[1].split("\nB.");
                options[0] = temp[0];
            }
            if (index.contains("\nC.")) {
                temp = temp[1].split("\nC.");
                options[1] = temp[0];
            }
            if (index.contains("\nC.")) {
                temp = temp[1].split("\nD.");
                options[2] = temp[0];
            }
            temp = temp[1].split("标准答案");
            options[3] = temp[0];
            question.setOptions(options);
            switch (temp[1].charAt(1)) {
                case 'A':
                    question.setAnswer(options[0]);
                    break;
                case 'B':
                    question.setAnswer(options[1]);
                    break;
                case 'C':
                    question.setAnswer(options[2]);
                    break;
                case 'D':
                    question.setAnswer(options[3]);
                    break;
            }
            questions.add(question);
        });
        return questions;
    }

    // 重新排列
    private static String[] sort(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int p = RandomUtil.randomInt(i + 1);
            String tmp = arr[i];
            arr[i] = arr[p];
            arr[p] = tmp;
        }
        return arr;
    }

    // 确认答案，返回结果
    public static String confirm(String read, String[] options, String answer) {
        StringBuffer stringBuffer = new StringBuffer();
        String readOption = "";
        String tempOption;
        switch (read) {
            case "A":
                readOption = options[0];
                break;
            case "B":
                readOption = options[1];
                break;
            case "C":
                readOption = options[2];
                break;
            case "D":
                readOption = options[3];
                break;
        }
        if (readOption.equals(answer)) {
            stringBuffer.append("正确!");
        } else {
            stringBuffer.append("错误!正确答案").append(answer);
        }
        return stringBuffer.toString();
    }
}

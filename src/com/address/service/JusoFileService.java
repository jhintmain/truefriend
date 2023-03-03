package com.address.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JusoFileService {


    // 주소목록 저장 위치
    private String jusoDataListPath = "src/data/juso_data_list.txt";
    // 주소매칭결과 저장 위치
    private String jusoDataResultPath = "src/data/juso_data_result.txt";

    private BufferedWriter writer;

    // 주소목록 file read후 List로 return
    public List<String> readList() throws IOException {

        List<String> addressList = new ArrayList<>();
        String line;

        // 주소 목록 읽기 (약 3만건 테스트 데이터)
        File file = new File(this.jusoDataListPath);
        FileReader filereader = new FileReader(file);
        BufferedReader bufReader = new BufferedReader(filereader);

        while ((line = bufReader.readLine()) != null) {
            String[] lineData = line.split("\\|");
//            for (int i = 0; i < lineData.length; i++) {
            for (int i = 0; i < 100; i++) {
                String address = lineData[i];
                addressList.add(address);
            }
        }
        bufReader.close();

        return addressList;
    }

    // 주소매칭결과 쓰기 객체 생성
    public void makeBufferedWriter() throws IOException {
        // 매칭 결과 파일 생성
        File fileResult = new File(this.jusoDataResultPath);
        if (!fileResult.exists()) { // 파일이 존재하지 않으면
            fileResult.createNewFile(); // 신규생성
        }
        setWriter(new BufferedWriter(new FileWriter(fileResult, false)));
    }

    // 파일 쓰기
    public void writer(String str) throws IOException {
        System.out.println(str);
        this.writer.write(str);
        this.writer.newLine();
    }

    public void writerNoNewLine(String str) throws IOException {
        System.out.print(str);
        this.writer.write(str);
    }

    // writer close
    public void close() throws IOException {
        this.writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
        this.writer.close(); // 스트림 종료
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }


    public String getJusoDataListPath() {
        return jusoDataListPath;
    }

    public void setJusoDataListPath(String jusoDataListPath) {
        this.jusoDataListPath = jusoDataListPath;
    }

    public String getJusoDataResultPath() {
        return jusoDataResultPath;
    }

    public void setJusoDataResultPath(String jusoDataResultPath) {
        this.jusoDataResultPath = jusoDataResultPath;
    }
}

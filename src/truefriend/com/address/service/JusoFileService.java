package truefriend.com.address.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JusoFileService {

    private static final String JUSO_DATA_LIST_PATH = "src/data/juso_data_api.txt";
    private static final String JUSO_DATA_RESULT_PATH = "src/data/juso_data_result.txt";

    private BufferedWriter writer;


    public List<String> readList() throws IOException {

        List<String> addressList = new ArrayList<>();
        String line;

        // 주소 목록 읽기 (약 3만건 테스트 데이터)
        File file = new File(JUSO_DATA_LIST_PATH);
        FileReader filereader = new FileReader(file);
        BufferedReader bufReader = new BufferedReader(filereader);

        while ((line = bufReader.readLine()) != null) {
            String[] rgAddress = line.split("\\|");
                for (int i = 0; i < rgAddress.length; i++) {
//            for (int i = 0; i < 100; i++) {
                String address = rgAddress[i];
                addressList.add(address);
            }
        }
        bufReader.close();

        return addressList;
    }

    public void makeBufferedWriter() throws IOException {
        // 매칭 결과 파일 생성
        File fileResult = new File(JUSO_DATA_RESULT_PATH);
        if (!fileResult.exists()) { // 파일이 존재하지 않으면
            fileResult.createNewFile(); // 신규생성
        }
        setWriter(new BufferedWriter(new FileWriter(fileResult, false)));

        writer("==============================================================");
        writer("[Thread name][로/길매칭된 문자 List] 주소(오리지널) : 최종결과(도/로)");
        writer("==============================================================");
    }


    public void writer(String str) throws IOException {
        System.out.println(str);
        this.writer.write(str);
        this.writer.newLine();
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void close() throws IOException {
        this.writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
        this.writer.close(); // 스트림 종료
    }
}

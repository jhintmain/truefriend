import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Main {

    public static void main(String[] args) throws Exception {

        String addr1 = "성남, 분당 백 현 로 265 푸른마을 아파트로 보내주세요!!";
        String addr2 = "마포구 도화-2길 코끼리분식";

        JusoAPIService juso = new JusoAPIService();
        Stack<String> addressList = new Stack<String>();

        try {
            //파일 객체 생성
            File file = new File("src/data/juso_data.txt");
            //입력 스트림 생성
            FileReader filereader = new FileReader(file);
            //입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(filereader);

            String line = "";
            while ((line = bufReader.readLine()) != null) {
                String[] rgAddress = line.split("\\|");
//                for (int i = 0; i < rgAddress.length; i++) {
                for (int i = 0; i < 10; i++) {

                    String address = rgAddress[i];
//                    String address = addr2;
                    System.out.print(address + "=>");
                    boolean flag = true;

                    addressList = juso.addressFilter(address);
                    int cnt = addressList.size();
                    for (int j = 0; j < cnt; j++) {
//                        System.out.println("filter result : " + addressList.peek());
                        String a = juso.findJuso(addressList.pop());
//                        System.out.println(a);
                        if (!a.equals("")) {
                            System.out.println(a);
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        addressList = juso.addressFilter2(address);
                        cnt = addressList.size();
                        for (int j = 0; j < cnt; j++) {
                            String a = juso.findJuso(addressList.pop());
                            if (!a.equals("")) {
                                System.out.println(a);
                                break;
                            }
                        }
                    }

                }
            }

            //.readLine()은 끝에 개행문자를 읽지 않는다.
            bufReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("???");
            // TODO: handle exception
        } catch (IOException e) {
            System.out.println(e);
        }


    }


}
import com.google.gson.JsonArray;

import java.io.*;
import java.util.Stack;

public class Main {

    public static void main(String[] args) {

        FindCorrectAddressService findCorrectAddressService = new FindCorrectAddressService();
        Stack<String> stackAddress;

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
                for (int i = 0; i < 5; i++) {

                    boolean findFlag = false;
                    String address = rgAddress[i];

                    for(int repet=0; repet<2; repet++){
                        stackAddress = repet ==0 ? findCorrectAddressService.addressFilter1(address) : findCorrectAddressService.addressFilter2(address);
    /*                    System.out.println(stackAddress);*/

                        for (String addr : stackAddress) {
                            String findAddr = findCorrectAddressService.findJuso(addr);
                            if (!findAddr.equals("")) {
//                                System.out.println(address + " : " + findAddr);
                                findFlag = true;
                                break;
                            } else {
//                                System.out.println(address + " : 올바른 주소가 아닙니다");
                            }
                        }
                        if(findFlag){
                            break;
                        }
                    }
                }
            }


            bufReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("???");
            // TODO: handle exception
        } catch (IOException e) {
            System.out.println(e);
        }

    }

//    public static void main(String[] args) {
//
//        JusoAPIService juso = new JusoAPIService();
//        Stack<String> addressList;
//
//        try {
//            //파일 객체 생성
//            File file = new File("src/data/juso_data.txt");
//            //입력 스트림 생성
//            FileReader filereader = new FileReader(file);
//            //입력 버퍼 생성
//            BufferedReader bufReader = new BufferedReader(filereader);
//
//            String line = "";
//            while ((line = bufReader.readLine()) != null) {
//                String[] rgAddress = line.split("\\|");
////                for (int i = 0; i < rgAddress.length; i++) {
//                for (int i = 0; i < 10; i++) {
//
//                    String address = rgAddress[i];
//                    System.out.print(address + "=>");
//                    boolean flag = true;
//
//                    addressList = juso.addressFilter(address);
//                    int cnt = addressList.size();
//                    for (int j = 0; j < cnt; j++) {
////                        System.out.println("filter result : " + addressList.peek());
//                        String a = juso.findJuso(addressList.pop());
////                        System.out.println(a);
//                        if (!a.equals("")) {
//                            System.out.println(a);
//                            flag = false;
//                            break;
//                        }
//                    }
//
//                    if (flag) {
//                        addressList = juso.addressFilter2(address);
//                        cnt = addressList.size();
//                        for (int j = 0; j < cnt; j++) {
//                            String a = juso.findJuso(addressList.pop());
//                            if (!a.equals("")) {
//                                System.out.println(a);
//                                break;
//                            }
//                        }
//                    }
//
//                }
//            }
//
//            //.readLine()은 끝에 개행문자를 읽지 않는다.
//            bufReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("???");
//            // TODO: handle exception
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//
//
//    }


}
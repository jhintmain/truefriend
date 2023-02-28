import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {



    public static Collection<List<String>> partition(Stack<String> stack) {
        final List<String> address = new ArrayList(stack);
        final int chunkSize = stack.size() / 4;
        System.out.println(chunkSize);
        final AtomicInteger counter = new AtomicInteger();
        final Collection<List<String>> result = address.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
        return result;
    }

    public static void main(String[] args) throws IOException {

        String start_time = String.valueOf(new Date());

        Stack<String> stackAddress = new Stack<>();

        try {
            // 주소 목록 읽기 약 3만건
            File file = new File("src/data/juso_data_api.txt");
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);

            String line = "";

            while ((line = bufReader.readLine()) != null) {
                String[] rgAddress = line.split("\\|");
                for (int i = 0; i < rgAddress.length; i++) {
//                for (int i = 0; i < 10000; i++) {
                    String address = rgAddress[i];
                    stackAddress.add(address);
                }
            }
            bufReader.close();


            System.out.println("================HASH SET ======================");
            Iterator<String> iterator = stackAddress.iterator();
            System.out.println(stackAddress.size());
            while (iterator.hasNext()) {
                System.out.print(iterator.next() + "|");
            }

            System.out.println("================PARTITION ======================");

            File file_result = new File("src/data/juso_data_result_single.txt"); // File객체 생성
            if (!file_result.exists()) { // 파일이 존재하지 않으면
                file_result.createNewFile(); // 신규생성
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file_result, true));

            partition(stackAddress).parallelStream().forEach(addressList -> {


                HashSet<String> set = new HashSet<>();
                boolean find_flag = false;
                FindCorrectAddressService findCorrectAddressService = new FindCorrectAddressService();

                for (String address : addressList) {

                    Stack<String> stack_address = findCorrectAddressService.addressFilter1(address);

//                    System.out.println("["+Thread.currentThread().getName()+"]" + stack_address.toString() + address+" : ");

                    for (String addr : stack_address) {

                        // HashSet에 같은 도로명 있는지 확인, 존재시 api 보내지 않는다
                        if (set.contains(addr)) {
                            try {
                                writer.write("[" + Thread.currentThread().getName() + "]" + stack_address.toString() + address + " : " + addr + "(hash)");
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("[" + Thread.currentThread().getName() + "]" + stack_address.toString() + address + " : " + addr + "(hash)");
                            find_flag = true;
                            break;
                        }

                        String findAddr = null;
                        try {
                            findAddr = findCorrectAddressService.findJuso(addr);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!findAddr.equals("")) {
                            set.add(addr);
                            try {
                                writer.write("[" + Thread.currentThread().getName() + "]" + stack_address.toString() + address + " : " + addr);
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("[" + Thread.currentThread().getName() + "]" + stack_address.toString() + address + " : " + addr);
                            find_flag = true;
                            break;
                        } else {
                            System.out.print("X");
                        }
                    }

                    if (!find_flag) {
                        String findAddr = null;
                        try {
                            findAddr = findCorrectAddressService.findJuso(address);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!findAddr.equals("")) {
                            try {
                                writer.write("[" + Thread.currentThread().getName() + "]" + stack_address.toString() + address + " : " + findAddr);
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }


                            System.out.println("[" + Thread.currentThread().getName() + "]" + stack_address.toString() + address + " : " + findAddr);
                            set.add(findAddr);
                        } else {
                            System.out.println("");
                        }
                    }
                }

            });

            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            writer.close(); // 스트림 종료

        } catch (FileNotFoundException e) {
            System.out.println(e);
            // TODO: handle exception
        } catch (IOException e) {
            System.out.println(e);
        }

        String end_time = String.valueOf(new Date());
        System.out.println(start_time);
        System.out.println(end_time);

    }


//    public static void main(String[] args) throws IOException {
//
////        test("ㅅㄱㄷ", false);
////        test("ㅎㅇㅁ", true);
//
//        FindCorrectAddressService findCorrectAddressService = new FindCorrectAddressService();
//        Stack<String> stackAddress;
//        HashSet<String> set = new HashSet<>();
//        boolean writer_flag = false;
//
//        try {
//            // 주소 목록 읽기
//            File file = new File("src/data/juso_data_api.txt");
//            FileReader filereader = new FileReader(file);
//            BufferedReader bufReader = new BufferedReader(filereader);
//
//            File file_result = new File("src/data/juso_data_result.txt"); // File객체 생성
//            if (!file_result.exists()) { // 파일이 존재하지 않으면
//                file_result.createNewFile(); // 신규생성
//            }
//
//            // BufferedWriter 생성
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file_result, false));
//
//            String line = "";
//
//            while ((line = bufReader.readLine()) != null) {
//                String[] rgAddress = line.split("\\|");
//                for (int i = 0; i < rgAddress.length; i++) {
////                for (int i = 0; i < 10; i++) {
//                    boolean find_flag = false;
//                    String address = rgAddress[i];
//
//                    stackAddress = findCorrectAddressService.addressFilter1(address);
//
//                    System.out.print(stackAddress);
//                    System.out.print(" " + address + " : ");
//
//                    if (writer_flag) {
//                        // 파일에 쓰기
//                        writer.write(String.valueOf(stackAddress));
//                        writer.write(" " + address + " : ");
//                    }
//
//
//                    for (String addr : stackAddress) {
//
//                        // HashSet에 같은 도로명 있는지 확인, 존재시 api 보내지 않는다
//                        if (set.contains(addr)) {
//                            System.out.println(addr + "(hash)");
//                            if (writer_flag) {
//                                writer.write(addr + "(hash)");
//                                writer.newLine();
//                            }
//                            find_flag = true;
//                            break;
//                        }
//
//                        String findAddr = findCorrectAddressService.findJuso(addr);
//                        if (!findAddr.equals("")) {
////                            System.out.println(addr+"|"+findAddr);
//                            set.add(addr);
//                            System.out.println(addr);
//                            if (writer_flag) {
//                                writer.write(addr);
//                                writer.newLine();
//                            }
//                            find_flag = true;
//                            break;
//                        } else {
//                            System.out.print("X");
//                        }
//                    }
//
//                    if (!find_flag) {
//                        String findAddr = findCorrectAddressService.findJuso(address);
//                        if (!findAddr.equals("")) {
////                            System.out.println(address+"|"+findAddr);
//                            set.add(findAddr);
//                            System.out.println(findAddr);
//                            if (writer_flag) {
//                                writer.write(findAddr);
//                                writer.newLine();
//                            }
//                        } else {
//                            System.out.println("");
//                            if (writer_flag) {
//                                writer.newLine();
//                            }
//                        }
//                    }
//
//                    System.out.println("================================");
//                    if (writer_flag) {
//                        writer.write("================================");
//                        writer.newLine();
//                    }
//                }
//            }
//
//
//            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
//            writer.close(); // 스트림 종료
//            bufReader.close();
//
//
//            System.out.println("================HASH SET ======================");
//            Iterator<String> iterator = set.iterator();
//            while (iterator.hasNext()) {
//                System.out.print(iterator.next() + "|");
//            }
//
//        } catch (FileNotFoundException e) {
//            System.out.println(e);
//            // TODO: handle exception
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//
//    }

}
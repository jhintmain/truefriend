import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        // 시작시간
        String start_time = String.valueOf(new Date());

        // 주소 목록 stack 저장 > 데이터 파일에서 읽어서 저장
        Stack<String> stackAddress = new Stack<>();

        try {
            // 주소 목록 읽기 (약 3만건 테스트 데이터)
            File file = new File("src/data/juso_data_api.txt");
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);

            String line = "";

            while ((line = bufReader.readLine()) != null) {
                String[] rgAddress = line.split("\\|");
//                for (int i = 0; i < rgAddress.length; i++) {
                for (int i = 0; i < 10; i++) {
                    String address = rgAddress[i];
                    stackAddress.add(address);
                }
            }
            bufReader.close();

            // 주소 목록 데이터 건수 확인
            System.out.println("================ STACK  ======================");
//            Iterator<String> iterator = stackAddress.iterator();
            System.out.println("Total Address Count :"+stackAddress.size());
//            while (iterator.hasNext()) {
//                System.out.print(iterator.next() + "|");
//            }

            System.out.println("================PARTITION ======================");

            // 매칭 결과 파일 생성
            File file_result = new File("src/data/juso_data_result_single.txt"); // File객체 생성
            if (!file_result.exists()) { // 파일이 존재하지 않으면
                file_result.createNewFile(); // 신규생성
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file_result, true));

            // 주소 목록 파티셔닝 후 병령 처리
            partition(stackAddress).parallelStream().forEach(addressList -> {

                // 도로명 검색후 실존재 도로명 확인된 문자열은 hash에 저장해 두고 같은 도로명시 api skip할 수 있게 한다.
                HashSet<String> set = new HashSet<>();

                boolean find_flag = false;

                // 도로명 검색
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
                            System.out.println("[" + Thread.currentThread().getName() + "]" + stack_address.toString() + address + " : " + findAddr);
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

    public static Collection<List<String>> partition(Stack<String> stack) {
        final List<String> address = new ArrayList<>(stack);
        final int chunkSize = stack.size() / 1;
        final AtomicInteger counter = new AtomicInteger();
        final Collection<List<String>> result = address.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
        return result;
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
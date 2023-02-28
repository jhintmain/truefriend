package truefriend.com.address;

import truefriend.com.address.service.FindCorrectAddressService;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        // 시작시간
        String startTime = String.valueOf(new Date());

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
                for (int i = 0; i < rgAddress.length; i++) {
//                for (int i = 0; i < 10; i++) {
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
            File fileResult = new File("src/data/juso_data_result_single.txt"); // File객체 생성
            if (!fileResult.exists()) { // 파일이 존재하지 않으면
                fileResult.createNewFile(); // 신규생성
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileResult, true));

            // 주소 목록 파티셔닝 후 병렬 처리
            partition(stackAddress).parallelStream().forEach(addressList -> {

                // 도로명 검색후 실존재 도로명 확인된 문자열은 hash에 저장해 두고 같은 도로명시 api skip할 수 있게 한다.
                HashSet<String> set = new HashSet<>();

                boolean findFlag = false;

                // 도로명 검색
                FindCorrectAddressService findCorrectAddressService = new FindCorrectAddressService();

                for (String address : addressList) {

                    // 도로명 주소 필터 (로/길)이 포함된 문자열
                    Stack<String> stackFilterAddress = findCorrectAddressService.addressFilter1(address);

//                    System.out.println("["+Thread.currentThread().getName()+"]" + stack_address.toString() + address+" : ");

                    for (String addr : stackFilterAddress) {

                        // 1. HashSet에 같은 도로명 있는지 확인, 존재시 api 보내지 않는다
                        if (set.contains(addr)) {
                            try {
                                writer.write("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + address + " : " + addr + "(hash)");
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + address + " : " + addr + "(hash)");
                            findFlag = true;
                            break;
                        }

                        // 2. 필터링한 주소 API검색
                        String findAddr = null;
                        try {
                            findAddr = findCorrectAddressService.findJuso(addr);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!findAddr.equals("")) {
                            set.add(findAddr);
                            try {
                                writer.write("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + address + " : " + findAddr);
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + address + " : " + findAddr);
                            findFlag = true;
                            break;
                        } else {
                            System.out.print("X");
                        }
                    }

                    // 3. 필터링한 주소 API검색 실패시 > 주소목록 데이터 그대로 한번더 보내본다
                    if (!findFlag) {
                        String findAddr = null;
                        try {
                            findAddr = findCorrectAddressService.findJuso(address);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!findAddr.equals("")) {
                            try {
                                writer.write("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + address + " : " + findAddr);
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            System.out.println("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + address + " : " + findAddr);
                            set.add(findAddr);
                        } else {
                            System.out.println("");
                        }
                    }
                }

            });

            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            writer.close(); // 스트림 종료

        } catch (IOException e) {
            System.out.println(e);
        }


        System.out.println("========== TIME REPORT ===========");
        String end_time = String.valueOf(new Date());
        System.out.println(startTime);
        System.out.println(end_time);
    }

    public static Collection<List<String>> partition(Stack<String> stack) {
        final List<String> address = new ArrayList<>(stack);
        final int chunkSize = stack.size()/20;
        final AtomicInteger counter = new AtomicInteger();
        return address.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
    }

}
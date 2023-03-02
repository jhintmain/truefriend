package truefriend.com.address;

import truefriend.com.address.service.JusoFileService;
import truefriend.com.address.service.FindCorrectAddressService;
import truefriend.com.address.service.JusoAPIService;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        // 시작시간
        String startTime = String.valueOf(new Date());

        // 주소 목록 read/write 관련 서비스 객체 선언
        JusoFileService jusoFileService = new JusoFileService();

        // 주소 목록 읽고 totalAddressList 에 저장.
        List<String> totalAddressList = jusoFileService.readList();

        try {

            // 파일쓰기버퍼 객체 생성
            jusoFileService.makeBufferedWriter();

            // 주소 목록 데이터 건수 확인
            jusoFileService.writer("============================ LIST ==================================");
            jusoFileService.writer("Total Address Count :" + totalAddressList.size());

            // 주소 목록 파티셔닝 후 병렬 처리
            partition(totalAddressList).parallelStream().forEach(addressList -> {

                // 도로명 검색후 실존재 도로명 확인된 문자열은 hash에 저장해 두고 같은 도로명시 api skip할 수 있게 한다.
                HashSet<String> set = new HashSet<>();

                // 도로명 검색 서비스 객체 선언
                FindCorrectAddressService findCorrectAddressService = new FindCorrectAddressService();

                for (String originalAddress : addressList) {

                    // API 검색 성공 플레그
                    boolean findFlag = false;

                    // 도로명 주소 필터 (로/길)이 포함된 문자열
                    Stack<String> stackFilterAddress = findCorrectAddressService.addressFilter1(originalAddress);

                    String findAddress;

                    // 도로명 주소 필터로 찾은 문자열 list 만큼 정확한 데이터인지 판단한다
                    for (String filterAddress : stackFilterAddress) {

                        // 1. HashSet에 같은 도로명 있는지 확인, 존재시 api 보내지 않는다 > HashSet에는 API응닶값이 존재하는 데이터만 존재
                        if (set.contains(filterAddress)) {
                            try {
                                jusoFileService.writer("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + originalAddress + " : " + filterAddress + "(hash)");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            findFlag = true;
                            break;
                        }

                        // 2. 필터링한 주소 API검색
                        try {
                            findAddress = findCorrectAddressService.findJuso(filterAddress);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!findAddress.equals("")) {
                            if(!findAddress.equals(filterAddress)){
                                Character c = filterAddress.charAt(filterAddress.length() - 1);
                                findAddress = findAddress.substring(0, findAddress.indexOf(c)+1);
                                findAddress = findAddress.length() < filterAddress.length() ? findAddress : filterAddress;
                            }
                            set.add(findAddress);
                            try {
                                jusoFileService.writer("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + originalAddress + " : " + findAddress);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            findFlag = true;
                            break;
                        }
                    }

                    // 3. Set에도 API검색에도 없는 경우 > 주소목록 데이터 그대로 한번더 보내본다
                    if (!findFlag) {
                        try {
                            findAddress = findCorrectAddressService.findJuso(originalAddress);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!findAddress.equals("")) {
                            set.add(findAddress);
                            try {
                                jusoFileService.writer("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + originalAddress + " : " + findAddress);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            String alert = (stackFilterAddress.size() < 1) ? "정확한 로/길을 찾지 못했습니다" : "매칭되는 로/길을 찾지 못했습니다";
                            try {
                                jusoFileService.writer("[" + Thread.currentThread().getName() + "]" + stackFilterAddress + originalAddress + " : " + alert);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }

            });


            jusoFileService.writer("========== TIME REPORT ===========");
            String end_time = String.valueOf(new Date());
            jusoFileService.writer(startTime);
            jusoFileService.writer(end_time);

            jusoFileService.close();


        } catch (IOException e) {
            System.out.println(e);
        }


    }

    // 데이터 파티셔닝 처리
    public static Collection<List<String>> partition(List<String> stack) {
        final List<String> address = new ArrayList<>(stack);
        final int chunkSize = stack.size() / 20;
        final AtomicInteger counter = new AtomicInteger();
        return address.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
    }

    /** 테스트 주소 데이터 생성시 호출 **/
    public static void makeTestJusoData() throws IOException {
        JusoAPIService jusoAPIService = new JusoAPIService();
        jusoAPIService.makeTestJusoData("ㄱㄷㅇ",false);
        jusoAPIService.makeTestJusoData("ㅂㅅㄱㅇㅅ",true);
        jusoAPIService.makeTestJusoData("ㄷㄱ",true);
        jusoAPIService.makeTestJusoData("ㄴㄹㅇ",true);
    }

}
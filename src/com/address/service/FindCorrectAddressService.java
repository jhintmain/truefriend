package com.address.service;

import com.address.service.dto.JusoApiResultParser;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindCorrectAddressService {

    /**
     * 주소 양식 다듬기
     * 1. 한글,영어,숫자,콤마(,)이외 특수문자 제거
     * 2. 1글자로된 문자열 병합
     * 3. 숫자는 앞 문자열과 병합
     */
    public String setAddress(String address) {

        String parseAddress = "";

        // 특수문자 제거
        address = address.replaceAll("\\s{2,}", " ");
        address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9, ]", "");
        String[] arg = address.split(" ");  // 공백으로 split
        boolean continueFlag = false;

        // 1글자 문자병합
        for (String a : arg) {
            if ((a.length() == 1) || (isInteger(a))) {
                parseAddress += !continueFlag ? " " + a : a;
                continueFlag = true;
            } else {
                continueFlag = false;
                parseAddress += " " + a;
            }
        }
        return parseAddress;
    }

    public boolean isInteger(String strValue) {
        try {
            Integer.parseInt(strValue);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public String findJuso(String address) throws IOException {
        // 주소 API 호출
        JusoAPIService jusoApiService = new JusoAPIService();
        String jsonResult = jusoApiService.callJusoAPI(address);
        JusoApiResultParser jusoApiResultParser = jusoApiService.parseJuso(jsonResult);

        return (jusoApiResultParser.getResult().getCommon().getTotalCount() > 0) ? jusoApiResultParser.getResult().getJuso().get(0).getRn() : "";
    }

    /**
     * 도로명 찾는 정규식함수
     * - 로/길 포함된 문자열
     */
    // 주소 필터 1 - 정규식 이용
    public Stack<String> addressFilter(String address) {

        Stack<String> addressList = new Stack<>();
        address = setAddress(address);

        if (address.isEmpty()) {
            return addressList;
        }

//        String regx = "[가-힣\\d]+(?:로|길)+(?:\\s+)+(?:\\d*)";
        String regx = "[가-힣\\d]+(?:로|길)";
        Matcher matcher = Pattern.compile(regx).matcher(address);

        while (matcher.find()) {
            // 예외처리 1 - 종로/ 구로는 통과
            if (checkException(matcher.group().trim())) {
                addressList.push(matcher.group().trim());
            }
        }

        if (addressList.empty()) {
            int lo_or_gil_idx = Math.max(address.lastIndexOf("로"), address.lastIndexOf("길"));
            if (lo_or_gil_idx != -1) {
                String arg[] = address.split(" ");
                for (String findLoGil : arg) {
                    if (findLoGil.length() > 0 && (findLoGil.charAt(findLoGil.length() - 1) == '로' || findLoGil.charAt(findLoGil.length() - 1) == '길')) {
                        addressList.push(findLoGil.trim());
                    }
                }
            }
        }
        
        return addressList;
    }

    /**
     * 로/길 정규식 예외 처리
     * - 행정구역중 로 or 길 로끝나는 곳이 두곳. 종로/구로
     */
    public boolean checkException(String findAddress) {
        String[] rgExceptLo = {"종로", "구로"};
        for (String lo : rgExceptLo) {
            if (findAddress.equals(lo)) {
                return false;
            }
        }
        return true;
    }

}

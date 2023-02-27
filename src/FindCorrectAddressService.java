import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindCorrectAddressService {

    public String setAddress(String address) {

        String parseAddress = "";

        address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9,() ]", "");    // 특수문자 제거
        String[] arg = address.split(" ");  // 공백으로 split
        boolean continueFlag = false;

        // 1글자 문자는 붙여준다
        for (String a : arg) {
            if ((a.length() == 1)) {
                parseAddress += !continueFlag ? " " + a : a;
                continueFlag = true;
            } else {
                continueFlag = false;
                parseAddress += " " + a;
            }
        }
        return parseAddress;
    }

    public String findJuso(String address) throws IOException {
        String rn = "";

        // 주소 API 호출
        JusoAPIService jusoApiService = new JusoAPIService();
        String jsonResult = jusoApiService.callJusoAPI(address);
        JsonArray juso = jusoApiService.parseJsonJuso(jsonResult);

        if (juso != null) {
            rn = juso.get(0).getAsJsonObject().get("rn").getAsString();
        }
        return rn;
    }

    // 주소 필터 1 - 정규식 이용
    public Stack<String> addressFilter2(String address) {

        Stack<String> addressList = new Stack<>();
        String regx = PatternDefine.PATTERN_ROAD;
        address = setAddress(address);

        Matcher matcher = Pattern.compile(regx).matcher(address);
        while (matcher.find()) {
            // 예외처리 1 - 종로/ 구로는 통과
            if(checkException(matcher.group())){
                addressList.push(matcher.group());
            }
        }

        return addressList;
    }

    public Stack<String> addressFilter1(String address) {

        Stack<String> addressList = new Stack<>();
        address = setAddress(address);

        String regx = "[가-힣\\d]+(?:로|길)";
        Matcher matcher = Pattern.compile(regx).matcher(address);

        while (matcher.find()) {
            // 예외처리 1 - 종로/ 구로는 통과
            if(checkException(matcher.group())){
                addressList.push(matcher.group());
            }
        }
        return addressList;
    }

    public boolean checkException(String findAddress) {
        String[] rgExceptLo = {"종로","구로"};
        for(String lo : rgExceptLo){
            if(findAddress.equals(lo)){
                return false;
            }
        }
       return true;
    }

}

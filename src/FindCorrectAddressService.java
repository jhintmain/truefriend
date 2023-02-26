import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindCorrectAddressService {
    public static final char LO_KOR = '로';
    public static final char GIL_KOR = '길';

    public String setAddress(String address) {
        // 특수문자 제거
        //address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9/s]", "");
        address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9-,() ]", "");
        return address;
    }

    public String findJuso(String address) throws IOException {
        String findAddress = "";

        // 주소 API 호출
        JusoAPIService jusoApiService = new JusoAPIService();
        String jsonResult = jusoApiService.callJusoAPI(address);
        JsonArray juso = jusoApiService.parseJsonJuso(jsonResult);

        if(juso != null){
            String rn = juso.get(0).getAsJsonObject().get("rn").getAsString();
            findAddress = rn;
//            System.out.println(rn +" : "+address);
        }else{
//            System.out.println("주소없음 : "+address);
        }
        return findAddress;
    }

    public Stack<String> addressFilter(String address) {

        Stack<String> stackAddress;

        stackAddress = addressFilter1(address).size() < 1 ? addressFilter2(address) : addressFilter1(address);

        return stackAddress;
    }

    // 주소 필터 1 - 정규식 이용
    public Stack<String> addressFilter1(String address) {
        address = setAddress(address);

        Stack<String> addressList = new Stack<>();

        String regx = PatternDefine.PATTERN_ROAD;

        Matcher matcher = Pattern.compile(regx).matcher(address);
        while (matcher.find()) {
            if(!matcher.group().equals("종로") && !matcher.group().equals("구로")){
                addressList.push(matcher.group());
            }
        }

        if(addressList.size() <1){
            addressList.push(address);
        }

//        System.out.println("addressFilter1");
//        System.out.println(address);
//        System.out.println(addressList);

        return addressList;
    }

    // 주소 필터 2 - 커스텀
    public Stack<String> addressFilter2(String address) {
        address =address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9/s]", "");
        address =address.replaceAll("[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z]", "");
        int uniqueWord = Math.max(address.indexOf("종로"), address.indexOf("구로"));
        if(uniqueWord != -1){
            address = address.substring(uniqueWord+3);
        }

        Stack<String> addressList = new Stack<>();

        // 무한 루프 방지 max = 5
        for (int cnt = 0; cnt < 5; cnt++) {
            // 로/길 이 들어간 문자열 찾기
            int loOrGilIndex = getLoOrGilIdx(address);

            if (loOrGilIndex != -1) {
                // 로/길을 찾은 index 중 큰값 기준으로 앞 부분 공백 제거
                String trimAddr = address.substring(0, loOrGilIndex + 1);
                address = address.substring(loOrGilIndex + 1);

                // 무한 루프 방지 max = 5
                int i = 0;
                if (!address.equals("")) {

                    for (; i < 5; i++) {
                        // 뒤에 문자가 숫자이면 계속 더해준다
                        char ch = address.charAt(i);
                        if (ch >= '0' && ch <= '9') {
                            trimAddr = trimAddr + ch;
                        } else {
                            break;
                        }
                    }
                }

                address = address.substring(i);
                addressList.push(trimAddr);
            } else {
                break;
            }
        }

        System.out.println("addressFilter2");
        System.out.println(address);
//        System.out.println(addressList);

        return addressList;
    }

    /**
     * 로 / 길이 포함되 있는 주소인지 체크
     */
    public int getLoOrGilIdx(String address) {
        return Math.max(address.indexOf(LO_KOR), address.indexOf(GIL_KOR));
    }


}

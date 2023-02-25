import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JusoAPIService {

    private final char LO_KOR = '로';
    private final char GIL_KOR = '길';

    public int currentPage = 1;  //요청 변수 설정 (현재 페이지. currentPage : n > 0)
    public int countPerPage = 1;  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
    final String resultType = "json";      //요청 변수 설정 (검색결과형식 설정, json)
    private final String confmKey = "devU01TX0FVVEgyMDIzMDIyNTE3MTYxNzExMzU0ODU=";          //요청 변수 설정 (승인키)
    final String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do?";

    public String findJuso(String address) throws IOException {
        return parseJsonJuso(execJusoAPI(address));
    }

    private String makeJusoApiUrl(String address) throws UnsupportedEncodingException {
        return apiUrl + "currentPage=" + this.currentPage +
                "&countPerPage=" + this.countPerPage +
                "&keyword=" + URLEncoder.encode(address, "UTF-8") +
                "&confmKey=" + this.confmKey +
                "&resultType=" + this.resultType;
    }

    private String execJusoAPI(String address) throws IOException {
        // OPEN API 호출 URL 정보 설정
        String apiUrl = makeJusoApiUrl(address);
        URL url = new URL(apiUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String tempStr = null;

        while (true) {
            tempStr = br.readLine();
            if (tempStr == null) break;
            sb.append(tempStr);                                // 응답결과 JSON 저장
        }
        br.close();

        return sb.toString();
    }

    private static String parseJsonJuso(String jsonAddress) {
        // {"results":{"common":{"errorMessage":"정상","countPerPage":"1","totalCount":"191","errorCode":"0","currentPage":"1"},"juso":[{"detBdNmList":"","engAddr":"26 Baekhyeon-ro, Bundang-gu, Seongnam-si, Gyeonggi-do","rn":"백현로","emdNm":"정자동","zipNo":"13553","roadAddrPart2":"(정자동)","emdNo":"02","sggNm":"성남시 분당구","jibunAddr":"경기도 성남시 분당구 정자동 3-2","siNm":"경기도","roadAddrPart1":"경기도 성남시 분당구 백현로 26","bdNm":"","admCd":"4113510300","udrtYn":"0","lnbrMnnm":"3","roadAddr":"경기도 성남시 분당구 백현로 26(정자동)","lnbrSlno":"2","buldMnnm":"26","bdKdcd":"0","liNm":"","rnMgtSn":"411353180022","mtYn":"0","bdMgtSn":"4113510300100030002000001","buldSlno":"0"}]}}
        String result = "";
        JsonParser jsonParser = new JsonParser();
        Object obj = jsonParser.parse(jsonAddress);
        JsonObject jsonObj = (JsonObject) obj;

        JsonObject results = jsonObj.getAsJsonObject().get("results").getAsJsonObject();
        JsonObject common = results.getAsJsonObject().get("common").getAsJsonObject();
        String totalCount = common.getAsJsonObject().get("totalCount").getAsString();

        if (Integer.parseInt(totalCount) > 0) {
            JsonArray juso = results.getAsJsonObject().get("juso").getAsJsonArray();
            result = juso.get(0).getAsJsonObject().get("rn").getAsString();
//            System.out.println(result);
        } else {
//            System.out.println("주소 없음 :"+result);
        }

        return result;
    }

    public  Stack<String> addressFilter2(String address) {
        Stack<String> addressList = new  Stack<String>();

        // 주소내 특수문자 제거
        address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9/s]", "");

        // 무한 루프 방지 max = 5
        for (int cnt = 0; cnt < 5; cnt++) {
            // 로/길 이 들어간 문자열 찾기
            int loOrGilIndex = Math.max(address.indexOf(this.LO_KOR), address.indexOf(this.GIL_KOR));

            if (loOrGilIndex != -1) {
                // 로/길을 찾은 index 중 큰값 기준으로 앞 부분 공백 제거
                String trimAddr = address.substring(0, loOrGilIndex + 1);
                address = address.substring(loOrGilIndex + 1);

                // 무한 루프 방지 max = 5
                int i = 0;
                if(!address.equals("")){

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

        return addressList;
    }

    public Stack<String> addressFilter(String address) {
        Stack<String> addressList = new Stack<String>();

        // 주소내 특수문자 제거
        //address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9/s]", "");
        address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9 ]", "");
//        System.out.println(address);

        String regx = PatternDefine.PATTERN_ROAD;

        Matcher matcher = Pattern.compile(regx).matcher(address);
        while(matcher.find()){
            addressList.push(matcher.group());
        }

        return addressList;
    }

}

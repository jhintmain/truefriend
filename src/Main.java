import com.google.gson.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static char LO_KOR = '로';

    public static char GIL_KOR = '길';

    public static void main(String[] args) throws Exception {

        String addr1 = "성남, 분당 백 현 로 265 푸른마을 아파트로 보내주세요!!";
        String addr2 = "마포구 도화-2길 코끼리분식";

        List<String> addressList = addressFilter2(addr2);
        System.out.println(addressList);


        for(String addr : addressList){
            String currentPage = "1";    //요청 변수 설정 (현재 페이지. currentPage : n > 0)
            String countPerPage = "1";  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
            String resultType = "json";      //요청 변수 설정 (검색결과형식 설정, json)
            String confmKey = "devU01TX0FVVEgyMDIzMDIyNTE3MTYxNzExMzU0ODU=";          //요청 변수 설정 (승인키)
            String keyword = addr;            //요청 변수 설정 (키워드)

            // OPEN API 호출 URL 정보 설정
            String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+confmKey+"&resultType="+resultType;
            URL url = new URL(apiUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
            StringBuffer sb = new StringBuffer();
            String tempStr = null;

            while(true){
                tempStr = br.readLine();
                if(tempStr == null) break;
                sb.append(tempStr);								// 응답결과 JSON 저장
            }
            br.close();

            System.out.println(sb.toString());
            // Json 문자열

            // {"results":{"common":{"errorMessage":"정상","countPerPage":"1","totalCount":"191","errorCode":"0","currentPage":"1"},"juso":[{"detBdNmList":"","engAddr":"26 Baekhyeon-ro, Bundang-gu, Seongnam-si, Gyeonggi-do","rn":"백현로","emdNm":"정자동","zipNo":"13553","roadAddrPart2":"(정자동)","emdNo":"02","sggNm":"성남시 분당구","jibunAddr":"경기도 성남시 분당구 정자동 3-2","siNm":"경기도","roadAddrPart1":"경기도 성남시 분당구 백현로 26","bdNm":"","admCd":"4113510300","udrtYn":"0","lnbrMnnm":"3","roadAddr":"경기도 성남시 분당구 백현로 26(정자동)","lnbrSlno":"2","buldMnnm":"26","bdKdcd":"0","liNm":"","rnMgtSn":"411353180022","mtYn":"0","bdMgtSn":"4113510300100030002000001","buldSlno":"0"}]}}

            //2. Parser
            JsonParser jsonParser = new JsonParser();

            //3. To Object
            Object obj = jsonParser.parse(sb.toString());

            //4. To JsonObject
            JsonObject jsonObj = (JsonObject) obj;

            JsonObject results = jsonObj.getAsJsonObject().get("results").getAsJsonObject();
            JsonObject common = results.getAsJsonObject().get("common").getAsJsonObject();
            String totalCount = common.getAsJsonObject().get("totalCount").getAsString();
            if(Integer.parseInt(totalCount)>0){
                JsonArray juso = results.getAsJsonObject().get("juso").getAsJsonArray();
                String rn = juso.get(0).getAsJsonObject().get("rn").getAsString();
                System.out.println(rn);
                break;
            }else{
                System.out.println("주소 없음");
            }
        }

    }

    public static String addressFilter(String address) {

        String filterAddress = address.replaceAll("[^\\uAC00-\\uD7A30-9a-zA-Z]", "");

        List<String> addressList = new ArrayList<>();

        int preIndex = 0;
        for (AddressPostFixType addressPostFixType : AddressPostFixType.values()) {
            int i = filterAddress.indexOf(addressPostFixType.postfix);
            if (i > preIndex) {
                addressList.add(filterAddress.substring(preIndex, i + 1));
                preIndex = i + 1;
            }
        }

        return String.join(" ", addressList);
    }

    public static List<String> addressFilter2(String address) {
        List<String> addressList = new ArrayList<>();

        // 주소내 특수문자 제거
        address = address.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9/s]", "");

        // 무한 루프 방지 max = 5
        for(int cnt = 0; cnt < 5; cnt ++) {
            // 로/길 이 들어간 문자열 찾기
            int loOrGilIndex = Math.max(address.indexOf(LO_KOR), address.indexOf(GIL_KOR));

            if(loOrGilIndex != -1){
                // 로/길을 찾은 index 중 큰값 기준으로 앞 부분 공백 제거
                String trimAddr = address.substring(0, loOrGilIndex+1);
                address = address.substring(loOrGilIndex+1);

                // 무한 루프 방지 max = 5
                int i = 0;
                for(; i < 5; i++){
                    // 뒤에 문자가 숫자이면 계속 더해준다
                    char ch = address.charAt(i);
                    if(ch >= '0' && ch <= '9'){
                        trimAddr = trimAddr+ch;
                    }else {
                        break;
                    }
                }

                address = address.substring(i);
                addressList.add(trimAddr);
            }else {
                break;
            }
        }

        return addressList;
    }
}
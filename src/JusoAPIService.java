import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class JusoAPIService {

    private int currentPage = 1;  //요청 변수 설정 (현재 페이지. currentPage : n > 0)
    private int countPerPage = 1;  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
    private static final String resultType = "json";      //요청 변수 설정 (검색결과형식 설정, json)
    private static final String confmKey = "devU01TX0FVVEgyMDIzMDIyNTE3MTYxNzExMzU0ODU=";          //요청 변수 설정 (승인키)
    public static final String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do?";     // 요청 주소 URL

    /**
     * juso api 호출
     *
     * @param address
     */
    public String callJusoAPI(String address) throws IOException {
        // OPEN API 호출 URL 정보 설정
        String apiUrl = makeJusoApiUrl(address);
        URL url = new URL(apiUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String tempStr = null;

        while (true) {
            tempStr = br.readLine();
            if (tempStr == null) break;
            sb.append(tempStr);
        }
        br.close();

        return sb.toString();
    }

    /**
     * jsuo API 요청 URL 생성
     *
     * @param address
     */
    private String makeJusoApiUrl(String address) throws UnsupportedEncodingException {
        return apiUrl + "currentPage=" + this.currentPage +
                "&countPerPage=" + this.countPerPage +
                "&keyword=" + URLEncoder.encode(address, "UTF-8") +
                "&confmKey=" + confmKey +
                "&resultType=" + resultType;
    }


    /**
     * juso API 응답 JSON 파싱
     * 응답 양식: // {"results":{ "common":{"errorMessage":"정상","countPerPage":"1","totalCount":"191","errorCode":"0","currentPage":"1"},"juso":[{"detBdNmList":"","engAddr":"26 Baekhyeon-ro, Bundang-gu, Seongnam-si, Gyeonggi-do","rn":"백현로","emdNm":"정자동","zipNo":"13553","roadAddrPart2":"(정자동)","emdNo":"02","sggNm":"성남시 분당구","jibunAddr":"경기도 성남시 분당구 정자동 3-2","siNm":"경기도","roadAddrPart1":"경기도 성남시 분당구 백현로 26","bdNm":"","admCd":"4113510300","udrtYn":"0","lnbrMnnm":"3","roadAddr":"경기도 성남시 분당구 백현로 26(정자동)","lnbrSlno":"2","buldMnnm":"26","bdKdcd":"0","liNm":"","rnMgtSn":"411353180022","mtYn":"0","bdMgtSn":"4113510300100030002000001","buldSlno":"0"}]}}
     *
     * @param jsonAddress
     */
    public JsonArray parseJsonJuso(String jsonAddress) {
        JsonArray juso = null;

        JsonParser jsonParser = new JsonParser();
        Object obj = jsonParser.parse(jsonAddress);
        JsonObject jsonObj = (JsonObject) obj;

        JsonObject results = jsonObj.getAsJsonObject().get("results").getAsJsonObject();
        JsonObject common = results.getAsJsonObject().get("common").getAsJsonObject();
        String totalCount = common.getAsJsonObject().get("totalCount").getAsString();

        if (Integer.parseInt(totalCount) > 0) {
            juso = results.getAsJsonObject().get("juso").getAsJsonArray();
        }

        return juso;
    }


    // 테스트 주소 데이터 셋팅함수
    public void makeJusoData(String address, boolean flag) throws IOException {

        // 한페이지당 100개 셋팅
        this.setCountPerPage(100);

        // 주소 정보 txt로 저장
        File file_result = new File("src/data/juso_data_api.txt");
        if (!file_result.exists()) { // 파일이 존재하지 않으면
            file_result.createNewFile(); // 신규생성
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file_result, flag));

        // writer.write("마포구 도화-2길 코끼리분식|성남, 분당 백 현 로 265 푸른마을 아파트로 보내주세요 !!|");

        for (int page = 1; page < 50; page++) {
            this.setCurrentPage(page);

            String jsonResult = this.callJusoAPI(address);
            JsonArray juso = this.parseJsonJuso(jsonResult);

            if (juso == null) {
                continue;
            }
            for (int i = 0; i < juso.size(); i++) {
                JsonObject tt = juso.get(i).getAsJsonObject();
                System.out.println("[" + page + "/" + i + "]" + tt.get("roadAddr").getAsString());
                writer.write(tt.get("roadAddr").getAsString() + "|");
            }
        }

        writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
        writer.close(); // 스트림 종료
    }

    /**
     * juso API 요청 페이지 셋팅
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = (currentPage > 0 && currentPage < 90) ? currentPage : 1;
    }

    /**
     * juso API 한페이지당 갯수셋팅
     *
     * @param countPerPage
     */
    public void setCountPerPage(int countPerPage) {
        this.countPerPage = (countPerPage > 0 && countPerPage <= 100) ? countPerPage : 1;
    }

}

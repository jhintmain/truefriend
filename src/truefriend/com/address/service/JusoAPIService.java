package truefriend.com.address.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import truefriend.com.address.vo.JusoApiResultParser;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JusoAPIService {

    private int currentPage = 1;  //요청 변수 설정 (현재 페이지. currentPage : n > 0)
    private int countPerPage = 1;  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
    private static final String RESULT_TYPE = "json";      //요청 변수 설정 (검색결과형식 설정, json)
    private static final String CONFM_KEY = "devU01TX0FVVEgyMDIzMDIyNTE3MTYxNzExMzU0ODU=";          //요청 변수 설정 (승인키)
    private static final String API_URL = "https://business.juso.go.kr/addrlink/addrLinkApi.do?";     // 요청 주소 URL

    /**
     * juso api 호출
     */
    public String callJusoAPI(String address) throws IOException {
        // OPEN API 호출 URL 정보 설정
        String apiUrl = makeJusoApiUrl(address);
        URL url = new URL(apiUrl);
        InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String tempStr;

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
     */
    private String makeJusoApiUrl(String address) {
        return API_URL + "currentPage=" + this.currentPage +
                "&countPerPage=" + this.countPerPage +
                "&keyword=" + URLEncoder.encode(address, StandardCharsets.UTF_8) +
                "&confmKey=" + CONFM_KEY +
                "&resultType=" + RESULT_TYPE;
    }


    /**
     * juso API 응답 JSON 파싱
     * 응답 데이터 : {"results":{ "common":{"errorMessage":"정상","countPerPage":"1","totalCount":"191","errorCode":"0","currentPage":"1"},"juso":[{"detBdNmList":"","engAddr":"26 Baekhyeon-ro, Bundang-gu, Seongnam-si, Gyeonggi-do","rn":"백현로","emdNm":"정자동","zipNo":"13553","roadAddrPart2":"(정자동)","emdNo":"02","sggNm":"성남시 분당구","jibunAddr":"경기도 성남시 분당구 정자동 3-2","siNm":"경기도","roadAddrPart1":"경기도 성남시 분당구 백현로 26","bdNm":"","admCd":"4113510300","udrtYn":"0","lnbrMnnm":"3","roadAddr":"경기도 성남시 분당구 백현로 26(정자동)","lnbrSlno":"2","buldMnnm":"26","bdKdcd":"0","liNm":"","rnMgtSn":"411353180022","mtYn":"0","bdMgtSn":"4113510300100030002000001","buldSlno":"0"}]}}
     */
    public JusoApiResultParser parseJuso(String jsonAddress) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonAddress, JusoApiResultParser.class);
    }


    // 테스트 주소 데이터 셋팅함수
    public void makeTestJusoData(String address, boolean flag) throws IOException {

        // 주소 정보 txt로 저장
        File fileResult = new File("src/data/juso_data_list.txt");
        if (!fileResult.exists()) { // 파일이 존재하지 않으면
            fileResult.createNewFile(); // 신규생성
        }
        FileWriter fileWriter = new FileWriter(fileResult, flag);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        // writer.write("마포구 도화-2길 코끼리분식|성남, 분당 백 현 로 265 푸른마을 아파트로 보내주세요 !!|");

        // 한페이지당 100개 셋팅
        this.setCountPerPage(100);

        // 50페이지까지 반복
        for (int currentPage = 1; currentPage < 50; currentPage++) {

            this.setCurrentPage(currentPage);
            String jsonResult = this.callJusoAPI(address);
            JusoApiResultParser jusoApiResultParser = this.parseJuso(jsonResult);

            int totalCount = jusoApiResultParser.getResult().getCommon().getTotalCount();

            if (totalCount > 0) {
                List<JusoApiResultParser.InnerResults.InnerJuso> juso = jusoApiResultParser.getResult().getJuso();
                for (int i = 0; i < juso.size(); i++) {
                    String roadAddr = juso.get(i).getRoadAddr();
                    System.out.println("[" + currentPage + "/" + i + "]" + roadAddr);
                    writer.write(roadAddr + "|");
                }
            }
        }

        writer.flush();
        writer.close();
    }

    /**
     * juso API 요청 페이지 셋팅
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = (currentPage > 0 && currentPage < 90) ? currentPage : 1;
    }

    /**
     * juso API 한페이지당 갯수셋팅
     */
    public void setCountPerPage(int countPerPage) {
        this.countPerPage = (countPerPage > 0 && countPerPage <= 100) ? countPerPage : 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getCountPerPage() {
        return countPerPage;
    }
}

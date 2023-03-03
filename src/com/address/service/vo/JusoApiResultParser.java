package com.address.service.vo;

import java.util.List;

public class JusoApiResultParser {
    private InnerResults results;

    public InnerResults getResult() {
        return results;
    }

    public void setResults(InnerResults results) {
        this.results = results;
    }

    public static class InnerResults{
        private InnerCommon common;
        private List<InnerJuso> juso;

        public InnerCommon getCommon() {
            return common;
        }

        public List<InnerJuso> getJuso() {
            return juso;
        }

        public void setCommon(InnerCommon common) {
            this.common = common;
        }

        public void setJuso(List<InnerJuso> juso) {
            this.juso = juso;
        }

        public static class InnerCommon{
            private String errorMessage;
            private Integer countPerPage;
            private Integer totalCount;
            private String errorCode;
            private String currentPage;

            public String getErrorMessage() {
                return errorMessage;
            }

            public void setErrorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
            }

            public Integer getCountPerPage() {
                return countPerPage;
            }

            public void setCountPerPage(Integer countPerPage) {
                this.countPerPage = countPerPage;
            }

            public Integer getTotalCount() {
                return totalCount;
            }

            public void setTotalCount(Integer totalCount) {
                this.totalCount = totalCount;
            }

            public String getErrorCode() {
                return errorCode;
            }

            public void setErrorCode(String errorCode) {
                this.errorCode = errorCode;
            }

            public String getCurrentPage() {
                return currentPage;
            }

            public void setCurrentPage(String currentPage) {
                this.currentPage = currentPage;
            }
        }

        public static class InnerJuso{
            private String roadAddr;
            private String roadAddrPart1;
            private String roadAddrPart2;
            private String jibunAddr;
            private String engAddr;
            private String zipNo;
            private String admCd;
            private String rnMgtSn;
            private String bdMgtSn;
            private String detBdNmList;
            private String bdNm;
            private String bdKdcd;
            private String siNm;
            private String sggNm;
            private String emdNm;
            private String emdNo;
            private String liNm;
            private String rn;
            private String udrtYn;
            private String lnbrMnnm;
            private String lnbrSlno;

            private String mtYn;
            private Integer buldMnnm;
            private Integer buldSlno;


            public String getMtYn() {
                return mtYn;
            }

            public void setMtYn(String mtYn) {
                this.mtYn = mtYn;
            }

            public String getLnbrMnnm() {
                return lnbrMnnm;
            }

            public void setLnbrMnnm(String lnbrMnnm) {
                this.lnbrMnnm = lnbrMnnm;
            }

            public String getLnbrSlno() {
                return lnbrSlno;
            }

            public void setLnbrSlno(String lnbrSlno) {
                this.lnbrSlno = lnbrSlno;
            }

            public String getRoadAddr() {
                return roadAddr;
            }

            public String getEmdNo() {
                return emdNo;
            }

            public void setEmdNo(String emdNo) {
                this.emdNo = emdNo;
            }

            public void setRoadAddr(String roadAddr) {
                this.roadAddr = roadAddr;
            }

            public String getRoadAddrPart1() {
                return roadAddrPart1;
            }

            public void setRoadAddrPart1(String roadAddrPart1) {
                this.roadAddrPart1 = roadAddrPart1;
            }

            public String getRoadAddrPart2() {
                return roadAddrPart2;
            }

            public void setRoadAddrPart2(String roadAddrPart2) {
                this.roadAddrPart2 = roadAddrPart2;
            }

            public String getJibunAddr() {
                return jibunAddr;
            }

            public void setJibunAddr(String jibunAddr) {
                this.jibunAddr = jibunAddr;
            }

            public String getEngAddr() {
                return engAddr;
            }

            public void setEngAddr(String engAddr) {
                this.engAddr = engAddr;
            }

            public String getZipNo() {
                return zipNo;
            }

            public void setZipNo(String zipNo) {
                this.zipNo = zipNo;
            }

            public String getAdmCd() {
                return admCd;
            }

            public void setAdmCd(String admCd) {
                this.admCd = admCd;
            }

            public String getRnMgtSn() {
                return rnMgtSn;
            }

            public void setRnMgtSn(String rnMgtSn) {
                this.rnMgtSn = rnMgtSn;
            }

            public String getBdMgtSn() {
                return bdMgtSn;
            }

            public void setBdMgtSn(String bdMgtSn) {
                this.bdMgtSn = bdMgtSn;
            }

            public String getDetBdNmList() {
                return detBdNmList;
            }

            public void setDetBdNmList(String detBdNmList) {
                this.detBdNmList = detBdNmList;
            }

            public String getBdNm() {
                return bdNm;
            }

            public void setBdNm(String bdNm) {
                this.bdNm = bdNm;
            }

            public String getBdKdcd() {
                return bdKdcd;
            }

            public void setBdKdcd(String bdKdcd) {
                this.bdKdcd = bdKdcd;
            }

            public String getSiNm() {
                return siNm;
            }

            public void setSiNm(String siNm) {
                this.siNm = siNm;
            }

            public String getSggNm() {
                return sggNm;
            }

            public void setSggNm(String sggNm) {
                this.sggNm = sggNm;
            }

            public String getEmdNm() {
                return emdNm;
            }

            public void setEmdNm(String emdNm) {
                this.emdNm = emdNm;
            }

            public String getLiNm() {
                return liNm;
            }

            public void setLiNm(String liNm) {
                this.liNm = liNm;
            }

            public String getRn() {
                return rn;
            }

            public void setRn(String rn) {
                this.rn = rn;
            }

            public String getUdrtYn() {
                return udrtYn;
            }

            public void setUdrtYn(String udrtYn) {
                this.udrtYn = udrtYn;
            }

            public Integer getBuldMnnm() {
                return buldMnnm;
            }

            public void setBuldMnnm(Integer buldMnnm) {
                this.buldMnnm = buldMnnm;
            }

            public Integer getBuldSlno() {
                return buldSlno;
            }

            public void setBuldSlno(Integer buldSlno) {
                this.buldSlno = buldSlno;
            }
        }
    }
}

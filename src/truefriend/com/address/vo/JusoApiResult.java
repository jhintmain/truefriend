package truefriend.com.address.vo;

import java.util.List;

public class JusoApiResult {

    private CommonResult common;

    private List<Juso> juso;

    public CommonResult getCommon() {
        return common;
    }

    public List<Juso> getJuso() {
        return juso;
    }

    public void setCommon(CommonResult common) {
        this.common = common;
    }

    public void setJuso(List<Juso> juso) {
        this.juso = juso;
    }

    public static class CommonResult {
        private String errorMessage;
        private Integer countPerPage;
        private Integer totalCount;
        private String errorCode;
        private String currentPage;

        public String getErrorMessage() {
            return errorMessage;
        }

        public Integer getCountPerPage() {
            return countPerPage;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getCurrentPage() {
            return currentPage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public void setCountPerPage(Integer countPerPage) {
            this.countPerPage = countPerPage;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public void setCurrentPage(String currentPage) {
            this.currentPage = currentPage;
        }
    }

    public static class Juso {
        private String rn;
        private String roadAddr;

        public String getRn() {
            return rn;
        }

        public String getRoadAddr() {
            return roadAddr;
        }

        public void setRn(String rn) {
            this.rn = rn;
        }

        public void setRoadAddr(String roadAddr) {
            this.roadAddr = roadAddr;
        }
    }

    //getter/setter 만들기

}

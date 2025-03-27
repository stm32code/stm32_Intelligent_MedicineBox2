package com.example.smartmedicinebox.entity.EquipmentData;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EquipmentBean {
    @Override
    public String toString() {
        return "EquipmentBean{" +
                "nullErrno=" + nullErrno +
                ", nullData=" + nullData +
                ", nullError='" + nullError + '\'' +
                '}';
    }

    @SerializedName("errno")
    private Integer nullErrno;
    @SerializedName("data")
    private DataDTO nullData;
    @SerializedName("error")
    private String nullError;

    public Integer getErrno() {
        return nullErrno;
    }

    public void setErrno(Integer errno) {
        nullErrno = errno;
    }

    public DataDTO getData() {
        return nullData;
    }

    public void setData(DataDTO data) {
        nullData = data;
    }

    public String getError() {
        return nullError;
    }

    public void setError(String error) {
        nullError = error;
    }

    public static class DataDTO {
        @Override
        public String toString() {
            return "DataDTO{" +
                    "nullCount=" + nullCount +
                    ", nullDatastreams=" + nullDatastreams +
                    '}';
        }

        @SerializedName("count")
        private Integer nullCount;
        @SerializedName("datastreams")
        private List<DatastreamsDTO> nullDatastreams;

        public Integer getCount() {
            return nullCount;
        }

        public void setCount(Integer count) {
            nullCount = count;
        }

        public List<DatastreamsDTO> getDatastreams() {
            return nullDatastreams;
        }

        public void setDatastreams(List<DatastreamsDTO> datastreams) {
            nullDatastreams = datastreams;
        }

        public static class DatastreamsDTO {
            @Override
            public String toString() {
                return "DatastreamsDTO{" +
                        "nullId='" + nullId + '\'' +
                        ", nullDatapoints=" + nullDatapoints +
                        '}';
            }

            @SerializedName("id")
            private String nullId;
            @SerializedName("datapoints")
            private List<DatapointsDTO> nullDatapoints;

            public String getId() {
                return nullId;
            }

            public void setId(String id) {
                nullId = id;
            }

            public List<DatapointsDTO> getDatapoints() {
                return nullDatapoints;
            }

            public void setDatapoints(List<DatapointsDTO> datapoints) {
                nullDatapoints = datapoints;
            }

            public static class DatapointsDTO {
                @Override
                public String toString() {
                    return "DatapointsDTO{" +
                            "nullAt='" + nullAt + '\'' +
                            ", nullValue='" + nullValue + '\'' +
                            '}';
                }

                @SerializedName("at")
                private String nullAt;
                @SerializedName("value")
                private String nullValue;

                public String getAt() {
                    return nullAt;
                }

                public void setAt(String at) {
                    nullAt = at;
                }

                public String getValue() {
                    return nullValue;
                }

                public void setValue(String value) {
                    nullValue = value;
                }
            }
        }
    }
}

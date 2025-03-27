package com.example.smartmedicinebox.entity.EquipmentData;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Equipment0nline {
    @Override
    public String toString() {
        return "Equipment0nline{" +
                "nullErrno=" + nullErrno +
                ", nullData=" + nullData.toString() +
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
                    "nullPrivateX=" + nullPrivateX +
                    ", nullCreateTime='" + nullCreateTime + '\'' +
                    ", nullActTime='" + nullActTime + '\'' +
                    ", nullAuthInfo='" + nullAuthInfo + '\'' +
                    ", nullLastCt='" + nullLastCt + '\'' +
                    ", nullTitle='" + nullTitle + '\'' +
                    ", nullProtocol='" + nullProtocol + '\'' +
                    ", nullOnline=" + nullOnline +
                    ", nullLocation=" + nullLocation +
                    ", nullId='" + nullId + '\'' +
                    ", nullDesc='" + nullDesc + '\'' +
                    ", nullKeys=" + nullKeys +
                    ", nullTags=" + nullTags +
                    ", nullDatastreams=" + nullDatastreams +
                    '}';
        }

        @SerializedName("private")
        private Boolean nullPrivateX;
        @SerializedName("create_time")
        private String nullCreateTime;
        @SerializedName("act_time")
        private String nullActTime;
        @SerializedName("auth_info")
        private String nullAuthInfo;
        @SerializedName("last_ct")
        private String nullLastCt;
        @SerializedName("title")
        private String nullTitle;
        @SerializedName("protocol")
        private String nullProtocol;
        @SerializedName("online")
        private Boolean nullOnline;
        @SerializedName("location")
        private LocationDTO nullLocation;
        @SerializedName("id")
        private String nullId;
        @SerializedName("desc")
        private String nullDesc;
        @SerializedName("keys")
        private List<KeysDTO> nullKeys;
        @SerializedName("tags")
        private List<?> nullTags;
        @SerializedName("datastreams")
        private List<DatastreamsDTO> nullDatastreams;

        public Boolean isPrivateX() {
            return nullPrivateX;
        }

        public void setPrivateX(Boolean privateX) {
            nullPrivateX = privateX;
        }

        public String getCreateTime() {
            return nullCreateTime;
        }

        public void setCreateTime(String createTime) {
            nullCreateTime = createTime;
        }

        public String getActTime() {
            return nullActTime;
        }

        public void setActTime(String actTime) {
            nullActTime = actTime;
        }

        public String getAuthInfo() {
            return nullAuthInfo;
        }

        public void setAuthInfo(String authInfo) {
            nullAuthInfo = authInfo;
        }

        public String getLastCt() {
            return nullLastCt;
        }

        public void setLastCt(String lastCt) {
            nullLastCt = lastCt;
        }

        public String getTitle() {
            return nullTitle;
        }

        public void setTitle(String title) {
            nullTitle = title;
        }

        public String getProtocol() {
            return nullProtocol;
        }

        public void setProtocol(String protocol) {
            nullProtocol = protocol;
        }

        public Boolean isOnline() {
            return nullOnline;
        }

        public void setOnline(Boolean online) {
            nullOnline = online;
        }

        public LocationDTO getLocation() {
            return nullLocation;
        }

        public void setLocation(LocationDTO location) {
            nullLocation = location;
        }

        public String getId() {
            return nullId;
        }

        public void setId(String id) {
            nullId = id;
        }

        public String getDesc() {
            return nullDesc;
        }

        public void setDesc(String desc) {
            nullDesc = desc;
        }

        public List<KeysDTO> getKeys() {
            return nullKeys;
        }

        public void setKeys(List<KeysDTO> keys) {
            nullKeys = keys;
        }

        public List<?> getTags() {
            return nullTags;
        }

        public void setTags(List<?> tags) {
            nullTags = tags;
        }

        public List<DatastreamsDTO> getDatastreams() {
            return nullDatastreams;
        }

        public void setDatastreams(List<DatastreamsDTO> datastreams) {
            nullDatastreams = datastreams;
        }

        public static class LocationDTO {
            @Override
            public String toString() {
                return "LocationDTO{" +
                        "nullLat=" + nullLat +
                        ", nullLon=" + nullLon +
                        '}';
            }

            @SerializedName("lat")
            private Integer nullLat;
            @SerializedName("lon")
            private Integer nullLon;

            public Integer getLat() {
                return nullLat;
            }

            public void setLat(Integer lat) {
                nullLat = lat;
            }

            public Integer getLon() {
                return nullLon;
            }

            public void setLon(Integer lon) {
                nullLon = lon;
            }
        }

        public static class KeysDTO {
            @Override
            public String toString() {
                return "KeysDTO{" +
                        "nullTitle='" + nullTitle + '\'' +
                        ", nullKey='" + nullKey + '\'' +
                        '}';
            }

            @SerializedName("title")
            private String nullTitle;
            @SerializedName("key")
            private String nullKey;

            public String getTitle() {
                return nullTitle;
            }

            public void setTitle(String title) {
                nullTitle = title;
            }

            public String getKey() {
                return nullKey;
            }

            public void setKey(String key) {
                nullKey = key;
            }
        }

        public static class DatastreamsDTO {
            @Override
            public String toString() {
                return "DatastreamsDTO{" +
                        "nullCreateTime='" + nullCreateTime + '\'' +
                        ", nullUuid='" + nullUuid + '\'' +
                        ", nullId='" + nullId + '\'' +
                        '}';
            }

            @SerializedName("create_time")
            private String nullCreateTime;
            @SerializedName("uuid")
            private String nullUuid;
            @SerializedName("id")
            private String nullId;

            public String getCreateTime() {
                return nullCreateTime;
            }

            public void setCreateTime(String createTime) {
                nullCreateTime = createTime;
            }

            public String getUuid() {
                return nullUuid;
            }

            public void setUuid(String uuid) {
                nullUuid = uuid;
            }

            public String getId() {
                return nullId;
            }

            public void setId(String id) {
                nullId = id;
            }
        }
    }
}

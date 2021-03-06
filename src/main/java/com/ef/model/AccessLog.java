package com.ef.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "accessTime", "ipAddress", "httpMethod", "httpStatus", "httpClient" })
public class AccessLog {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime accessTime;
    private String ipAddress;
    private String httpMethod;
    private Integer httpStatus;
    private String httpClient;
}

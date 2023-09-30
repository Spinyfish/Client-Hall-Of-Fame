package dev.eternal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Endpoint {

 DATA_ADD("/data/add"),
 USER_ADD("/user/add"),
 USER_GET("/user/get"),
 USER_LIST("/user/list"),
 SERVER_ADD("/server/add"),
 CRASH_REPORT_ADD("/crash/add");

 private final String endPoint;

}
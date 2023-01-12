package dev.eternal.structures;

import lombok.Builder;
import lombok.ToString;

@Builder@ToString
public class User {

  // Primary key in database
  private int uid;
  private String name;
  //unix time since epoch
  private long last_login;
  private int client_version;
  private String hwid;
  private String os;

}
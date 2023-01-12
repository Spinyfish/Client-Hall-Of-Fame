package dev.eternal.structures;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class BanData {

  private int id; //server id
  private String currentConfig; //current config
  private long playtime; //time spent on that server in millis

}
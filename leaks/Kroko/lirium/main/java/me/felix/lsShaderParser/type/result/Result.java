package me.felix.lsShaderParser.type.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public class Result {

    public String submitter, date, type, main;

    public Result(String submitter, String date, String type, String main) {
        this.submitter = submitter;
        this.date = date;
        this.type = type;
        this.main = main;
    }

}

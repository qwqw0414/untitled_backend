package com.joje.untitled.model.vo;

import com.joje.untitled.common.constants.StatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo {

    private String code = StatusType.SUCCESS.getCode();
    private String message = StatusType.SUCCESS.getMessage();
    private Boolean status = true;
    private Map<String, Object> data = new HashMap<>();

    public ResultVo(StatusType statusType){
        this.code = statusType.getCode();
        this.message = statusType.getMessage();
        this.status = StatusType.SUCCESS.equals(statusType);
    }

    public void put(String key, Object value) {
        log.debug("[{}]=[{}]", key, value);
        data.put(key, value);
    }

}

package com.example.yun.meetup.models;

/**
 * Created by andreoliveira on 12/8/2017.
 */

public class APIResult {
    public static final String RESULT_SUCCESS = "SUCCESS";
    private boolean resultSuccess = false;
    private String resultMessage = null;
    private Object resultEntity = null;

    public APIResult() {

    }

    public APIResult(boolean resultSuccess, String resultMessage, Object resultEntity) {
        this.resultSuccess = resultSuccess;
        this.resultMessage = resultMessage;
        this.resultEntity = resultEntity;
    }

    public boolean isResultSuccess() {
        return resultSuccess;
    }

    public void setResultSuccess(boolean resultSuccess) {
        this.resultSuccess = resultSuccess;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Object getResultEntity() {
        return resultEntity;
    }

    public void setResultEntity(Object resultEntity) {
        this.resultEntity = resultEntity;
    }
}

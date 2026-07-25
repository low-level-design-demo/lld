package com.demo.lld.chainOfResponsibility;

/**
 * LoggerObject
 */
public abstract class LoggerObject {
    LoggerObject loggerObject;

    public LoggerObject(LoggerObject loggerObject) {
        
        this.loggerObject = loggerObject;

    }
    public void log(String logLevel,String message){
       this.loggerObject.log(logLevel, message);

    }

}

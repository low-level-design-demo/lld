package com.demo.lld.chainOfResponsibility;

/**
 * LoggerObject
 */
public  class LoggerDebugObject extends LoggerObject{
    LoggerObject loggerObject;

    public LoggerDebugObject(LoggerObject loggerObject) {
        super(loggerObject);
        System.out.println("Inside LoggerDebugObject constructor");
        this.loggerObject = loggerObject;

    }
    public void log(String logLevel,String message){
        if("DEBUG".equals(logLevel)){
            System.out.println("Printing DEBUG message"+ message);
        }else{
            super.log(logLevel,message);
        }

    }

}

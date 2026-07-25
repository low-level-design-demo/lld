package com.demo.lld.chainOfResponsibility;

/**
 * LoggerObject
 */
public  class LoggerErrorObject extends LoggerObject{
    LoggerObject loggerObject;

    public LoggerErrorObject(LoggerObject loggerObject) {
        super(loggerObject);
        System.out.println("Inside LoggerErrorObject constructor");
        this.loggerObject = loggerObject;

    }
    public void log(String logLevel,String message){
        if("ERROR".equals(logLevel)){
            System.out.println("Printing ERROR message"+ message);
        }else{
            super.log(logLevel,message);
        }

    }

}

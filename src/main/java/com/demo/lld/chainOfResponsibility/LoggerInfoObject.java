package com.demo.lld.chainOfResponsibility;

/**
 * LoggerObject
 */
public  class LoggerInfoObject extends LoggerObject{
    LoggerObject loggerObject;

    public LoggerInfoObject(LoggerObject loggerObject) {
        
        super(loggerObject);
        System.out.println("Inside LoggerInfoObject constructor");
        this.loggerObject = loggerObject;

    }
    public void log(String logLevel,String message){
        if("INFO".equals(logLevel)){
            System.out.println("Printing INFO message"+ message);
        }else{
            super.log(logLevel,message);
        }

    }

}

package com.demo.lld.chainOfResponsibility;

public class ChainOfResponsibilityDemo {
public static void main(String[] args) {
 
    LoggerObject loggerObj=new LoggerInfoObject(new LoggerDebugObject(new LoggerErrorObject(null)));
    loggerObj.log("ERROR", "This is demo message");

    
}
}

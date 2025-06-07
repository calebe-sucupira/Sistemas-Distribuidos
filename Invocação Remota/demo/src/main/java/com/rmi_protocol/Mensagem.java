package com.rmi_protocol;

import java.io.Serializable;

public class Mensagem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int messageType; 
    private int requestId;
    private String objectReference;
    private String methodId;
    private byte[] arguments;

    public Mensagem(int messageType, int requestId, String objectReference, String methodId, byte[] arguments) {
        this.messageType = messageType;
        this.requestId = requestId;
        this.objectReference = objectReference;
        this.methodId = methodId;
        this.arguments = arguments;
    }
    
    public int getMessageType() { return messageType; }
    public int getRequestId() { return requestId; }
    public String getObjectReference() { return objectReference; }
    public String getMethodId() { return methodId; }
    public byte[] getArguments() { return arguments; }
}
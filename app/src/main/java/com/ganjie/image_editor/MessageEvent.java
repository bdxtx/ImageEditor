package com.ganjie.image_editor;

/**
 * 作者：陈思村 on 2019/6/21.
 * 邮箱：chensicun@51ganjie.com
 */
public class MessageEvent {
    private String message;

    public MessageEvent(String message){
        this.message=message;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

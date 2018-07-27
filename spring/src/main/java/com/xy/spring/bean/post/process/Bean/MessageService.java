package com.xy.spring.bean.post.process.Bean;

@Service(name = "messageService")
public class MessageService {

    public void sendMessage() {

        System.out.println(" send message!");
    }
}
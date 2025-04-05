package com.openclassrooms.ChatTop.Service;

import com.openclassrooms.ChatTop.Model.Message;
import com.openclassrooms.ChatTop.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message SaveMessage(Message message){
        return messageRepository.save(message);
    }

}

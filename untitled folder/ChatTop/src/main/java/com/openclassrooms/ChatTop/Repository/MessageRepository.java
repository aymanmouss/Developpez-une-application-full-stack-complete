package com.openclassrooms.ChatTop.Repository;

import com.openclassrooms.ChatTop.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}

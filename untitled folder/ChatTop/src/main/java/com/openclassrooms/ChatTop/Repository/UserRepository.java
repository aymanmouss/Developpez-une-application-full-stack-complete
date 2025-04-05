package com.openclassrooms.ChatTop.Repository;

import com.openclassrooms.ChatTop.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}

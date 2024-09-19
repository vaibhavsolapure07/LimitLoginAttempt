package com.infosys.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.Entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{
	
	public User findByEmail(String email);
	
	public User findByVerificationCode(String verificationCode);
	
	@Modifying
	@Query("update User u set u.failedAttempts=?1 where u.email=?2")
	public void updateFailedAttempt(int attempt, String email);
}

package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.User;
import org.opensrp.repository.AllUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	private final AllUsers allUsers;
	
	@Autowired
	public UserService(AllUsers allUsers)
	{
		this.allUsers = allUsers;
	}
	
	public User getUserByEntityId(String baseEntityId)
	{
		return allUsers.findByBaseEntityId(baseEntityId);
	}
	public List<User> getAllUsers()
	{
		return allUsers.findAllUsers();
	}
	
	public void addUser(User user)
	{
		allUsers.add(user);
	}

	public void updateUser(User user)
	{
		allUsers.update(user);
	}
}

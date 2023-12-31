package com.project.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.entities.User;
import com.project.exceptions.ResourceNotFoundException;
import com.project.module.dto.UserDto;
import com.project.repositories.UserRepo;
import com.project.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserDto createUser(UserDto userDto) {		
		User user = this.dtoToUser(userDto);
		User savedUser = this.userRepo.save(user);
		return this.userToDto(savedUser);
	}

	@Override
	public UserDto updateUser(UserDto userDto, long userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User","Id",userId));
		
		user.setBranch(userDto.getBranch());
		user.setName(userDto.getName());
		user.setBranch(userDto.getBranch());
		user.setSemester(userDto.getSemester());
		user.setEnrollment(userDto.getEnrollment());
		user.setPassword(encodePassword(userDto.getPassword()));
		
		User updatedUser = this.userRepo.save(user);
		UserDto userDto1 = this.userToDto(updatedUser);
		return userDto1;
	}

	@Override
	public UserDto getUserById(String enrollment) {
		User user = userRepo.findByEnrollment(enrollment);
        if (user != null) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            userDto.setBranch(user.getBranch());
            userDto.setEnrollment(enrollment);
            userDto.setSemester(user.getSemester());
            return userDto;
        } else {
            return null; 
        }
        
	}
	
//	@Override
//	public List<String> getAllEnrollments(){
//		List<String> users = this.userRepo.getAllEnrollments();
//		//List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());
//		return users;
//	
//	}
	@Override
	public List<UserDto> getAllUser() {
		List<User> users = this.userRepo.findAll();
		List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());
		return userDtos;
	}

	@Override
	public void deleteUser(Long userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User","Id",userId));
		
		this.userRepo.delete(user);
		
	}

	private User dtoToUser(UserDto userDto) {
		User user = new User();
		user.setId(userDto.getId());
		user.setName(userDto.getName());
		user.setBranch(userDto.getBranch());
		user.setEnrollment(userDto.getEnrollment());
		user.setSemester(userDto.getSemester());
		user.setPassword(encodePassword(userDto.getPassword()));
		user.setRoles("ROLE_USER");
		return user;
	}
	private UserDto userToDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setBranch(user.getBranch());
		userDto.setEnrollment(user.getEnrollment());
		userDto.setSemester(user.getSemester());
		userDto.setPassword(user.getPassword());
		userDto.setRoles(user.getRoles());
		return userDto;
	}	
	
	@Override
	public User findByEnrollementAndPassword(String enrollment, String password) {
		return userRepo.findByEnrollmentAndPassword(enrollment, password);
	}
	@Override
	public User findByEnrollment(String enrollment) {
		return userRepo.findByEnrollment(enrollment);
	}
	
	private String encodePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}

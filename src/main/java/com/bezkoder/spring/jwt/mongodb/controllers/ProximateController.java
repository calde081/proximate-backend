package com.bezkoder.spring.jwt.mongodb.controllers;

import com.bezkoder.spring.jwt.mongodb.models.Post;
import com.bezkoder.spring.jwt.mongodb.models.UpdateUserData;
import com.bezkoder.spring.jwt.mongodb.models.User;
import com.bezkoder.spring.jwt.mongodb.models.UserData;
import com.bezkoder.spring.jwt.mongodb.payload.request.PostRequest;
import com.bezkoder.spring.jwt.mongodb.payload.request.UpdateUserRequest;
import com.bezkoder.spring.jwt.mongodb.payload.response.MessageResponse;
import com.bezkoder.spring.jwt.mongodb.repository.PostRepository;
import com.bezkoder.spring.jwt.mongodb.repository.RoleRepository;
import com.bezkoder.spring.jwt.mongodb.repository.UserRepository;
import com.bezkoder.spring.jwt.mongodb.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/proximate")
public class ProximateController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@GetMapping("/post")
	@PreAuthorize("isAuthenticated()")
	public List<Post> getPosts() {
		return postRepository.findAll();
	}

	@PostMapping("/post")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> postPost(@RequestBody PostRequest newPostRequest) {
		Post post = new Post(newPostRequest.getTitle(), newPostRequest.getSubtitle(), newPostRequest.getDescription(),
				newPostRequest.getImageUrl(), newPostRequest.getProfileImageUrl(), newPostRequest.getUser());
		postRepository.save(post);
		return ResponseEntity.ok(new MessageResponse("Post created successfully!"));
	}

	@GetMapping("/user")
	@PreAuthorize("isAuthenticated()")
	public UserData getPosts(@RequestParam String username) {
		if(userRepository.findByUsername(username).isPresent()) {
			User user = userRepository.findByUsername(username).get();
			UserData userData = new UserData();
			userData.setBio(user.getBio());
			userData.setUsername(user.getUsername());
			userData.setEmail(user.getEmail());
			userData.setProfileImageUrl(user.getProfileImageUrl());

			return userData;
		}
		return null;
	}

	@PostMapping("/update-user")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
		if(userRepository.findByUsername(updateUserRequest.getUsername()).isPresent()) {
			User user = userRepository.findByUsername(updateUserRequest.getUsername()).get();
			user.setBio(updateUserRequest.getBio());
			user.setProfileImageUrl(updateUserRequest.getProfileImageUrl());
			userRepository.save(user);
			return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
		}
		return null;
	}
}

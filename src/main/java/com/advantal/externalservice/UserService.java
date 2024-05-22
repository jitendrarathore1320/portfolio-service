package com.advantal.externalservice;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserService {
	
	@GetMapping("/api/user/get_profile_by_id")
	public Map<String, Object> getProfileById(@RequestParam(required = true) Long userId);
		

}

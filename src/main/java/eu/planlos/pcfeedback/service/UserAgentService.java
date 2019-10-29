package eu.planlos.pcfeedback.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.UserAgent;
import eu.planlos.pcfeedback.repository.UserAgentRepository;

@Service
public class UserAgentService {

	@Autowired
	private UserAgentRepository uaRepo;
	
	public void saveUserAgent(String text) {
		
		UserAgent ua = new UserAgent();
		ua.setText(text);
		
		uaRepo.save(ua);
	}
	
	public List<UserAgent> findAll() {
		
		return (List<UserAgent>) uaRepo.findAll();
	}
}

package com.uc.test;

import com.uc.web.domain.security.UserProfile;
import com.uc.web.service.SecurityServiceBase;
import com.uc.web.service.Service;

public abstract class AbstractServiceTest<KeyType> extends AbstractMapperTest{
	
	private Service service;
	private SecurityServiceBase<KeyType> securityService;
		
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public SecurityServiceBase<KeyType> getSecurityService() {
		return securityService;
	}
	public void setSecurityService(SecurityServiceBase<KeyType> securityService) {
		this.securityService = securityService;
	}
	UserProfile getUser(String loginId){
		return securityService.selectUserProfile(loginId);
	}
}

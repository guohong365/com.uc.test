package com.uc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.uc.utils.LoggerSupportor;
import com.uc.web.persistence.Mapper;

public abstract class AbstractMapperTest 
	extends AbstractTransactionalJUnit4SpringContextTests implements LoggerSupportor {
	private Logger logger;
	@Override
	public Logger getLogger() {
		return logger;
	}
	private Mapper mapper;
	
	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}
	
	public Mapper getMapper() {
		return mapper;
	}
	
	public AbstractMapperTest() {
		logger=LoggerFactory.getLogger(getClass());
	}
}

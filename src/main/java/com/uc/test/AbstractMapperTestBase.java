package com.uc.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.StringUtils;

import com.uc.web.domain.WithId;
import com.uc.web.domain.WithUuid;
import com.uc.web.forms.ListQueryForm;
import com.uc.web.persistence.AppDeleteMapper;
import com.uc.web.persistence.AppInsertMapper;
import com.uc.web.persistence.AppListMapper;
import com.uc.web.persistence.AppOptimizedMapper;
import com.uc.web.persistence.AppSelectByKeyMapper;
import com.uc.web.persistence.AppUpdateMapper;
import com.uc.web.persistence.AppUuidMapper;
import com.uc.web.persistence.Example;
import com.uc.web.persistence.ExampleImpl;

public abstract class AbstractMapperTestBase<KeyType, QueryFormType extends ListQueryForm, DetailType> 
	extends AbstractMapperTest {
	

	public AppInsertMapper getInsertMapper() {
		return getMapper() instanceof AppInsertMapper ? (AppInsertMapper)getMapper() : null;
	}
	
	public AppUpdateMapper getUpdateMapper() {
		return getMapper() instanceof AppUpdateMapper ? (AppUpdateMapper)getMapper() : null;
	}
	public AppDeleteMapper getDeleteMapper() {
		return getMapper() instanceof AppDeleteMapper ? (AppDeleteMapper)getMapper() : null;
	}
	public AppUuidMapper getUuidMapper() {
		return getMapper() instanceof AppUuidMapper ? (AppUuidMapper) getMapper() : null;
	}
	public AppOptimizedMapper getOptimizedMapper() {
		return getMapper() instanceof AppOptimizedMapper ? (AppOptimizedMapper) getMapper() : null;
	}
	
	public AppSelectByKeyMapper getSelectByKeyMapper(){
		return getMapper() instanceof AppSelectByKeyMapper ? (AppSelectByKeyMapper)getMapper() : null;
	}	
	
	public AppListMapper getListMapper() {
		return getMapper() instanceof AppListMapper ? (AppListMapper)getMapper() : null;
	}
	
	protected boolean verifyInserted(DetailType loadback, DetailType inserted){
		System.err.println("verify inserted entity...");
		return true;
	}
	protected boolean verifyUpdated(DetailType loadback, DetailType updated){
		System.err.println("verify updated entity...");		
		return true;
	}
	protected boolean verifyPartlyUpdated(DetailType loadback, DetailType updated){
		
		return true;
	}	
	protected boolean hasLimit(){
		return true;
	}
	protected void testOther(){
		System.err.println("test OTHER");
	};

	@SuppressWarnings("unchecked")
	KeyType getEntityId(Object entity){
		if(entity instanceof WithId){
			KeyType id=(KeyType) ((WithId)entity).getId();
			if(id!=null) return id;
 
			if(entity instanceof WithUuid && getUuidMapper()!=null){
				String uuid=((WithUuid)entity).getUuid();
				if(!StringUtils.isEmpty(uuid)){
					id=(KeyType) getUuidMapper().selectIdByUuid(uuid);
					return id;
				}
			}			
		}
		fail("entity no KEY!");
		return null;
	}
	
	String getEntityUuid(DetailType entity){
		return ((WithUuid)entity).getUuid();
	}
	@SuppressWarnings("unchecked")
	DetailType loadBack(DetailType entity){
		KeyType key=(KeyType) getEntityId(entity);
		assertNotNull(key);				
		DetailType loadback=(DetailType) getSelectByKeyMapper().selectById(key);
		assertNotNull(loadback);
		assertNotNullFields(loadback);	
		return loadback;
	}
	void verifyInserted(DetailType inserted){
		DetailType loadback=loadBack(inserted);
		assertTrue(verifyInserted(loadback, inserted));
	}
	void verifyUpdated(DetailType updated){
		DetailType loadback=loadBack(updated);
		assertTrue(verifyUpdated(loadback, updated));
	}
	void verifyPartlyUpdated(DetailType updated){
		DetailType loadback=loadBack(updated);
		assertTrue(verifyPartlyUpdated(loadback, updated));
	}
	@SuppressWarnings("unchecked")
	@Test
	@Rollback
	public void runTest()
	{	
		DetailType entity=prepareEntity();
		int ret;
		if(getInsertMapper()!=null){
			System.err.println("test insert....");
			assertNotNull(entity);
			ret=getInsertMapper().insertDetail(entity);
			assertTrue(ret > 0);
			System.err.println("entity inserted....");
			if(getSelectByKeyMapper() !=null){		
				System.err.println("verify inserted entity....");
				verifyInserted(entity);
			}
		}
		DetailType result;
		if(getUuidMapper()!=null){
			System.err.println("test uuid mapper....");
			String uuid=getEntityUuid(entity);			
			result =(DetailType) getUuidMapper().selectByUuid(uuid);
			System.err.println("entity loaded by uuid.....");			
			assertNotNull(result);
			System.err.println(result.toString());
			assertNotNullFields(result);
			System.err.println("verify loaded by uuid.....");
			verifyInserted(result, entity);
		}
			
		if(getUpdateMapper() !=null){
			System.err.println("test update mapper....");
			result=loadBack(entity);
			assertNotNull(result);
			System.err.println("updating entity loaded....");
			System.err.println(result.toString());
			DetailType updated=changeForUpdate(result);
			System.err.println("entity changed....");
			System.err.println(updated.toString());			
			ret=getUpdateMapper().updateDetail(updated);
			assertTrue(ret>0);
			System.err.println("entity updated....");
			if(getSelectByKeyMapper()!=null){
				System.err.println("verify updated entity....");
				verifyUpdated(updated);					
				System.err.println("updated entity verified....");
			}				
			updated=changeForPartlyUpdate(result);
			System.err.println("entity partly changed...");
			System.err.println(updated.toString());
			ret=getUpdateMapper().updateDetailSelective(updated);
			assertTrue(ret > 0);
			System.err.println("entity partlly updated....");
			if(getSelectByKeyMapper()!=null){
				System.err.println("verify updated entity....");
				verifyPartlyUpdated(updated);				
				System.err.println("updated entity verified....");
			}
		}	
			
		if(getDeleteMapper()!=null){
			System.err.println("test delete mapper");
			DetailType deleted= loadBack(entity);
			assertNotNull(deleted);
			ret=getDeleteMapper().deleteDetail(deleted);
			assertTrue(ret > 0);
			System.err.println("entity deleted....");
		}
	
		if(getListMapper()!=null){
			System.err.println("test list mapper.....");
			Example example=new ExampleImpl();
			System.err.println("test select by empty example....");
			Long count=getListMapper().selectCountByExample(example);
			assertNotNull(count);			
			System.err.println("count=" + count);
			List<?> list = getListMapper().selectByExample(example, 0, 10);
			if(hasLimit()){
				assertTrue(count > 10 ? list.size()==10 : list.size()==count);
			}
			System.err.println("load " + count + " records.....");			
			prepareExample(example);
			System.err.println("example prepared....");
			System.err.println(example.toString());			
			count=getListMapper().selectCountByExample(example);
			assertNotNull(count);			
			System.err.println("count=" + count);
			list = getListMapper().selectByExample(example, 0, 10);
			if(hasLimit()){
				assertTrue(count > 10 ? list.size()==10 : list.size()==count);
			}
			System.err.println("load " + count + " records.....");
		}
		
		if(getOptimizedMapper()!=null){
			System.err.println("test optimized mapper....");
			QueryFormType queryForm=createQueryForm();
			System.err.println("test select by empty queryFrom....");
			Long count = getOptimizedMapper().selectCountOptimized(queryForm);
			System.err.println("count=" + count);
			List<?> list=getOptimizedMapper().selectOptimized(queryForm, 0, 10);
			if(hasLimit()){
				assertTrue(count > 10 ? list.size()==10 : list.size()==count);
			}
			System.err.println("load " + count + " records.....");
			prepareQueryForm(queryForm);
			System.err.println("queryForm prepared.....");
			System.err.println(queryForm.toString());			
			count = getOptimizedMapper().selectCountOptimized(queryForm);
			System.err.println("count=" + count);
			list=getOptimizedMapper().selectOptimized(queryForm, 0, 10);
			if(hasLimit()){
				assertTrue(count > 10 ? list.size()==10 : list.size()==count);
			}
			System.err.println("load " + count + " records.....");
		}
		
		testOther();
	}
	protected DetailType prepareEntity(){
		return null;
	}
	protected QueryFormType createQueryForm(){
		return null;
	}
	protected void prepareQueryForm(QueryFormType queryForm){
		
	}
	protected void prepareExample(Example example){
		
	}
	protected void assertNotNullFields(DetailType entity){
		
	}
	protected DetailType changeForUpdate(DetailType entity){
		return entity;
	}
	protected DetailType changeForPartlyUpdate(DetailType entity){
		return entity;
	}

}

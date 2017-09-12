package com.uc.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import com.uc.web.forms.ListQueryForm;
import com.uc.web.forms.ui.componet.PageCtrl;
import com.uc.web.forms.ui.componet.PageCtrlImpl;
import com.uc.web.service.AppDetailService;
import com.uc.web.service.AppExportService;
import com.uc.web.service.AppListService;
import com.uc.web.service.AppWebListService;

public abstract class AbstractServiceTestBase<KeyType, QueryFormType extends ListQueryForm, EntityType> extends AbstractServiceTest<KeyType>{
	
	@SuppressWarnings("unchecked")
	AppDetailService<KeyType, EntityType> getDetailService(){
		if(getService() instanceof AppDetailService){
			return (AppDetailService<KeyType, EntityType>) getService();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	AppListService<QueryFormType, EntityType> getListService(){
		if(getService() instanceof AppListService){
			return (AppListService<QueryFormType, EntityType>) getService();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	AppExportService<QueryFormType, EntityType> getExportService(){
		if(getService() instanceof AppExportService){
			return (AppExportService<QueryFormType, EntityType>) getService();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	AppWebListService<QueryFormType, EntityType> getWebListService(){
		if(getService() instanceof AppWebListService){
			return (AppWebListService<QueryFormType, EntityType>) getService();
		}
		return null;
	}
	@Ignore
	@Test
	@Rollback
	public void runTest(){
		if(getDetailService()!=null){
			System.err.println("begin test detail service ......");
			EntityType entity= createEntity();
			int ret=getDetailService().insert(entity);
			if(ret > 0){
				KeyType id=getEntityId(entity);
				EntityType loadback=getDetailService().selectById(id);
				assertNotNull(loadback);
				assertNotNullFields(loadback);
				verifyInserted(loadback, entity);
			}
			EntityType updating=getUpdating();
			ret = getDetailService().update(updating);
			updating= getPartlyUpdating();
			ret = getDetailService().updateSelective(updating);
			
			getDetailService().delete(updating);
		}
		QueryFormType queryForm = createQueryForm();
		if(getListService()!=null){			
			assertNotNull(queryForm);			
			Long count=getListService().selectCount(queryForm);
			assertNotNull(count);			
			List<EntityType> list= getListService().select(queryForm, 0, 10);
			assertTrue(list.size() == (count > 10 ? 10 : count));
		}
		
		if(getExportService()!=null){			
			List<EntityType> list= getExportService().selectForExport(queryForm);
			assertNotNull(list);			
		}
		
		if(getWebListService()!=null){			
			queryForm=createQueryForm();
			PageCtrl pageCtrl=new PageCtrlImpl();	
			getWebListService().select(queryForm, pageCtrl);
		}
	}

	protected QueryFormType createQueryForm(){
		return null;
	}

	protected EntityType getPartlyUpdating() {
		return null;
	}

	protected EntityType getUpdating() {
		return null;
	}

	protected void verifyInserted(EntityType loadback, EntityType entity) {
	}

	protected void assertNotNullFields(EntityType loadback) {
	}

	protected KeyType getEntityId(EntityType entity) {
		return null;
	}

	protected EntityType createEntity(){
		return null;
	}
}

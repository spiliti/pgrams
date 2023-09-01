Insert into eg_appconfig (id,key_name,description,version,createdby,lastmodifiedby,createddate,lastmodifieddate,module) 
  values (nextval('SEQ_EG_APPCONFIG'),'PTIS_DESIGNATIONFORWF_MOBILE','PTIS - Designation for Workflow for Mobile Application', 0, 
(select id from eg_user where username='egovernments'),(select id from eg_user where username='egovernments'),now(),now(),
(select id from eg_module where name='Property Tax'));

Insert into eg_appconfig_values (id, key_id, effective_from, createdby,lastmodifiedby,createddate,lastmodifieddate,value, version) values 
  (nextval('SEQ_EG_APPCONFIG_VALUES'), (select id from eg_appconfig where key_name = 'PTIS_DESIGNATIONFORWF_MOBILE' and module = (select id from eg_module where name = 'Property Tax')),
  now(), (select id from eg_user where username='egovernments'),(select id from eg_user where username='egovernments'),now(),now(), 'UD Revenue Inspector', 0);



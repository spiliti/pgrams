INSERT INTO eg_feature_action (ACTION, FEATURE) VALUES ((select id FROM eg_action  WHERE name = 'VacantLandDCBReport') ,(select id FROM eg_feature WHERE name = 'Property Tax Reports'));
INSERT INTO eg_feature_action (ACTION, FEATURE) VALUES ((select id FROM eg_action  WHERE name = 'VacantLandDCBReportResult') ,(select id FROM eg_feature WHERE name = 'Property Tax Reports'));
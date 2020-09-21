-- 10001,10016
-- SELECT  * FROM subjectinfo   WHERE account = '100200001' AND CONCAT(all_subject,subject_code)  LIKE '2191/40/10'  ;

-- SELECT  * FROM subjectinfo   WHERE account = '100200001' AND CONCAT(all_subject,subject_code)  LIKE '1243/20/02'  ;


UPDATE  subjectinfo SET  special_id = '10001,10016'   WHERE account = '100200001' AND CONCAT(all_subject,subject_code)  LIKE '2191/40/10' ;
UPDATE  subjectinfo SET  special_id = '10001,10016'   WHERE account = '100200001' AND CONCAT(all_subject,subject_code)  LIKE '1243/20/02' ;

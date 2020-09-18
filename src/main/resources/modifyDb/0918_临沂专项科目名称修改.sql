# 1151/10 修改科目名称
UPDATE  subjectinfo SET  subject_name ='预付账款-集团外'  WHERE account = '100200001' AND all_subject LIKE '1151/';
UPDATE  subjectinfo SET  subject_name ='预付账款-集团外-省内'  WHERE account = '100200001' AND all_subject LIKE '1151/10/';

# 修改专项名称名称 鲁Q363CK押金qy冯
UPDATE  specialinfo SET  special_name ='鲁Q363CK押金qy冯' ,special_namep ='鲁Q363CK押金qy冯'   WHERE account = '100200001' AND special_code LIKE 'WLDX0250889';
# 修改专项名称名称  鲁Q3WN70wy保养
UPDATE  specialinfo SET  special_name ='鲁Q3WN70wy保养' ,special_namep ='鲁Q3WN70wy保养'  WHERE account = '100200001' AND special_code LIKE 'WLDX0250948';

commit;
DROP TABLE IF EXISTS `bz_configureManage`;

CREATE TABLE bz_configureManage(
	subject_code VARCHAR(50) NOT NULL COMMENT '科目代码',
	subject_name VARCHAR(50) NOT NULL COMMENT '科目名称',
	special_code VARCHAR(50) COMMENT '专项末级信息',
	special_super_code VARCHAR(50) COMMENT '一级专项信息',
	interface_info VARCHAR(2) NOT NULL COMMENT '接口信息',
	interface_type VARCHAR(2) NOT NULL COMMENT '接口类型',
	create_time VARCHAR(20) COMMENT '创建日期',
	temp1 VARCHAR(50) COMMENT '科目顺序',
	temp2 VARCHAR(50) COMMENT '备用字段2',
	temp3 VARCHAR(50) COMMENT '备用字段3',
	PRIMARY KEY (`subject_code`,`interface_info`,`interface_type`)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='接口类型与科目关联关系表';


-- 第二个接口，类型1
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1131/90/40/','应收账款-VSB-NV-冲销账户*','0000','1','2','1','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('5101/06/61/','主营收入-PbP-GLK-零售','1001','1','2','1','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('5401/06/61/','主营成本-PbP-GLK-零售','1001','1','2','1','2020-08-12 18:00:00','3');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1243/26/60/','库存商品-车辆-PbP-GLK *','0000','1','2','1','2020-08-12 18:00:00','4');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('9101/00/00/','单位-PL中间科目','1001','1','2','1','2020-08-12 18:00:00','5');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('9101/06/61/','销售单位-PbP-GLK-零售','1001','1','2','1','2020-08-12 18:00:00','6');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('2432/00/00/','单位-BS中间科目','0000','1','2','1','2020-08-12 18:00:00','7');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('2432/66/00/','库存单位-车辆-PbP-GLK','0000','1','2','1','2020-08-12 18:00:00','8');



-- 第二个接口， 类型2
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1131/90/40/','应收账款-VSB-NV-冲销账户*','0000','1','2','2','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1131/11/01/','应收账款-集团外-省内','0000','1','2','2','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('2171/01/05/','应交税金-增值税-销项税额','0000','1','2','2','2020-08-12 18:00:00','3');



-- 第二个接口， 类型3
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1131/90/40/','应收账款-VSB-NV-冲销账户*','0000','1','2','3','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('5101/06/61/','主营收入-PbP-GLK-零售','1001','1','2','3','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('5401/06/61/','主营成本-PbP-GLK-零售','1001','1','2','3','2020-08-12 18:00:00','3');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1243/26/60/','库存商品-车辆-PbP-GLK *','0000','1','2','3','2020-08-12 18:00:00','4');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('9101/00/00/','单位-PL中间科目','1001','1','2','3','2020-08-12 18:00:00','5');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('9101/06/61/','销售单位-PbP-GLK-零售','1001','1','2','3','2020-08-12 18:00:00','6');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('2432/00/00/','单位-BS中间科目','0000','1','2','3','2020-08-12 18:00:00','7');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('2432/66/00/','库存单位-车辆-PbP-GLK','0000','1','2','3','2020-08-12 18:00:00','8');



-- 第二个接口， 类型4
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1131/90/40/','应收账款-VSB-NV-冲销账户*','0000','1','2','4','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('1131/11/01/','应收账款-集团外-省内','0000','1','2','4','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1) VALUES('2171/01/05/','应交税金-增值税-销项税额','0000','1','2','4','2020-08-12 18:00:00','3');


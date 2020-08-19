DROP TABLE IF EXISTS `bz_configureManage`;

CREATE TABLE bz_configureManage(
	branch_code VARCHAR(50) NOT NULL COMMENT '机构编码',
	subject_code VARCHAR(50) NOT NULL COMMENT '科目代码',
	subject_name VARCHAR(50) NOT NULL COMMENT '科目名称',
	special_code VARCHAR(50) COMMENT '专项末级信息',
	special_super_code VARCHAR(50) COMMENT '一级专项信息',
	interface_info VARCHAR(2) NOT NULL COMMENT '接口信息',
	interface_type VARCHAR(2) NOT NULL COMMENT '接口类型',
	create_time VARCHAR(20) COMMENT '创建日期',
	temp1 VARCHAR(50) NOT NULL COMMENT '科目顺序',
	temp2 VARCHAR(50) COMMENT '备用字段2',
	temp3 VARCHAR(50) COMMENT '备用字段3',
	PRIMARY KEY (`branch_code`,`subject_code`,`interface_info`,`interface_type`,`temp1`)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='接口类型与科目关联关系表';




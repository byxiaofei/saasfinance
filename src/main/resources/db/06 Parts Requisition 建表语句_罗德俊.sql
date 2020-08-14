DROP TABLE IF EXISTS bz_partsrequisition;

CREATE TABLE bz_partsrequisition(
	id INT(11)  PRIMARY KEY AUTO_INCREMENT COMMENT '自增id',
	dealer_no VARCHAR(50) COMMENT '经销商 GSSN 号',
	company_no VARCHAR(50) COMMENT '记账公司 GSSN 号',
	customer_name VARCHAR(50) COMMENT '客户名称',
	company_name VARCHAR(50) COMMENT '公司名称',
	customer_id VARCHAR(50) COMMENT '客户唯一标识',
	company_id VARCHAR(50) COMMENT '公司唯一标识',
	fin VARCHAR(50) COMMENT 'fin',
	vin VARCHAR(50) COMMENT 'vin',
	registration_no  VARCHAR(50) COMMENT '车牌号',
	doc_type VARCHAR(50)  COMMENT '文档类型',
	doc_no VARCHAR(50) COMMENT '单号',
	doc_date VARCHAR(50) COMMENT '日期',
	operation_date VARCHAR(50) COMMENT '操作日期',
	order_no  VARCHAR(50) COMMENT '工单号码',
	line VARCHAR(50) COMMENT '行号',
	parts_no VARCHAR(50) COMMENT '零件编号',
	description VARCHAR(50) COMMENT '说明',
	genuine_flag VARCHAR(50) COMMENT '原厂配件标记',
	parts_analysis_code VARCHAR(50) COMMENT 'Parts 分析代码',
	quantity DECIMAL(14,2) COMMENT '数量',
	parts_unit_cost DECIMAL(14,2) COMMENT '配件单位成本',
	ict_order VARCHAR(50) COMMENT 'ICT 参考号',
	ict_company VARCHAR(50) COMMENT '需求方经销商'
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='所有已打印的零件领料单和退货单的数据';
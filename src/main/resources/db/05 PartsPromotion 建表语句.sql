DROP TABLE IF EXISTS bz_partspromotion;
-- 所有总成件组装和拆分的数据
CREATE TABLE bz_partspromotion(
	id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '自增id',
	dealer_no VARCHAR(50) COMMENT '经销商 GSSN 号',
	company_no VARCHAR(50) COMMENT '记账公司 GSSN 号',
	transaction_type VARCHAR(50) COMMENT '类型',
	promotion_date VARCHAR(50) COMMENT '组装/拆分日期',
	parts_no VARCHAR(50) COMMENT '零件编号',
	flag VARCHAR(50) COMMENT '1-总成件 2-零件',
	genuine_flag VARCHAR(50) COMMENT 'Y-原厂 N-非原厂',
	description VARCHAR(50) COMMENT '描述说明',
	parts_analysis_code VARCHAR(50) COMMENT 'Parts 分析代码',
	quantity DECIMAL(14,2) COMMENT '数量',
	parts_unit_cost DECIMAL(14,2) COMMENT '配件单位成本',
	operation_date VARCHAR(50) COMMENT '操作日期'
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='所有总成件组装和拆分的数据';
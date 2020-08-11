DROP TABLE IF EXISTS `bz_vehicleInvoice`;
-- 车辆销售订单结账、退账的数据
CREATE TABLE bz_vehicleinvoice(
	id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '自增id',
	dealer_no VARCHAR(50) COMMENT '经销商 GSSN 号',
	company_no VARCHAR(50) COMMENT '记账公司 GSSN 号',
	invoice_type VARCHAR(50) COMMENT '账单类型',
	invoice_no VARCHAR(50) COMMENT '账单/退款单编号',
	origin_invoice_no VARCHAR(50) COMMENT '原账单编号',
	commission_no VARCHAR(50) COMMENT '生产号',
	vin VARCHAR(50) COMMENT '美版底盘号',
	fin VARCHAR(50) COMMENT '欧版底盘号',
	baumuster VARCHAR(50) COMMENT 'baumuster',
	nst VARCHAR(50) COMMENT 'nst',
	brand VARCHAR(50) COMMENT '品牌',
	origin VARCHAR(50) COMMENT '产地',
	model VARCHAR(50) COMMENT '车型',
	type_class VARCHAR(50) COMMENT '车款',
	engine_no VARCHAR(50) COMMENT '引擎号',
	book_in_status VARCHAR(50) COMMENT '车辆入库状态',
	book_in_date VARCHAR(50) COMMENT '入库日期',
	description VARCHAR(50) COMMENT '车辆中文描述',
	order_id VARCHAR(50) COMMENT '订单号',
	sales_type VARCHAR(50) COMMENT '销售类型',
	customer_name VARCHAR(50) COMMENT '客户姓名',
	company_name VARCHAR(50)  COMMENT '公司名称',
	vehicle_price_without_consumtion_tax DECIMAL(14,2) COMMENT '车辆价格（不含消费税）',
	consumption_tax DECIMAL(14,2) COMMENT '消费税',
	vehicle_price DECIMAL(14,2) COMMENT '不含增值税含消费税，车辆价格合计',
	vat_tax DECIMAL(14,2) COMMENT '增值税',
	vehicle_cost DECIMAL(14,2) COMMENT '车辆成本',
	deposit DECIMAL(14,2) COMMENT '首笔定金',
	retail_invoice_date VARCHAR(50) COMMENT '开票日期',
	credit_date  VARCHAR(50) COMMENT '退票日期',
	operation_date VARCHAR(50) COMMENT '操作日期'
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='车辆销售订单结账、退账的数据';



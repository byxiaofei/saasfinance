
DROP TABLE IF EXISTS `bz_partsstock`;

CREATE TABLE `bz_partsstock` (
                               `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `dealer_no` VARCHAR(50) DEFAULT NULL COMMENT '经销商GSSN号',
                               `company_no` VARCHAR(50) DEFAULT NULL COMMENT '记账公司GSSN 号',
                               `transaction_type` VARCHAR(50) DEFAULT NULL COMMENT '业务类型',
                               `operation_date` VARCHAR(50) DEFAULT NULL COMMENT '操作日期',
                               `parts_no` VARCHAR(50) DEFAULT NULL COMMENT '零件编号',
                               `description` VARCHAR(200) DEFAULT NULL COMMENT '说明',
                               `genuine_flag` VARCHAR(10) DEFAULT NULL COMMENT '原厂配件标记',
                               `parts_analysis_code` VARCHAR(10) DEFAULT NULL COMMENT 'parts分析代码',
                               `business_date` VARCHAR(10) DEFAULT NULL  COMMENT '业务发生日期',
                               `po_no` VARCHAR(50) DEFAULT NULL COMMENT '采购订单编号',
                               `quantity` DECIMAL(14,0) DEFAULT NULL COMMENT '入库数量',
                               `parts_unit_cost` DECIMAL(14,2) DEFAULT NULL COMMENT '配件单位成本',
                               `supplier_no` VARCHAR(50) DEFAULT NULL COMMENT '供应商编号',
                               `supplier_description` VARCHAR(200) DEFAULT NULL COMMENT '供应商描述',

                               PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='配件库存变动';
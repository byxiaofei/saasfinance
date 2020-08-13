
DROP TABLE IF EXISTS `bz_partsinvoice`;
CREATE TABLE `bz_partsinvoice`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_no` varchar(50)  DEFAULT NULL COMMENT '记账公司GSSN号',
  `dealer_no` varchar(50)  NULL DEFAULT NULL COMMENT '经销商GSSN号',
  `biz_type` varchar(50)  DEFAULT NULL COMMENT '零件外销工单',
  `doc_type` varchar(50)  DEFAULT NULL COMMENT 'I - 打印账单  C - 退款单',
  `doc_no` varchar(50)  DEFAULT NULL COMMENT '账单编号/退款编号',
  `doc_date` date DEFAULT NULL COMMENT '账单/退款单日期',
  `customer_name` varchar(50)  DEFAULT NULL,
  `company_name` varchar(50)  DEFAULT NULL,
  `franchise` varchar(50)  DEFAULT NULL COMMENT '品牌',
  `order_no` varchar(50)  DEFAULT NULL COMMENT '工单号码',
  `operation_date` date DEFAULT NULL COMMENT '操作日期',
  `line` varchar(50) DEFAULT NULL COMMENT '行号',
  `parts_no` varchar(50)  DEFAULT NULL COMMENT '零件编号',
  `parts_analysis_code` varchar(50)  DEFAULT NULL COMMENT 'parts分析代码',
  `department_code` varchar(50)  DEFAULT NULL COMMENT '部门代码',
  `quantity` decimal(14, 0)  DEFAULT NULL COMMENT '数量',
  `parts_unit_cost` decimal(14, 2)  DEFAULT NULL COMMENT '配件单位成本',
  `unit_selling_price` decimal(14, 2)  DEFAULT NULL COMMENT '单位售价',
  `total_price` decimal(14, 2) DEFAULT NULL COMMENT '总销售价格',
  `discount_rate` decimal(14, 2)  DEFAULT NULL COMMENT '折扣百分比',
  `discount_amount` decimal(14, 2)  DEFAULT NULL COMMENT '折扣金额',
  `contribution` decimal(14, 2)  DEFAULT NULL COMMENT '分担比例',
  `net_value` decimal(14, 2)  DEFAULT NULL COMMENT '净价（不含税）',
  `vat_rate` decimal(14, 2)  DEFAULT NULL COMMENT '增值税税率',
  `vat_amount` decimal(14, 2)  DEFAULT NULL COMMENT '增值税税额',
  `customer_type_no` varchar(50)  DEFAULT NULL COMMENT '客户类型编号',
  `company_id` varchar(50)  DEFAULT NULL,
  `customer_id` varchar(50)  DEFAULT NULL,
  `fin` varchar(50)  DEFAULT NULL,
  `model` varchar(50)  DEFAULT NULL,
  `registration_no` varchar(50)  DEFAULT NULL COMMENT '车牌号',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = '所有零件结账及退账的数据';

-- 如果由当前表，将进行删除
DROP TABLE IF EXISTS `bz_partsverification`;
-- 建表语句
CREATE TABLE `bz_partsverification` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `batch` INT(11) DEFAULT NULL COMMENT '批次',
  `dealer_no` VARCHAR(50) DEFAULT NULL COMMENT '经销商 GSSN 号',
  `company_no` VARCHAR(50) DEFAULT NULL COMMENT '记账公司 GSSN 号',
  `verify_no` VARCHAR(50) DEFAULT NULL COMMENT '校验编号',
  `verify_date` VARCHAR(50) DEFAULT NULL COMMENT '校验日期',
  `supplier_no` VARCHAR(50) DEFAULT NULL COMMENT '供应商编号',
  `supplier_no_description` VARCHAR(50) DEFAULT NULL COMMENT '供应商编号描述',
  `purchase_invoice_no` VARCHAR(50) DEFAULT NULL COMMENT ' 采购发票号',
  `purchase_invoice_reference` VARCHAR(50) DEFAULT NULL COMMENT '采购发票参考（财务人员 手工录入）',
  `purchase_invoice_net` DECIMAL(14,2) DEFAULT NULL COMMENT '发票金额(不含税)',
  `purchase_vat` DECIMAL(14,2) DEFAULT NULL COMMENT '增值税金额',
  `total_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '价税合计',
  `dn_no` VARCHAR(50) DEFAULT NULL COMMENT '发货单号',
  `accept_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '配件入库成本(每张发货单的金额)',
  `variance` DECIMAL(14,2) DEFAULT NULL COMMENT ' 差异',
  `accept_total_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '配件入库总成本',
  `operation_date` VARCHAR(50) DEFAULT NULL COMMENT ' 操作日期',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='所有配件发票校验的数据'
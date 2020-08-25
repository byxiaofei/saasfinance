DROP TABLE IF EXISTS `bz_spring_scheduled_cron`;
CREATE TABLE `bz_spring_scheduled_cron` (
  `cron_id`         INT PRIMARY KEY           AUTO_INCREMENT
  COMMENT '主键id',
  `cron_key`        VARCHAR(128) NOT NULL UNIQUE
  COMMENT '定时任务完整类名',
  `cron_expression` VARCHAR(20)  NOT NULL
  COMMENT 'cron表达式',
  `task_explain`    VARCHAR(100)  NOT NULL     DEFAULT ''
  COMMENT '任务描述',
  `status`          TINYINT      NOT NULL     DEFAULT 1
  COMMENT '状态,1:正常;2:停用',
  UNIQUE INDEX cron_key_unique_idx(`cron_key`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '定时任务表';


insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('1','com.sinosoft.httpclient.controller.OptionChangeController','0 0 22 * * ?','09-获取上次调用时间到当前时间，所有选装件变动的相关数据','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('2','com.sinosoft.httpclient.controller.PartsInvoiceController','0 30 22 * * ?','07-获取上次调用时间到当前时间，所有零件结账及退账的数据','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('3','com.sinosoft.httpclient.controller.PartsPromotionController','0 00 23 * * ?','05-获取上次调用时间到当前时间，所有总成件组装和拆分的数据','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('4','com.sinosoft.httpclient.controller.PartsRequisitionController','0 30 23 * * ?','06-获取上次调用时间到当前时间，所有已打印的零件领料单和退货单的数据','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('5','com.sinosoft.httpclient.controller.PartsStockController','0 20 14 * * ?','03-获取上次调用时间到当前时间，所有配件库存变动相关的数据，包括入库、退库、库存盘点','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('6','com.sinosoft.httpclient.controller.PartsVerificationController','0 30 0 * * ?','04-获取上次调用时间到当前时间，所有配件发票校验的数据','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('7','com.sinosoft.httpclient.controller.ServiceInvoiceController','0 0 1 * * ?','10-获取上次调用时间到当前时间，所有售后结账、退账的相关数据','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('8','com.sinosoft.httpclient.controller.VehicleInvoiceController','0 30 1 * * ?','02-获取上次调用时间到当前时间，所有车辆销售订单结账、退账的数据','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('9','com.sinosoft.httpclient.controller.VehicleStockController','0 36 17 * * ?','01-获取上次调用时间到当前时间，所有车辆库存变动的数据，包含入库退库和成本变更','1');
insert into `bz_spring_scheduled_cron` (`cron_id`, `cron_key`, `cron_expression`, `task_explain`, `status`) values('10','com.sinosoft.httpclient.controller.WarrantyConfirmController','0 30 2 * * ?','08-获取上次调用时间到当前时间，所有索赔确认的数据','1');

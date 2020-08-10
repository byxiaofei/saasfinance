DROP TABLE IF EXISTS `bz_vehiclestock`;

CREATE TABLE `bz_vehiclestock` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `dealer_no` varchar(50) DEFAULT NULL COMMENT '经销商GSSN号',
                               `company_no` varchar(50) DEFAULT NULL COMMENT '记账公司GSSN 号',
                               `transaction_type` varchar(50) DEFAULT NULL COMMENT '业务类型',
                               `stock_change_date` varchar(50) DEFAULT NULL COMMENT '库存变更时录入的日期',
                               `commission_no` varchar(50) DEFAULT NULL COMMENT '生产号',
                               `vin` varchar(50) DEFAULT NULL COMMENT '美版底盘号',
                               `fin` varchar(50) DEFAULT NULL COMMENT '欧版底盘号',
                               `baumuster` varchar(10) DEFAULT NULL ,
                               `nst` varchar(10) DEFAULT NULL ,
                               `brand` varchar(50) DEFAULT NULL COMMENT '品牌',
                               `origin` varchar(50) DEFAULT NULL COMMENT '产地',
                               `model` varchar(50) DEFAULT NULL COMMENT '车型',
                               `type_class` varchar(50) DEFAULT NULL COMMENT '车款',
                               `engine_no` varchar(50) DEFAULT NULL COMMENT '引擎号',
                               `origin_vehicle_status` varchar(50) DEFAULT NULL COMMENT '原车辆状态',
                               `current_vehicle_status` varchar(50) DEFAULT NULL COMMENT '当前车辆状态',
                               `description` varchar(200) DEFAULT NULL COMMENT '车辆中文描述',
                               `vehicle_current_cost` decimal(14,2) DEFAULT NULL COMMENT '车辆最新成本',
                               `vehicle_old_cost` decimal(14,2) DEFAULT NULL COMMENT '车辆上一次成本',
                               `vehicle_cost_change` decimal(14,2) DEFAULT NULL COMMENT '车辆成本变动',
                               `operation_date` varchar(50) DEFAULT NULL COMMENT '操作日期',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='车辆库存变动接口';

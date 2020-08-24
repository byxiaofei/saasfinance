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





-- 接口1 入库分录   业务1
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/25/10/','库存商品-车辆-M*','BM0000','BM','1','1','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2121/01/14/','应付账款-VSB-GRIR-新车*','BM0000','BM','1','1','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','9401/02/10/','采购单位-M','BM1001','BM','1','1','2020-08-12 18:00:00','3');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','9101/00/00/','单位-PL中间科目','BM1001','BM','1','1','2020-08-12 18:00:00','4');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2432/51/00','库存商品-车辆-M*','BM0000','BM','1','1','2020-08-12 18:00:00','5');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2432/00/00/','单位-BS中间科目','BM0000','BM','1','1','2020-08-12 18:00:00','6');

-- 接口1 采购退货分录   业务2
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/25/10/','库存商品-车辆-M*','BM0000','BM','1','2','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2121/01/14/','应付账款-VSB-GRIR-新车*','BM0000','BM','1','2','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','9401/02/10/','采购单位-M','BM1001','BM','1','2','2020-08-12 18:00:00','3');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','9101/00/00/','单位-PL中间科目','BM1001','BM','1','2','2020-08-12 18:00:00','4');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2432/51/00','库存商品-车辆-M*','BM0000','BM','1','2','2020-08-12 18:00:00','5');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2432/00/00/','单位-BS中间科目','BM0000','BM','1','2','2020-08-12 18:00:00','6');

-- 接口1 成本变更（COST_CHANGE 成 本变更）   业务3
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/25/10/','库存商品-车辆-M*','BM0000','BM','1','3','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2121/01/14/','应付账款-VSB-GRIR-新车*','BM0000','BM','1','3','2020-08-12 18:00:00','2');



INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/11/10/','库存商品-配件-MB配件-配件','BM0000','BM','3','1','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/12/30/','库存商品-配件-非MB件-轮胎','BM0000','BM','3','1','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/12/10/','库存商品-配件-非MB件-油料','BM0000','BM','3','1','2020-08-12 18:00:00','3');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/11/20/','库存商品-配件-MB配件-精品件','BM0000','BM','3','1','2020-08-12 18:00:00','4');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2121/02/10/','应付账款-系统用户-GRIR-配件','BM1001','BM','3','1','2020-08-12 18:00:00','5');



-- 接口3 配件退库（- 取消接收入库）时产生的分录   业务2
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/11/10/','库存商品-配件-MB配件-配件','BM0000','BM','3','2','2020-08-12 18:00:00','1');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/12/30/','库存商品-配件-非MB件-轮胎','BM0000','BM','3','2','2020-08-12 18:00:00','2');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/12/10/','库存商品-配件-非MB件-油料','BM0000','BM','3','2','2020-08-12 18:00:00','3');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','1243/11/20/','库存商品-配件-MB配件-精品件','BM0000','BM','3','2','2020-08-12 18:00:00','4');
INSERT INTO bz_configureManage(branch_code,subject_code,subject_name,special_code,special_super_code,interface_info,interface_type,create_time,temp1)
VALUES('GS0036160','2121/02/10/','应付账款-系统用户-GRIR-配件','BM1001','BM','3','2','2020-08-12 18:00:00','5');



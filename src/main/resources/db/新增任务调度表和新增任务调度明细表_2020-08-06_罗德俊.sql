-- 任务调度表 taskschedulingmanage
CREATE TABLE taskschedulingmanage(
	id  INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '自增id',
	url VARCHAR(50) NOT NULL COMMENT '地址路径',
	remark VARCHAR(100) NOT NULL COMMENT '接口信息' ,
	temp1 VARCHAR(50) COMMENT '备注字段1',
	temp2 VARCHAR(50) COMMENT '备注字段2',
	temp3 VARCHAR(50) COMMENT '备注字段3'
);

-- 任务调度明细表 taskschedulingdetailsinfo
CREATE TABLE taskschedulingdetailsinfo(
	id INT(11) PRIMARY KEY AUTO_INCREMENT  COMMENT '自增id',
	url VARCHAR(100) NOT NULL  COMMENT '地址路径',
	start_time VARCHAR(30) NOT NULL COMMENT '开始日期',
	end_time VARCHAR(30) NOT NULL COMMENT '结束日期',
	flag VARCHAR(2) NOT NULL COMMENT '0 fail 1 success',
	batch VARCHAR(20) NOT NULL COMMENT '标识号',
	temp1 VARCHAR(50) COMMENT '备注字段1',
	temp2 VARCHAR(50) COMMENT '备注字段2',
	temp3 VARCHAR(50) COMMENT '备注字段3',
	start_time varchar(50) COMMENT '开始日期',
	end_time varchar(50) COMMENT '结束日期'
);
-- 维护10个接口地址信息，及接口名称。
INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/vehicle-stock-change','Vehicle_Stock');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/vehicle-invoice','Vehicle_Invoice');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/parts-stock-change','Parts_Stock_In_Checking');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/parts-verification','Parts_Verification');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/parts-promotion','Parts_Promotion');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/parts-requisition','Parts_Requisition');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/parts-invoice','Parts_Invoice');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/warranty-confirm','Warranty_Confirm');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/option-change','Option_Change');

INSERT INTO taskschedulingmanage (url,remark) VALUES('/api/accounting/service-invoice','Service_Invoice');


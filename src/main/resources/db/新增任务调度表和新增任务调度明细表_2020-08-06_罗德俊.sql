-- 任务调度表 TaskSchedulingManage
CREATE TABLE TaskSchedulingManage(
	id  INT(11) PRIMARY KEY AUTO_INCREMENT,
	url VARCHAR(50) NOT NULL,
	remark VARCHAR(100) NOT NULL,
	temp1 VARCHAR(50),
	temp2 VARCHAR(50),
	temp3 VARCHAR(50)
);

-- 任务调度明细表 TaskSchedulingDetailsInfo
CREATE TABLE TaskSchedulingDetailsInfo(
	id INT(11) PRIMARY KEY AUTO_INCREMENT,
	url VARCHAR(100) NOT NULL,
	start_time VARCHAR(30) NOT NULL,
	end_time VARCHAR(30) NOT NULL,
	flag VARCHAR(2) NOT NULL,
	batch VARCHAR(20) NOT NULL,
	temp1 VARCHAR(50),
	temp2 VARCHAR(50),
	temp3 VARCHAR(50)
);
-- 维护10个接口地址信息，及接口名称。
INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/vehicle-stock-change','Vehicle_Stock');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/vehicle-invoice','Vehicle_Invoice');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/parts-stock-change','Parts_Stock_In_Checking');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/parts-verification','Parts_Verification');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/parts-promotion','Parts_Promotion');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/parts-requisition','Parts_Requisition');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/parts-invoice','Parts_Invoice');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/warranty-confirm','Warranty_Confirm');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/option-change','Option_Change');

INSERT INTO TaskSchedulingManage (url,remark) VALUES('/api/accounting/service-invoice','Service_Invoice');


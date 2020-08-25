-- 任务调度明细表 taskschedulingdetailsinfo
CREATE TABLE taskschedulingdetailsinfo(
        id INT(11) PRIMARY KEY AUTO_INCREMENT  COMMENT '自增id',
        url VARCHAR(100) NOT NULL  COMMENT '地址路径',
        start_time VARCHAR(30) NOT NULL COMMENT '开始日期',
        end_time VARCHAR(30) NOT NULL COMMENT '结束日期',
        flag VARCHAR(2) NOT NULL COMMENT '0 fail 1 success',
        batch VARCHAR(50) NOT NULL COMMENT '接口标识号'
);

insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('1','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/vehicle-stock-change','1598345400007','1596001003220','1','Vehicle_Stock',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('2','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/vehicle-invoice','1596001003220','1596001003220','1','Vehicle_Invoice',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('3','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-stock-change','1596001003220','1596001003220','1','Parts_Stock_In_Checking',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('4','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-verification','1596001003220','1596001003220','1','Parts_Verification',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('5','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-promotion','1596001003220','1596001003220','1','Parts_Promotion',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('6','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-requisition','1596001003220','1596001003220','1','Parts_Requisition',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('7','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-invoice','1596001003220','1596001003220','1','Parts_Invoice',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('8','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/warranty-confirm','1596001003220','1596001003220','1','Warranty_Confirm',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('9','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/option-change','1596001003220','1596001003220','1','Option_Change',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('10','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/service-invoice','1596001003220','1596001003220','1','Service_Invoice',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('11','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/vehicle-stock-change','1598347020008','1596001003220','1','Vehicle_Stock',NULL,NULL,NULL);
insert into `taskschedulingdetailsinfo` (`id`, `url`, `start_time`, `end_time`, `flag`, `batch`, `temp1`, `temp2`, `temp3`) values('12','https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/vehicle-stock-change','1596001003220','1598348160007','1','Vehicle_Stock',NULL,NULL,NULL);

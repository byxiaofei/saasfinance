package com.sinosoft.util;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.VoucherService;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: luodejun
 * @Date: 2020/5/11 20:24
 * @Description:
 */
public class Dom4jUtil {


    @Resource
    private VoucherService voucherService;

    private static final String CHARSET = "UTF-8";

    private static void  testParserByDom4j(MultipartFile transFile ,String xml){
        //  1. 传入为我们定义好的文件里面的数据类型。
        VoucherDTO dto=new VoucherDTO();
        String errorMsg = "";
        //  创建SAXReader  阅读器对象
        SAXReader reader = new SAXReader();
        try {
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes(CHARSET));
            //  获取文件名称并变成唯一标识的文件名称。
//            String originalFilename = transFile.getOriginalFilename();
//            String replace = UUID.randomUUID().toString().replace("-", "");
//            String fileName = originalFilename+replace;
//            File file = new File(fileName);
//            FileUtils.copyInputStreamToFile(transFile.getInputStream(),file);
            // 命令阅读器从输入流中读取文档对象
//            Document document = reader.read(file);
            Document document = reader.read(inputStream);
            // 获得文档对象的根节点
            Element rootElement = document.getRootElement();
            // 获取凭证头节点
            Element voucherHeader = rootElement.element("voucher_header");
            // 获取凭证头公司节点
            String company = voucherHeader.element("company").getText();
            if("".equals(company) || company == null){
                // return
                errorMsg = errorMsg+"公司标签不能为空"+",";
            }
            //  操作人代码
            String createBy = voucherHeader.element("createBy").getText();
            if("".equals(createBy) || createBy == null){
                // 新增方法进行错误信息的插入表
                errorMsg = errorMsg+"操作人信息不能为空"+",";
            }
            //  凭证类型
            String voucherType = voucherHeader.elementText("voucherType");
            if("".equals(voucherType) || voucherType == null){
                errorMsg = errorMsg+"凭证类型不能为空"+",";
            }
            String voucharDate = voucherHeader.elementText("voucharDate");
            if("".equals(voucharDate) || voucharDate == null){
                errorMsg = errorMsg+"制单日期不能为空"+",";
            }
            //  会计期间
            String yearMonth = voucherHeader.elementText("yearMonth");
            if("".equals(yearMonth) || yearMonth == null){
                errorMsg = errorMsg+"会计期间不能为空"+",";
            }
            //  附件
            String auxNumber = voucherHeader.elementText("auxNumber");
            if("".equals(auxNumber) || auxNumber == null){
                errorMsg = errorMsg+"附件不能为空"+",";
            }
            //  凭证提体信息条数
            String entryNumber = voucherHeader.elementText("entryNumber");
            //  财套类型
            String accbookType = voucherHeader.elementText("accbookType");
            if("".equals(accbookType) || accbookType == null){
                errorMsg = errorMsg+"账套类型不能为空"+",";
            }
            //财套编码
            String accbookCode = voucherHeader.elementText("accbookCode");
            if("".equals(accbookCode) || accbookCode == null){
                errorMsg = errorMsg+"账套编码不能为空"+",";
            }
            //  业务机构
            String branchCode = voucherHeader.elementText("branchCode");
            if("".equals(branchCode) || branchCode == null){
                errorMsg = errorMsg+"业务机构不能为空"+",";
            }
            //  核算中心
            String centerCode = voucherHeader.elementText("centerCode");
            if("".equals(centerCode) || centerCode == null){
                errorMsg = errorMsg+"核算中心不能为空"+",";
            }
            //  文件名称
            String fileName = voucherHeader.elementText("fileName");
            //  凭证号的自己查询出来
//            VoucherDTO voucher = voucherService.setVoucher(yearMonth, centerCode, branchCode, accbookCode, accbookType);
//            dto.setVoucherNo(voucher.getVoucherNo());

            dto.setYearMonth(yearMonth);
            dto.setVoucherDate(voucharDate);
            dto.setAuxNumber(auxNumber);
            dto.setCreateBy(createBy);
            dto.setAccBookCode(accbookCode);
            dto.setAccBookType(accbookType);
            dto.setBranchCode(branchCode);
            dto.setCenterCode(centerCode);
            //  不存在使用过凭证号
//            dto.setOldVoucherNo(null);

            List<VoucherDTO> list2 = new ArrayList<>();
            List<VoucherDTO> list3 = new ArrayList<>();

            //  获取凭证body节点清单
            List<Element> entrys = rootElement.element("voucher_body").elements();
            //  首次判断看凭证条数是不是大于二
            if(entrys.size()<=2){
                //  不能小于两条。
            }
            for(int i = 0; i<entrys.size();i++){
//                BigDecimal deditNumber = new BigDecimal("0.00");
//                BigDecimal creditNumber = new BigDecimal("0.00");
                VoucherDTO voucherDTO = new VoucherDTO();
                VoucherDTO voucherDTO1 = new VoucherDTO();
                // 循环遍历每一个对象
                Element entry = entrys.get(i);
                //  摘要
                String remarkName = entry.elementText("remarkName");
                if("".equals(remarkName) || remarkName == null){
                    errorMsg = errorMsg+"第"+(i+1)+"条xml中，摘要不能为空"+",";
                }
                //  科目代码 需要进行数据库进行校验。
                String subjectCode = entry.elementText("subjectCode");
                //  科目名称
                String subjectName = entry.elementText("subjectName");
                //  借方金额
                String debit = entry.elementText("debit");
                BigDecimal borrowMoney = new BigDecimal(debit);
                //  贷方金额
                String credit = entry.elementText("credit");
                BigDecimal creditMoney = new BigDecimal(credit);

                //  传入的明细专项
                String specialCodes = entry.elementText("specialCodes");
                //  专项一级编码，
                String specialSuperCodes = entry.elementText("specialSuperCodes");
                //  部门
                String s01 = entry.elementText("S01");
                //  预算科目
                String s02 = entry.elementText("S02");
                //  产品
                String s03 = entry.elementText("S03");
                //  渠道
                String s04 = entry.elementText("S04");
                //  内部往来
                String s05 = entry.elementText("S05");
                //  现金流量
                String s06 = entry.elementText("S06");
                //  资金调拨
                String s07 = entry.elementText("S07");
                //往来对象
                String s08 = entry.elementText("S08");
                //业务条线
                String s09 = entry.elementText("S09");



                voucherDTO.setRemarkName(remarkName);
                voucherDTO.setSubjectCode(subjectCode);
                voucherDTO.setSubjectName(subjectName);
                voucherDTO.setDebit(debit);
                voucherDTO.setCredit(credit);

                voucherDTO1.setSubjectCodeS(subjectCode);
                voucherDTO1.setSubjectNameS(subjectName);
                voucherDTO1.setSpecialCodeS(specialCodes);
                voucherDTO1.setSpecialSuperCodeS(specialSuperCodes);
                list2.add(voucherDTO);
                list3.add(voucherDTO1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void  testParserByDom4j(String xml){
        VoucherDTO dto=new VoucherDTO();
        //  创建SAXReader  阅读器对象
        SAXReader reader = new SAXReader();
        try {
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes(CHARSET));
//            File file = new File(transFile.getOriginalFilename());
            // 命令阅读器从输入流中读取文档对象
//            FileUtils.copyInputStreamToFile(transFile.getInputStream(),file);
            Document document = reader.read(inputStream);
            // 获得文档对象的根节点
            Element rootElement = document.getRootElement();
            // 获取凭证头节点
            Element voucherHeader = rootElement.element("voucher_header");
            // 获取凭证头公司节点
            String company = voucherHeader.element("company").getText();
            //  操作人代码
            String operCode = voucherHeader.element("opercode").getText();
            //  凭证类型
            String voucherId = voucherHeader.elementText("voucher_id");
            //  下载日期
            String loadDate = voucherHeader.elementText("loaddate");
            //  0
            String attachment = voucherHeader.elementText("attachment");
            //  凭证提体信息条数
            String entryNumber = voucherHeader.elementText("entry_number");


            //  凭证号
            dto.setVoucherNo(null);
            //
            dto.setYearMonth(null);
            dto.setVoucherDate(null);
            dto.setAuxNumber(null);
            dto.setCreateBy(null);
            dto.setOldVoucherNo(null);

            List<VoucherDTO> list2 = new ArrayList<>();
            List<VoucherDTO> list3 = new ArrayList<>();

            //  获取凭证body节点清单
            List<Element> voucherBody = rootElement.element("voucher_body").elements();
            //  首次判断看凭证条数是不是大于二
            for(int i = 0; i<voucherBody.size();i++){
                // 循环遍历每一个对象
                Element entry = voucherBody.get(i);
                //  凭证体分录ID
                String entryId = entry.elementText("entry_id");
                //  财套类型
                String accbooktype = entry.elementText("accbooktype");
                //财套编码
                String accbookcode = entry.elementText("accbookcode");
                //  业务机构
                String branchcode = entry.elementText("branchcode");
                //  成本中心
                String costcenter = entry.elementText("costcenter");
                //  业务行为代码明细项
                String business_code = entry.elementText("business_code");
                //  单证号
                String receiptno = entry.elementText("receiptno");
                //  业务数据入账日
                String validate = entry.elementText("validate");
                //  业务日期（操作日期）
                String bussdate = entry.elementText("bussdate");
                //  货币类型（RMB）
                String currency = entry.elementText("currency");
                //  汇率类型，Corporate
                String currencyType = entry.elementText("currency_type");
                //  汇率，人民币为1
                String currencyRate = entry.elementText("currency_rate");
                //  本位币借方金额
                String primaryDebitAmount = entry.elementText("primary_debit_amount");
                //  原币借方金额
                String naturalDebitAmount = entry.elementText("natural_debit_amount");
                //  本位币贷方金额
                String primaryCreditAmount = entry.elementText("primary_credit_amount");
                //  原币贷方金额
                String naturalCreditAmount = entry.elementText("natural_credit_amount");
                //  科目明细代码
                String accountCode = entry.elementText("account_code");
                //  成本中心
                String s01 = entry.elementText("S01");
                //  预算科目
                String s02 = entry.elementText("S02");
                //  产品
                String s03 = entry.elementText("S03");
                //  渠道
                String s04 = entry.elementText("S04");
                //  内部往来
                String s05 = entry.elementText("S05");
                //  现金流量
                String s06 = entry.elementText("S06");
                //  资金调拨
                String s07 = entry.elementText("S07");
                //往来对象
                String s08 = entry.elementText("S08");
                //业务条线
                String s09 = entry.elementText("S09");
                //  摘要
                String temp = entry.elementText("temp");
                //  凭证类型
                String anAbstract = entry.elementText("abstract");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        String reportXml ="<?xml version=\"1.0\" encoding=\"utf-8\"?> \n" +
                "<sfinterface xsi:schemaLocation=\"http://www.sinosoft.com/bbef/intf/sfinterface.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.sinosoft.com/bbef/intf/sfinterface.xsd\" xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\" subtype=\"run\" sender=\"1101\" roottag=\"voucher\" replace=\"Y\" receiver=\"1003\" proc=\"add\" operation=\"req\" isexchange=\"Y\" filename=\"pz.xml\" billtype=\"gl\"> \n"+
                "<voucher loadbatch = \"20999999\"> \n" +
                "<voucher_header>  \n" +
                    "<company>1001000000</company>  \n" +
                    "<opercode>admin</opercode>  \n" +
                    "<voucher_id>LNAE_XM</voucher_id>  \n" +
                    "<loaddate>2020-02-25</loaddate>  \n" +
                    "<attachment/>  \n" +
                    "<entry_number>3</entry_number>  \n" +
                "</voucher_header>  \n" +
                "<voucher_body>  \n" +
                    "<entry>  \n" +
                        "<entry_id>101</entry_id> \n" +
                        "<accbooktype>01</accbooktype> \n" +
                        "<accbookcode>11</accbookcode> \n" +
                        "<branchcode>1005000000</branchcode>  \n" +
                        "<branchcode>1005000000</branchcode>  \n" +
                        "<costcenter>1005000000</costcenter>  \n" +
                        "<business_code>LOAN</business_code>  \n" +
                        "<receiptno>101</receiptno>  \n" +
                        "<key_no1/>  \n" +
                        "<key_no2/>  \n" +
                        "<key_no3/>  \n" +
                        "<key_no4/>  \n" +
                        "<key_no5/>  \n" +
                        "<validate>2020-02-24</validate>  \n" +
                        "<bussdate>2020-02-24</bussdate>  \n" +
                        "<revpaydate/>  \n" +
                        "<currency>RMB</currency>  \n" +
                        "<currency_type>Corporate</currency_type>  \n" +
                        "<currency_rate>1</currency_rate>  \n" +
                        "<primary_debit_amount>240199.83</primary_debit_amount>  \n" +
                        "<natural_debit_amount>240199.83</natural_debit_amount>  \n" +
                        "<primary_credit_amount>0</primary_credit_amount>  \n" +
                        "<natural_credit_amount>0</natural_credit_amount>  \n" +
                        "<account_code>1061/01/2222/</account_code>  \n" +
                        "<S01>null</S01>  \n" +
                        "<S02/>  \n" +
                        "<S03>null</S03>  \n" +
                        "<S04>1005000000</S04>  \n" +
                        "<S05>1005000000</S05>  \n" +
                        "<S06>null</S06>  \n" +
                        "<S07/>  \n" +
                        "<S08>null</S08>  \n" +
                        "<S09/>  \n" +
                        "<S10>null</S10>  \n" +
                        "<S11/>  \n" +
                        "<S12/>  \n" +
                        "<S13/>  \n" +
                        "<S14/>  \n" +
                        "<S15/>  \n" +
                        "<S16/>  \n" +
                        "<S17/>  \n" +
                        "<S18/>  \n" +
                        "<S19/>  \n" +
                        "<S20/>  \n" +
                        "<rawunit/>  \n" +
                        "<rawoper/>  \n" +
                        "<temp/>  \n" +
                        "<abstract>LNAE_XM</abstract>  \n" +
                        "<account_period/>  \n" +
                    "</entry>  \n" +
                "</voucher_body>  \n" +
                " </voucher> \n" +
                "</sfinterface>";

        testParserByDom4j(reportXml);
    }

    private  static  String errorMsgXML(String errorMsg){
        String xmlMsg = "<?xml version=\"1.0\" encoding=\"utf-8\"?> \n" +
                "<sfinterface xsi:schemaLocation=\"http://www.sinosoft.com/bbef/intf/sfinterface.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.sinosoft.com/bbef/intf/sfinterface.xsd\" xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\" subtype=\"run\" sender=\"1101\" roottag=\"voucher\" replace=\"Y\" receiver=\"1003\" proc=\"add\" operation=\"req\" isexchange=\"Y\" filename=\"pz.xml\" billtype=\"gl\"> \n"+
                "<returnMessage> \n" +
                "<errorMessage> \n" + errorMsg +
                "</errorMessage> \n" +
                "</returnMessage> \n" +
                "</sfinterface>";
        return xmlMsg;
    }
}

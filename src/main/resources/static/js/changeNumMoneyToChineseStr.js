var chinese_str_cnNums = new Array("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖");
var chinese_str_cnIntRadice = new Array("", "拾", "佰", "仟");
/*var chinese_str_cnIntUnits = new Array("", "万", "亿", "兆");*/
var chinese_str_cnIntUnits = new Array("", "万", "亿");
var chinese_str_cnDecUnits = new Array("角", "分", "毫", "厘");
var chinese_str_cnInteger = "整";
var chinese_str_cnIntLast = "元";
/**
 * 将金额数据转换为大写汉字形式
 * @param currency 币种（汉字）
 * @param money 金额，当前最大允许值为：999999999999.9999
 * @returns {*}
 */
function changeNumMoneyToChineseStr(currency, money){
    /*var maxNum = 999999999999999.9999;*/
    var maxNum = 999999999999.9999;
    var IntegerNum;
    var DecimalNum;
    var ChineseStr = "";
    var parts;
    var flag = true;
    if (money == ""){
        return "";
    }
    money = parseFloat(money);
    if (money<0) {
        console.log('金额不可为负数');
        return "";
    }
    if (money >= maxNum) {
        console.log('超出最大处理数字');
        return "";
    }
    if (money == 0) {
        ChineseStr = chinese_str_cnNums[0] + chinese_str_cnIntLast + chinese_str_cnInteger;
        return ChineseStr;
    }
    money = money.toString();
    if (money.indexOf(".") == -1) {
        IntegerNum = money;
        DecimalNum = '';
    } else {
        parts = money.split(".");
        IntegerNum = parts[0];
        DecimalNum = parts[1].substr(0, 4);
    }
    if (parseInt(IntegerNum, 10) > 0) {
        var zeroCount = 0;
        var IntLen = IntegerNum.length;
        for (var index = 0; index < IntLen; index++) {
            var n = IntegerNum.substr(index, 1);
            var p = IntLen - index - 1;
            var q = p / 4;
            var m = p % 4;
            if (n == "0") {
                zeroCount++;
            } else {
                if (zeroCount > 0) {
                    ChineseStr += chinese_str_cnNums[0];
                }
                //归零
                zeroCount = 0;
                ChineseStr += chinese_str_cnNums[parseInt(n)] + chinese_str_cnIntRadice[m];
            }
            if (m == 0 && zeroCount < 4) {
                ChineseStr += chinese_str_cnIntUnits[q];
            }
        }
        ChineseStr += chinese_str_cnIntLast;
    }
    if (DecimalNum != '') {
        var decLen = DecimalNum.length;
        for (var index = 0; index < decLen; index++) {
            var n = DecimalNum.substr(index, 1);
            if (n != '0') {
                ChineseStr += chinese_str_cnNums[Number(n)] + chinese_str_cnDecUnits[index];
            }
        }
    }
    if (ChineseStr == '') {
        ChineseStr += chinese_str_cnNums[0] + chinese_str_cnIntLast + chinese_str_cnInteger;
    } else if (DecimalNum == '') {
        ChineseStr += chinese_str_cnInteger;
    } else if (DecimalNum.length<2) {
        ChineseStr += chinese_str_cnInteger;
    }
    return currency+ChineseStr;
}
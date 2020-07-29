/**
 * number: 为需要格式化的数值
 * cent：为number要保留的小数位
 * isThousand：为是否需要千分位 0:不需要,1:需要(数值类型);
 * 方法返回结果为格式的字符串,如'1,234,567.00'
 */
function formatNumber(number, cent, isThousand) {
    number = number.toString().replace(/\$|\,/g, '');
    // 1、检查传入数值为数值类型
    if (isNaN(number))
        number = '0';
    sign = (number == (number = Math.abs(number)));//Math.abs(number)取出number的绝对值

    // 3、把指定的小数位先转换成整数.多余的小数位四舍五入，Math.pow(10, cent)为10的cent次方
    number = Math.floor(number * Math.pow(10, cent) + 0.5);
    // 4、求出小数位数值
    cents = number % Math.pow(10, cent);
    // 5、求出整数位数值
    number = Math.floor(number / Math.pow(10, cent)).toString();
    // 6、把小数位转换成字符串,以便求小数位长度
    cents = cents.toString();

    // 7、补足小数位到指定的位数
    while (cents.length < cent)
        cents = "0" + cents;
    // 8、对整数部分进行千分位格式化.
    if (isThousand) {
        for (var i = 0; i < Math.floor((number.length - (1 + i)) / 3) ; i++)
            number = number.substring(0, number.length - (4 * i + 3)) + ',' + number.substring(number.length - (4 * i + 3));
    }
    //9、返回格式化转换后的结果
    if (cent > 0)
        return (((sign) ? '' : '-')  + number + '.' + cents);
    else
        return (((sign) ? '' : '-')  + number);
}

function removeThousand(thousandString) {
    if(thousandString.trim()==""){
        return '';
    }
    return thousandString.trim().replace(/,/gi,'');
}

/**
 * 加法运算，避免数据相加小数点后产生多位数和计算精度损失
 * @param num1 num1加数1
 * @param num2 num2加数2
 * @returns {number}
 */
function decimalAdd(num1, num2) {
    var baseNum, baseNum1, baseNum2;
    try {
        baseNum1 = num1.toString().split(".")[1].length;
    } catch (e) {
        baseNum1 = 0;
    }
    try {
        baseNum2 = num2.toString().split(".")[1].length;
    } catch (e) {
        baseNum2 = 0;
    }
    baseNum = Math.pow(10, Math.max(baseNum1, baseNum2));
    return (num1 * baseNum + num2 * baseNum) / baseNum;
};

/**
 * 减法运算，避免数据相减小数点后产生多位数和计算精度损失
 * @param num1 num1被减数
 * @param num2 num2减数
 * @returns {string}
 */
function decimalSub(num1, num2) {
    var baseNum, baseNum1, baseNum2;
    var precision;// 精度
    try {
        baseNum1 = num1.toString().split(".")[1].length;
    } catch (e) {
        baseNum1 = 0;
    }
    try {
        baseNum2 = num2.toString().split(".")[1].length;
    } catch (e) {
        baseNum2 = 0;
    }
    baseNum = Math.pow(10, Math.max(baseNum1, baseNum2));
    precision = (baseNum1 >= baseNum2) ? baseNum1 : baseNum2;
    return ((num1 * baseNum - num2 * baseNum) / baseNum).toFixed(precision);
};

/**
 * 乘法运算，避免数据相乘小数点后产生多位数和计算精度损失
 * @param num1 num1被乘数
 * @param num2 num2乘数
 * @returns {number}
 */
function decimalMulti(num1, num2) {
    var baseNum = 0;
    try {
        baseNum += num1.toString().split(".")[1].length;
    } catch (e) {
    }
    try {
        baseNum += num2.toString().split(".")[1].length;
    } catch (e) {
    }
    return Number(num1.toString().replace(".", "")) * Number(num2.toString().replace(".", "")) / Math.pow(10, baseNum);
};

/**
 * 除法运算，避免数据相除小数点后产生多位数和计算精度损失
 * @param num1 被除数
 * @param num2 除数
 * @returns {number}
 */
function decimalDiv(num1, num2) {
    var baseNum1 = 0, baseNum2 = 0;
    var baseNum3, baseNum4;
    try {
        baseNum1 = num1.toString().split(".")[1].length;
    } catch (e) {
        baseNum1 = 0;
    }
    try {
        baseNum2 = num2.toString().split(".")[1].length;
    } catch (e) {
        baseNum2 = 0;
    }
    with (Math) {
        baseNum3 = Number(num1.toString().replace(".", ""));
        baseNum4 = Number(num2.toString().replace(".", ""));
        return (baseNum3 / baseNum4) * pow(10, baseNum2 - baseNum1);
    }
}
var timer_1 = null;var timer_2 = null;var timer_3 = null;var timer_4 = null;var timer_5 = null;var timer_6 = null;var timer_7 = null;var timer_8 = null;var timer_9 = null;var timer_10 = null;var timer_11 = null;var timer_12 = null;var timer_13 = null;var timer_14 = null;var timer_15 = null;//定时器的ID值
var timer_target_obj_1 = null;var timer_target_obj_2 = null;var timer_target_obj_3 = null;var timer_target_obj_4 = null;var timer_target_obj_5 = null;var timer_target_obj_6 = null;var timer_target_obj_7 = null;var timer_target_obj_8 = null;var timer_target_obj_9 = null;var timer_target_obj_10 = null;var timer_target_obj_11 = null;var timer_target_obj_12 = null;var timer_target_obj_13 = null;var timer_target_obj_14 = null;var timer_target_obj_15 = null;//局部刷新对象

//定时器定时时间到时要执行的方法
function timer_refresh_count_1(index){
    if (timer_target_obj_1) {
        var ids = timer_target_obj_1.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_1) {
            timer_target_obj_1 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_1);
            timer_1 = null;
        }
    }
}

function timer_refresh_count_2(index){
    if (timer_target_obj_2) {
        var ids = timer_target_obj_2.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_2) {
            timer_target_obj_2 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_2);
            timer_2 = null;
        }
    }
}

function timer_refresh_count_3(index){
    if (timer_target_obj_3) {
        var ids = timer_target_obj_3.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_3) {
            timer_target_obj_3 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_3);
            timer_3 = null;
        }
    }
}

function timer_refresh_count_4(index){
    if (timer_target_obj_4) {
        var ids = timer_target_obj_4.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_4) {
            timer_target_obj_4 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_4);
            timer_4 = null;
        }
    }
}

function timer_refresh_count_5(index){
    if (timer_target_obj_5) {
        var ids = timer_target_obj_5.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_5) {
            timer_target_obj_5 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_5);
            timer_5 = null;
        }
    }
}

function timer_refresh_count_6(index){
    if (timer_target_obj_6) {
        var ids = timer_target_obj_6.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_6) {
            timer_target_obj_6 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_6);
            timer_6 = null;
        }
    }
}

function timer_refresh_count_7(index){
    if (timer_target_obj_7) {
        var ids = timer_target_obj_7.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_7) {
            timer_target_obj_7 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_7);
            timer_7 = null;
        }
    }
}

function timer_refresh_count_8(index){
    if (timer_target_obj_8) {
        var ids = timer_target_obj_8.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_8) {
            timer_target_obj_8 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_8);
            timer_8 = null;
        }
    }
}

function timer_refresh_count_9(index){
    if (timer_target_obj_9) {
        var ids = timer_target_obj_9.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_9) {
            timer_target_obj_9 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_9);
            timer_9 = null;
        }
    }
}

function timer_refresh_count_10(index){
    if (timer_target_obj_10) {
        var ids = timer_target_obj_10.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_10) {
            timer_target_obj_10 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_10);
            timer_10 = null;
        }
    }
}

function timer_refresh_count_11(index){
    if (timer_target_obj_11) {
        var ids = timer_target_obj_11.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_11) {
            timer_target_obj_11 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_11);
            timer_11 = null;
        }
    }
}

function timer_refresh_count_12(index){
    if (timer_target_obj_12) {
        var ids = timer_target_obj_12.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_12) {
            timer_target_obj_12 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_12);
            timer_12 = null;
        }
    }
}

function timer_refresh_count_13(index){
    if (timer_target_obj_13) {
        var ids = timer_target_obj_13.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_13) {
            timer_target_obj_13 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_13);
            timer_13 = null;
        }
    }
}

function timer_refresh_count_14(index){
    if (timer_target_obj_14) {
        var ids = timer_target_obj_14.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_14) {
            timer_target_obj_14 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_14);
            timer_14 = null;
        }
    }
}

function timer_refresh_count_15(index){
    if (timer_target_obj_15) {
        var ids = timer_target_obj_15.split(",");
        for (var i=0;i<ids.length;i++) {
            $.parser.parse($('#'+ids[i]).parent());
        }
        var rows = $('#dgS').datagrid('getRows');
        var row = null;
        if (rows.length>0){row = rows[index];}
        //结算日期默认凭证录入制单日期，结算日期可修改，并不得晚于凭证制单日期
        $("#bankDeposit_date_"+index).datebox({
            onChange:function (newValue, oldValue) {
                var vDate = $("#voucherDate").datebox("getValue");
                if (vDate && (vDate<newValue)) {
                    $.messager.alert('提示','结算日期不得晚于凭证制单日期','warning');
                    $('#bankDeposit_date_'+index).datebox('setValue', vDate);
                } else {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_type_"+index).combobox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        $("#bankDeposit_no_"+index).textbox({
            onChange:function (newValue, oldValue) {
                if (newValue!=oldValue) {
                    dgSUpdateRow($(this)[0].id, newValue);
                }
            }
        });
        if (row) {
            if (row.settlementType) {
                $('#bankDeposit_type_'+index).combobox('setValue', row.settlementType);
            }
            if (row.settlementNo) {
                $('#bankDeposit_no_'+index).textbox('setValue', row.settlementNo);
            }
            if (row.settlementDate) {
                $('#bankDeposit_date_'+index).datebox('setValue', row.settlementDate);
            } else {
                $('#bankDeposit_date_'+index).datebox('setValue', $("#voucherDate").datebox("getValue"));
            }
        }
        if (timer_15) {
            timer_target_obj_15 = null;
            //取消由setInterval()方法设置的定时器
            window.clearInterval(timer_15);
            timer_15 = null;
        }
    }
}

function timer_bankDepositIds_index(bankDepositIds, index, time) {
    if (!timer_1) {
        timer_target_obj_1 = bankDepositIds;
        //循环执行，每隔 time 毫秒执行一次
        timer_1=window.setInterval('timer_refresh_count_1("'+index+'")', time);
    } else if (!timer_2) {
        timer_target_obj_2 = bankDepositIds;
        timer_2=window.setInterval('timer_refresh_count_2("'+index+'")', time);
    } else if (!timer_3) {
        timer_target_obj_3 = bankDepositIds;
        timer_3=window.setInterval('timer_refresh_count_3("'+index+'")', time);
    } else if (!timer_4) {
        timer_target_obj_4 = bankDepositIds;
        timer_4=window.setInterval('timer_refresh_count_4("'+index+'")', time);
    } else if (!timer_5) {
        timer_target_obj_5 = bankDepositIds;
        timer_5=window.setInterval('timer_refresh_count_5("'+index+'")', time);
    } else if (!timer_6) {
        timer_target_obj_6 = bankDepositIds;
        timer_6=window.setInterval('timer_refresh_count_6("'+index+'")', time);
    } else if (!timer_7) {
        timer_target_obj_7 = bankDepositIds;
        timer_7=window.setInterval('timer_refresh_count_7("'+index+'")', time);
    } else if (!timer_8) {
        timer_target_obj_8 = bankDepositIds;
        timer_8=window.setInterval('timer_refresh_count_8("'+index+'")', time);
    } else if (!timer_9) {
        timer_target_obj_9 = bankDepositIds;
        timer_9=window.setInterval('timer_refresh_count_9("'+index+'")', time);
    } else if (!timer_10) {
        timer_target_obj_10 = bankDepositIds;
        timer_10=window.setInterval('timer_refresh_count_10("'+index+'")', time);
    } else if (!timer_11) {
        timer_target_obj_11 = bankDepositIds;
        timer_11=window.setInterval('timer_refresh_count_11("'+index+'")', time);
    } else if (!timer_12) {
        timer_target_obj_12 = bankDepositIds;
        timer_12=window.setInterval('timer_refresh_count_12("'+index+'")', time);
    } else if (!timer_13) {
        timer_target_obj_13 = bankDepositIds;
        timer_13=window.setInterval('timer_refresh_count_13("'+index+'")', time);
    } else if (!timer_14) {
        timer_target_obj_14 = bankDepositIds;
        timer_14=window.setInterval('timer_refresh_count_14("'+index+'")', time);
    } else if (!timer_15) {
        timer_target_obj_15 = bankDepositIds;
        timer_15=window.setInterval('timer_refresh_count_15("'+index+'")', time);
    }
}

function dgSUpdateRow(id, value) {
    if (id) {
        //bankDeposit_type_1，bankDeposit_no_1，bankDeposit_date_1
        var ids = id.split("_");
        if (ids.length==3) {
            var prefix = ids[0];
            var type = ids[1];
            var index = parseInt((id.split("_"))[2]);
            if (type == 'type') {
                window.setTimeout(function () {
                    $('#dgS').datagrid('updateRow',{
                        index: index,
                        row: {
                            settlementType: value
                        }
                    });
                },20);
            } else if (type == 'no') {
                window.setTimeout(function () {
                    $('#dgS').datagrid('updateRow',{
                        index: index,
                        row: {
                            settlementNo: value
                        }
                    });
                },20);
            } else if (type == 'date') {
                window.setTimeout(function () {
                    $('#dgS').datagrid('updateRow',{
                        index: index,
                        row: {
                            settlementDate: value
                        }
                    });
                },20);
            }
        }
    }
}
var uflag=false;
var dgheight="";
$(document).ready(function(){
    //设置主键冲突提醒方式
    $('.primary-tooltip').tooltip({
        position: 'right',
        content: '<span style="color:#fff">主键冲突请重写选择</span>',
        onShow: function(){
            $(this).tooltip('tip').css({
                backgroundColor: '#666',
                borderColor: '#666'
            });
        }
    });
    //设置搜索结果为空的情况下展示页面
    $('#dg').datagrid({
        //加载成功
        onLoadSuccess: function(data){
            if(dgheight==""){
                dgheight=$('#dg').datagrid('options').height;
            }
            if(data.total<1){
                uflag=!uflag;
                if(uflag){
                    $('#dg').datagrid({
                        view:cardview
                    });
                    $('#dg').datagrid('resize',{
                        height:'400px'
                    })
                }
            }else{
                $('#dg').datagrid('resize',{
                    height:dgheight
                });
                return;
            }
        }
    });
})
var cardview = $.extend({}, $.fn.datagrid.defaults.view, {
    onAfterRender : function(target) {
        //	$.fn.datagrid.defaults.view.onAfterRender.call(this, target);
        var opts = $(target).datagrid('options');
        var vc = $(target).datagrid('getPanel').children(
            'div.datagrid-view');
        vc.children('div.datagrid-empty').remove();
        console.log(!$(target).datagrid('getRows').length);
        if (!$(target).datagrid('getRows').length) {
            var d = $('<div class="datagrid-empty"></div>').html(
                '没有符合条件的查询结果'|| 'NO records').appendTo(vc);
            d.css({
                position : 'absolute',
                left : 0,
                top : 100,
                width : '100%',
                textAlign : 'center'
            });
        }
    }
});
var jsutil = {};
//内核
jsutil.primaryValidata=function(url,params){
    var flag=true;
    var param={};
    //参数整理
    if(params != undefined){
        for(it in params){
            var prop=params[it];
            var propValue=$("#"+prop).textbox('getText');
            if(propValue=="")return;
            param[it]=propValue;
        }
        $.ajax({
            url:url,
            type:'post',
            async:false,
            data:param,
            success:function(result){
                if(result.data!=null){
                    $('.primary-tooltip').tooltip('show');
                    for(it in params){
                        var prop=params[it];
                        $('#'+prop).textbox('textbox').focus(
                            function(){
                                $('.primary-tooltip').tooltip('hide');
                            }
                        );
                    }
                    flag=false;
                }else{
                    flag=true;
                }
            }
        });
    }
    return flag;
}
jsutil.core = {
    /**
     * 调用服务器端的应用方法
     * @param act 方法名称
     * @param param 参数
     * @param callback 回调函数，有回调函数为异步调用
     * @param callbackParam 回调函数参数
     */
    invoke:function(act, param, callback, callbackParam) {
        var res = null;
        jsutil.msg.layer.mask();
        $.ajax({
            type:"POST",
            async:(callback != undefined), //有回调函数，使用异步方式
            dataType:"text",
            url:act,
            data:param,
            success:function(rtn) {
                jsutil.msg.layer.unmask();
                //res=rtn;
                eval("res = " + rtn);
                if (res.Exception != undefined) {
                    jsutil.msg.err(res.Exception, res.Cause);
                } else if (callback != undefined) {
                    callback.call(this, res, callbackParam);
                }
            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
                jsutil.msg.layer.unmask();
                //错误
                if (XMLHttpRequest.status === 0) {
                    jsutil.msg.err('服务器连接错误');
                } else if (XMLHttpRequest.status == 404) {
                    jsutil.msg.err('无效的访问地址. [404]');
                } else if (XMLHttpRequest.status == 500) {
                    jsutil.msg.err('服务器内部错误. [500]');
                } else if (errorThrown === 'parsererror') {
                    jsutil.msg.err('JSON解析错误.');
                } else if (errorThrown === 'timeout') {
                    jsutil.msg.err('访问超时.');
                } else if (errorThrown === 'abort') {
                    jsutil.msg.err('Ajax请求中断.');
                } else {
                    jsutil.msg.err('错误：' + errorThrown + '<hr>' + XMLHttpRequest.responseText);
                }
            }
        });
        return res;
    },
    exportXls:function(gridId, xlsName) {
        var grid = $("#" + gridId);
        var datas = grid.datagrid("getRows");
        if (datas.length > 0) {
            var cols = grid.datagrid("getColumnFields");
            if (cols.length > 0 && cols[0] == "ck") {
                cols.shift();
            }
            var param = {name: xlsName, cols: [], datas: []};
            var col;
            var colOps = [];
            for (var i = 0; i < cols.length; i++) {
                col = grid.datagrid("getColumnOption", cols[i]);
                param.cols[i] = {field: col.field, title: col.title, width: col.width};
                colOps.push(col);
            }
            for (var i = 0; i < datas.length; i++) {
                param.datas[i] = {};
                for (var j = 0; j < cols.length; j++) {
                    if (colOps[j].formatter) {
                        param.datas[i][cols[j]] = colOps[j].formatter.call(this, datas[i][cols[j]], datas[i]);
                        if ((param.datas[i][cols[j]] + "").indexOf('<a ') == 0) {
                            param.datas[i][cols[j]] = datas[i][cols[j]];
                        }
                    } else {
                        param.datas[i][cols[j]] = datas[i][cols[j]];
                    }
                }
            }
            return param;
        } else {
            jsutil.msg.alert("请选择数据");
        }
    },
    //导出excel
    download:function(act, param, target) {
        var formId = "__downloadForm";
        var form = document.getElementById(formId);
        if (form == null) {
            form = document.createElement("form");
            form.id = formId;
            form.style.display = "none";
            document.body.appendChild(form);
            form.method = 'post';
        } else {
            form.innerHTML = "";
        }
        form.action = act;
        if (target == undefined) {
            target = "_blank";
        }
        form.target = target;
        for(var a in param) {
            var el = document.createElement("input");
            el.setAttribute("id", formId + a);
            el.setAttribute("name", a);
            el.setAttribute("type", "hidden");
            form.appendChild(el);
            if (typeof(param[a]) == "object") {
                document.getElementById(formId + a).value = JSON.stringify(param[a]);
            } else {
                document.getElementById(formId + a).value = param[a];
            }
        }
        form.submit();
    },
    //上传
    upload:function(item, format, act, param, callback) {
        new AjaxUpload(item, {
            action: act,
            data: param,
            responseType:"json",
            onSubmit: function(file, ext) {
                if (format != "") {
                    var b = false;
                    eval("b = /^(" + format.replace(",", "|") + ")$/.test(ext)");
                    if (!b) {
                        jsutil.msg.alert("请选择[" + format + "]文件！");
                        return false;
                    }
                }
                jsutil.msg.layer.mask();
            },
            onComplete: function(file, res) {
                jsutil.msg.layer.unmask();
                if (callback != undefined) {
                    callback.call(this, res);
                }
            }
        });
    }
};
//消息
jsutil.msg = {
    //显示信息
    info:function(msg) {
        $.messager.show({
            title:"信息",
            msg:msg,
            style:{
            }
        });
    },
    //显示提示信息
    alert:function(msg,fn) {
        $.messager.alert("提示", msg, 'info',fn)
    },
    warn:function(msg) {
        $.messager.alert("警告", msg, 'warning');
    },
    //确认
    confirm:function(msg, onOk) {
        $.messager.confirm("确认", msg, onOk);
    },
    //显示错误信息
    err:function(msg, cause) {
        $.messager.alert("错误", msg,"error");
    },
    layer:function(){
        var $mask,$maskMsg;
        function init(){
            if(!$mask){
                $mask = $("<div class=\"datagrid-mask mymask\"></div>").appendTo("body");
            }
            $mask.css({width:"100%",height:$(document).height(),zIndex:100000});
        }
        return {
            mask:function(msg){
                init();
                $mask.show();
            }
            ,unmask:function(){
                $mask.hide();
            }
        }
    }()
};
//工具
jsutil.tool = {
    //加载HTML
    loadHtml:function(src) {
        var html = "";
        $.ajax({
            type:"GET",
            async:false,
            cache:false,
            dataType:"text",
            url:src,
            success:function(rtn) {
                html = rtn;
            }
        });
        return html;
    },
    //加载JS
    loadJs:function(src) {
        $.ajax({
            type:"GET",
            async:false,
            cache:false,
            dataType:"text",
            url:src,
            success:function(rtn) {
                eval(rtn);
            }
        });
    },
    //替换变量
    replaceVar:function(str, obj) {
        var key = "";
        var res = "";
        for (var i = 0; i < str.length; i++) {
            if (str.charAt(i) == '$' && i + 1 < str.length && str.charAt(i + 1) == '{') {
                i += 2;
                key = "";
                while (i < str.length) {
                    if (str.charAt(i) == '}') {
                        if (obj[key]) {
                            res += obj[key];
                        } else {
                            res += "${" + key + "}"
                        }
                        break;
                    } else {
                        key += str.charAt(i++);
                    }
                }
            } else {
                res += str.charAt(i);
            }
        }
        return res;
    },
    refreshToken : function(url,$element){
        $.get(url+'/springmvc.token').done(function(data){
            $element.val(data);
        });
    },
    /**
     * 将数值四舍五入(保留2位小数)后格式化成金额形式
     *
     * @param num 数值(Number或者String)
     * @return 金额格式的字符串,如'1,234,567.45'
     * @type String
     */
    formatCurrency :function(num) {
        if(num==""||isNaN(num)||num==null){
            return "无效数字";
        }else{
            num = num.toString().replace(/\$|\,/g,'');
            if(isNaN(num))
                return "无效数字";
            sign = (num == (num = Math.abs(num)));
            num = Math.floor(num*100+0.50000000001);
            cents = num%100;
            num = Math.floor(num/100).toString();
            if(cents<10)
                cents = "0" + cents;
            for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
                num = num.substring(0,num.length-(4*i+3))+','+
                    num.substring(num.length-(4*i+3));
            return (((sign)?'':'-') + num + '.' + cents);
        }
    },
    /**
     * 将数值四舍五入(保留1位小数)后格式化成金额形式
     *
     * @param num 数值(Number或者String)
     * @return 金额格式的字符串,如'1,234,567.4'
     * @type String
     */
    formatCurrencyTenThou:function(num) {
        if(num==""||isNaN(num)||num==null){
            return "无效数字";
        }else{
            num = num.toString().replace(/\$|\,/g,'');
            if(isNaN(num))
                return "无效数字";
            //num = "0";
            sign = (num == (num = Math.abs(num)));
            num = Math.floor(num*10+0.50000000001);
            cents = num%10;
            num = Math.floor(num/10).toString();
            for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
                num = num.substring(0,num.length-(4*i+3))+','+
                    num.substring(num.length-(4*i+3));
            return (((sign)?'':'-') + num + '.' + cents);
        }
    }
}
//禁用backspace导航
$(document).keydown(function(event) {
    if (event.keyCode == 8 && !$("input").is(":focus") && !$("textarea").is(":focus")) {
        event.preventDefault();
    }
});


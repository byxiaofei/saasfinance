$.ajaxSetup({
    cache:false,
    //设置ajax请求结束后的执行动作
    complete : function(XMLHttpRequest, textStatus) {
        // 通过XMLHttpRequest取得响应头，REDIRECT
        //若HEADER中含有REDIRECT说明后端想重定向
        var redirect = XMLHttpRequest.getResponseHeader("REDIRECT");
        if (redirect == "REDIRECT") {
            var win = window;
            while (win != win.top){
                win = win.top;
            }
            $.messager.alert("提示", "由于你长时间没有操作，登录信息已失效，请重新登录！", "info", function () {
                //将后端重定向的地址取出来,使用win.location.href去实现重定向的要求
                win.location.href= XMLHttpRequest.getResponseHeader("CONTEXTPATH");
            });
        } else {
            var hashNoPrivilege = XMLHttpRequest.getResponseHeader("HASH_NO_PRIVILEGE");
            //若HEADER中含有HASH_NO_PRIVILEGE说明没有权限访问该服务
            if (hashNoPrivilege == "HASH_NO_PRIVILEGE") {
                $.messager.alert("提示", "您已无权限进行此项操作！原因：当前机构已停用", "info");
            }
        }
    }
});
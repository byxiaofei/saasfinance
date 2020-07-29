/**
 * 通过window.open()打开一个新的窗口，当以jsonData数据携带参数时则隐藏参数
 * @param url
 * @param jsonData JSON数据，可为空
 */
function windowOpenNewPage(url,jsonData) {
    if (jsonData) {
        //首先创建一个form表单
        var tempForm = document.createElement("form");
        tempForm.id="tempForm1";
        //制定发送请求的方式为post
        tempForm.method="post";
        //此为window.open的url，通过表单的action来实现
        tempForm.action=url;
        //利用表单的target属性来绑定window.open的一些参数（如设置窗体属性的参数等）
        tempForm.target="_blank";
        //循环遍历
        for(var i in jsonData){
            //创建input标签，用来设置参数
            var hideInput = document.createElement("input");
            hideInput.type="hidden";
            hideInput.name= i;
            hideInput.value= jsonData[i];
            //将input表单放到form表单里
            tempForm.appendChild(hideInput);
        }
        //将此form表单添加到页面主体body中
        document.body.appendChild(tempForm);
        //手动触发，提交表单
        tempForm.submit();
        //从body中移除form表单
        document.body.removeChild(tempForm);
    } else {
        window.open(url);
    }
}
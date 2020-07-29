/**
 * 打印指定內容
 *  appointPrintContent(doc)为打印内容函数
 * @param title 标题
 */
function printByAssignContent(title) {
    //判断iframe是否存在，不存在则创建iframe
    var iframe=document.getElementById(title);
    if(!iframe){
        iframe = document.createElement('IFRAME');
        var doc = null;
        iframe.setAttribute("id", title);
        iframe.setAttribute('style', 'position:absolute;width:0px;height:0px;left:-500px;top:-500px;');
        document.body.appendChild(iframe);
        doc = iframe.contentWindow.document;

        appointPrintContent(doc);

        doc.close();
        iframe.contentWindow.focus();
    }
    iframe.contentWindow.print();
    if (navigator.userAgent.indexOf("MSIE") > 0){
        document.body.removeChild(iframe);
    }
}
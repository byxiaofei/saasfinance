/**
 * 修改 EasyUI DataGrid Table 表格样式
 * @param panel
 */
function updata_easyUI_dataGrid_table_style(panel) {
    var table = panel.find('.datagrid-view2 div.datagrid-body table,.datagrid-view2 div.datagrid-header table');
    table.css({'border-right':'1px solid black','border-bottom':'1px solid black','border-collapse':'collapse'});
    table.css({'border-collapse':'collapse'});
    var tr = panel.find('.datagrid-view2 div.datagrid-header table tr,.datagrid-view2 div.datagrid-body table tr');
    var tr2 = panel.find('.datagrid-view2 div.datagrid-body table tr');
    tr2.css({'height':tr.height()-1})
    tr.each(function () {
        var td = $(this).children('td');
        td.css({'border-left':'1px solid black','border-top':'1px solid black'});
    });
}
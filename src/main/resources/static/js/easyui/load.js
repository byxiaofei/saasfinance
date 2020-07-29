/**
 * 页面加载等待页面
 * 
 * @author sinosoft zh
 * @date 2010/7/24
 * 
 */
var height = window.screen.height - 250;
var width = window.screen.width;
var leftW = 300;
if (width > 1200) {
	leftW = 500;
} else if (width > 1000) {
	leftW = 350;
} else {
	leftW = 100;
}
var _html = "<div id='loading' style='position:absolute;left:0;width:100%;height:"
		+ height
		+ "px;top:0;background:white;opacity:1.0;filter:alpha(opacity=100);'>";
/*
 * _html+="<div style='position:absolute;
 * cursor1:wait;left:"+leftW+"px;top:200px;width:auto;height:16px;padding:12px
 * 5px 10px 30px;" _html+=" background:#fff url("
 * _html+="/cf/js/easyui/themes/default/images/loading.gif) no-repeat scroll 5px
 * 10px;border:2px solid #ccc;color:#000;'>" _html+="正在加载，请等待..." _html+="</div>"
 */
_html += "</div><script>$.messager.progress({ title:'请稍后', msg:'页面加载中...'})</script>";

window.onload = function() {
	var _mask = document.getElementById('loading');
	_mask.parentNode.removeChild(_mask);
	$.messager.progress('close');
}
document.write(_html);

/**
 * 清空指定表单中的内容,参数为目标form的id 注：在使用Jquery EasyUI的弹出窗口录入新增内容时，每次打开必须清空上次输入的历史
 * 数据，此时通常采用的方法是对每个输入组件进行置空操作:$("#name").val(""),这样做，
 * 当输入组件比较多时会很繁琐，产生的js代码很长，这时可以将所有的输入组件放入个form表单 中，然后调用以下方法即可。
 * 
 * @param formId将要清空内容的form表单的id
 */
function resetContent(formId) {
	var clearForm = document.getElementById(formId);
	if (null != clearForm && typeof (clearForm) != "undefined") {
		clearForm.reset();
	}
}

/**
 * 刷新DataGrid列表(适用于Jquery Easy Ui中的dataGrid)
 * 注：建议采用此方法来刷新DataGrid列表数据(也即重新加载数据)，不建议直接使用语句
 * $('#dataTableId').datagrid('reload');来刷新列表数据，因为采用后者，如果日后
 * 在修改项目时，要在系统中的所有刷新处进行其他一些操作，那么你将要修改系统中所有涉及刷新
 * 的代码，这个工作量非常大，而且容易遗漏；但是如果使用本方法来刷新列表，那么对于这种修 该需求将很容易做到，而去不会出错，不遗漏。
 * 
 * @paramdataTableId将要刷新数据的DataGrid依赖的table列表id
 */
function flashTable(dataTableId) {
	$('#' + dataTableId).datagrid('reload');
}

/**
 * 取消DataGrid中的行选择(适用于Jquery Easy Ui中的dataGrid)
 * 注意：解决了无法取消"全选checkbox"的选择,不过，前提是必须将列表展示
 * 数据的DataGrid所依赖的Table放入html文档的最全面，至少该table前没有 其他checkbox组件。
 * 
 * @paramdataTableId将要取消所选数据记录的目标table列表id
 */
function clearSelect(dataTableId) {
	$('#' + dataTableId).datagrid('clearSelections');
	// 取消选择DataGrid中的全选
	$("input[type='checkbox']").eq(0).attr("checked", false);
}

/**
 * 关闭Jquery EasyUi的弹出窗口(适用于Jquery Easy Ui)
 * 
 * @paramdialogId将要关闭窗口的id
 */
function closeDialog(dialogId) {
	$('#' + dialogId).dialog('close');
}

/**
 * 自适应表格的宽度处理(适用于Jquery Easy Ui中的dataGrid的列宽),
 * 注：可以实现列表的各列宽度跟着浏览宽度的变化而变化，即采用该方法来设置DataGrid
 * 的列宽可以在不同分辨率的浏览器下自动伸缩从而满足不同分辨率浏览器的要求
 * 使用方法：(如:{field:'ymName',title:'编号',width:fillsize(0.08),align:'center'},)
 * 
 * @parampercent当前列的列宽所占整个窗口宽度的百分比(以小数形式出现，如0.3代表30%)
 * 
 * @return通过当前窗口和对应的百分比计算出来的具体宽度
 */
function fillsize(percent) {
	var bodyWidth = document.body.clientWidth;
	return (bodyWidth - 90) * percent;
}

/**
 * 获取所选记录行(单选)
 * 
 * @paramdataTableId目标记录所在的DataGrid列表的table的id
 * @paramerrorMessage 如果没有选择一行(即没有选择或选择了多行)的提示信息
 * 
 * @return 所选记录行对象，如果返回值为null,或者"null"(有时浏览器将null转换成了字符串"null")说明没有 选择一行记录。
 */
function getSingleSelectRow(dataTableId, errorMessage) {
	var rows = $('#' + dataTableId).datagrid('getSelections');
	var num = rows.length;
	if (num == 1) {
		return rows[0];
	} else {
		$.messager.alert('提示消息', errorMessage, 'info');
		return null;
	}
}

/**
 * 在DataGrid中获取所选记录的id,多个id用逗号分隔
 * 注：该方法使用的前提是：DataGrid的idField属性对应到列表Json数据中的字段名必须为id
 * 
 * @paramdataTableId目标记录所在的DataGrid列表table的id
 * 
 * @return 所选记录的id字符串(多个id用逗号隔开)
 */
function getSelectIds(dataTableId, noOneSelectMessage) {
	var rows = $('#' + dataTableId).datagrid('getSelections');
	var num = rows.length;
	var ids = null;
	if (num < 1) {
		if (null != noOneSelectMessage)
			$.messager.alert('提示消息', noOneSelectMessage, 'info');
		return null;
	} else {
		for (var i = 0; i < num; i++) {
			if (null == ids || i == 0) {
				ids = rows[i].id;
			} else {
				ids = ids + "," + rows[i].id;
			}
		}
		return ids;
	}
}
/**
 * 在DataGrid中获取所选记录的id,多个id用逗号分隔
 * 注：该方法使用的前提是：DataGrid的idField属性对应到列表Json数据中的字段名
 * 
 * @paramdataTableId目标记录所在的DataGrid列表table的id
 * 
 * @return 所选记录的id字符串(多个id用逗号隔开)
 */
function getSelectIdsByFiled(dataTableId,filed,noOneSelectMessage) {
	var rows = $('#' + dataTableId).datagrid('getSelections');
	var num = rows.length;
	
	var ids = null;
	if (num < 1) {
		if (null != noOneSelectMessage)
			$.messager.alert('提示消息', noOneSelectMessage, 'info');
		return null;
	} else {
		var handle='rows[i].'+filed;
		handle.replace(/\'/g,"");
		for (var i = 0; i < num; i++) {
			if (null == ids || i == 0) {
				ids = eval(handle);
			} else {
				ids = ids + "," + eval(handle);
			}
		}
		return ids;
	}
}

/**
 * 删除所选记录(适用于Jquery Easy Ui中的dataGrid)(删除的依据字段是id)
 * 注：该方法会自动将所选记录的id(DataGrid的idField属性对应到列表Json数据中的字段名必须为id)
 * 动态组装成字符串，多个id使用逗号隔开(如：1,2,3,8,10)，然后存放入变量ids中传入后台，后台
 * 可以使用该参数名从request对象中获取所有id值字符串，此时在组装sql或者hql语句时可以采用in 关键字来处理，简介方便。
 * 另外，后台代码必须在操作完之后以ajax的形式返回Json格式的提示信息，提示的json格式信息中必须有一个
 * message字段，存放本次删除操作成功与失败等一些提示操作用户的信息。
 * 
 * @paramdataTableId将要删除记录所在的列表table的id
 * @paramrequestURL与后台服务器进行交互，进行具体删除操作的请求路径
 * @paramconfirmMessage 删除确认信息
 */

function deleteNoteById(dataTableId, requestURL, confirmMessage) {
	if (null == confirmMessage || typeof (confirmMessage) == "undefined"
			|| "" == confirmMessage) {
		confirmMessage = "确定删除所选记录?";
	}
	var rows = $('#' + dataTableId).datagrid('getSelections');
	var num = rows.length;
	var ids = null;
	if (num < 1) {
		$.messager.alert('提示消息', '请选择你要删除的记录!', 'info');
	} else {
		$.messager.confirm('确认', confirmMessage, function(r) {
			if (r) {
				for (var i = 0; i < num; i++) {
					if (null == ids || i == 0) {
						ids = rows[i].id;
					} else {
						ids = ids + "," + rows[i].id;
					}
				}
				$.getJSON(requestURL, {
					"ids" : ids
				}, function(data) {
					if (null != data && null != data.message
							&& "" != data.message) {
						$.messager.alert('提示消息', data.message, 'info');
						flashTable(dataTableId);
					} else {
						$.messager.alert('提示消息', '删除失败！', 'warning');
					}
					clearSelect(dataTableId);
				});
			}
		});
	}
}

function deleteNoteByFiled(dataTableId, filed,requestURL, confirmMessage) {
	if (null == confirmMessage || typeof (confirmMessage) == "undefined"
			|| "" == confirmMessage) {
		confirmMessage = "确定删除所选记录?";
	}
	var rows = $('#' + dataTableId).datagrid('getSelections');
	var num = rows.length;
	var ids = null;
	if (num < 1) {
		$.messager.alert('提示消息', '请选择你要删除的记录!', 'info');
	} else {
		$.messager.confirm('确认', confirmMessage, function(r) {
			if (r) {
				var handle='rows[i].'+filed;
				handle.replace(/\'/g,"");
				for (var i = 0; i < num; i++) {
					if (null == ids || i == 0) {
						ids = eval(handle);
					} else {
						ids = ids + "," + eval(handle);
					}
				}
				$.getJSON(requestURL, {
					"ids" : ids
				}, function(data) {
					if (null != data && null != data.message
							&& "" != data.message) {
						$.messager.alert('提示消息', data.message, 'info');
						flashTable(dataTableId);
					} else {
						$.messager.alert('提示消息', '删除失败！', 'warning');
					}
					clearSelect(dataTableId);
				});
			}
		});
	}
}

/**
 * 创建上传窗口 公共方法
 * 
 * @param chunk
 *            是否分割大文件
 * @param callBack
 *            上传成功之后的回调
 */
function Uploader(chunk, basePath,url,callBack) {
	var addWin = $('<div style="overflow: hidden;"/>');
	var upladoer = $('<iframe/>');
	var ourl = url;
	upladoer
			.attr({
				'src' : basePath+'/page/system/uploader.jsp?chunk='
						+ chunk + '&ourl="' + ourl + '"',
				width : '100%',
				height : '100%',
				frameborder : '0',
				scrolling : 'no'
			});
	addWin.window({
		title : "上传文件",
		height : 350,
		width : 550,
		top : 0 ,
		minimizable : false,
		modal : true,
		collapsible : false,
		maximizable : false,
		resizable : false,
		content : upladoer,
		onClose : function() {
			var fw = GetFrameWindow(upladoer[0]);
			var files = fw.files;
			$(this).window('destroy');
			callBack.call(this, files);
		},
		onOpen : function() {
			var target = $(this);
			setTimeout(function() {
				var fw = GetFrameWindow(upladoer[0]);
				fw.target = target;
			}, 100);
		}
	});
}
//修改自评估汇总页面 上传文件 在屏幕中位置  2017-9-15 20:01:56
function Uploader1(chunk, basePath,url,callBack) {
	var addWin = $('<div style="overflow: hidden;"/>');
	var upladoer = $('<iframe/>');
	var ourl = url;
	upladoer
			.attr({
				'src' : basePath+'/page/system/uploader.jsp?chunk='
						+ chunk + '&ourl="' + ourl + '"',
				width : '100%',
				height : '100%',
				frameborder : '0',
				scrolling : 'no'
			});
	addWin.window({
		title : "上传文件",
		height : 350,
		width : 550,
		top : 500 ,
		minimizable : false,
		modal : true,
		collapsible : false,
		maximizable : false,
		resizable : false,
		content : upladoer,
		onClose : function() {
			var fw = GetFrameWindow(upladoer[0]);
			var files = fw.files;
			$(this).window('destroy');
			callBack.call(this, files);
		},
		onOpen : function() {
			var target = $(this);
			setTimeout(function() {
				var fw = GetFrameWindow(upladoer[0]);
				fw.target = target;
			}, 100);
		}
	});
}
/**
 * 自评估制度文件上传
 */
function Uploader2(chunk, basePath,url,callBack) {
	var addWin = $('<div style="overflow: hidden;"/>');
	var upladoer = $('<iframe/>');
	var ourl = url;
	upladoer
			.attr({
				'src' : basePath+'/page/system/uploader2.jsp?chunk='
						+ chunk + '&ourl="' + ourl + '"',
				width : '100%',
				height : '100%',
				frameborder : '0',
				scrolling : 'no'
			});
	addWin.window({
		title : "上传文件",
		height : 350,
		width : 550,
		top : 0 ,
		minimizable : false,
		modal : true,
		collapsible : false,
		maximizable : false,
		resizable : false,
		content : upladoer,
		onClose : function() {
			var fw = GetFrameWindow(upladoer[0]);
			var files = fw.files;
			$(this).window('destroy');
			callBack.call(this, files);
		},
		onOpen : function() {
			var target = $(this);
			setTimeout(function() {
				var fw = GetFrameWindow(upladoer[0]);
				fw.target = target;
			}, 100);
		}
	});
}
/**
 * 根据iframe对象获取iframe的window对象
 * 
 * @param frame
 * @returns {Boolean}
 */
function GetFrameWindow(frame) {
	return frame && typeof (frame) == 'object' && frame.tagName == 'IFRAME'
			&& frame.contentWindow;
}
/**
 * 需要3个参数，是否分块，基础路径，上传地址，回调函数
 * @param chunk
 * @param basePath
 * @param url
 * @param callBack
 * @returns
 */
function makerUpload(chunk,basePath,url,callBack) {
	Uploader(chunk,basePath,url,callBack);
}
function makerUpload1(chunk,basePath,url,callBack) {
	Uploader1(chunk,basePath,url,callBack);
}
function makerUpload2(chunk,basePath,url,callBack) {
	Uploader2(chunk,basePath,url,callBack);
}
/**
 * 打开单元格提示
 * @param dataTableId
 * @returns
 */
function showCellTip(dataTableId) {
	$('#' + dataTableId).datagrid('doCellTip',{cls:{'background-color':'rgb(247, 245, 209)'},delay:500});
}

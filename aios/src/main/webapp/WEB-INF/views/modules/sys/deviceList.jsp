<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备管理</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/device/">设备管理</a></li>
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead><tr><th>设备名称</th><th>网络地址</th></tr></thead>
		<tbody>
			<c:forEach items="${list}" var="row">
				<tr><td>${row.camera}</td><td>${row.url}</td></tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
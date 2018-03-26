<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>门店管理</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/shop/">门店管理</a></li>
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead><tr><th>门店</th><th>设备</th><th>所属地区</th></tr></thead>
		<tbody>
			<c:forEach items="${list}" var="row">
				<tr><td>${row.location}</td><td>${row.camera}</td><td>${row.area}</td></tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
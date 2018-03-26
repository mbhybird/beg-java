<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>门店统计</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/report/">门店统计</a></li>
	</ul>
	<div style="float:left;width:400px;height:600px;padding-bottom: 10px;">
		<canvas id="myChartBar" width="400" height="600"></canvas>
	</div>
	<div style="float:left;width:500px;height:600px;padding-bottom: 10px;">
		<canvas id="myChartLine" width="500" height="600"></canvas>
	</div>
	<div style="float:left;width:500px;height:600px;padding-bottom: 10px;">
		<canvas id="myChartPie" width="500" height="600"></canvas>
	</div>
	<script>
		var arrLabel = [];
		var arrValue = [];
        <c:forEach items="${list}" var="row">
			arrLabel.push("${row.dt}");
        	arrValue.push("${row.count}");
        </c:forEach>
        var ctxBar = document.getElementById("myChartBar").getContext('2d');
        var ctxLine = document.getElementById("myChartLine").getContext('2d');
        var ctxPie = document.getElementById("myChartPie").getContext('2d');
        var myChartBar = new Chart(ctxBar, {
            type: 'bar',
            data: {
                labels: arrLabel,
                datasets: [{
                    label: '进店人数',
                    data: arrValue,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)',
                        'rgba(200, 99, 132, 0.2)',
                        'rgba(200, 162, 235, 0.2)',
                        'rgba(200, 206, 86, 0.2)',
                        'rgba(200, 192, 192, 0.2)',
                        'rgba(200, 102, 255, 0.2)',
                        'rgba(200, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255,99,132,1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)',
                        'rgba(200, 99, 132, 1)',
                        'rgba(200, 162, 235, 1)',
                        'rgba(200, 206, 86, 1)',
                        'rgba(200, 192, 192, 1)',
                        'rgba(200, 102, 255, 1)',
                        'rgba(200, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
        var myChartLine = new Chart(ctxLine, {
            type: 'line',
            data: {
                labels: arrLabel,
                datasets: [{
                    label: '进店人数',
                    data: arrValue,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)',
                        'rgba(200, 99, 132, 0.2)',
                        'rgba(200, 162, 235, 0.2)',
                        'rgba(200, 206, 86, 0.2)',
                        'rgba(200, 192, 192, 0.2)',
                        'rgba(200, 102, 255, 0.2)',
                        'rgba(200, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255,99,132,1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)',
                        'rgba(200, 99, 132, 1)',
                        'rgba(200, 162, 235, 1)',
                        'rgba(200, 206, 86, 1)',
                        'rgba(200, 192, 192, 1)',
                        'rgba(200, 102, 255, 1)',
                        'rgba(200, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
        var myChartPie = new Chart(ctxPie, {
            type: 'pie',
            data: {
                labels: arrLabel,
                datasets: [{
                    label: '进店人数',
                    data: arrValue,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)',
                        'rgba(200, 99, 132, 0.2)',
                        'rgba(200, 162, 235, 0.2)',
                        'rgba(200, 206, 86, 0.2)',
                        'rgba(200, 192, 192, 0.2)',
                        'rgba(200, 102, 255, 0.2)',
                        'rgba(200, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255,99,132,1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)',
                        'rgba(200, 99, 132, 1)',
                        'rgba(200, 162, 235, 1)',
                        'rgba(200, 206, 86, 1)',
                        'rgba(200, 192, 192, 1)',
                        'rgba(200, 102, 255, 1)',
                        'rgba(200, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
	</script>
</body>
</html>
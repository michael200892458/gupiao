<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page language="java" import="org.apache.taglibs.standard.*" %>
<%@ page language="java" import="javax.servlet.jsp.jstl.*" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>AdminLTE | Data Tables</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
    <!-- bootstrap 3.0.2 -->
    <link href="/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <!-- font Awesome -->
    <link href="/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
    <!-- Ionicons -->
    <link href="/css/ionicons.min.css" rel="stylesheet" type="text/css"/>
    <!-- DATA TABLES -->
    <link href="/css/datatables/dataTables.bootstrap.css" rel="stylesheet" type="text/css"/>
    <!-- Theme style -->
    <link href="/css/AdminLTE.css" rel="stylesheet" type="text/css"/>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>
<body class="skin-blue">
<!-- header logo: style can be found in header.less -->
<header class="header">
    <a href="/" class="logo">
        <!-- Add the class icon to your logo image or logo icon to add the margining -->
        AdminLTE
    </a>
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top" role="navigation">
        <!-- Sidebar toggle button-->
        <a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </a>

        <div class="navbar-right">
            <ul class="nav navbar-nav">
            </ul>
        </div>
    </nav>
</header>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="left-side sidebar-offcanvas">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
            <!-- Sidebar user panel -->
            <div class="user-panel">
                <div class="pull-left image">
                    <img src="/img/avatar3.png" class="img-circle" alt="User Image"/>
                </div>
                <div class="pull-left info">
                    <p>Hello, Michael</p>

                    <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
                </div>
            </div>
            <!-- sidebar menu: : style can be found in sidebar.less -->
            <ul class="sidebar-menu">
                <li>
                    <a href="/">
                        <i class="fa fa-dashboard"></i> <span>Dashboard</span>
                    </a>
                </li>
                <li class="treeview active">
                    <a href="#">
                        <i class="fa fa-table"></i> <span>股票</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li class="active"><a href="/"><i class="fa fa-angle-double-right"></i>
                            股票查询</a></li>
                    </ul>
                </li>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>

    <!-- Right side column. Contains the navbar and content of the page -->
    <aside class="right-side">
        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <div class="row">
                                <div class="col-xs-3">
                                    <input id="selectedSocketCode" type="text" class="form-control" placeholder="股票代码">
                                </div>
                                <div class="col-xs-3">
                                    <button class="btn btn-primary" onclick="addSelectedCode();">添加</button>
                                </div>
                            </div>
                        </div>
                        <div class="box-body table-responsive">
                            <table id="selectedCodeTable" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th>股票代码</th>
                                    <th>股票名称</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${selectedCodes}" var="code">
                                    <tr>
                                        <td>${code.socketCode}</td>
                                        <td>${code.name}</td>
                                        <td>
                                            <button class="btn btn-info" onclick="getSocketInfoObjects('${code.socketCode}', '${code.name}');">查看</button>
                                            <button class="btn btn-info" onclick="delSelectedCode('${code.socketCode}');">删除</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>

                <%--k线图--%>
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body" id="socketChart" style="height:600px">
                        </div>
                    </div>
                </div>
                <%--end k线图--%>

                <div class="col-xs-12">
                    <div class="box">
                        <%--<div class="box-header">--%>
                            <%--<div class="box-body">--%>
                                <%--<div class="row">--%>
                                    <%--<div class="col-xs-9">--%>
                                        <%--<input id="socketCode" type="text" class="form-control" placeholder="股票代码">--%>
                                    <%--</div>--%>
                                    <%--<div class="col-xs-3">--%>
                                        <%--<button class="btn btn-primary" onclick="getSocketInfoObjects();">查询</button>--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                        <div class="box-body table-responsive">
                            <table id="example1" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th>日期</th>
                                    <th>开盘价</th>
                                    <th>当前价</th>
                                    <th>最高价</th>
                                    <th>最低价</th>
                                    <th>成交量</th>
                                    <th>5日均值</th>
                                    <th>10日均值</th>
                                    <th>20日均值</th>
                                    <th>30日均值</th>
                                    <th>60日均值</th>
                                </tr>
                                </thead>
                                <tbody id="socketInfoContent">
                                <tr>
                                    <td>20150818</td>
                                    <td>8.3</td>
                                    <td>8.5</td>
                                    <td>9.2</td>
                                    <td>8.2</td>
                                    <td>10000</td>
                                    <td>0</td>
                                    <td>0</td>
                                    <td>0</td>
                                    <td>0</td>
                                    <td>0</td>
                                </tr>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <th>日期</th>
                                    <th>开盘价</th>
                                    <th>当前价</th>
                                    <th>最高价</th>
                                    <th>最低价</th>
                                    <th>成交量</th>
                                    <th>5日均值</th>
                                    <th>10日均值</th>
                                    <th>20日均值</th>
                                    <th>30日均值</th>
                                    <th>60日均值</th>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                    <!-- /.box-body -->
                </div>
                <!-- /.box -->
            </div>
        </section>
        <!-- /.content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-6">
                    <div class="box">
                        <div class="box-header">
                            <h4>添加股票代码</h4>
                        </div>
                        <div class="box-body">
                            <div class="row">
                                <div class="col-xs-4">
                                    <input id="addSocketCode" type="text" class="form-control" placeholder="股票代码">
                                </div>
                                <div class="col-xs-4">
                                    <button class="btn btn-primary" onclick="addSocketCode();">提交</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-xs-6">
                    <div class="box">
                        <div class="box-header">
                            <h4>删除股票代码</h4>
                        </div>
                        <div class="box-body">
                            <div class="row">
                                <div class="col-xs-4">
                                    <input id="delSocketCode" type="text" class="form-control" placeholder="股票代码">
                                </div>
                                <div class="col-xs-4">
                                    <button class="btn btn-primary" onclick="delSocketCode();">删除</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <h4>推荐股票列表</h4>
                        </div>
                        <div class="box-body table-responsive">
                            <table id="example2" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th>股票代码</th>
                                    <th>股票</th>
                                    <th>推荐理由</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${recommendCodes}" var="recommendCode">
                                    <tr>
                                        <td>${recommendCode.code}</td>
                                        <td>${recommendCode.name}</td>
                                        <td>${recommendCode.reasons}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <th>股票代码</th>
                                    <th>推荐理由</th>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                        <!-- /.box-body -->
                    </div>

                </div>
            </div>
        </section>
    </aside>
    <!-- /.right-side -->
</div>
<!-- ./wrapper -->


<!--modal-->
<div class="modal fade" id="modalTarget" tabindex="-1" role="dialog"
     aria-labelledby="modalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title">
                    <span>温馨提示</span>
                </h4>
            </div>
            <div class="modal-body" id="modalContent">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary"
                        data-dismiss="modal">确定
                </button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
</div>


<!-- jQuery 2.0.2 -->
<script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
<!-- Bootstrap -->
<script src="/js/bootstrap.min.js" type="text/javascript"></script>
<!-- DATA TABES SCRIPT -->
<script src="/js/plugins/datatables/jquery.dataTables.js" type="text/javascript"></script>
<script src="/js/plugins/datatables/dataTables.bootstrap.js" type="text/javascript"></script>
<!-- AdminLTE App -->
<script src="/js/AdminLTE/app.js" type="text/javascript"></script>
<%--echart js--%>
<script src="/js/echarts.js"></script>
<!-- page script -->
<script type="text/javascript">
    // 路径配置
    require.config({
        paths: {
            echarts: '/js'
        }
    });
    var myChart;
    // 使用
    require(
            [
                'echarts',
                'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
                'echarts/chart/line',
                'echarts/chart/k'
            ],
            function (ec) {
                // 基于准备好的dom，初始化echarts图表
                myChart = ec.init(document.getElementById('socketChart'));

                var option = {
                    title : {
                        text: '股票走势图',
                        subtext: ''
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['5日均线', '10日均线', '20日均线', '30日均线', '60日均线']
                    },
                    toolbox: {
                        show : true,
                        feature : {
                            mark : {show: true},
                            dataView : {show: true, readOnly: false},
                            magicType : {show: true, type: ['line']},
                            restore : {show: true},
                            saveAsImage : {show: true}
                        }
                    },
                    calculable : true,
                    yAxis : [
                        {
                            type : 'value',
                            axisLabel : {
                                formatter: '{value} 元'
                            }
                        }
                    ]
                };

                // 为echarts对象加载数据
                myChart.setOption(option);
            }
    );
    getSocketInfoObjects("sh000001", "上证指数");
    $(function () {
        $("#example1").dataTable();
        $('#example2').dataTable({
            "bPaginate": true,
            "bLengthChange": true,
            "bFilter": true,
            "bSort": true,
            "bInfo": true,
            "bAutoWidth": false
        });
    });

    function addSocketCode() {
        var socketCode = $("#addSocketCode").val();
        if (!socketCode) {
            $("#modalContent").text("代码不能为空");
            $("#modalTarget").modal("show");
            return ;
        }
        $.ajax({
            url: "/api/addSocketCode",
            type: "POST",
            data:{
                socketCode:socketCode
            },
            success: function(data) {
                data = JSON.parse(data);
                if (data.status == 0) {
                    $("#modalContent").text("添加成功");
                    $("#modalTarget").modal("show");
                } else {
                    $("#modalContent").text("添加失败, message:" + data.message);
                    $("#modalTarget").modal("show");
                }
            }
        });
    }

    function addSelectedCode() {
        var socketCode = $("#selectedSocketCode").val();
        if (!socketCode) {
            $("#modalContent").text("代码不能为空");
            $("#modalTarget").modal("show");
            return;
        }
        $.ajax({
            url: "/api/addSelectedCode",
            type: "POST",
            data: {
                socketCode:socketCode
            },
            success: function(data) {
                data = JSON.parse(data);
                if (data.status == 0) {
                    // 提示成功
                    $("#modalContent").text("添加成功");
                    $("#modalTarget").modal("show");
                } else {
                    // 提示失败
                    $("#modalContent").text("添加失败, message:" + data.message);
                    $("#modalTarget").modal("show");
                }
            }
        });
    }

    function delSelectedCode(code) {
        var socketCode = code;
        if (!socketCode) {
            $("#modalContent").text("代码不能为空");
            $("#modalTarget").modal("show");
            return;
        }
        $.ajax({
            url: "/api/delSelectedCode",
            type: "POST",
            data: {
                socketCode: socketCode
            },
            success: function (data) {
                data = JSON.parse(data);
                if (data.status == 0) {
                    $("#modalContent").text("删除成功");
                    $("#modalTarget").modal("show");
                } else {
                    $("#modalContent").text("删除失败, message:{}", data.message);
                    $("#modalTarget").modal("show");
                }
            }
        });
    }

    function delSocketCode() {
        var socketCode = $("#delSocketCode").val();
        if (!socketCode) {
            $("#modalContent").text("代码不能为空");
            $("#modalTarget").modal("show");
            return ;
        }
        $.ajax({
            url: "/api/delSocketCode",
            type: "POST",
            data:{
                socketCode:socketCode
            },
            success: function(data) {
                data = JSON.parse(data);
                if (data.status == 0) {
                    $("#modalContent").text("删除成功");
                    $("#modalTarget").modal("show");
                } else {
                    $("#modalContent").text("删除失败, message:{}", data.message);
                    $("#modalTarget").modal("show");
                }
            }
        });
    }

    function getSocketInfoObjects(socketCode, socketName) {
        $.ajax({
            url: "/api/getSocketInfoObjects",
            type: "POST",
            data: {
                socketCode:socketCode
            },
            success: function(data) {
                data = JSON.parse(data);
                var content = "";
                var days = [];
                var avg5Value = [];
                var avg10Value = [];
                var avg20Value = [];
                var avg30Value = [];
                var avg60Value = [];
                var kLineData = [];
                var n = data.length;
                var minValue = 100000;
                var maxValue = 0;
                for(var i = 0; i < data.length; i++) {
                    content += "<tr>";
                    content += "<td>" + data[i]["day"] + "</td>";
                    content += "<td>" + data[i]["openPrice"]/100.0+ "</td>";
                    content += "<td>" + data[i]["currentPrice"]/100.0 + "</td>";
                    content += "<td>" + data[i]["todayMaxPrice"]/100.0 + "</td>";
                    content += "<td>" + data[i]["todayMinPrice"]/100.0 + "</td>";
                    content += "<td>" + data[i]["volume"] + "</td>";
                    content += "<td>" + data[i]["avgPrice5"]/100.0 + "</td>";
                    content += "<td>" + data[i]["avgPrice10"]/100.0 + "</td>";
                    content += "<td>" + data[i]["avgPrice20"]/100.0 + "</td>";
                    content += "<td>" + data[i]["avgPrice30"]/100.0 + "</td>";
                    content += "<td>" + data[i]["avgPrice60"]/100.0 + "</td>";
                    content += "</tr>";
                    days[n - i - 1] = data[i]["day"];
                    avg5Value[n - i - 1] = data[i]["avgPrice5"]/100.0;
                    avg10Value[n - i - 1] = data[i]["avgPrice10"]/100.0;
                    avg20Value[n - i - 1] = data[i]["avgPrice20"]/100.0;
                    avg30Value[n - i - 1] = data[i]["avgPrice30"]/100.0;
                    avg60Value[n - i - 1] = data[i]["avgPrice60"]/100.0;
                    kLineData[n - i - 1] = [data[i]["openPrice"]/100.0, data[i]["currentPrice"]/100.0, data[i]["todayMinPrice"]/100.0, data[i]["todayMaxPrice"]/100.0];
                    if (minValue > data[i]["todayMinPrice"]/100.0) {
                        minValue = data[i]["todayMinPrice"]/100.0;
                    }
                    if (maxValue < data[i]["todayMaxPrice"]/100.0) {
                        maxValue = data[i]["todayMaxPrice"]/100.0;
                    }
                }
                if (maxValue < minValue) {
                    maxValue = minValue + 1;
                }
                maxValue = maxValue*1.05;
                minValue = minValue*0.95;
                $("#socketInfoContent").html(content);
                var option = {
                    title : {
                        text: '股票走势图',
                        subtext: socketName
                    },
                    xAxis : [
                        {
                            type : 'category',
                            boundaryGap : false,
                            data : days
                        }
                    ],
                    yAxis : [
                        {
                            type : 'value',
                            axisLabel : {
                                formatter: '{value} 元'
                            },
                            min : minValue,
                            max : maxValue
                        }
                    ],
                    series : [
                        {
                            name: 'k线',
                            type : 'k',
                            data:kLineData,
                            symbol: "none"
                        },
                        {
                            name:'5日均线',
                            type:'line',
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        width:1
                                    }
                                }
                            },
                            data:avg5Value,
                            symbol: "none"

                        },
                        {
                            name:'10日均线',
                            type:'line',
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        width:1
                                    }
                                }
                            },
                            data:avg10Value,
                            symbol: "none"
                        },
                        {
                            name:'20日均线',
                            type:'line',
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        width:1
                                    }
                                }
                            },
                            data:avg20Value,
                            symbol: "none"
                        },
                        {
                            name:'30日均线',
                            type:'line',
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        width:1
                                    }
                                }
                            },
                            data:avg30Value,
                            symbol: "none"
                        },
                        {
                            name:'60日均线',
                            type:'line',
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        width:1
                                    }
                                }
                            },
                            data:avg60Value,
                            symbol: "none"
                        }
                    ]
                };
                myChart.setOption(option);
            }
        });
    }
</script>

</body>
</html>
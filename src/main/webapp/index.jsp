<%--
  Created by IntelliJ IDEA.
  User: yliu4
  Date: 2018/9/11
  Time: 19:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
</head>
<body>
<h2>用户注册</h2>
<form action="/up" enctype="multipart/form-data" method="post">
    <table>
        <tr>
            <td>请选择文件:</td>
            <td><input type="file" name="uploadFile"></td>
        </tr>
        <tr>
            <td><input type="submit" value="上传"></td>
        </tr>
    </table>
</form>
</body>
</html>

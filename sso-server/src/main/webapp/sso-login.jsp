<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/5/4
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
<h3>Login Out Page </h3>
<form action="${pageContext.request.contextPath}/login?callback=${callback}" method="post">
    <table>
        <tr>
            <td>username</td>
            <td><input name="username"></td>
        </tr>
        <tr>
            <td>password</td>
            <td><input name="password"></td>
        </tr>
        <tr>
            <td>action</td>
            <td><input type="submit"></td>
        </tr>
    </table>

</form>
</body>
</html>

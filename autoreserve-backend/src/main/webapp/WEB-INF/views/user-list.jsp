<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>User List</title>
</head>
<body>

<h2>Users</h2>

<table border="1">
    <tr>
        <th>ID</th>
        <th>Email</th>
    </tr>

    <c:forEach var="user" items="${users}">
        <tr>
            <td>${user.id}</td>
            <td>${user.email}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>

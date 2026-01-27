<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Create User</title>
</head>
<body>

<h2>Create User</h2>

<form action="${pageContext.request.contextPath}/users/save" method="post">
    <label>Email:</label>
    <input type="email" name="email" required />

    <br/><br/>

    <label>Password:</label>
    <input type="password" name="password" required />

    <br/><br/>

    <button type="submit">Save</button>
</form>

</body>
</html>


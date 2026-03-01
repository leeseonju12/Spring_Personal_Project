<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Flask 데이터</title>
</head>
<body>

<h2>Flask 서버에서 받은 데이터</h2>

    <p>메시지: ${message}</p>
    <p>값: ${value}</p>

    <h3>아이템 목록</h3>
    <ul>
        <c:forEach var="item" items="${items}">
            <li>${item}</li>
        </c:forEach>
    </ul>

    <a href="/">메인으로 돌아가기</a>

</body>
</html>
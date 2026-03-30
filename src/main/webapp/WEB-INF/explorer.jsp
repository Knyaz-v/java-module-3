<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Explorer</title>
    <style>
        a {text-decoration: none; color: #0000FF;}
        a:visited {color: #0000FF;}
        a:hover {color: #FF0000;}
    </style>
</head>
<body>

<fmt:formatDate value="${generationTime}" pattern="dd.MM.yyyy HH:mm:ss"/>

<h1>C:/${not empty currentPath ? currentPath.concat('/') : ''}</h1>

<hr>

<c:if test="${parentPath != null}">
    <p><b><a href="${pageContext.request.contextPath}/explorer/${parentPath}">🔝 Вверх</a></b></p>
</c:if>

<table>
    <thead>
        <tr><td><b>Файл</b></td><td><b>Размер</b></td><td><b>Дата</b></td></tr>
    </thead>
    <tbody>
        <c:forEach items="${items}" var="item">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${item.isDirectory}">
                            📁 <a href="${pageContext.request.contextPath}/explorer/${currentPath}${not empty currentPath ? '/' : ''}${item.name}" class="dir">${item.name}/</a>
                        </c:when>
                        <c:otherwise>
                            📄 <a href="${pageContext.request.contextPath}/explorer/${currentPath}${not empty currentPath ? '/' : ''}${item.name}" class="file">${item.name}</a>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${!item.isDirectory}">${item.size} bytes</c:when>
                    </c:choose>
                </td>
                <td><fmt:formatDate value="${item.lastModified}" pattern="dd.MM.yyyy HH:mm:ss"/></td>
            </tr>
        </c:forEach>

        <c:if test="${empty items}">
            <tr><td colspan="3">Directory is empty</td></tr>
        </c:if>
    </tbody>
</table>

</body>
</html>
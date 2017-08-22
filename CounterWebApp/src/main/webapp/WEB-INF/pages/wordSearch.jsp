<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h2>SearchDocs</h2>

<b>Total Hits:</b> <cout>${fn:length(foundResults)}</cout>
</br></br>
	<c:forEach var="data" items="${foundResults}">
	<b>File Path:</b> <cout>${data.docPath}</cout><br>
	<b>Score:</b> <cout>${data.score}</cout><br>
	<b>Highlighted Text:</b> </br>
	<c:forEach var="frag" items="${data.highlightedText}">
	<cout>${frag}</cout><br>
	</c:forEach>
	<br><br>
	</c:forEach>
</body>
</html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
        
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
        
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
        
        <style> 

</style>

        
</head>
<body style="background-color:lavender;">

<nav class="navbar navbar-inverse bg-primary">
  
  <a class="navbar-brand" href="#">Lucene Word Search</a>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
   
    
  </div>
</nav>

<div class="col-md-2"></div>
<div class="col-md-8">
<div class="row">
<center>
<form class="form-inline my-2 my-lg-0" action="userSearch.htm" method="get">
      <input name = "usearch" class="form-control mr-sm-2" type="text" placeholder="Search" required="required" pattern=".*\S+.*" >
      <button class="btn btn-outline-success my-2 my-sm-0 btn-primary" type="submit">Search</button>
</form>
</center>
</div>
<!-- row for results -->
<div class="row">


</br></br>
<c:choose>
  <c:when test="${fn:length(searchedData) gt 0}">
<button type="button" class="btn btn-primary btn-lg"><cout>Total Hits: ${fn:length(searchedData)}</cout></button>
</br></br>

	<c:forEach var="data" items="${searchedData}">
	<div class="panel-body panel-warning">
	<pre><b>File:</b> <cout>${data.docPath}</cout>  <b>Score:</b> <cout>${data.score}</cout></pre>
	<br>
	
	<c:forEach var="frag" items="${data.highlightedText}">
	<cout>${frag}</cout><br>
	</c:forEach>
	</div>
	<br><br>
	</c:forEach>

	</hr>
</c:when>
<c:otherwise>
<button type="button" class="btn btn-primary btn-lg"><cout>Total Hits: 0</cout></button>
   
    <h3>No Results Found</h3>
  </c:otherwise>
</c:choose>
</div>
</div>
<div class="col-md-2"></div>
  
</body>
</html>
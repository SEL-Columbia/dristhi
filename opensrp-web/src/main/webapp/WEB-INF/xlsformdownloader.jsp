<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html lang="en">

<head>
<%-- <c:set var="context" value='${pageContext.request.contextPath}'  />
 --%><meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1" />

<title>XLS Form Downloader</title>


<script type="text/javascript"
	src="<c:url value='/resources/js/jquery.js'/>"></script>
<script type="text/javascript"
	src="<c:url value='/resources/js/jquery.dataTables.js'/>"></script>
<script type="text/javascript"
	src="<c:url value='/resources/js/bootstrap.min.js'/>"></script>


<link href="<c:url value='/resources/css/jquery.dataTables.min.css'/>"
	rel="stylesheet">
<link href="<c:url value='/resources/css/bootstrap.min.css'/>"
	rel="stylesheet">
<link href="<c:url value='/resources/css/style.css'/>" rel="stylesheet">
<!-- <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
	<script  src="//cdn.datatables.net/1.10.7/js/jquery.dataTables.min.js"></script> -->






<script type="text/javascript" language="javascript" class="init"></script>
</head>
<body>
<%-- <c:out value="${context}" />
 --%><c:set var="c"  value="${check}"/>
<c:if test="${c==true}">
<div class="alert alert-success">
  <strong>Success!</strong> <c:out value="${msg}"></c:out>
</div>
</c:if>

<c:if test="${c==false}">
<div class="alert alert-danger">
  <strong>Failure!</strong> <c:out value="${msg}"></c:out>
</div>
</c:if>

<div class="row">
<div class="col-md-6">
<%-- <p><c:out value="${servletContext.contextPath}" /></p>
<p><c:out value="${request.contextPath}"/></p> --%>

	<form class="form-horizontal" method="post" action="${servletContext.contextPath}/xlsform/addfiles">
		<fieldset>

			<!-- Form Name -->
			<legend>XLS Form Converter</legend>

	

			<!-- Text input-->
			<div class="form-group">
				<label class="col-md-4 control-label" for="userName">User
					Name</label>
				<div class="col-md-5">
					<input id="userName" name="userName" placeholder="ahmedihs"
						class="form-control input-md" required="" type="text">

				</div>
			</div>
				<!-- Text input-->
			<div class="form-group">
				<label class="col-md-4 control-label" for="password">Password</label>
				<div class="col-md-5">
					<input id="password" name="password" placeholder="ahmedihs"
						class="form-control input-md" required="" type="password">

				</div>
			</div>

			<!-- Text input-->
			<div class="form-group">
				<label class="col-md-4 control-label" for="formId">Form ID</label>
				<div class="col-md-5">
					<input id="formid" name="formId" placeholder="household"
						class="form-control input-md" required="" type="text">

				</div>
			</div>
				<!-- Text input-->
			<div class="form-group">
				<label class="col-md-4 control-label" for="formPk">Form PK</label>
				<div class="col-md-5">
					<input id="formPk" name="formPk" placeholder="12313"
						class="form-control input-md" required="" type="text">

				</div>
			</div>

			<!-- Text input-->
			<div class="form-group">
				<label class="col-md-4 control-label" for="formName">Form Name (Directory)</label>
				<div class="col-md-5">
					<input id="formName" name="formName" placeholder="placeholder"
						class="form-control input-md" type="text">

				</div>
			</div>

			<!-- Button -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="convert"></label>
				<div class="col-md-4">
					<button id="convert" name="convert" class="btn btn-success">Convert</button>
				</div>
			</div>

		</fieldset>
	</form>
</div>
<div class="col-md-6">
<h3>Form Definition Output</h3>
<textarea rows="20" cols="100" name="definitionTextArea" id="definitionTextArea">
<c:out value="${definition}"></c:out>
</textarea>
<button id="generateButton" name="generateButton" class="btn btn-success">Generate</button>
</div>

</div>
</body>
</html>

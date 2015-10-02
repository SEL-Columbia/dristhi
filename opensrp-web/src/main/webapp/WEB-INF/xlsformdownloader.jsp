<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

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
<c:set var="c"  value="${check}"/>
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
	<form class="form-horizontal" method="post" action="/xlsform/addfiles">
		<fieldset>

			<!-- Form Name -->
			<legend>XLS Form Converter</legend>

			<!-- Select Basic -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="selectServer">Select
					Server</label>
				<div class="col-md-5">
					<select id="selectServer" name="selectServer" class="form-control">
						<option value="ona">Ona</option>
						<option value="formhub">Form</option>
					</select>
				</div>
			</div>

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
				<label class="col-md-4 control-label" for="formId">Form ID</label>
				<div class="col-md-5">
					<input id="formid" name="formId" placeholder="household"
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
</div>

</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script>
	let historyBack = '${historyBack}' == true;

	let resultMsg = '${msg}'.trim();

	if (resultMsg.length > 0) {
		alert(resultMsg);
	}

	history.back(); 
</script>
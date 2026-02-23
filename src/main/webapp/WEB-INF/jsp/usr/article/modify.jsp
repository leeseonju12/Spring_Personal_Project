<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="P ARTICLE MODIFY"></c:set>

<%@ include file="../common/head.jspf"%>

<hr />

<section class="mt-24 text-xl px-4">
	<div class="mx-auto">
		<form action="../article/doModify" method="POST">
			<input type="hidden" name="id" value="${article.id}" />
			<table class="table" border="1" cellspacing="0" cellpadding="5" style="width: 100%; border-collapse: collapse;">
				<tbody>
					<tr>
						<th style="text-align: center;">Article ID</th>
						<td style="text-align: center;">${article.id }</td>
					</tr>
					<tr>
						<th style="text-align: center;">Writer</th>
						<td style="text-align: center;">${article.extra__writer }</td>
					</tr>
					<tr>
						<th style="text-align: center;">Registration Date</th>
						<td style="text-align: center;">${article.regDate }</td>
					</tr>
					<tr>
						<th style="text-align: center;">Update Date</th>
						<td style="text-align: center;">${article.updateDate }</td>
					</tr>
					<tr>
						<th style="text-align: center;">Title</th>
						<td style="text-align: center;">
							<input class="input input-neutral input-sm" value="${article.title }" required="required" name="title" type="text"
								autocomplete="off" placeholder="새 제목 입력" />
						</td>
					</tr>
					<tr>
						<th style="text-align: center;">Body</th>
						<td style="text-align: center;">
							<input class="input input-neutral input-sm" value="${article.body }" required="required" name="body" type="text"
								autocomplete="off" placeholder="새 내용 입력" />

						</td>
					</tr>
					<tr>
						<th></th>
						<td style="text-align: center;">
							<input class="btn btn-soft btn-success" type="submit" value="수정" />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
		<div class="btns">
			<button class="btn btn-soft" type="button" onClick="history.back();">뒤로가기</button>
			<c:if test="${article.userCanDelete }">
				<a class="btn btn-soft btn-error" href="../article/doDelete?id=${article.id }">삭제</a>
			</c:if>
		</div>
	</div>
</section>

<%@ include file="../common/foot.jspf"%>
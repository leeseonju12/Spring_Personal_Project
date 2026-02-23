<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="P ARTICLE WRITE"></c:set>

<%@ include file="../common/head.jspf"%>

<hr />
<section class="mt-24 text-xl px-4">
	<div class="mx-auto">
		<form action="../article/doWrite" method="POST">
			<table class="table" border="1" style="width: 100%; border-collapse: collapse;">
				<tbody>
					<tr>
						<th style="text-align: center;">게시판</th>
						<td style="text-align: center;">
							<select class="select select-success" name="boardId">
								<option value="" disabled selected>게시판을 선택하세요.</option>
								<option value="1">NOTICE</option>
								<option value="2">FREE</option>
								<option value="3">QnA</option>
							</select>
						</td>
					</tr>
					<tr>
						<th style="text-align: center;">제목</th>
						<td style="text-align: center;">
							<input class="input input-neutral input-sm" name="title" autocomplete="off" type="text" placeholder="제목을 입력하세요." />
						</td>
					</tr>
					<tr>
						<th style="text-align: center;">내용</th>
						<td style="text-align: center;">
							<input class="input input-neutral input-sm" name="body" type="text" autocomplete="off" placeholder="내용을 입력하세요." />
						</td>
					</tr>
					<tr>
						<th></th>
						<td style="text-align: center;">
							<input class="btn btn-soft btn-success" type="submit" value="작성 완료" />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
		<div class="btns">
			<button class="btn btn-soft" type="button" onClick="history.back();">뒤로가기</button>
		</div>
	</div>
</section>

<%@ include file="../common/foot.jspf"%>
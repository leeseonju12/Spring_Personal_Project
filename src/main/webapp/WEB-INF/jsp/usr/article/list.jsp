<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="${board.code} P LIST"></c:set>

<%@ include file="../common/head.jspf"%>

<hr />
<%-- <div>${board }</div> --%>
<section class="mt-24 text-xl px-4">
	<div class="mx-auto">

		<!-- 검색 폼 -->
		<form action="list" method="get" class="mb-4">
			<input type="hidden" name="boardId" value="${param.boardId}" />
			<div class="flex gap-2 justify-center items-center">
				<select class="select select-sm select-bordered
						max-w-xs" name="searchKeywordTypeCode"
					data-value="${param.searchKeywordTypeCode } ">
					<option value="" disabled selected>검색 기준</option>
					<option value="title">제목</option>
					<option value="body">내용</option>
					<option value="title,body">제목 + 내용</option>
					<option value="nickname">작성자</option>
				</select>
				<label class="ml-3 input input-bordered input-sm w-64 flex items-center gap-2">
					<input type="text" placeholder="검색어를 입력하세요" name="searchKeyword" value="${param.searchKeyword }" />

					<button type="submit" class="btn btn-sm btn-success">검색</button>

					<!-- 검색 초기화 버튼 (검색어가 있을 때만 표시) -->
					<c:if test="${not empty searchKeyword}">
						<a href="list?boardId=${board.id}" class="btn btn-sm btn-ghost">초기화</a>
					</c:if>
			</div>
		</form>

		<!-- 검색 결과 정보 (검색어가 있을 때만 표시) -->
		<c:if test="${not empty searchKeyword}">
			<div class="alert alert-ghost mb-4">
				<span>"${searchKeyword}"에 대한 검색 결과 입니다.</span>
			</div>
		</c:if>

		<div>${articlesCount }개</div>
		<div class="mb-3 text-2xl font-bold">${board.code}게시판</div>
		<table class="table" border="1" cellspacing="0" cellpadding="5" style="width: 100%; border-collapse: collapse;">
			<thead>
				<tr>
					<th style="text-align: center;">Board</th>
					<th style="text-align: center;">ID</th>
					<th style="text-align: center;">Registration date</th>
					<th style="text-align: center;">TITLE</th>
					<th style="text-align: center;">Writer</th>
					<th style="text-align: center;">Views</th>
					<th style="text-align: center;">LIKE</th>
					<th style="text-align: center;">DISLIKE</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="article" items="${articles }">
					<tr class="hover:bg-base-300">
						<td style="text-align: center;">${board.code}</td>
						<td style="text-align: center;">${article.id }</td>
						<td style="text-align: center;">${article.regDate.substring(0,10) }</td>
						<td style="text-align: center;">
							<a href="detail?id=${article.id } ">${article.title }</a>
						</td>
						<td style="text-align: center;">${article.extra__writer }</td>
						<td style="text-align: center;">${article.hitCount }</td>
						<td style="text-align: center;">${article.goodReactionPoint }</td>
						<td style="text-align: center;">${article.badReactionPoint }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

		<!-- 	동적 페이징 -->
		<div class="flex justify-center mt-8">
			<div class="btn-group join">
				<c:set var="paginationLen" value="3" />
				<c:set var="startPage" value="${page - paginationLen >= 1 ? page - paginationLen : 1}" />
				<c:set var="endPage" value="${page + paginationLen <= pagesCount ?  page + paginationLen : pagesCount}" />
				<c:set var="baseUri" value="?boardId=${boardId }" />
				<c:set var="baseUri" value="${baseUri }&searchKeywordTypeCode=${searchKeywordTypeCode }" />
				<c:set var="baseUri" value="${baseUri }&searchKeyword=${searchKeyword }" />

				<c:if test="${startPage > 1}">
					<a class="join-item btn btn-sm" href="${baseUri }&page=1">1</a>
				</c:if>

				<c:if test="${startPage > 2}">
					<button class="join-item btn btn-sm btn-disabled">...</button>
				</c:if>

				<c:forEach begin="${startPage }" end="${endPage }" var="i">
					<a class="join-item btn btn-sm ${param.page == i ? 'btn-active' : ''}" href="${baseUri }&page=${i }">${i }</a>
				</c:forEach>

				<c:if test="${endPage < pagesCount - 1}">
					<button class="join-item btn btn-sm btn-disabled">...</button>
				</c:if>

				<c:if test="${endPage < pagesCount}">
					<a class="join-item btn btn-sm" href="${baseUri }&page=${pagesCount }">${pagesCount }</a>
				</c:if>

			</div>
		</div>
</section>


<%@ include file="../common/foot.jspf"%>
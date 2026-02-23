<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="P MEMBER JOIN"></c:set>

<%@ include file="../common/head.jspf"%>

<hr />

<section class="mt-24 text-xl px-4">
	<div class="mx-auto">
		<form action="../member/doJoin" method="POST">
			<table class="table" border="1" cellspacing="0" cellpadding="5" style="width: 100%; border-collapse: collapse;">
				<tbody>
					<tr>
						<th style="text-align: right;">아이디</th>
						<td style="text-align: center;">
							<input class="input input-neutral" name="loginId" autocomplete="off" type="text" placeholder="아이디 입력" />
						</td>
					</tr>
					<tr>
						<th style="text-align: right;">비밀번호</th>
						<td style="text-align: center;">
							<input class="input input-neutral" name="loginPw" autocomplete="off" type="text" placeholder="비밀번호 입력" />
						</td>
					</tr>
					<tr>
						<th style="text-align: right;">이름</th>
						<td style="text-align: center;">
							<input class="input input-neutral" name="name" autocomplete="off" type="text" placeholder="이름 입력" />
						</td>
					</tr>
					<tr>
						<th style="text-align: right;">닉네임</th>
						<td style="text-align: center;">
							<input class="input input-neutral" name="nickname" autocomplete="off" type="text" placeholder="닉네임 입력" />
						</td>
					</tr>
					<tr>
						<th style="text-align: right;">전화번호</th>
						<td style="text-align: center;">
							<input class="input input-neutral" name="cellphoneNum" autocomplete="off" type="text" placeholder="전화번호 입력" />
						</td>
					</tr>
					<tr>
						<th style="text-align: right;">이메일</th>
						<td style="text-align: center;">
							<input class="input input-neutral" name="email" autocomplete="off" type="text" placeholder="이메일 입력" />
						</td>
					</tr>
					<tr>
						<th></th>
						<td style="text-align: center;">
							<input class="btn btn-soft btn-success" type="submit" value="회원가입" />
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
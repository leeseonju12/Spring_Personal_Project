<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<c:set var="pageTitle" value="F1 Driver Championship"></c:set>
<%@ include file="../common/head.jspf"%>
</head>
<body>
	<section class="max-w-5xl mx-auto px-4 py-6">
		<div class="flex items-center gap-3 mb-4">
			<label for="yearSelect" class="font-semibold">시즌</label>
			<select id="yearSelect" class="select select-bordered select-sm"></select>
			<button id="loadBtn" class="btn btn-sm btn-primary">조회</button>
		</div>

		<div id="statusMessage" class="mb-3 text-sm"></div>

		<div class="overflow-x-auto">
			<table class="table table-zebra w-full">
				<thead>
					<tr>
						<th>순위</th>
						<th>번호</th>
						<th>드라이버</th>
						<th>팀</th>
						<th>포인트</th>
						<th>포인트 변화</th>
						<th>순위 변화</th>
					</tr>
				</thead>
				<tbody id="championshipRows">
					<tr>
						<td colspan="7" class="text-center py-6">시즌을 선택하고 조회를 눌러주세요.</td>
					</tr>
				</tbody>
			</table>
		</div>
	</section>

	<script>
		const yearSelect = document.getElementById('yearSelect');
		const loadBtn = document.getElementById('loadBtn');
		const statusMessage = document.getElementById('statusMessage');
		const championshipRows = document.getElementById('championshipRows');

		async function loadSeasons() {
			const res = await fetch('/api/seasons');
			const json = await res.json();

			if (json.ResultCode !== 'S-1' || !Array.isArray(json.data1)) {
				statusMessage.textContent = json.msg || '시즌 목록 조회에 실패했습니다.';
				statusMessage.className = 'mb-3 text-sm text-red-500';
				return;
			}

			yearSelect.innerHTML = json.data1.map(year => `<option value="${year}">${year}</option>`).join('');
			if (json.data1.length > 0) {
				await loadChampionship(json.data1[0]);
			}
		}

		function renderRows(rows) {
			if (!rows || rows.length === 0) {
				championshipRows.innerHTML = '<tr><td colspan="7" class="text-center py-6">표시할 데이터가 없습니다.</td></tr>';
				return;
			}

			championshipRows.innerHTML = rows.map(row => {
				const photo = row.headshotUrl ? `<img src="${row.headshotUrl}" alt="${row.fullName || row.driverNumber}" class="w-8 h-8 rounded-full inline-block mr-2"/>` : '';
				const name = row.fullName || row.acronym || '-';
				const team = row.teamName || '-';
				const pointsDelta = row.pointsDeltaInRace ?? '';
				const posDelta = row.positionDeltaInRace ?? '';

				return `
					<tr>
						<td>${row.position ?? ''}</td>
						<td>${row.driverNumber ?? ''}</td>
						<td>${photo}${name}</td>
						<td>${team}</td>
						<td>${row.points ?? ''}</td>
						<td>${pointsDelta}</td>
						<td>${posDelta}</td>
					</tr>
				`;
			}).join('');
		}

		async function loadChampionship(year) {
			const res = await fetch(`/api/seasons/${year}/championship/drivers`);
			const json = await res.json();

			if (json.ResultCode !== 'S-1') {
				statusMessage.textContent = json.msg || '챔피언십 조회에 실패했습니다.';
				statusMessage.className = 'mb-3 text-sm text-red-500';
				renderRows([]);
				return;
			}

			statusMessage.textContent = `${year} 시즌 드라이버 챔피언십`; 
			statusMessage.className = 'mb-3 text-sm text-green-600';
			renderRows(json.data1 || []);
		}

		loadBtn.addEventListener('click', () => {
			const selectedYear = yearSelect.value;
			if (!selectedYear) {
				return;
			}
			loadChampionship(selectedYear);
		});

		loadSeasons();
	</script>

	<%@ include file="../common/foot.jspf"%>
</body>
</html>

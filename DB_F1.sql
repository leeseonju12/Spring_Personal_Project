# =============================
# OpenF1 Drivers Championship 수집용 테이블

CREATE TABLE f1_session (
    session_key BIGINT NOT NULL,
    meeting_key BIGINT NOT NULL,
    YEAR INT NOT NULL,
    session_name VARCHAR(30) NOT NULL,
    race_name VARCHAR(120) NULL,
    date_start DATETIME NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (session_key),
    INDEX idx_f1_session_year (YEAR),
    INDEX idx_f1_session_meeting (meeting_key),
    INDEX idx_f1_session_date_start (date_start)
);

CREATE TABLE f1_session_driver (
    session_key BIGINT NOT NULL,
    driver_number INT NOT NULL,
    full_name VARCHAR(120) NULL,
    acronym VARCHAR(8) NULL,
    team_name VARCHAR(120) NULL,
    headshot_url VARCHAR(255) NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (session_key, driver_number),
    INDEX idx_f1_session_driver_num (driver_number)
);

CREATE TABLE f1_championship_driver_snapshot (
    session_key BIGINT NOT NULL,
    meeting_key BIGINT NOT NULL,
    YEAR INT NOT NULL,
    driver_number INT NOT NULL,
    points_start INT NULL,
    points_current INT NULL,
    position_start INT NULL,
    position_current INT NULL,
    raw_json JSON NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (session_key, driver_number),
    INDEX idx_f1_snapshot_year (YEAR),
    INDEX idx_f1_snapshot_meeting (meeting_key),
    INDEX idx_f1_snapshot_driver (driver_number)
);

# =============================
# OpenF1 DB 적재 여부 확인용

SELECT COUNT(*) AS race_sessions_2024
FROM f1_session
WHERE YEAR = 2024
  AND session_name = 'Race';

SELECT COUNT(*) AS snapshots_2024
FROM f1_championship_driver_snapshot
WHERE YEAR = 2024;

SELECT COUNT(*) AS drivers_rows
FROM f1_session_driver;

# “최신 스냅샷 세션키” 찾기 (시즌 기준)
SELECT S.session_key, FS.race_name, FS.date_start
FROM f1_championship_driver_snapshot AS S
INNER JOIN f1_session AS FS
  ON FS.session_key = S.session_key
WHERE S.year = 2024
  AND FS.session_name = 'Race'
ORDER BY FS.date_start DESC, S.session_key DESC
LIMIT 1;

# 챔피언십 테이블 형태로 보기 (추천)
SET @sessionKey = 9662;

SELECT
  S.position_current AS POSITION,
  S.driver_number,
  D.full_name,
  D.acronym,
  D.team_name,
  D.headshot_url,
  S.points_current AS points,
  (S.points_current - S.points_start) AS points_delta_in_race,
  (S.position_start - S.position_current) AS position_delta_in_race,
  FS.race_name,
  FS.date_start
FROM f1_championship_driver_snapshot AS S
LEFT JOIN f1_session_driver AS D
  ON D.session_key = S.session_key
 AND D.driver_number = S.driver_number
LEFT JOIN f1_session AS FS
  ON FS.session_key = S.session_key
WHERE S.session_key = @sessionKey
ORDER BY S.position_current ASC, S.driver_number ASC;

# “2024 최종(또는 최신) 스탠딩”을 한 번에
SELECT
  S.position_current AS POSITION,
  S.driver_number,
  D.full_name,
  D.acronym,
  D.team_name,
  S.points_current AS points,
  (S.points_current - S.points_start) AS points_delta_in_race,
  (S.position_start - S.position_current) AS position_delta_in_race,
  FS.race_name,
  FS.date_start
FROM f1_championship_driver_snapshot AS S
LEFT JOIN f1_session_driver AS D
  ON D.session_key = S.session_key
 AND D.driver_number = S.driver_number
INNER JOIN f1_session AS FS
  ON FS.session_key = S.session_key
WHERE S.year = 2024
  AND FS.session_name = 'Race'
  AND S.session_key = (
    SELECT S2.session_key
    FROM f1_championship_driver_snapshot AS S2
    INNER JOIN f1_session AS FS2
      ON FS2.session_key = S2.session_key
    WHERE S2.year = 2024
      AND FS2.session_name = 'Race'
    ORDER BY FS2.date_start DESC, S2.session_key DESC
    LIMIT 1
  )
ORDER BY S.position_current ASC, S.driver_number ASC;
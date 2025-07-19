package com.fortify.web.repository;

import com.fortify.web.domain.AnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.UUID;

@Repository
public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, UUID> {

    // 분석 상태별 개수 조회
    @Query("SELECT aj.status, COUNT(aj) FROM AnalysisJob aj GROUP BY aj.status")
    List<Object[]> countAnalysisJobsByStatus();

    // 완료된 분석 작업의 평균 시간 (초 단위)
    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, aj.startedAt, aj.finishedAt)) FROM AnalysisJob aj WHERE aj.status = 'COMPLETED' AND aj.startedAt IS NOT NULL AND aj.finishedAt IS NOT NULL")
    Double findAverageDurationForCompletedJobs();

    // 지난 30일간의 일별 분석 작업 트렌드
    @Query(value = "SELECT DATE(aj.created_at) AS job_date, COUNT(*) AS count FROM analysis_job aj WHERE aj.created_at >= CURDATE() - INTERVAL 30 DAY GROUP BY job_date ORDER BY job_date ASC", nativeQuery = true)
    List<Object[]> findDailyAnalysisJobTrendsLast30Days();
}
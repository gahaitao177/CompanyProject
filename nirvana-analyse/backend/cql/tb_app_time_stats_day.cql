--日用户统计启动时间，使用时间
create table tb_app_time_stats_day (
	monthcode text,
    statistic_timestamp text,
    adddate timestamp,
    app_start_cnt bigint,
	app_time_all_user bigint,
	app_time_avg_user bigint,
	app_time_avg_per_start bigint,
    primary key (monthcode,statistic_timestamp)
)WITH CLUSTERING ORDER BY (statistic_timestamp desc);
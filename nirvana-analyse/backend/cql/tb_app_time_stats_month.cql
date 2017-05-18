--月统计启东事件，使用时间
create table tb_app_time_stats_month (
	yearcode text,
    statistic_timestamp text,
    adddate timestamp,
    app_start_cnt bigint,
	app_time_all_user bigint,
	app_time_avg_user bigint,
	app_time_avg_per_start bigint,
    primary key (yearcode,statistic_timestamp)
)WITH CLUSTERING ORDER BY (statistic_timestamp desc);
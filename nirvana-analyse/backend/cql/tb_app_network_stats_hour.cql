--小时统计网络
create table tb_app_network_stats_hour (
	daycode text,
    statistic_timestamp text,
    adddate timestamp,
    network_user_cnt_other bigint,
	network_user_cnt_2g bigint,
	network_user_cnt_3g bigint,
	network_user_cnt_4g bigint,
	network_user_cnt_wifi bigint,
    primary key (daycode,statistic_timestamp)
)WITH CLUSTERING ORDER BY (statistic_timestamp desc);
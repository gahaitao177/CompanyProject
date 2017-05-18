--小时用户统计
create table nirvana.tb_app_users_stats_hour (
	daycode text,
    statistic_timestamp text,
	prefix_id text,
    adddate timestamp,
    app_source text,
	app_name text,
	app_key text,
	app_version text,
	device_type text,
	active_user_login bigint,
	active_user_nologin bigint,
	retained_user_rate float,
	new_users bigint,
    primary key (daycode,statistic_timestamp,prefix_id)
)WITH CLUSTERING ORDER BY (statistic_timestamp desc);
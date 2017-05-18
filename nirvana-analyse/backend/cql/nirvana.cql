drop keyspace nirvana;
create keyspace nirvana with replication = {'class':'SimpleStrategy', 'replication_factor':3};

use nirvana;

create type event(event_id text, time timestamp, extra text);
create type history(page text, enter_time timestamp, exit_time timestamp);

create table app_profile(
    id uuid primary key,
    app_key text,
    device_id text,
    device_type text,
    device_os text,
    device_model text,
    device_brand text,
    device_res text,
    app_version text,
    app_name text,
    app_source text,
    app_channel text,
    app_network text,
    app_gps text,
    user_id text,
    user_name text,
    province text,
    city text,
    city_code text,
    events set<frozen<event>>,
    histories set<frozen<history>>
);

ALTER TABLE app_profile add app_ip text;
//ALTER TABLE app_profile add app_ip_loc text;
ALTER TABLE app_profile add ctime timestamp;

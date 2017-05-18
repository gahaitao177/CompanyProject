package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;

import java.util.UUID;

/**
 * Created by been on 2016/12/27.
 */
@Accessor
public interface UserAccessor {
    @Query("select * from user")
    Result<User> getAll();

    @Query(("insert into user (user_id, name) values (?, ?)"))
    ResultSet insert(UUID userId, String name);

    @Query("insert into user (user_id, name) values (:u, :n)")
    ResultSet insertBindName(@Param("u") UUID userId, @Param("n") String name);

}

package cassandra;

import com.caiyi.financial.nirvana.cassandra.CassandraCQL;

/**
 * Created by root on 2016/12/1.
 * create table demotable (
 * id text PRIMARY KEY,
 * game text,
 * players int,
 * scores list<int>,
 * tags set<text>,
 * favs map<text,text>
 * );
 */
public class CQL {
    public static void main(String[] args) {
        CassandraCQL cassandraCQL = new CassandraCQL();

        String cql = "";
        //执行插入操作
        cql = "insert into demotable (id,game,players,scores,tags,favs) values ('555-afde', 'quake', 3, [17, 4, 2],{ " +
                "'pet', 'cute' },{ 'fruit' : 'Apple', 'band' : 'Beatles' });";

        //替换：Replace the existing list entirely
        /*cql = "update demotable set scores = [3,9,4],tags = { 'kitten', 'cat', 'lol' } where id = '123-afde';";*/

        //增加:Adding one or multiple elements
        /*cql = "update demotable set scores = scores + [ 15, 21 ],tags = tags + { 'gray', 'cuddly' } where id = " +
                "'123-afde';";*/

        //移除:Removing one or multiple elements
        /*cql = "update demotable set scores = scores - [ 14, 21 ],tags = tags - { 'gray', 'cuddly' },favs = favs - {
         " +
                "'movie', 'Apple'} where id = '123-afde';";*/

        cassandraCQL.insertBankCQL(cql);
    }
}

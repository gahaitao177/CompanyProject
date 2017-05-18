package cassandra;

import com.caiyi.financial.nirvana.conf.ConfigurationManager;
import com.caiyi.financial.nirvana.constant.Constants;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.net.InetAddress;
import java.util.Collection;

/**
 * Created by Socean on 2016/12/2.
 * <p/>
 * Creates a keyspace and tables, and loads some data into them
 */
public class CreateAndPopulateKeySpace {

    /*static String[] CASSANDRA_CONTACT_POINT = {"192.168.1.80", "192.168.1.81", "192.168.1.70"};
    static int CASSANDRA_PORT = 9042;*/

    public static void main(String[] args) {
        CreateAndPopulateKeySpace client = new CreateAndPopulateKeySpace();
        Integer clusterPort = ConfigurationManager.getInteger(Constants.CASSANDRA_CLUSTER_PORT);

        try {
            client.connect(Constants.CASSANDRA_DB_ADDRESS, clusterPort);
            client.createSchema();
            client.loadData();
            client.querySchema();

        } finally {
            client.close();
        }
    }

    private Cluster cluster;

    private Session session;

    /**
     * Initiates a connection to the cluster
     * specified by the given contact point.
     *
     * @param contactPoints
     * @param port
     */
    public void connect(Collection<InetAddress> contactPoints, int port) {

        cluster = Cluster.builder().addContactPoints(contactPoints).withPort(port).build();

        System.out.printf("Connected to cluster: %s%n", cluster.getMetadata().getClusterName());

        session = cluster.connect();
    }

    /**
     * Create the schema (keyspace) and tables
     */

    public void createSchema() {

        session.execute("CREATE KEYSPACE IF NOT EXISTS simplex WITH replication " +
                "= {'class':'SimpleStrategy', 'replication_factor':1};");

        session.execute(
                "CREATE TABLE IF NOT EXISTS simplex.songs (" +
                        "id uuid PRIMARY KEY," +
                        "title text," +
                        "album text," +
                        "artist text," +
                        "tags set<text>," +
                        "data blob" +
                        ");");

        session.execute(
                "CREATE TABLE IF NOT EXISTS simplex.playlists (" +
                        "id uuid," +
                        "title text," +
                        "album text, " +
                        "artist text," +
                        "song_id uuid," +
                        "PRIMARY KEY (id, title, album, artist)" +
                        ");");
    }

    /**
     * Insert data into the tables
     */
    public void loadData() {
        session.execute(
                "INSERT INTO simplex.songs (id, title, album, artist, tags) " +
                        "VALUES (" +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'," +
                        "{'jazz', '2013'})" +
                        ";");

        session.execute(
                "INSERT INTO simplex.playlists (id, song_id, title, album, artist) " +
                        "VALUES (" +
                        "2cc9ccb7-6221-4ccb-8387-f22b6a1b354d," +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'" +
                        ");");
    }


    public void querySchema() {
        ResultSet resultSet = session.execute(
                "SELECT * FROM simplex.playlists " +
                        "WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;");

        System.out.printf("%-30s\t%-20s\t%-20s%n", "title", "album", "artist");
        System.out.println("-------------------------------+-----------------------+--------------------");

        for (Row row : resultSet) {

            System.out.printf("%-30s\t%-20s\t%-20s%n",
                    row.getString("title"),
                    row.getString("album"),
                    row.getString("artist"));
        }
    }

    public void close() {
        session.close();
        cluster.close();
    }

}

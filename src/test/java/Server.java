import com.gridgain.ecosystem.vault.vault.model.Person;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;

import javax.cache.Cache;

public class Server {
    public static void main(String[] args) {
        Ignite ignite = Ignition.start(Server.class.getResourceAsStream("/config.xml"));

        IgniteCache c = ignite.cache("PERSON_CACHE");
        c.loadCache(null);

        try (QueryCursor<Cache.Entry<Long,Person>> cursor = c.query(new ScanQuery())) {
            for (Cache.Entry<Long,Person> e : cursor) {
                System.out.println(e);
            }
        }
    }
}

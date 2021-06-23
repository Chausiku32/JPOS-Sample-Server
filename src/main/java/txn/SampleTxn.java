package txn;

import org.jpos.iso.*;
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.channel.PostChannel;
import org.jpos.util.*;
import packager.CRDB_Packager;
import packager.TransportPackager;
import packager.Tranzware_Packager;

import java.io.IOException;

public class SampleTxn {
    public static void main(String[] args) throws ISOException, IOException {
         int PORT = 4123;

        Logger logger = new Logger();
        logger.addListener((LogListener) new SimpleLogListener(System.out));
        PostChannel channel = new PostChannel((ISOPackager) new CRDB_Packager());
//        channel.setHeader("6004003800");

        ((LogSource) channel).setLogger(logger, "Test gateway");
        ThreadPool serverPool = new ThreadPool(10, 1000);
        ISOServer server = new ISOServer(PORT, (ServerChannel) channel, serverPool);

        server.setLogger(logger, "server");

        server.addISORequestListener((source, m) -> {
            m.dump(System.out, "incoming message");
            try {
                m.set(39, "00");
                source.send(m);
            } catch (ISOException | IOException e) {
                e.printStackTrace();
            }
            return true;
        });
        server.run();
    }
}

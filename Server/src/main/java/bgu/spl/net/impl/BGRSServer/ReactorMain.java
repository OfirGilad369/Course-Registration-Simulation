package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.CRSMessageEncoderDecoder;
import bgu.spl.net.api.CRSMessagingProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        Database serverDatabase = Database.getInstance(); //one shared object
        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), //port
                () ->  new CRSMessagingProtocol(serverDatabase), //protocol factory
                CRSMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}

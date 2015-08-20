package cz.vutbr.wislab.symphony.upnp.discovery;

import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;

import org.fourthline.cling.transport.impl.apache.StreamClientImpl;
import org.fourthline.cling.transport.impl.apache.StreamClientConfigurationImpl;
import org.fourthline.cling.transport.impl.apache.StreamServerImpl;
import org.fourthline.cling.transport.impl.apache.StreamServerConfigurationImpl;

//import org.apache.http.conn.scheme.;

public class ApacheServiceConfiguration extends DefaultUpnpServiceConfiguration {

    @Override
    protected Namespace createNamespace() {
        return new Namespace("/upnp"); // This will be the servlet context path
    }

    @Override
    public StreamClient createStreamClient() {
    	return new StreamClientImpl(new StreamClientConfigurationImpl(getSyncProtocolExecutorService()));
    }

    @Override
        public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory)
       {
           return new StreamServerImpl(
                   new StreamServerConfigurationImpl(
                            networkAddressFactory.getStreamListenPort()
                    )
            );
        }
    
    @Override
    public int getAliveIntervalMillis() {
        return 2000;
    }

}
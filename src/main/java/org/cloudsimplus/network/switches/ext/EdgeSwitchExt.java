package org.cloudsimplus.network.switches.ext;

import lombok.NonNull;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.hosts.network.ext.NetworkHostExt;
import org.cloudsimplus.network.HostPacket;
import org.cloudsimplus.network.ext.HostPacketExt;
import org.cloudsimplus.network.switches.AbstractSwitch;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EdgeSwitchExt extends AbstractSwitchExt {
    private static final double DEF_DOWNLINK_BW = 100 * 8;
    private static final double DEF_SWITCHING_DELAY = 0.00157;
    private Net net = null;
    public static final int PORTS = 4;
    private final List<NetworkHostExt> hostList;


    public EdgeSwitchExt(CloudSimPlus simulation, NetworkDatacenter dc, double upLink) {
        super(simulation, dc);
        this.hostList = new ArrayList<>();
        setUplinkBandwidth(upLink);
        setPorts(PORTS);
    }

    @Override
    protected void processPacketDown(final SimEvent evt) {
        super.processPacketDown(evt);

        // packet to be received by the Host
        final HostPacketExt pkt = extractReceivedHostPacket(evt);
        addPacketToSendToHost(pkt.getDestination(), pkt);
    }




    /* ESTA HAY QUECAMBIARLA!!!!*/
    @Override
    protected void processPacketUp(final SimEvent evt) {
        super.processPacketUp(evt);

        /* the packet received from the Host and to be sent to
        aggregate level or to another Host in the same level */
        final HostPacketExt pkt = extractReceivedHostPacket(evt);

        // the packet needs to go to a host which is connected directly to switch
        if (pkt.getDestination() != null && pkt.getDestination() != Host.NULL) {
           addPacketToSendToHost(pkt.getDestination(), pkt);

            return;
        }



       addPacketToBeSentToFirstUplinkSwitch(pkt);
    }


    protected NetworkHostExt getHostfromVm(final Vm vm) {
        return (NetworkHostExt)vm.getHost();
    }

    private HostPacketExt extractReceivedHostPacket(final SimEvent evt) {
        final var pkt = (HostPacketExt) evt.getData();
        final var receiverVm = pkt.getVmPacket().getDestination();
        final var host = getHostfromVm(receiverVm);
        pkt.setDestination(host);
        return pkt;
    }

    public void connectToNet(Net network, double delay){
        this.net = network;
        network.connectToEdgeSwitch(this, delay);
    }


    public void connectHost(final NetworkHostExt host) {
        hostList.add(host);
        host.setEdgeSwitchExt(this);
    }


    public boolean disconnectHost(final NetworkHostExt host) {
        if(hostList.remove(host)){
            host.setEdgeSwitchExt(null);
            return true;
        }

        return false;
    }

    public List<NetworkHostExt> getHostList() {
        return Collections.unmodifiableList(hostList);
    }

    @Override
    public int getLevel() {
        return 2;
    }
}

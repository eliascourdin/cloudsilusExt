package org.cloudsimplus.network.switches.ext;

import lombok.NonNull;
import org.cloudsimplus.core.CloudSimEntity;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.hosts.network.ext.NetworkHostExt;
import org.cloudsimplus.network.ext.HostPacketExt;
import org.cloudsimplus.network.switches.Switch;
import org.cloudsimplus.network.switches.ext.EdgeSwitchExt;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Net extends CloudSimEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(Net.class.getSimpleName());

    private final List<EdgeSwitchExt> switchList;

    private final Map<EdgeSwitchExt, Double> delays;

    private final Map<EdgeSwitchExt, NetworkDatacenter> datacenters;




    public Net(@NonNull Simulation simulation) {
        super(simulation);
        this.switchList = new ArrayList<>();
        this.delays = new HashMap<>();
        this.datacenters = new HashMap<>();
    }

    @Override
    protected void startInternal() {
        LOGGER.info("{} is starting...", this);

    }

    @Override
    public void processEvent(SimEvent evt) {
        switch (evt.getTag()) {
            case CloudSimTag.NETWORK_EVENT_UP -> processPacketUp(evt);
            case CloudSimTag.NETWORK_EVENT_DOWN -> processPacketDown(evt); //MEPA QUE SOLO CNO EL EVENTO UP NOS ALCANZA
            default -> LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clockStr(), this, evt.getTag());
        }
    }

    private void processPacketDown(SimEvent evt) {
    }

    private void processPacketUp(SimEvent evt) {    //LO QUE INTENTO HACER ES EXTRAER EL PAQUETE, MIRAR HACIA DONDE VA Y ENVIARLO AL SWITCH
        final HostPacketExt pkt = (HostPacketExt) evt.getData();
        NetworkHostExt hostDest = pkt.getDestination();
        EdgeSwitchExt switchDest = hostDest.getEdgeSwitchExt();
        if (hostDest.getDatacenter().getId() == pkt.getSource().getDatacenter().getId()){
            LOGGER.trace("ERROR"); //MENSAJE DE ERROR DICIENDO QUE EL DATACENTER DEL QUE PROVIENE EL PAQUETE ES EL MISMO QUE EL DESTINO
        }
        //send(switchDest, pkt, ) //HABRÍA QUE COMPLETAR PRIMERO AL CLASE EDGESWITCHEXT PARA VER BIEN COMO FUNCIONAN TODOS LOS EVENTOS INVOLUCRADOS


    }

    private EdgeSwitchExt findSwitchDest(List<Switch> switches, NetworkHostExt hostDest) {
        return null; //ESTO HABRIA QUE HACERLO DESPUES, DEBERIA BUSCAR CUAL ES EL SWITCH QUE ESTA CONECTADO AL HOST DESTINO
    }


    @Override
    public boolean schedule(int tag) {
        return super.schedule(tag);
    }

    public void connectToEdgeSwitch(EdgeSwitchExt edge, double delay){
        this.switchList.add(edge);
        this.delays.put(edge, delay);
        this.datacenters.put(edge, edge.getDatacenter());
    }
}

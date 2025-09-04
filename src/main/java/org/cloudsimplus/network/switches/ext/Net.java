package org.cloudsimplus.network.switches.ext;

import lombok.NonNull;
import org.cloudsimplus.core.CloudSimEntity;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.core.events.PredicateType;
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


public class Net extends CloudSimEntity{

    private static final Logger LOGGER = LoggerFactory.getLogger(Net.class.getSimpleName());

    private final List<EdgeSwitchExt> switchList;

    private final Map<EdgeSwitchExt, Integer> levels;

    private double[][] delayMatrix;





    public Net(Simulation simulation) {
        super(simulation);
        this.levels =  new HashMap<>();
        this.switchList = new ArrayList<>();
        this.delayMatrix = new double[4][4];
    }

    @Override
    protected void startInternal() {
        LOGGER.info("{} is starting...", this);

    }



    //level 0 for client, level 1 for edge level 2 for fog, level 3 for cloud
    public void setDelay(int i, int j, double value) {
        if (i < 4 && j < 4) {
            delayMatrix[i][j] = value;
        } else {
            throw new IndexOutOfBoundsException("Indices fuera de rango para delayMatrix");
        }
    }

    public double getDelay(int i, int j) {
        if (i < 4 && j < 4) {
            return delayMatrix[i][j];
        } else {
            throw new IndexOutOfBoundsException("Indices fuera de rango para delayMatrix");
        }
    }



    @Override
    public void processEvent(SimEvent evt) {
        if (evt.getTag() == CloudSimTag.NETWORK_EVENT_SEND) {
            processPacketForward(evt);
        }
        else{
            LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clockStr(), this, evt.getTag());
        }
    }


    private void processPacketForward(SimEvent evt) {    //LO QUE INTENTO HACER ES EXTRAER EL PAQUETE, MIRAR HACIA DONDE VA Y ENVIARLO AL SWITCH
        final HostPacketExt pkt = (HostPacketExt) evt.getData();
        NetworkHostExt hostDest = pkt.getDestination();
        EdgeSwitchExt switchDest = hostDest.getEdgeSwitchExt();
        NetworkHostExt hostOrigin = pkt.getSource();
        EdgeSwitchExt switchOrigin = hostOrigin.getEdgeSwitchExt();
        if (hostDest.getDatacenter().getId() == hostOrigin.getDatacenter().getId()){
            LOGGER.trace("ERROR EL PAQUETE NO DEBERIA LLEGAR HASTA ACA"); //MENSAJE DE ERROR DICIENDO QUE EL DATACENTER DEL QUE PROVIENE EL PAQUETE ES EL MISMO QUE EL DESTINO
        }
        if (this.levels.containsKey(switchDest)) {
            final int levelOrigin = this.levels.get(switchOrigin);
            final int levelDest = this.levels.get(switchDest);
            final double delay = getDelay(levelOrigin, levelDest);
            send(switchDest, delay, CloudSimTag.NETWORK_EVENT_DOWN, pkt);
        }
        else{
            //ERROR EL DESTINO NO ESTA CONECTADO A LA RED
            LOGGER.trace("ERROR EL DESTINO NO ESTA CONECTADO A LA RED");
        }
    }




    @Override
    public boolean schedule(int tag) {
        return super.schedule(tag);
    }

    public void connectToEdgeSwitch(EdgeSwitchExt edge){
        this.switchList.add(edge);
        this.levels.put(edge, edge.getLevel());
    }



}

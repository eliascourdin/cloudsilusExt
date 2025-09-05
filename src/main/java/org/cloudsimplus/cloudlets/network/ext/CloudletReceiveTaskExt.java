/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network.ext;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.network.ext.VmPacketExt;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Getter @Setter
public class CloudletReceiveTaskExt extends CloudletTaskExt {
    private final List<VmPacketExt> packetsReceived;

    /**
     * The number of packets that are expected to be received.
     * After receiving them, the task is marked as finished.
     */
    private long expectedPacketsToReceive;

    /**
     * Vm from where packets are expected to be received.
     */
    @NonNull
    private final Vm sourceVm;

    /**
     * Creates a new task.
     *
     * @param id id to assign to the task
     * @param sourceVm Vm from where packets are expected to be received
     */
    public CloudletReceiveTaskExt(final int id, final Vm sourceVm) {
        super(id);
        this.packetsReceived = new ArrayList<>();
        this.sourceVm = sourceVm;
    }


    public void receivePacket(final VmPacketExt packet) {
        packet.setReceiveTime(getCloudlet().getSimulation().clock());
        this.packetsReceived.add(packet);
        final boolean finished = this.packetsReceived.size() >= expectedPacketsToReceive;
        setFinished(finished);
    }

    /**
     * @return a read-only list of received packets
     */
    public List<VmPacketExt> getPacketsReceived() {
        return Collections.unmodifiableList(packetsReceived);
    }
}

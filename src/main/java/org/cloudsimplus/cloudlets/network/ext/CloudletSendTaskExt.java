/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network.ext;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.network.ext.VmPacketExt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class CloudletSendTaskExt extends CloudletTaskExt {
    private final List<VmPacketExt> packetsToSend;

    /**
     * Creates a new task.
     *
     * @param id id to assign to the task
     */
    public CloudletSendTaskExt(final int id) {
        super(id);
        this.packetsToSend = new ArrayList<>();
    }


    public VmPacketExt addPacket(final NetworkCloudletExt destinationCloudlet, final long bytes) {
        Objects.requireNonNull(getCloudlet(), "You must assign a NetworkCloudlet to this Task before adding packets.");
        if(!getCloudlet().isBoundToVm()) {
            throw new IllegalStateException("The source Cloudlet has to have an assigned VM.");
        }
        if(!destinationCloudlet.isBoundToVm()) {
            throw new IllegalStateException("The destination Cloudlet has to have an assigned VM.");
        }

        final var packet = new VmPacketExt(
            getCloudlet().getVm(), destinationCloudlet.getVm(),
            bytes, getCloudlet(), destinationCloudlet);
        packetsToSend.add(packet);
        return packet;
    }

    /**
     * @return a <b>read-only</b> list of packets to send
     */
    public List<VmPacketExt> getPacketsToSend() {
        return Collections.unmodifiableList(packetsToSend);
    }

    /**
     * Gets the list of packets to send,
     * updating packets' send time to the given time
     * and clearing the list of packets, marking the
     * task as finished.
     *
     * @param sendTime packets' send time to set (in seconds)
     * @return the packet list with their send time updated to the given time
     */
    public List<VmPacketExt> getPacketsToSend(final double sendTime) {
        packetsToSend.forEach(pkt ->  pkt.setSendTime(sendTime));

        if(isFinished())
            packetsToSend.clear();
        else setFinished(true);

        return packetsToSend;
    }
}

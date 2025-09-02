package org.cloudsimplus.cloudlets.network.ext;

import org.cloudsimplus.cloudlets.Cloudlet;
//import org.cloudsimplus.cloudlets.network.CloudletReceiveTask;
import org.cloudsimplus.cloudlets.network.CloudletSendTask;
import org.cloudsimplus.cloudlets.network.CloudletTask;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.network.CloudletPacket;
//import org.cloudsimplus.network.VmPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *Same as {@link CloudletSendTask} but uses CloudletPackets instead.
 */

public non-sealed class SendTask extends CloudletTask {
    private final List<CloudletPacket> packetsToSend;

    /**
     * Creates a new task.
     *
     * @param id id to assign to the task
     */
    public SendTask(final int id) {
        super(id);
        this.packetsToSend = new ArrayList<>();
    }

    /**
     * Creates and adds a packet to the list of packets to be sent to a
     * {@link Cloudlet} .
     *
     * @param destinationCloudlet destination cloudlet to send packets to
     * @param bytes the number of data bytes of the packet to create
     * @return the created packet
     * @throws RuntimeException when a NetworkCloudlet was not assigned to the Task
     * @throws IllegalArgumentException when the source or destination Cloudlet doesn't have an assigned VM
     */
    public CloudletPacket addPacket(final NetworkCloudlet destinationCloudlet, final long bytes) {
        Objects.requireNonNull(getCloudlet(), "You must assign a NetworkCloudlet to this Task before adding packets.");

        /*
        if(!getCloudlet().isBoundToVm()) {
            throw new IllegalStateException("The source Cloudlet has to have an assigned VM.");
        }
        if(!destinationCloudlet.isBoundToVm()) {
            throw new IllegalStateException("The destination Cloudlet has to have an assigned VM.");
        }
        */

        final var packet = new CloudletPacket(
            getCloudlet(), destinationCloudlet, bytes);
        packetsToSend.add(packet);
        return packet;
    }

    /**
     * @return a <b>read-only</b> list of packets to send
     */
    public List<CloudletPacket> getPacketsToSend() {
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
    public List<CloudletPacket> getPacketsToSend(final double sendTime) {
        packetsToSend.forEach(pkt ->  pkt.setSendTime(sendTime));

        if(isFinished())
            packetsToSend.clear();
        else setFinished(true);

        return packetsToSend;
    }
}

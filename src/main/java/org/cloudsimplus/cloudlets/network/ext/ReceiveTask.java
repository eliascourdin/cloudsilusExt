package org.cloudsimplus.cloudlets.network.ext;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.cloudlets.network.CloudletSendTask;
import org.cloudsimplus.cloudlets.network.CloudletTask;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.network.CloudletPacket;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A task executed by a {@link NetworkCloudlet} that
 * receives data from a {@link CloudletSendTask}.
 * Each receiver task expects to receive packets
 * from just one VM.
 *
 * <p>
 * Please refer to the following publication for more details:
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 1.0
 *
 * TODO For how long will the task be waiting for packets?
 * The sender task has a defined amount of packets to send, but
 * the receiver doesn't have to know how many packets to wait for.
 * Considering a real distributed app such as a web app, the sender can be
 * a browser and the receiver a web server. In this case,
 * the web server runs indefinitely. However, the simulation
 * has to have a time interval. For instance, it may be simulated
 * the operation of this distributed app for 24 hours.
 * This way, the receiver task could have a specific amount
 * of time to run. In the web app scenario, the web server just
 * keep running, waiting for any client to send packets to it,
 * but it is not required that a client do that. The web app
 * may not be accessed during this time.
 * On other hand, the client may send packets to the server
 * and require a response. It then waits for this response
 * and must have a timeout period, so that the client can:
 * (i) stop waiting a reply, (ii) then move to the next task or finish.
 *
 * Each task has to have a status (such as the Cloudlet itself)
 * to define if it was executed successfully or not.
 * For instance, a CloudletReceiveTask that is waiting to receive
 * a list of packets can be configured to finish
 * after a given timeout without receiving the expected packets.
 *
 * How is the network delay being computed?
 */
@Getter @Setter
public non-sealed class ReceiveTask extends CloudletTask {
    private final List<CloudletPacket> packetsReceived;

    /**
     * The number of packets that are expected to be received.
     * After receiving them, the task is marked as finished.
     */
    private long expectedPacketsToReceive ;

    /**
     * Vm from where packets are expected to be received.
     */
    @NonNull
    private final NetworkCloudlet sourceCloudlet;

    /**
     * Creates a new task.
     *
     * @param id id to assign to the task
     * @param sourceCloudlet Cloudlet from where packets are expected to be received
     */
    public ReceiveTask(final int id, final NetworkCloudlet sourceCloudlet) {
        super(id);
        this.packetsReceived = new ArrayList<>();
        this.sourceCloudlet = sourceCloudlet;
    }

    /**
     * Receives a packet sent from a {@link SendTask}
     * and add it to the received packet list.
     *
     * @param packet the packet received
     */
    public void receivePacket(final CloudletPacket packet) {
        packet.setReceiveTime(getCloudlet().getSimulation().clock());
        this.packetsReceived.add(packet);
        final boolean finished = this.packetsReceived.size() >= expectedPacketsToReceive;
        setFinished(finished);
    }

    /**
     * @return a read-only list of received packets
     */
    public List<CloudletPacket> getPacketsReceived() {
        return Collections.unmodifiableList(packetsReceived);
    }
}

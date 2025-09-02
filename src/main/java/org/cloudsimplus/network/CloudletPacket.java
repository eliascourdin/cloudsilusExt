package org.cloudsimplus.network;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.jetbrains.annotations.NotNull;


@Accessors @Getter @Setter
public class CloudletPacket implements NetworkPacket<NetworkCloudlet>{

    /**
     * The NetworkCloudlet sending the packet.
     */
    @NonNull
    private NetworkCloudlet source;

    /**
     * The NetworkCloudlet that has to receive the packet.
     */
    @NonNull
    private NetworkCloudlet destination;

    private double sendTime;
    private double receiveTime;


    private final long size;


    @Override
    public long getSize() {
        return size;
    }

    @Override
    public @NotNull NetworkCloudlet getSource() {
        return source;
    }

    public CloudletPacket(
        final NetworkCloudlet sourceCl,
        final NetworkCloudlet destinationCl,
        final long size)
    {
        super();
        this.source = sourceCl;
        this.destination = destinationCl;
        this.size = size;
    }

    @Override
    public NetworkCloudlet getDestination() {
        return destination;
    }


    @Override
    public double getSendTime() {
        return sendTime;
    }


    @Override
    public double getReceiveTime() {
        return receiveTime;
    }

}

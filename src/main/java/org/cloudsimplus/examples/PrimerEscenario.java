package org.cloudsimplus.examples;


import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.network.CloudletExecutionTask;
import org.cloudsimplus.cloudlets.network.CloudletReceiveTask;
import org.cloudsimplus.cloudlets.network.CloudletSendTask;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.datacenters.network.ext.NetworkDatacenterExt;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.hosts.network.ext.NetworkHostExt;
import org.cloudsimplus.network.switches.EdgeSwitch;
import org.cloudsimplus.network.switches.ext.EdgeSwitchExt;
import org.cloudsimplus.network.switches.ext.Net;
import org.cloudsimplus.provisioners.ResourceProvisionerSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.network.NetworkVm;

import java.util.ArrayList;
import java.util.List;

import static org.cloudsimplus.examples.NetworkVmExampleCustomAbstract.getSwitchIndex;


/**
 * A simple example simulating a distributed application.
 * It shows how 2 {@link NetworkCloudlet}'s communicate,
 * each one running inside VMs on different hosts.
 *
 * @author Manoel Campos da Silva Filho
 */
public class PrimerEscenario {
    private static final int HOSTS = 1;
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 4;
    private static final int HOST_RAM = 2048; // host memory (Megabyte)
    private static final long HOST_STORAGE = 1000000; // host storage
    private static final long HOST_BW = 10000;

    private static final int TASK_LENGTH = 4000;
    private static final int CLOUDLET_FILE_SIZE = 300;
    private static final int CLOUDLET_OUTPUT_SIZE = 300;
    private static final long PACKET_DATA_LENGTH_IN_BYTES = 1000;
    private static final int NUMBER_OF_PACKETS_TO_SEND = 1;
    private static final long TASK_RAM = 100; // in Megabytes


    private static final long NUM_PACKETS = 1;
    private static final long PACKET_LENGTH = 1;
    private static final int CANT_VMS = 7;

    private final CloudSimPlus simulation;

    private List<NetworkVm> vmList;
    private List<NetworkCloudlet> cloudletList;
    private final NetworkDatacenterExt datacenterEdge;
    private final NetworkDatacenterExt datacenterFog;
    private final NetworkDatacenterExt datacenterCloud;
    private final DatacenterBroker broker;
    private final Net net;

    /**
     * Starts the execution of the example.
     * @param args
     */
    public static void main(String[] args) {
        new PrimerEscenario();
    }

    /**
     * Creates, starts, stops the simulation and shows results.
     */
    private PrimerEscenario() {
        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSimPlus();
        net = crateNet();
        datacenterEdge = createDatacenterEdge();
        datacenterFog = createDatacenterFog();
        datacenterCloud = createDatacenterCloud();
        broker = new DatacenterBrokerSimple(simulation);
        vmList = createAndSubmitVMs(broker);
        createServices(broker, 100);
        simulation.start();
        showSimulationResults();
    }

    private Net crateNet() {
        Net n =  new Net(simulation);
        n.setDelay(0,0, 0);
        n.setDelay(0,1, 10);
        n.setDelay(0,2, 15);
        n.setDelay(0,3, 30);
        n.setDelay(1,0,10);
        n.setDelay(1,1, 0);
        n.setDelay(1,2, 10);
        n.setDelay(1,3, 20);
        n.setDelay(2,0, 15);
        n.setDelay(2,1, 10);
        n.setDelay(2,2, 0);
        n.setDelay(2,3, 10);
        n.setDelay(3,0, 30);
        n.setDelay(3,1, 20);
        n.setDelay(3,2, 10);
        n.setDelay(3,3, 0);
        return n;
    }


    private void createServices(DatacenterBroker broker, int cantServices){
        for (int i = 0; i < cantServices; i++) {
            int path = Sorteo();  //sorteo el path, 86% path0, 12% path1, 2% path2.
            if (path == 0){
                cloudletList = createPath0();
            }
            if (path == 1){
                cloudletList = createPath1();
            }
            else{
                cloudletList = createPath2();
            }
            broker.submitCloudletList(cloudletList, i*5);
        }
    }

    private List<NetworkCloudlet> createPath2() {
        return List.of();
    }

    private List<NetworkCloudlet> createPath1() {
        return List.of();
    }

    private NetworkDatacenterExt createDatacenterCloud() {
        NetworkDatacenterExt dc = createDatacenter();
        createNetwork(dc, 3);
        return dc;
    }

    private NetworkDatacenterExt createDatacenterFog() {
        NetworkDatacenterExt dc = createDatacenter();
        createNetwork(dc, 2);
        return dc;
    }

    private NetworkDatacenterExt createDatacenterEdge() {
        NetworkDatacenterExt dc = createDatacenter();
        createNetwork(dc, 1);
        return dc;
    }

    private NetworkDatacenterExt createDatacenterClient(){
        NetworkDatacenterExt dc = createDatacenter();
        createNetwork(dc, 0);
        return dc;
    }

    private int Sorteo() {
        double r = Math.random(); // número aleatorio entre 0 y 1
        if (r < 0.86) {
            return 0; // 86%
        } else if (r < 0.86 + 0.12) {
            return 1; // 12%
        } else {
            return 2; // 2%
        }
    }


    private void showSimulationResults() {
        final var cloudletFinishedList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudletFinishedList).build();

        System.out.println();
        for (NetworkHostExt host : datacenterEdge.getHostList()) {
            System.out.printf("Host %d data transferred: %d bytes%n",
                host.getId(), host.getTotalDataTransferBytes());
        }
        for (NetworkHostExt host : datacenterFog.getHostList()) {
            System.out.printf("Host %d data transferred: %d bytes%n",
                host.getId(), host.getTotalDataTransferBytes());
        }
        for (NetworkHostExt host : datacenterCloud.getHostList()) {
            System.out.printf("Host %d data transferred: %d bytes%n",
                host.getId(), host.getTotalDataTransferBytes());
        }

        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private NetworkDatacenterExt createDatacenter() {
        final var netHostList = new ArrayList<NetworkHostExt>();
        for (int i = 0; i < HOSTS; i++) {
            final NetworkHostExt host = createHost();
            netHostList.add(host);
        }

        final var dc = new NetworkDatacenterExt(simulation, netHostList);

        return dc;
    }

    private NetworkHostExt createHost() {
        final var peList = createPEs(HOST_PES, HOST_MIPS);
        final var host = new NetworkHostExt(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
        host
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());

        return host;
    }

    private List<Pe> createPEs(final int pesNumber, final long mips) {
        final var peList = new ArrayList<Pe>();
        for (int i = 0; i < pesNumber; i++) {
            peList.add(new PeSimple(mips));
        }

        return peList;
    }

    /**
     * Creates internal Datacenter network.
     *
     * @param datacenter Datacenter where the network will be created
     */
    private void createNetwork(final NetworkDatacenterExt datacenter, int level) {
        final var edgeSwitchExt = new EdgeSwitchExt(simulation, datacenter, level);
        datacenter.addSwitch(edgeSwitchExt);
        for (NetworkHostExt host : datacenter.getHostList()) {
            edgeSwitchExt.connectHost(host);
        }
        edgeSwitchExt.connectToNet(net);
    }

    /**
     * Creates a list of virtual machines in a Datacenter for a given broker and
     * submit the list to the broker.
     *
     * @param broker The broker that will own the created VMs
     * @return the list of created VMs
     */
    private List<NetworkVm> createAndSubmitVMs(DatacenterBroker broker) {
        final var netVmList = new ArrayList<NetworkVm>();
        for (int i = 0; i < CANT_VMS; i++) {
            final NetworkVm vm = createVm(i);
            netVmList.add(vm);
        }

        broker.submitVmList(netVmList);
        return netVmList;
    }

    private NetworkVm createVm(int id) {
        final var vm = new NetworkVm(id, HOST_MIPS, HOST_PES);
        vm
            .setRam(HOST_RAM)
            .setBw(HOST_BW)
            .setSize(HOST_STORAGE)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the
     * distributed processes of a given fictitious application.
     *
     * @return the list of create NetworkCloudlets
     */
    private List<NetworkCloudlet> createPath0() {
        final int cloudletsNumber = 3;
        final var netCloudletList = new ArrayList<NetworkCloudlet>(cloudletsNumber);

        for (int i = 0; i < cloudletsNumber; i++) {
            netCloudletList.add(createNetworkCloudlet(vmList.get(i)));
        }
        NetworkCloudlet CLIENT = netCloudletList.get(0);
        NetworkCloudlet NGINX = netCloudletList.get(1);
        NetworkCloudlet MEMCACHED = netCloudletList.get(2);

        //NetworkCloudlet 0 CLIENT
        addSendTask(CLIENT, NGINX, NUM_PACKETS, PACKET_LENGTH);
        addReceiveTask(CLIENT, NGINX, NUM_PACKETS);

        //NetworkCloudlet 1 NGINX
        //epoll como un exec
        addReceiveTask(NGINX, CLIENT, NUM_PACKETS);
        addExecutionTask(NGINX,1500); //epoll
        addExecutionTask(NGINX,2000); //socket
        addExecutionTask(NGINX, 40000); //process
        //Consulta MemCached
        addExecutionTask(MEMCACHED, 1500); //epoll
        addSendTask(NGINX, MEMCACHED, NUM_PACKETS, PACKET_LENGTH); //consulta
        addReceiveTask(NGINX, MEMCACHED, NUM_PACKETS);  //wait recv
        addExecutionTask(NGINX, 40000);  //process
        addSendTask(NGINX, CLIENT, NUM_PACKETS, PACKET_LENGTH);  //send client

        //NetworkCloudlet 2 MEMCACHED
        addReceiveTask(MEMCACHED, NGINX, NUM_PACKETS);       // (recibe petición)
        addExecutionTask(MEMCACHED, 1500);                   // epoll
        addExecutionTask(MEMCACHED, 2000);                   // socket
        addExecutionTask(MEMCACHED, 600);                    // proc_read
        addSendTask(MEMCACHED, NGINX, NUM_PACKETS, PACKET_LENGTH); // send (responde a nginx)
        return netCloudletList;
    }

    /**
     * Creates a {@link NetworkCloudlet}.
     *
     * @param vm the VM that will run the created {@link NetworkCloudlet)
     * @return
     */
    private NetworkCloudlet createNetworkCloudlet(NetworkVm vm) {
        final var netCloudlet = new NetworkCloudlet(HOST_PES);
        netCloudlet
            .setFileSize(CLOUDLET_FILE_SIZE)
            .setOutputSize(CLOUDLET_OUTPUT_SIZE)
            .setUtilizationModel(new UtilizationModelFull())
            .setVm(vm)
            .setBroker(vm.getBroker())
            .setId(vm.getId());

        return netCloudlet;
    }

    /**
     * Adds an execution-task to the list of tasks of the given
     * {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} the task will belong to
     */
    private static void addExecutionTask(NetworkCloudlet cloudlet, long task_length) {
        final var task = new CloudletExecutionTask(cloudlet.getTasks().size(), task_length);
        task.setMemory(TASK_RAM);
        cloudlet.addTask(task);
    }

    /**
     * Adds a send-task to the list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param sourceCloudlet the {@link NetworkCloudlet} from which packets will be sent
     * @param destinationCloudlet the destination {@link NetworkCloudlet} to send packets to
     */
    private void addSendTask(final NetworkCloudlet sourceCloudlet, final NetworkCloudlet destinationCloudlet, long packet_num, long packet_length) {
        final var task = new CloudletSendTask(sourceCloudlet.getTasks().size());
        task.setMemory(TASK_RAM);
        sourceCloudlet.addTask(task);
        for (int i = 0; i < packet_num; i++) {
            task.addPacket(destinationCloudlet, packet_length);
        }
    }

    /**
     * Adds a receive-task to the list of tasks of the given
     * {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} the task will belong to
     * @param sourceCloudlet the {@link NetworkCloudlet} expected to receive packets from
     */
    private void addReceiveTask(final NetworkCloudlet cloudlet, final NetworkCloudlet sourceCloudlet, long packet_num) {
        final var task = new CloudletReceiveTask(cloudlet.getTasks().size(), sourceCloudlet.getVm());
        task.setMemory(TASK_RAM);
        task.setExpectedPacketsToReceive(packet_num);
        cloudlet.addTask(task);
    }
}

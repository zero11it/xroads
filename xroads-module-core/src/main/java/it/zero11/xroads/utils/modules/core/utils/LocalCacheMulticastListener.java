package it.zero11.xroads.utils.modules.core.utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalCacheMulticastListener extends Thread {
	private boolean shutdown = true;
	private final String PLATFORM_NAME = "Shopify";
	private final Map<String, LocalCache> localCaches = new HashMap<>();
	private final Queue<String> toClearQueue = new LinkedBlockingQueue<>();
	
	private static LocalCacheMulticastListener instance;
	public static LocalCacheMulticastListener getInstance() {
		if (instance == null) {
			synchronized (LocalCache.class){
				if (instance == null){
					instance = new LocalCacheMulticastListener();
				}
			}
		}
		return instance;
	}
	
	private LocalCacheMulticastListener() {
	}

	public void registerCache(LocalCache localCache) {
		localCaches.put(PLATFORM_NAME + localCache.name, localCache);
	}
	
	public void shutdown() {
		if (isAlive()) {
			shutdown = true;
			localCaches.clear();
			toClearQueue.clear();
		}
	}
	
	@Override
	public void run() {
		int port = 6000 + PLATFORM_NAME.hashCode() % 1024;
		setName("LocalCacheMulticastListener-" + PLATFORM_NAME + "-" + port);
		shutdown = false;
		try {
			InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("228.5.6.7"), port);
			NetworkInterface networkInterface = getDefaultNetworkInterface();
			MulticastSocket multicastSocket = new MulticastSocket(port);
			multicastSocket.setNetworkInterface(networkInterface);
			multicastSocket.setLoopbackMode(true);
			multicastSocket.setSoTimeout(5000);
			multicastSocket.joinGroup(address, networkInterface);

			byte[] buffer = new byte[1024];
			while(!shutdown) {
				String value;
				while((value = toClearQueue.poll()) != null) {
					byte[] data = value.getBytes();
					multicastSocket.send(new DatagramPacket(data, data.length, address));
				}
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					multicastSocket.receive(packet);
					String data = new String(packet.getData(), 0, packet.getLength());
					
					for (Map.Entry<String, LocalCache> entry : localCaches.entrySet()) {
						if (data.startsWith(entry.getKey())) {
							entry.getValue().invalidateLocalOnly(data.substring(entry.getKey().length()));
						}
					}
				}catch (SocketTimeoutException e) {
				}
			}

			multicastSocket.leaveGroup(address, networkInterface);
	
			multicastSocket.close();
		}catch (Exception e) {
			shutdown = true;
			toClearQueue.clear();
			
			e.printStackTrace();
		}
	}

	private NetworkInterface getDefaultNetworkInterface() throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while(networkInterfaces.hasMoreElements()){
		    NetworkInterface networkInterface = networkInterfaces.nextElement();
		    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
		    while (inetAddresses.hasMoreElements()){
		        InetAddress inetAddress = inetAddresses.nextElement();
		        if (inetAddress.isSiteLocalAddress()) {
		        	return networkInterface;
		        }
		    }
		}
		throw new IllegalArgumentException("No interface found with a site local ip");
	}
	
	public void invalidate(LocalCache localCache, String key) {
		if (!shutdown) {
			toClearQueue.add(PLATFORM_NAME + localCache.name + key);
		}
	}
}

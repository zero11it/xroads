package it.zero11.xroads.utils.modules.core.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections4.map.LRUMap;

public class LocalCacheUDPListener extends Thread {
	
	private boolean shutdown = true;
	private final Map<String, LocalCache> localCaches = new HashMap<>();
	private final Queue<String> toClearQueue = new LinkedBlockingQueue<>();
	
	private int port = 6000;
	private LocalCacheUDPListenerNodeListProvider nodeListProvider;
	private Set<InetAddress> nodeList;
	private Map<InetAddress, Boolean> localAddresses = new LRUMap<>(50);
	
	private static LocalCacheUDPListener instance;
	public static LocalCacheUDPListener getInstance() {
		if (instance == null) {
			synchronized (LocalCache.class){
				if (instance == null){
					instance = new LocalCacheUDPListener();
				}
			}
		}
		return instance;
	}
	
	public interface LocalCacheUDPListenerNodeListProvider{
		Collection<String> getCurrentNodes();
	}
	
	private LocalCacheUDPListener() {
		super("LocalCacheUDPListener");
	}

	public void registerCache(LocalCache localCache) {
		localCaches.put(LocalCache.PLATFORM_NAME + localCache.name, localCache);
	}
	
	public void setNodeListProvider(LocalCacheUDPListenerNodeListProvider nodeListProvider) {
		this.nodeListProvider = nodeListProvider;
	}
	
	public void shutdown() {
		if (isAlive()) {
			shutdown = true;
			localCaches.clear();
			toClearQueue.clear();
		}
	}

	public void setContextName(String servletContextName) {
		if (!shutdown)
			throw new RuntimeException("LocalCacheUDPListener already started");
		

		port = 6000 + servletContextName.hashCode() % 1024;
	}
	
	@Override
	public void run() {
		setName("LocalCacheUDPListener-" + LocalCache.PLATFORM_NAME + "-" + port);
		shutdown = false;
		try {
			DatagramSocket datagramSocket = new DatagramSocket(null);
			datagramSocket.setSoTimeout(5000);
			datagramSocket.setReuseAddress(true);
			datagramSocket.bind(new InetSocketAddress(port));

			updateAliveNodes();
			
			int updateNodeTime = 0;
			byte[] buffer = new byte[1024];
			while(!shutdown) {
				if (++updateNodeTime % 100 == 0) {
					updateAliveNodes();
				}
				String value;
				while((value = toClearQueue.poll()) != null) {
					byte[] data = value.getBytes();
					
					for (InetAddress node : nodeList) {
						datagramSocket.send(new DatagramPacket(data, data.length, new InetSocketAddress(node, port)));
					}
				}
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					datagramSocket.receive(packet);
					if (!nodeList.contains(packet.getAddress()) && !isLocalhost(packet.getAddress())) {
						nodeList.add(packet.getAddress());
					}
					String data = new String(packet.getData(), 0, packet.getLength());
					
					for (Map.Entry<String, LocalCache> entry : localCaches.entrySet()) {
						if (data.startsWith(entry.getKey())) {
							entry.getValue().invalidateLocalOnly(data.substring(entry.getKey().length()));
						}
					}
				}catch (SocketTimeoutException e) {
				}
			}
			
			datagramSocket.close();
		}catch (Exception e) {
			shutdown = true;
			toClearQueue.clear();
			
			e.printStackTrace();
		}
	}

	private void updateAliveNodes() {
		Set<InetAddress> nodes = new HashSet<>();
		try {
			if (nodeListProvider != null) {
				for (String node : nodeListProvider.getCurrentNodes()) {
					try {
						InetAddress address = InetAddress.getByName(node);
						if (!isLocalhost(address)) {
							nodes.add(address);
						}
					}catch(Exception e) {
					}
				}
			}
		}catch(Exception e) {
			
		}
		this.nodeList = nodes;
	}
	
	public boolean isLocalhost(InetAddress addr) {
		Boolean isLocal = localAddresses.get(addr);
		if (isLocal == null) {
		    if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
		    	isLocal = Boolean.TRUE;
		    }else {
			    // Check if the address is defined on any interface
			    try {
			        isLocal = NetworkInterface.getByInetAddress(addr) != null;
			    } catch (SocketException e) {
			    	isLocal = Boolean.FALSE;
			    }
		    }
		    localAddresses.put(addr, isLocal);
		}
		
		return isLocal;
	}
	
	public void invalidate(LocalCache localCache, String key) {
		if (!shutdown) {
			toClearQueue.add(LocalCache.PLATFORM_NAME + localCache.name + key);
		}
	}
}

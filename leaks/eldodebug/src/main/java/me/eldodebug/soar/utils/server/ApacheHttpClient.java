package me.eldodebug.soar.utils.server;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import me.eldodebug.soar.Soar;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.http.HypixelHttpResponse;

public class ApacheHttpClient implements HypixelHttpClient {

	private final UUID apiKey;
	private final HttpClient httpClient;

	public ApacheHttpClient(UUID apiKey) {
		this.apiKey = apiKey;
		this.httpClient = HttpClientBuilder.create().setUserAgent(Soar.instance.getName()).build();
	}

	@Override
	public CompletableFuture<HypixelHttpResponse> makeRequest(String url) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HttpResponse response = this.httpClient.execute(new HttpGet(url));
				return new HypixelHttpResponse(response.getStatusLine().getStatusCode(),
						EntityUtils.toString(response.getEntity(), "UTF-8"));
			}
			catch(IOException e) {
				throw new RuntimeException(e);
			}
		}, Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors(), 2)));
	}

	@Override
	public CompletableFuture<HypixelHttpResponse> makeAuthenticatedRequest(String url) {
		return CompletableFuture.supplyAsync(() -> {
			HttpGet request = new HttpGet(url);
			request.addHeader("API-Key", this.apiKey.toString());
			try {
				HttpResponse response = this.httpClient.execute(request);
				return new HypixelHttpResponse(response.getStatusLine().getStatusCode(),
						EntityUtils.toString(response.getEntity(), "UTF-8"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors(), 2)));
	}

	@Override
	public void shutdown() {
	}

}
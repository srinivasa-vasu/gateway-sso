package io.pivotal.gateway.filter;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class RewriteLocationURLResponseHeaderGatewayFilterFactory extends
		AbstractGatewayFilterFactory<RewriteLocationURLResponseHeaderGatewayFilterFactory.Config> {

	public RewriteLocationURLResponseHeaderGatewayFilterFactory() {
		super(RewriteLocationURLResponseHeaderGatewayFilterFactory.Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new GatewayFilter() {
			@Override
			public Mono<Void> filter(ServerWebExchange exchange,
					GatewayFilterChain chain) {
				return chain.filter(exchange)
						.then(Mono.fromRunnable(() -> rewriteLocation(exchange, config)));
			}

			@Override
			public String toString() {
				// @formatter:off
				return filterToStringCreator(
						this)
						.append("locationHeaderName", config.locationHeaderName)
						.append("hostValue", config.hostValue)
						.append("overrideHostValue", config.overrideHostValue)
						.toString();
				// @formatter:on
			}
		};
	}

	void rewriteLocation(ServerWebExchange exchange, Config config) {
		String location = exchange.getResponse().getHeaders()
				.getFirst(config.getLocationHeaderName());
		if (location != null && location.contains(config.hostValue)) {
			exchange.getResponse().getHeaders().set(config.getLocationHeaderName(),
					location.replace(config.getHostValue(),
							config.getOverrideHostValue()));
		}
	}

	public static class Config {

		private String locationHeaderName = HttpHeaders.LOCATION;

		private String hostValue;

		private String overrideHostValue;

		public String getLocationHeaderName() {
			return locationHeaderName;
		}

		public Config setLocationHeaderName(String locationHeaderName) {
			this.locationHeaderName = locationHeaderName;
			return this;
		}

		public String getHostValue() {
			return hostValue;
		}

		public Config setHostValue(String hostValue) {
			this.hostValue = hostValue;
			return this;
		}

		public String getOverrideHostValue() {
			return overrideHostValue;
		}

		public Config setOverrideHostValue(String overrideHostValue) {
			this.overrideHostValue = overrideHostValue;
			return this;
		}

	}

}

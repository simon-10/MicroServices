package com.simon.microservices.currencyconversionservice;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//@FeignClient(name="currency-exchange-service", url="localhost:8001")
//@FeignClient(name="currency-exchange-service")//rest client
@FeignClient(name="netflix-zuul-api-gateway-server")//rest client

@RibbonClient(name="currency-exchange-service")//load balancer
public interface CurrencyExchangeServiceProxy {
	@GetMapping("/currency-exchange-service/currency-exchange/from/{from}/to/{to}")
	public CurrenyConversionBean retrieveExchangeValue(@PathVariable String from, @PathVariable String to);
}

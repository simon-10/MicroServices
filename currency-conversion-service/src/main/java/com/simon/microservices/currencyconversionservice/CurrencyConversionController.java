package com.simon.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;


@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	@Autowired
	private RestTemplate template;
	
	@Autowired
	private EurekaClient client;
	
	@Value("${currency-exchange-service}")
	private String url;
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());


	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrenyConversionBean convertCurrency(@PathVariable String from, 
			@PathVariable String to, @PathVariable BigDecimal quantity) {

		Map<String,String> uriVariables=new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrenyConversionBean> responseEntity=new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}",
				CurrenyConversionBean.class, uriVariables);
		CurrenyConversionBean response=responseEntity.getBody();
		
		System.out.println("simon"+response.toString());
		
		return new CurrenyConversionBean(response.getId(),from,to,response.getConversionMultiple(),
				quantity,quantity.multiply(response.getConversionMultiple()),response.getPort());
		
		
	}
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrenyConversionBean convertCurrencyFeign(@PathVariable String from, 
			@PathVariable String to, @PathVariable BigDecimal quantity) {

		CurrenyConversionBean response=proxy.retrieveExchangeValue(from,to);
		
		logger.info("{}", response);
				
		return new CurrenyConversionBean(response.getId(),from,to,response.getConversionMultiple(),
				quantity,quantity.multiply(response.getConversionMultiple()),response.getPort());
		
		
	}
	
	
	
	@GetMapping("/currency-converter-template/from/{from}/to/{to}/quantity/{quantity}")
	public CurrenyConversionBean getConversion(@PathVariable String from, 
			@PathVariable String to, @PathVariable BigDecimal quantity) {
		
		
		InstanceInfo info=client.getNextServerFromEureka(url, false);
		
				
		CurrenyConversionBean response=template.getForObject(info.getHomePageUrl()+"/currency-exchange/from/"+from+"/to/"+to, CurrenyConversionBean.class);
		
		return response;
	}
}

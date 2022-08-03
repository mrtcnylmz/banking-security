package com.mrtcn.bankingSystem.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mrtcn.bankingSystem.Models.Account;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class CurrencyExchange {
	public JsonNode responseNode;
	JsonNode resultNode;
	String base = "";
	String to = "";
	String url = "";
	double goldBuy;
	double goldSell;

	public double Exchange(Account senderAccount, Account receiverAccount) {

		//Accessing to the api service for current exchange rates.
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "apikey 5qx4WWOHrr9PAwWFLCB7aj:7bQ5LKO3NWFCKPjrtn2lEK");
		headers.add("content-type", "application/json");
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);

		//Api access url formed based on the parameters.
		if (senderAccount.getType().equals("TL")) {
			base = "TRY";
			if (receiverAccount.getType().equals("Dolar")) {
				to = "USD";
				url = "https://api.collectapi.com/economy/exchange?int=1&to=" + to + "&base=" + base;
			} else {
				to = "Altın";
				url = "https://api.collectapi.com/economy/goldPrice";
			}
		} else if (senderAccount.getType().equals("Dolar")) {
			base = "USD";
			if (receiverAccount.getType().equals("TL")) {
				to = "TRY";
				url = "https://api.collectapi.com/economy/exchange?int=1&to=" + to + "&base=" + base;
			} else {
				to = "Altın";
				url = "https://api.collectapi.com/economy/goldPrice";
			}
		} else {
			base = "Altın";
			if (receiverAccount.getType().equals("Dolar")) {
				to = "USD";
				url = "https://api.collectapi.com/economy/goldPrice";
			} else {
				to = "TRY";
				url = "https://api.collectapi.com/economy/goldPrice";
			}
		}

		//Api call.
		ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		//Response mapped.
		try {
			responseNode = new ObjectMapper().readTree(apiResponse.getBody());
		} catch (
				JsonProcessingException e) {
			e.printStackTrace();
		}

		resultNode = responseNode.get("result");

		//If any account has its type as gold, the url and json parsing chances, this particular api is not the best on gold to foreign currency exhange rate calls.
		if (Objects.equals(base, "Altın") || Objects.equals(to, "Altın")) {
			if (resultNode.isArray()) {
				ArrayNode pricesNode = (ArrayNode) resultNode;

				for (int i = 0; i < pricesNode.size(); i++) {
					JsonNode singlePrice = pricesNode.get(i);

					if (singlePrice.get("name").asText().equals("Gram Altın")) {
						//Buy and Sell rates received as int.
						goldBuy = singlePrice.get("buying").asInt();
						goldSell = singlePrice.get("selling").asInt();
					}
				}
			}
			if (to.equals("TRY")){
				return goldBuy;
			}else if (base.equals("TRY")){
				return (1/goldSell);
			}else {
				//If transfers not between gold types extra api calls are necessary.
				if (to.equals("USD")){
					String tlToDolarUrl = "https://api.collectapi.com/economy/exchange?int=1&to=USD&base=TRY";
					apiResponse = restTemplate.exchange(tlToDolarUrl, HttpMethod.GET, requestEntity, String.class);

					try {
						responseNode = new ObjectMapper().readTree(apiResponse.getBody());
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

					resultNode = responseNode.get("result");
					double tlToDolarRate = resultNode.get("data").get(0).get("rate").asDouble();
					return tlToDolarRate * goldBuy;

				} else if (base.equals("USD")){
					String dolarToTlUrl = "https://api.collectapi.com/economy/exchange?int=1&to=USD&base=TRY";
					apiResponse = restTemplate.exchange(dolarToTlUrl, HttpMethod.GET, requestEntity, String.class);

					try {
						responseNode = new ObjectMapper().readTree(apiResponse.getBody());
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

					resultNode = responseNode.get("result");
					double dolarToTlRate = resultNode.get("data").get(0).get("rate").asDouble();
					return dolarToTlRate * goldBuy;
				}
			}
		}else {
			//If transfers are just between currencies(and not gold) then a simple api call with basic urls are sufficient.
			if (resultNode.get("data").isArray()) {
				ArrayNode pricesNode = (ArrayNode) resultNode.get("data");
				return pricesNode.get(0).get("rate").asDouble();	//Exchange rates returned as double.
			}
		}
			return -1;
		}
}

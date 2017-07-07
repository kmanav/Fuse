package com.redhat.training.jb421;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.jsonpath.JsonPath;
import org.apache.camel.util.URISupport;

/**
 * Bean used to parse JSON Address data and set headers on the exchange for the
 * SQL component to use when creating the dynamic queries.
 */
public class JSONPathAddressBean {

	private static final String ID_KEY = "id";

	/**
	 * Sample JSON data: { "address": { "id": "1", "streetAddress1": "One
	 * Progress Plaza", "streetAddress2": "411 Fayetteville Street",
	 * "streetAddress3": "", "city": "Raleigh", "state": "NC", "postalCode":
	 * "27601", "country": "US", "type": "billing" } }
	 * 
	 */
	public void process(@JsonPath("$.address.id") Integer id,
			@JsonPath("$.address.streetAddress1") String streetAddress1,
			@JsonPath("$.address.streetAddress2") String streetAddress2,
			@JsonPath("$.address.streetAddress3") String streetAddress3, @JsonPath("$.address.city") String city,
			@JsonPath("$.address.state") String state, @JsonPath("$.address.postalCode") String postalCode,
			@JsonPath("$.address.country") String country, @JsonPath("$.address.type") String type, Exchange exchange)
			throws Exception {
		// Set headers for sql component place holders
		exchange.getIn().setHeader("id", id);
		exchange.getIn().setHeader("streetAddress1", streetAddress1);
		exchange.getIn().setHeader("streetAddress2", streetAddress2);
		exchange.getIn().setHeader("streetAddress3", streetAddress3);
		exchange.getIn().setHeader("city", city);
		exchange.getIn().setHeader("state", state);
		exchange.getIn().setHeader("postalCode", postalCode);
		exchange.getIn().setHeader("country", country);

		String addressColumn;

		if (type.equals("billing")) {
			addressColumn = "bill_addr_id";
		} else {
			addressColumn = "ship_addr_id";
		}
		// Update column based on Address type
		exchange.getIn().setHeader("addressColumn", addressColumn);

		String query = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
		Map<String, Object> queryParams = URISupport.parseQuery(query);
		if (queryParams.containsKey(ID_KEY)) {
			exchange.getIn().setHeader("customerId", queryParams.get(ID_KEY));
		} else {
			throw new UnsupportedOperationException("A query parameter containing the 'id' is required!");
		}
	}

}
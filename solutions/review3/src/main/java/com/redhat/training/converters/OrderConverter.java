package com.redhat.training.converters;

import java.math.BigDecimal;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;

import com.redhat.training.model.Order;
import com.redhat.training.model.OrderReceived;
//TODO Add converter annotations
@Converter
public class OrderConverter {
	@Converter
	public static OrderReceived orderToOrderReceived(Order order, Exchange exchange) {
		return new OrderReceived(order.getId(), order.getName(), order.getItem(), order.getQuantity(),
				order.getExtendedAmount().divide(new BigDecimal(order.getQuantity())));
	}

	@Converter
	public static Order OrderReceivedToOrder(OrderReceived order, Exchange exchange) {
		return new Order(order.getId(), order.getCustomerName(), order.getItemName(), order.getQuantity(),
				order.getPrice().multiply(new BigDecimal(order.getQuantity())));
	}
}
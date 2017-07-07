package com.redhat.training.jb421;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;

import com.redhat.training.jb421.model.InventoryUpdate;

public class ConcurrentRouteBuilder extends RouteBuilder {

	public static String INPUT_FOLDER = "/home/student/jb421/solutions/throughput-files/items/incoming";
	public static String OUTPUT_FOLDER = "/home/student/jb421/solutions/throughput-files/items/outgoing";

	@Override
	public void configure() throws Exception {

		ExecutorService threadPool = Executors.newFixedThreadPool(10);

		from("file://"+ INPUT_FOLDER+"?delete=true")
			.bean("timerBean","start")
			.threads().executorService(threadPool)
				.marshal().bindy(BindyType.Csv,InventoryUpdate.class)
				.process(new InventoryUpdateProcessor())
				.wireTap("direct:partnerUpdate")
				.to("file:"+OUTPUT_FOLDER+"?fileName=${header.publisherId}/"
					+ "inventory-reservation-${date:now:yyyy-MM-dd}.csv&fileExist=Append")
				.bean("timerBean","stop")
			.end();

		from("direct:partnerUpdate")
			.process(new PartnerInventoryUpdateProcessor());
	}

}

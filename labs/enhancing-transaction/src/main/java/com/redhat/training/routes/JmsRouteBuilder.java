package com.redhat.training.routes;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.persistence.EntityManager;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jpa.JpaComponent;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.wildfly.extension.camel.CamelAware;

import com.redhat.training.model.Contact;

import com.redhat.training.jms.transacted.JmsTransactionManager;

@Startup
@CamelAware
@ApplicationScoped
public class JmsRouteBuilder extends RouteBuilder {

	@Inject
	private EntityManager entityManager;

	//TODO inject TransactionManager

	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;

	@Override
	public void configure() throws Exception {

		// TODO configure JMS component to support JMS transactions.

		// TODO configure JPA component to support transactions.


		/**
		 * Configure JAXB so that it can discover model classes.
		 */
		JaxbDataFormat jaxbDataFormat = new JaxbDataFormat();
		jaxbDataFormat.setContextPath(Contact.class.getPackage().getName());

		onException(IllegalStateException.class)
		.maximumRedeliveries(1)
		.handled(true)
		.to("jms:queue:DeadLetter")
		.markRollbackOnly();

		from("file:/home/student/jb421/labs/enhancing-transaction/contact")
			.unmarshal(jaxbDataFormat)
				.to("jpa:com.redhat.training.model.Contact");

		/**
		 *
		 * Whenever the email is empty, the route  throws an IllegalStateException which forces the JMS / JPA
		 * transaction to be rolled back and the message to be delivered to the
		 * dead letter queue.
		 */
		from("jpa:com.redhat.training.model.Contact")
				.to("jms:queue:ContactsQueue")
				//TODO Configure as transacted
				.choice()
					.when(simple("${body.email} == null"))
						.log("Invalid email - rolling back transaction!")
						.throwException(new IllegalStateException())
					.otherwise()
						.log("Contact processed successfully");

	}
}
